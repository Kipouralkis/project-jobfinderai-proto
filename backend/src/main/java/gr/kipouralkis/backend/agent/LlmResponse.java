package gr.kipouralkis.backend.agent;

import gr.kipouralkis.backend.dto.Message;

import java.util.List;

public record LlmResponse(
        String content,
        List<ToolCall> toolCalls, // Changed from single to List
        Message rawMessage        // Added to keep track of the Assistant's turn
) {
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}