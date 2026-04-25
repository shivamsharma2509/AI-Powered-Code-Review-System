CREATE TABLE code_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code_snippet TEXT NOT NULL,
    review_result TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
