package gr.kipouralkis.backend.dto;

/**
 * Represents the chatbot's generated response.
 * <p>
 * Contains the final answer returned to the client after processing
 * the user's message through the RAG pipeline.
 * </p>
 */
public record ChatResponse(String answer){

}
