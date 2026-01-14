package gr.kipouralkis.backend.agent;

import java.util.Map;

public record ToolCall(
        String id,       // The unique 'call_xxx' ID from Gemini
        String name,     // The function name
        Map<String, Object> arguments
) {}