package com.ai.codereview.service;

import com.ai.codereview.client.HuggingFaceClient;
import com.ai.codereview.model.CodeReview;
import com.ai.codereview.repository.CodeReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CodeReviewServiceTest {

    @Mock
    private CodeReviewRepository repository;

    @Mock
    private HuggingFaceClient hfClient;

    @InjectMocks
    private CodeReviewService codeReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReviewCode() {
        String code = "public class Test {}";
        String reviewResult = "Good code";
        
        when(hfClient.reviewCode(code)).thenReturn(reviewResult);
        when(repository.save(any(CodeReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CodeReview result = codeReviewService.reviewCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCodeSnippet());
        assertEquals(reviewResult, result.getReviewResult());
        verify(hfClient, times(1)).reviewCode(code);
        verify(repository, times(1)).save(any(CodeReview.class));
    }

    @Test
    void testGetAllReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CodeReview> page = new PageImpl<>(Collections.emptyList());
        
        when(repository.findAll(pageable)).thenReturn(page);

        Page<CodeReview> result = codeReviewService.getAllReviews(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void testGetReviewById() {
        Long id = 1L;
        CodeReview review = new CodeReview();
        review.setId(id);
        
        when(repository.findById(id)).thenReturn(Optional.of(review));

        CodeReview result = codeReviewService.getReviewById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testGetReviewByIdNotFound() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> codeReviewService.getReviewById(id));
    }
}
