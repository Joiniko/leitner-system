package com.leitner.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO représentant une carte pour les réponses API.
 * Correspond au schema Card du Swagger.
 */
public record CardDTO(
    @JsonProperty("id")
    String id,
    
    @JsonProperty("question")
    String question,
    
    @JsonProperty("answer")
    String answer,
    
    @JsonProperty("tag")
    String tag,
    
    @JsonProperty("category")
    String category
) {
}
