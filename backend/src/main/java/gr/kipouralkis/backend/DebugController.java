package gr.kipouralkis.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${embedding.api.key}")
    private String apiKey;

    @Value("${embedding.api.url}")
    private String url;

    @Value("${embedding.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();


    @GetMapping("/embedding-size")
    public String testEmbedding() {
        // Build request body
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("input", "hello world");

        // Build headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Send request
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // Extract embedding
        List data = (List) response.getBody().get("data");
        Map first = (Map) data.get(0);
        List<Double> embedding = (List<Double>) first.get("embedding");

        return "Embedding size: " + embedding.size();
    }

}
