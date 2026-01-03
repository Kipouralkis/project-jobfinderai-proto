package gr.kipouralkis.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Value("${embedding.api.url}")
    private String apiUrl;

    @Value("${embedding.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public EmbeddingApiService() {
        this.restTemplate = new RestTemplate();

        // Remove CBOR converter (not needed)
        restTemplate.getMessageConverters()
                .removeIf(c -> c instanceof MappingJackson2CborHttpMessageConverter);

        // Ensure JSON converter is present
        restTemplate.getMessageConverters()
                .add(new MappingJackson2HttpMessageConverter());
    }

    public float[] embed(String text){

        Map<String, Object> requestBody = Map.of(
                "inputs", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Expects a JSON array [0.1, 0.2, 0.3, ...]
        ResponseEntity<List> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                List.class
        );

        List<Double> embeddingList = response.getBody();
        if(embeddingList == null || embeddingList.isEmpty()) {
            throw new IllegalStateException("Embedding API returned empty list");
        }


        float[] vector = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            vector[i] = embeddingList.get(i).floatValue();
        }

        return vector;
    }
}
