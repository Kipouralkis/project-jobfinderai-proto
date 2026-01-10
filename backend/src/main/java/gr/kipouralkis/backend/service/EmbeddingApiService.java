package gr.kipouralkis.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Low-level service for generating vector embeddings from text.
 * <p>
 * This service abstracts away the details of calling the external embedding API.
 * It returns a numeric vector representation of the input text, which is used
 * by the indexing and search pipelines.
 */
@Service
public class EmbeddingApiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public EmbeddingApiService(
            RestTemplateBuilder builder,
            @Value("${embedding.api.key}") String apiKey,
            @Value("${embedding.api.url}") String apiUrl
    ) {
        this.restTemplate = builder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public float[] embed(String text) {

        // Build request body
        Map<String, Object> request = new HashMap<>();
        request.put("model", "models/gemini-embedding-001");

        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", text);
        content.put("parts", List.of(part));

        request.put("content", content);
        request.put("outputDimensionality", 768);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // Call Gemini
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Gemini returned null response");
        }

        // Error handling
        if (body.containsKey("error")) {
            throw new RuntimeException("Gemini error: " + body.get("error"));
        }

        // Extract embedding
        Map<String, Object> embedding = (Map<String, Object>) body.get("embedding");
        if (embedding == null) {
            throw new RuntimeException("Gemini returned no embedding: " + body);
        }

        List<Double> values = (List<Double>) embedding.get("values");
        if (values == null) {
            throw new RuntimeException("Gemini embedding missing 'value': " + body);
        }

        // Convert to float[]
        float[] vector = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            vector[i] = values.get(i).floatValue();
        }

        return vector;
    }
}
