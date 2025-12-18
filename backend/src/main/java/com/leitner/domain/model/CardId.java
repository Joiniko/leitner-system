package com.leitner.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object représentant l'identifiant unique d'une carte.
 * Immutable et auto-généré si non fourni.
 */
public final class CardId {

    private final String value;

    private CardId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CardId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * Crée un nouvel identifiant avec une valeur UUID générée automatiquement.
     */
    public static CardId generate() {
        return new CardId(UUID.randomUUID().toString());
    }

    /**
     * Crée un identifiant à partir d'une valeur existante.
     */
    public static CardId of(String value) {
        return new CardId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardId cardId = (CardId) o;
        return Objects.equals(value, cardId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
