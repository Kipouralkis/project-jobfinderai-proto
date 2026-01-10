package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.model.JobChunk;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import gr.kipouralkis.backend.repository.JobChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for indexing Job entities for semantic search.
 * <p>
 * This service performs three main tasks:
 * <ul>
 *     <li>Splits a job description into smaller text chunks</li>
 *     <li>Generates vector embeddings for each chunk via the EmbeddingApiService</li>
 *     <li>Persists the resulting JobChunk entities for later similarity search</li>
 * </ul>
 * <p>
 * This class is internal to the search pipeline and is not exposed through any controller.
 * It is invoked automatically when a new Job is created.
 */
@Service
public class JobIndexService {

    private final JobChunkRepository jobChunkRepository;
    private final EmbeddingApiService embeddingApiService;
    private final JobChunkJdbcRepository jobChunkJdbcRepository;

    public JobIndexService(JobChunkRepository jobChunkRepository,
                           EmbeddingApiService embeddingApiService,
                           JobChunkJdbcRepository jobChunkJdbcRepository) {
        this.jobChunkRepository = jobChunkRepository;
        this.embeddingApiService = embeddingApiService;
        this.jobChunkJdbcRepository = jobChunkJdbcRepository;
    }

    public void indexJob(Job job){

        String fullText = job.getTitle() + "\n" +
                job.getSeniority() + "\n" +
                job.getLocation() + "\n" +
                job.getCompany() + "\n\n" +
                job.getDescription();

        List<String> jobChunks = splitIntoChunks(fullText);


        int index = 0;

        for (String jobChunk : jobChunks) {

            float[] embedding = embeddingApiService.embed(jobChunk);
            UUID uuid = UUID.randomUUID();

            JobChunk jc = new JobChunk();
            jc.setJob(job);
            jc.setChunkIndex(index++);
            jc.setChunkContent(jobChunk);
            jc.setEmbedding(embedding);

            // Use JDBC for vector insert
            jobChunkJdbcRepository.insertJobChunk(
                    uuid,
                    job.getId(),
                    jc.getChunkIndex(),
                    jc.getChunkContent(),
                    jc.getEmbedding()
            );
        }
    }

    private List<String> splitIntoChunks(String text){
        int size = 500;
        List<String> jobChunks = new ArrayList<>();
        for(int i = 0; i < text.length(); i+=size){
            int end = Math.min(text.length(), i+size);
            jobChunks.add(text.substring(i, end));
        }
        return jobChunks;
    }
}
