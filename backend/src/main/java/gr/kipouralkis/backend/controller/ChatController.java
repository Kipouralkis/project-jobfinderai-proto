package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.dto.ChatRequest;
import gr.kipouralkis.backend.dto.ChatResponse;
import gr.kipouralkis.backend.model.Intent;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import gr.kipouralkis.backend.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller responsible for handling chat interactions.
 * <p>
 * Exposes a single endpoint that accepts a user message and returns
 * a generated response based on retrieved job-related context.
 * </p>
 *
 * <p>This controller delegates all conversational logic to {@link ChatService}.</p>
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final AgentChatService agentChatService;

    // We only need the Agent Service now.
    // The agent will internally use JobSearch and TextGen as needed.
    public ChatController(AgentChatService agentChatService) {
        this.agentChatService = agentChatService;
    }

    /**
     * The unified agent endpoint.
     * No more switch(intent) logic! Gemini/OpenAI handles the routing
     * automatically via Tool Calling.
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        // This triggers the while-loop in your AgentChatService
        return agentChatService.chat(req);
    }
}