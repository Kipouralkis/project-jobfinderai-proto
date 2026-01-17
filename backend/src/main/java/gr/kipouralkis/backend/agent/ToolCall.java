package gr.kipouralkis.backend.agent;

import java.util.Map;

/**
 * Captured intent from the AI to execute a specific backend function.
 */
public record ToolCall(
        String id,       // The unique 'call_xxx' ID from Gemini
        String name,     // The function name
        Map<String, Object> arguments
) {}