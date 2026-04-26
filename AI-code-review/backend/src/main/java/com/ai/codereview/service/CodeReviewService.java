package com.ai.codereview.service;

import com.ai.codereview.client.HuggingFaceClient;
import com.ai.codereview.model.CodeReview;
import com.ai.codereview.repository.CodeReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CodeReviewService {

    private static final Logger logger = LoggerFactory.getLogger(CodeReviewService.class);

    private final CodeReviewRepository repository;
    private final HuggingFaceClient hfClient;

    public CodeReviewService(CodeReviewRepository repository, HuggingFaceClient hfClient) {
        this.repository = repository;
        this.hfClient = hfClient;
    }

    public Page<CodeReview> getAllReviews(Pageable pageable) {
        logger.info("Fetching all code reviews with pagination");
        return repository.findAll(pageable);
    }

    public CodeReview getReviewById(Long id) {
        logger.info("Fetching code review with id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Code review not found with id: " + id));
    }

    public CodeReview reviewCode(String codeSnippet) {
        logger.info("Processing code review");

        CodeReview review = new CodeReview();
        review.setCodeSnippet(codeSnippet);
        review.setCreatedAt(LocalDateTime.now());

        String result = callAIWithFallback(codeSnippet);

        review.setReviewResult(result);

        CodeReview savedReview = repository.save(review);
        logger.info("Code review saved with id: {}", savedReview.getId());

        return savedReview;
    }

    /**
     * Calls HuggingFace API with safe fallback handling
     */
    private String callAIWithFallback(String codeSnippet) {
        try {
            logger.info("Calling HuggingFace API");

            String response = hfClient.reviewCode(codeSnippet);

            if (response == null || response.isBlank()) {
                logger.warn("Empty response from HuggingFace, switching to mock");
                return getMockReview(codeSnippet);
            }

            return response;

        } catch (Exception e) {
            logger.warn("HuggingFace API failed, using mock. Reason: {}", e.getMessage());
            return getMockReview(codeSnippet);
        }
    }

    /**
     * Mock AI response for development / fallback
     */
    private String getMockReview(String code) {
        return """
                Mock AI Code Review:
                - Code structure looks good
                - Consider adding proper error handling
                - Improve variable naming for clarity
                - Add comments for better readability
                - Follow consistent coding standards
                """;
    }
}