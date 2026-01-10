package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.dto.ChatResponse;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Core service responsible for processing chat messages using a retrieval‑augmented generation (RAG) pipeline.
 * <p>
 * This service performs the following steps:
 * </p>
 * <ul>
 *     <li>Embeds the user's message using {@link EmbeddingApiService}</li>
 *     <li>Retrieves semantically relevant job chunks from the database</li>
 *     <li>Constructs a grounded prompt using the retrieved context</li>
 *     <li>Generates a response using {@link TextGenerationService}</li>
 * </ul>
 */
@Service
public class ChatService {

    private final EmbeddingApiService embeddingApiService;
    private final JobChunkJdbcRepository jobChunkJdbcRepository;
    private final TextGenerationService textGenerationService;

    public ChatService(
            EmbeddingApiService embeddingApiService,
            JobChunkJdbcRepository jobChunkJdbcRepository,
            TextGenerationService textGenerationService
    ) {
        this.embeddingApiService = embeddingApiService;
        this.jobChunkJdbcRepository = jobChunkJdbcRepository;
        this.textGenerationService = textGenerationService;
    }

    public ChatResponse respond(String message){

        // EMbed user message
        float[] embedding = embeddingApiService.embed(message);

        // Retrieve relevant chunks
        var chunks = jobChunkJdbcRepository.searchChunks(embedding, 5);

        // build context
        StringBuilder context = new StringBuilder();
        for(var chunk : chunks){
            context.append(chunk.chunkContent()).append("\n\n");
        }

        // build prompt
        String prompt = """
                You are a helpful assistant. Use ONLY the following context to answer the user's question.
                If the answer is not in the context, say you don't know.
                
                CONTEXT:
                %s
                
                USER QUESTION:
                %s
                """.formatted(context, message);

        // generate answer
        String answer = textGenerationService.generateText(prompt);

        return new ChatResponse(answer);
    }

}
