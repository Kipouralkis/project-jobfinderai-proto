package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.dto.JobChunkSearchResult;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import gr.kipouralkis.backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobSearchService {

    private final EmbeddingApiService embeddingApiService;
    private final JobChunkJdbcRepository jobChunkJdbcRepository;
    private final JobRepository jobRepository;
    private final RerankingService rerankingService;

    public JobSearchService(
            EmbeddingApiService embeddingApiService,
            JobChunkJdbcRepository jobChunkJdbcRepository,
            JobRepository jobRepository,
            RerankingService rerankingService){
        this.embeddingApiService=embeddingApiService;
        this.jobChunkJdbcRepository=jobChunkJdbcRepository;
        this.jobRepository=jobRepository;
        this.rerankingService = rerankingService;
    }


    public List<Job> searchJobs(String query) {

        long t0 = System.currentTimeMillis();

        // 1) Embedding
        long eStart = System.currentTimeMillis();
        float[] embedding = embeddingApiService.embed(query);

        System.out.println("Embedding hash(query): " + Arrays.hashCode(embedding));

        long eEnd = System.currentTimeMillis();

        // 2) Vector search
        long vStart = System.currentTimeMillis();
        List<JobChunkSearchResult> chunks = jobChunkJdbcRepository.searchChunks(embedding, 20);
        long vEnd = System.currentTimeMillis();

        // 3) Grouping
        long gStart = System.currentTimeMillis();
        Map<UUID, JobChunkSearchResult> bestPerJob =
                chunks.stream()
                        .collect(Collectors.toMap(
                                JobChunkSearchResult::jobId,
                                c -> c,
                                (c1, c2) -> c1.distance() < c2.distance() ? c1 : c2
                        ));
        long gEnd = System.currentTimeMillis();

        // 4) DB fetch
        long fStart = System.currentTimeMillis();
        List<Job> jobs = bestPerJob.keySet().stream()
                .map(jobRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        long fEnd = System.currentTimeMillis();

        long total = System.currentTimeMillis() - t0;

        System.out.println("=== VECTOR SEARCH RESULTS ===");
        for (JobChunkSearchResult c : chunks) {
            System.out.println("job=" + c.jobId() + "  distance=" + c.distance());
        }
        System.out.println("=============================");


        // JSON log
        System.out.println("""
        {
          "event": "job_search_timing",
          "query": "%s",
          "timings": {
            "embedding_ms": %d,
            "vector_search_ms": %d,
            "grouping_ms": %d,
            "job_fetch_ms": %d,
            "total_ms": %d
          }
        }
        """.formatted(
                        query,
                        (eEnd - eStart),
                        (vEnd - vStart),
                        (gEnd - gStart),
                        (fEnd - fStart),
                        total
                )
        );

//        return rerankJobs(query, jobs);

        return jobs;
    }

    public List<Job> rerankJobs(String query, List<Job> jobs) {

        if (jobs.isEmpty()) {
            return jobs;
        }

        // Make a fresh mutable copy to avoid JPA proxy weirdness
        List<Job> ranked = jobs.stream()
                .map(j -> j) // shallow copy of references
                .collect(Collectors.toCollection(java.util.ArrayList::new));

        // Extract documents
        List<String> docs = ranked.stream()
                .map(Job::getDescription)
                .toList();

        // Call reranker
        List<Double> scores = rerankingService.rerank(query, docs);

        // Attach scores
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setRerankScore(scores.get(i));
            System.out.println("Job Score: " + ranked.get(i).getRerankScore());
        }

        System.out.println("DOC COUNT = " + docs.size());
        System.out.println("SCORE COUNT = " + scores.size());
        System.out.println("SCORES = " + scores);

        System.out.println("=== JOBS BEFORE SORT ===");
        for (int i = 0; i < jobs.size(); i++) {
            Job j = jobs.get(i);
            System.out.println(i + " -> "
                    + j.getId()
                    + " / " + j.getTitle()
                    + " / score=" + j.getRerankScore());
        }


        // Sort the fresh list
//        ranked.sort((a, b) -> Double.compare(b.getRerankScore(), a.getRerankScore()));

//        return ranked;

        return jobs;
    }


}
