package com.leitner.domain.port.in;

import com.leitner.domain.model.CardId;

/**
 * Port d'entrée pour répondre à une carte.
 * Gère la logique de progression/régression dans les catégories de Leitner.
 */
public interface AnswerCardUseCase {

    /**
     * Enregistre une réponse pour une carte.
     * - Si isValid=true : la carte passe à la catégorie suivante
     * - Si isValid=false : la carte revient en catégorie FIRST
     *
     * @param command les données de la réponse
     * @throws CardNotFoundException si la carte n'existe pas
     */
    void execute(AnswerCardCommand command);

    /**
     * Commande pour répondre à une carte.
     */
    record AnswerCardCommand(
        String cardId,
        boolean isValid
    ) {
        public AnswerCardCommand {
            if (cardId == null || cardId.isBlank()) {
                throw new IllegalArgumentException("Card ID is required");
            }
        }

        public CardId toCardId() {
            return CardId.of(cardId);
        }
    }
}
