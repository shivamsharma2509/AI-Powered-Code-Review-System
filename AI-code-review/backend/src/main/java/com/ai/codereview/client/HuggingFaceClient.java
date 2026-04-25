package com.ai.codereview.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HuggingFaceClient {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey;
    private final String apiUrl;

    public HuggingFaceClient(
            @Value("${huggingface.api.key}") String apiKey,
            @Value("${huggingface.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    @CircuitBreaker(name = "huggingFaceClient", fallbackMethod = "fallbackReviewCode")
    @Retry(name = "huggingFaceClient")
    public String reviewCode(String codeSnippet) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("inputs", "Review this Java code and suggest improvements:\n" + codeSnippet);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            logger.info("Sending request to HuggingFace API: {}", apiUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            logger.info("Received response from HuggingFace API with status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error during HuggingFace API call: {}", e.getMessage());
            throw e; // Rethrow to trigger circuit breaker/retry
        }
    }

    public String fallbackReviewCode(String codeSnippet, Throwable t) {
        logger.warn("HuggingFace API fallback triggered due to: {}", t.getMessage());
        return "The AI code review service is currently unavailable. Please try again later. (Reason: " + t.getMessage() + ")";
    }
}