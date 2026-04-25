package com.ai.codereview.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CodeSnippetRequest {
    @NotBlank(message = "Code snippet cannot be empty")
    @Size(max = 10000, message = "Code snippet is too long (max 10000 characters)")
    private String code;
    
    @NotBlank(message = "Language cannot be empty")
    @Size(max = 50, message = "Language name is too long")
    private String language;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}