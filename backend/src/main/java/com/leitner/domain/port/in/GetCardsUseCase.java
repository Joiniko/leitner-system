package com.leitner.domain.port.in;

import com.leitner.domain.model.Card;

import java.util.List;
import java.util.Optional;

/**
 * Port d'entrée pour la récupération des cartes.
 * Permet de filtrer par tags.
 */
public interface GetCardsUseCase {

    /**
     * Récupère toutes les cartes, optionnellement filtrées par tags.
     *
     * @param query les critères de recherche
     * @return la liste des cartes correspondantes
     */
    List<Card> execute(GetCardsQuery query);

    /**
     * Query pour récupérer les cartes.
     * Pattern CQRS : séparation des commandes et des requêtes.
     */
    record GetCardsQuery(
        List<String> tags
    ) {
        public GetCardsQuery {
            tags = tags == null ? List.of() : tags;
        }

        public static GetCardsQuery all() {
            return new GetCardsQuery(List.of());
        }

        public static GetCardsQuery withTags(List<String> tags) {
            return new GetCardsQuery(tags);
        }

        public boolean hasTagFilter() {
            return !tags.isEmpty();
        }
    }
}
