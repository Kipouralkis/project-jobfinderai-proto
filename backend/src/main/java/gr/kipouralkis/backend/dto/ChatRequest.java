package gr.kipouralkis.backend.dto;

/**
 * Represents an incoming chat request from the client.
 * <p>
 * Contains only the raw user message that will be processed by the chatbot.
 * </p>
 */
public record ChatRequest(String message) {
}
