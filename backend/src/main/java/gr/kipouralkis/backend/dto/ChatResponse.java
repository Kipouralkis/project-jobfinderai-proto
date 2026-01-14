package gr.kipouralkis.backend.dto;

import gr.kipouralkis.backend.agent.LlmResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chatbot's generated response.
 * <p>
 * Contains the final answer returned to the client after processing
 * the user's message through the RAG pipeline.
 * </p>
 */

public record ChatResponse(
        String answer,
        List<String> toolLogs,
        Object data
) {
    // Keep the simple constructor for non-tool responses
    public ChatResponse(String answer) {
        this(answer, List.of(), null);
    }

    // REFACTORED: Now accepts 'rawData' to pass to the UI
    public static ChatResponse from(LlmResponse llmResponse, Object rawData) {
        String content = llmResponse.content();
        List<String> logs = new ArrayList<>();

        if (llmResponse.hasToolCalls()) {
            llmResponse.toolCalls().forEach(call ->
                    logs.add("Agent decided to: " + call.name())
            );
            // Fallback text while the agent is "thinking" or calling tools
            if (content == null) content = "Processing your request...";
        }

        return new ChatResponse(content, logs, rawData);
    }
}