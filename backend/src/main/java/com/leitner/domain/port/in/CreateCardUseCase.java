package com.leitner.domain.port.in;

import com.leitner.domain.model.Card;

/**
 * Port d'entrée pour la création d'une carte.
 * Interface Segregation Principle (ISP) : chaque use case a sa propre interface.
 */
public interface CreateCardUseCase {

    /**
     * Crée une nouvelle carte dans le système.
     * La carte sera automatiquement placée en catégorie FIRST.
     *
     * @param command les données de la carte à créer
     * @return la carte créée avec son identifiant généré
     */
    Card execute(CreateCardCommand command);

    /**
     * Commande pour créer une carte.
     * Pattern CQRS : séparation des commandes et des requêtes.
     */
    record CreateCardCommand(
        String question,
        String answer,
        String tag
    ) {
        public CreateCardCommand {
            if (question == null || question.isBlank()) {
                throw new IllegalArgumentException("Question is required");
            }
            if (answer == null || answer.isBlank()) {
                throw new IllegalArgumentException("Answer is required");
            }
        }
    }
}
