package com.leitner.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la requête de réponse à une carte.
 * Correspond au schema de la requête PATCH /cards/{cardId}/answer du Swagger.
 */
public record AnswerRequest(
    @NotNull(message = "isValid is required")
    @JsonProperty("isValid")
    Boolean isValid
) {
}
