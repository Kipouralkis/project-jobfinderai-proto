package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.model.JobChunk;
import gr.kipouralkis.backend.repository.JobChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public JobIndexService(JobChunkRepository jobChunkRepository,
                           EmbeddingApiService embeddingApiService) {
        this.jobChunkRepository = jobChunkRepository;
        this.embeddingApiService = embeddingApiService;
    }

    public void indexJob(Job job){

        List<String> jobChunks = splitIntoChunks(job.getDescription());

        for(String jobChunk : jobChunks){

            float[] embedding = embeddingApiService.embed(jobChunk);

            JobChunk jc = new JobChunk();
            jc.setJob(job);
            jc.setChunkContent(jobChunk);
            jc.setEmbedding(embedding);

            jobChunkRepository.save(jc);
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
