package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.dto.ChatRequest;
import gr.kipouralkis.backend.dto.ChatResponse;
import gr.kipouralkis.backend.repository.JobChunkJdbcRepository;
import gr.kipouralkis.backend.service.ChatService;
import gr.kipouralkis.backend.service.EmbeddingApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        return chatService.respond(chatRequest.message());
    }

}
