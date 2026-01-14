package gr.kipouralkis.backend.agent;

import java.util.Map;

public record ToolResult(String content) {
    @Override
    public String toString() {
        return content;
    }
}
