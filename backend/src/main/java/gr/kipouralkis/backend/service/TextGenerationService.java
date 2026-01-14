package gr.kipouralkis.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.kipouralkis.backend.agent.LlmResponse;
import gr.kipouralkis.backend.agent.ToolCall;
import gr.kipouralkis.backend.agent.ToolResult;
import gr.kipouralkis.backend.dto.ChatRequest;
import gr.kipouralkis.backend.dto.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for communicating with the text‑generation model (Gemini).
 * <p>
 * This class abstracts away all HTTP communication, request construction,
 * and response parsing required to generate text from a prompt.
 * </p>
 *
 */

@Service
public class TextGenerationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public TextGenerationService(
            RestTemplateBuilder builder,
            ObjectMapper objectMapper,
            @Value("${chatmodel.api.key}") String apiKey,
            @Value("${chatmodel.api.url}") String apiUrl,
            @Value("${chatmodel.api.model}") String model
    ) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * UNIFIED PIPELINE: Handles the full conversation history.
     * This replaces sendChatMessage and sendToolResult.
     */
    public LlmResponse handleConversation(List<Message> history) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", history);
        requestBody.put("tools", toolSchemas());
        requestBody.put("tool_choice", "auto");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, Map.class);

            return parseLlmResponse(response.getBody());
        } catch (Exception e) {
            System.err.println("LLM API Error: " + e.getMessage());
            return new LlmResponse("Error communicating with AI.", null, null);
        }
    }

    /**
     * LEGACY SUPPORT: Simple prompt-to-text.
     * Keeps older parts of the app working while using the new logic.
     */
    public String generateText(String prompt) {
        List<Message> simpleHistory = List.of(new Message("user", prompt, null, null));
        LlmResponse response = handleConversation(simpleHistory);
        return response.content() != null ? response.content() : "No response generated.";
    }

    private LlmResponse parseLlmResponse(Map<String, Object> body) {
        var choices = (List<Map<String, Object>>) body.get("choices");
        if (choices == null || choices.isEmpty()) return new LlmResponse("No response", null, null);

        var messageMap = (Map<String, Object>) choices.get(0).get("message");

        // 1. Capture the raw message exactly as returned for conversation history
        Message rawMessage = new Message(
                (String) messageMap.get("role"),
                (String) messageMap.get("content"),
                (List<Map<String, Object>>) messageMap.get("tool_calls"),
                null
        );

        // 2. Handle Tool Calls
        if (messageMap.get("tool_calls") != null) {
            List<Map<String, Object>> toolCallsRaw = (List<Map<String, Object>>) messageMap.get("tool_calls");
            // Sequential logic: take the first one
            Map<String, Object> firstCall = toolCallsRaw.get(0);

            String callId = (String) firstCall.get("id");
            Map<String, Object> function = (Map<String, Object>) firstCall.get("function");
            String name = (String) function.get("name");
            Map<String, Object> args = parseJson((String) function.get("arguments"));

            return new LlmResponse(null, List.of(new ToolCall(callId, name, args)), rawMessage);
        }

        return new LlmResponse((String) messageMap.get("content"), null, rawMessage);
    }

    private List<Map<String, Object>> toolSchemas() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // TOOL 1: Semantic Search
        tools.add(tool(
                "semantic_search",
                "Search for real-time job openings based on a query. Use this whenever the user asks for jobs.",
                Map.of("query", Map.of(
                        "type", "string",
                        "description", "The job title and location, e.g., 'Python developer in Athens'"
                ))
        ));

        // TOOL 2: Job Apply
        tools.add(tool(
                "job_apply",
                "Submit a job application for a specific job ID.",
                Map.of(
                        "job_id", Map.of("type", "integer"),
                        "motivation_text", Map.of("type", "string", "description", "Why the user is a good fit")
                )
        ));

        return tools;
    }
    private Map<String, Object> tool(String name,String description, Map<String, Object> properties) {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", name,
                        "description", description, // ADD THIS
                        "parameters", Map.of(
                                "type", "object",
                                "properties", properties,
                                "required", new ArrayList<>(properties.keySet())
                        )
                )
        );
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }

    public String agentSystemPrompt() {
        return "You are a Job Finder Assistant. Use tools to find jobs. Summarize results clearly.";
    }
}