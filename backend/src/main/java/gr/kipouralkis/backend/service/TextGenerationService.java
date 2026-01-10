package gr.kipouralkis.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public TextGenerationService(
            RestTemplateBuilder builder,
            @Value("${chatmodel.api.key}") String apiKey,
            @Value("${chatmodel.api.url}") String apiUrl,
            @Value("${chatmodel.api.model}") String model
    ) {
        this.restTemplate = builder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public String generateText(String prompt) {
        // OpenAI-style request body
        var request = Map.of(
                "model", model, // e.g., "gemini-1.5-flash"
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Use standard Bearer token for the OpenAI compatible endpoint
        headers.setBearerAuth(apiKey);

        // Using HttpEntity<?> avoids the "Incompatible Bounds" generic error
        HttpEntity<?> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // OpenAI-style response parsing
            var choices = (List<Map<String, Object>>) response.getBody().get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
