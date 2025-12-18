package com.leitner.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour la requête de création d'une carte.
 * Correspond au schema CardUserData du Swagger.
 */
public record CreateCardRequest(
    @NotBlank(message = "Question is required")
    @JsonProperty("question")
    String question,
    
    @NotBlank(message = "Answer is required")
    @JsonProperty("answer")
    String answer,
    
    @JsonProperty("tag")
    String tag
) {
}
