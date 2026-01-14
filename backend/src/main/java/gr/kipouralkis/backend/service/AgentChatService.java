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

    public ChatResponse chat(ChatRequest req) {
        // Ideally, history should be passed in req to maintain state across clicks
        List<Message> history = (req.history() != null)
                ? new ArrayList<>(req.history())
                : new ArrayList<>();

        Object lastToolRawData = null;

        // if it's the very first message, add the system prompt
        if (history.isEmpty()) {
            history.add(new Message("system", llm.agentSystemPrompt(), null, null));
        }

        history.add(new Message("user", req.message(), null, null));

        int maxTurns = 5;
        for (int i = 0; i < maxTurns; i++) {
            LlmResponse response = llm.handleConversation(history);
            history.add(response.rawMessage());

            if (response.hasToolCalls()) {
                ToolCall call = response.toolCalls().get(0);
                String resultString;

                // INTERCEPT: If it's a search, handle it here to keep the "List<Job>"
                if ("semantic_search".equals(call.name())) {
                    String query = (String) call.arguments().get("query");
                    List<Job> jobs = jobSearchService.searchJobs(query);

                    lastToolRawData = jobs; // STORE the objects for the UI cards
                    resultString = toJson(jobs); // Convert to String for the AI
                } else {
                    // For other tools (like job_apply), use the standard toolService
                    resultString = toolService.execute(call);
                }

                history.add(Message.tool(call.id(), resultString));
            } else {
                // Final turn: Return the summary AND the stored data for cards
                List<String> logs = history.stream()
                        .filter(m -> "tool".equals(m.role()))
                        .map(m -> "Executed: " + m.toolCallId())
                        .toList();

                return new ChatResponse(response.content(), logs, lastToolRawData);
            }
        }
        return new ChatResponse("I'm sorry, I couldn't finish the task.", List.of(), null);
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { return "[]"; }
    }
}