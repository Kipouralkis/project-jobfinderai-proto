package gr.kipouralkis.backend.dto;

import java.util.List;

/**
 * Represents an incoming chat request from the client.
 * <p>
 * Contains only the raw user message that will be processed by the chatbot.
 * </p>
 */
public record ChatRequest(
        String message,
        List<Message> history) {
}
