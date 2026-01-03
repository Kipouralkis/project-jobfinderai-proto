package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.dto.JobChunkSearchResult;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import gr.kipouralkis.backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobSearchService {

    private final EmbeddingApiService embeddingApiService;
    private final JobChunkJdbcRepository jobChunkJdbcRepository;
    private final JobRepository jobRepository;

    public JobSearchService(
            EmbeddingApiService embeddingApiService,
            JobChunkJdbcRepository jobChunkJdbcRepository,
            JobRepository jobRepository
    ){
        this.embeddingApiService=embeddingApiService;
        this.jobChunkJdbcRepository=jobChunkJdbcRepository;
        this.jobRepository=jobRepository;
    }


    public List<Job> searchJobs(String query) {

        long t0 = System.currentTimeMillis();

        // 1) Embedding
        long eStart = System.currentTimeMillis();
        float[] embedding = embeddingApiService.embed(query);
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
                .toList();
        long fEnd = System.currentTimeMillis();

        long total = System.currentTimeMillis() - t0;

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

        return jobs;
    }

}
