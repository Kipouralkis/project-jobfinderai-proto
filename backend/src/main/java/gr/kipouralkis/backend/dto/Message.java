package gr.kipouralkis.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Represents a single exchange in the conversation.
 */
public record Message(
        String role, // user, assistant, tool
        String content, // text content of the message
        @JsonProperty("tool_calls") List<Map<String, Object>> toolCalls,
        @JsonProperty("tool_call_id") String toolCallId // Links a tool message to the assistant request
) {
    // Helper for Assistant's tool request
    public static Message assistant(String content, List<Map<String, Object>> toolCalls) {
        return new Message("assistant", content, toolCalls, null);
    }

    // Helper for your Tool's response
    public static Message tool(String callId, String result) {
        return new Message("tool", result, null, callId);
    }
}