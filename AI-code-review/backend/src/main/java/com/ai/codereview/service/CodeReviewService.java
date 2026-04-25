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
        logger.info("Processing code review in service");
        CodeReview review = new CodeReview();
        review.setCodeSnippet(codeSnippet);
        review.setCreatedAt(LocalDateTime.now());

        // Call Hugging Face client
        logger.info("Calling HuggingFace API");
        String result = hfClient.reviewCode(codeSnippet);
        review.setReviewResult(result);

        CodeReview savedReview = repository.save(review);
        logger.info("Code review saved with id: {}", savedReview.getId());
        return savedReview;
    }
}