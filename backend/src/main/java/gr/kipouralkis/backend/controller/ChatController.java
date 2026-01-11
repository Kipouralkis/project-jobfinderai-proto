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

    private final ChatService chatService;
    private final JobSearchService jobSearchService;
    private final TextGenerationService textGenerationService;
    private final IntentService intentService;

    public ChatController(
            ChatService chatService,
            JobSearchService jobSearchService,
            TextGenerationService textGenerationService,
            IntentService intentService
    ) {
        this.chatService = chatService;
        this.jobSearchService = jobSearchService;
        this.textGenerationService = textGenerationService;
        this.intentService = intentService;
    }

//    @PostMapping
//    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
//        String intent = intentService.classify(chatRequest.message());
//        System.out.println("INTENT = " + intent);
//        return chatService.respond(intent);
//    }

    @PostMapping
    public Object chat(@RequestBody ChatRequest req){
        Intent intent =  intentService.classify(req.message());
        System.out.println("INTENT = " + intent);

        switch (intent){

            case SEARCH_JOBS -> {
                return jobSearchService.searchJobs(req.message());
            }

            case GENERAL_CHAT -> {
                String answer = textGenerationService.generateText(req.message());
                return Map.of("answer", answer);
            }

            default -> {
                return Map.of("answer", "feature not implemented");
            }
        }
    }

}
