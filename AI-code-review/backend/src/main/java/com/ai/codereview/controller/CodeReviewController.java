package com.ai.codereview.controller;

import com.ai.codereview.dto.ApiResponse;
import com.ai.codereview.model.CodeReview;
import com.ai.codereview.model.CodeSnippetRequest;
import com.ai.codereview.service.CodeReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@Tag(name = "Code Review Controller", description = "API for AI-based code review")
public class CodeReviewController {

    private static final Logger logger = LoggerFactory.getLogger(CodeReviewController.class);
    private final CodeReviewService service;

    public CodeReviewController(CodeReviewService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all code reviews", description = "Returns a paginated list of code reviews")
    public ApiResponse<Page<CodeReview>> getAllReviews(Pageable pageable) {
        logger.info("Received request for all code reviews");
        Page<CodeReview> result = service.getAllReviews(pageable);
        return ApiResponse.success(result, "Code reviews retrieved successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get code review by ID", description = "Returns a single code review")
    public ApiResponse<CodeReview> getReviewById(@PathVariable Long id) {
        logger.info("Received request for code review with id: {}", id);
        CodeReview result = service.getReviewById(id);
        return ApiResponse.success(result, "Code review retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Submit code for review", description = "Sends code snippet to AI and returns analysis")
    public ApiResponse<CodeReview> reviewCode(@Valid @RequestBody CodeSnippetRequest request) {
        logger.info("Received code review request for language: {}", request.getLanguage());
        CodeReview result = service.reviewCode(request.getCode());
        return ApiResponse.success(result, "Code review completed successfully");
    }
}