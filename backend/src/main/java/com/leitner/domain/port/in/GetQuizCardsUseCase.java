package com.leitner.domain.port.in;

import com.leitner.domain.model.Card;

import java.time.LocalDate;
import java.util.List;

/**
 * Port d'entrée pour récupérer les cartes d'un quiz.
 * Retourne les cartes à réviser pour une date donnée selon le système de Leitner.
 */
public interface GetQuizCardsUseCase {

    /**
     * Récupère toutes les cartes à réviser pour une date donnée.
     * Applique les règles de fréquence du système de Leitner.
     *
     * @param query la date du quiz
     * @return la liste des cartes à réviser
     */
    List<Card> execute(GetQuizCardsQuery query);

    /**
     * Query pour récupérer les cartes du quiz.
     */
    record GetQuizCardsQuery(
        LocalDate date
    ) {
        public GetQuizCardsQuery {
            if (date == null) {
                date = LocalDate.now();
            }
        }

        public static GetQuizCardsQuery forToday() {
            return new GetQuizCardsQuery(LocalDate.now());
        }

        public static GetQuizCardsQuery forDate(LocalDate date) {
            return new GetQuizCardsQuery(date);
        }
    }
}
