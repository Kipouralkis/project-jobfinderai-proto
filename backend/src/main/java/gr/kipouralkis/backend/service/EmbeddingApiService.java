package gr.kipouralkis.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Low-level service for generating vector embeddings from text.
 * <p>
 * This service abstracts away the details of calling the external embedding API.
 * It returns a numeric vector representation of the input text, which is used
 * by the indexing and search pipelines.
 */
@Service
public class EmbeddingApiService {

    @Value("${embedding.api.url}")
    private String apiUrl;

    @Value("${embedding.api.key}")
    private String apiKey;

    @Value("${embedding.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public float[] embed(String text){

        //build request body
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "input", text
        );

        // build headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Send request
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Extract embedding
        Map<String,Object> body = response.getBody();
        if(body == null || !body.containsKey("data")) {
            throw new IllegalStateException("Invalid embedding API response");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");
        Map<String, Object> first = data.get(0);

        List<Double> embeddingList = (List<Double>) first.get("embedding");

        // Convert List<Double to float[]
        float[] vector = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            vector[i] = embeddingList.get(i).floatValue();
        }

        return vector;
    }

}
