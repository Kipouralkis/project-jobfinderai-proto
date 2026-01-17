package gr.kipouralkis.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.kipouralkis.backend.agent.LlmResponse;
import gr.kipouralkis.backend.agent.ToolCall;
import gr.kipouralkis.backend.agent.ToolResult;
import gr.kipouralkis.backend.dto.ChatRequest;
import gr.kipouralkis.backend.dto.ChatResponse;
import gr.kipouralkis.backend.dto.Message;
import gr.kipouralkis.backend.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Orchestrates the conversational flow between the User, the Large Language Model (LLM),
 * and the specialized Tool Services.
 */
@Service
public class AgentChatService {

    private final TextGenerationService llm;
    private final ToolService toolService;
    private final JobSearchService jobSearchService; // Add this
    private final ObjectMapper objectMapper; // To convert List to String for LLM

    public AgentChatService(TextGenerationService llm, ToolService toolService,
                            JobSearchService jobSearchService, ObjectMapper objectMapper) {
        this.llm = llm;
        this.toolService = toolService;
        this.jobSearchService = jobSearchService;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes a user chat request by maintaining conversation history and
     * executing tool calls as requested by the AI.
     * @param req The incoming request containing the user message and previous history
     * @return A ChatResponse containing the AI's answer, execution logs and other raw data.
     */
    public ChatResponse chat(ChatRequest req) {
        // State management
        List<Message> history = (req.history() != null)
                ? new ArrayList<>(req.history())
                : new ArrayList<>();

        Object lastToolRawData = null;

        // Inject system identity of new conversation
        if (history.isEmpty()) {
            history.add(new Message("system", llm.agentSystemPrompt(), null, null));
        }

        history.add(new Message("user", req.message(), null, null));

        // REASONING LOOP
        int maxTurns = 10;
        for (int i = 0; i < maxTurns; i++) {
            // Ask the LLM for the next step (response or tool call)
            LlmResponse response = llm.handleConversation(history);
            history.add(response.rawMessage());

            if (response.hasToolCalls()) {
                ToolCall call = response.toolCalls().get(0);
                String resultString;

                // INTERCEPT: special handling for searches to capture data
                if ("semantic_search".equals(call.name())) {
                    String query = (String) call.arguments().get("query");
                    List<Job> jobs = jobSearchService.searchJobs(query);

                    lastToolRawData = jobs; // STORE the objects for the UI cards
                    resultString = toJson(jobs); // Convert to String for the AI
                } else {
                    // For other tools (like job_apply), use the standard toolService
                    resultString = toolService.execute(call);
                }

                // feed the result of the tool back to the history so that AI can observe it.
                history.add(Message.tool(call.id(), resultString));
            } else {
                // Final synthesis: Return the final answer and collected metadata
                List<String> logs = history.stream()
                        .filter(m -> "tool".equals(m.role()))
                        .map(m -> "Executed: " + m.toolCallId())
                        .toList();

                return new ChatResponse(response.content(), logs, lastToolRawData);
            }
        }
        return new ChatResponse("I'm sorry, I couldn't finish the task.", List.of(), null);
    }

    // Helper to serialize objects to JSON for AI consumption
    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { return "[]"; }
    }
}