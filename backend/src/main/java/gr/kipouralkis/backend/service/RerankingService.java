package gr.kipouralkis.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RerankingService {

    @Value("${reranker.api.key}")
    private String apiKey;

    @Value("${reranker.api.url}")
    private String apiUrl;

    @Value("${reranker.api.model}")
    private String apiModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> rerank(String query, List<String> documents) {

        Map<String, Object> body = new HashMap<>();
        body.put("model", apiModel);
        body.put("query", query);
        body.put("documents", documents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(apiUrl, HttpMethod.POST, request,
                        new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("data")) {
            throw new IllegalStateException("VoyageAI returned no data: " + response);
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

        List<Double> scores = new ArrayList<>();
        for (Map<String, Object> item : data) {
            scores.add(((Number) item.get("relevance_score")).doubleValue());
        }

        return scores;
    }
}
