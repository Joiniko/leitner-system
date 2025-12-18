package com.leitner.domain.model;

/**
 * Énumération représentant les catégories du système de Leitner.
 * Chaque catégorie a une fréquence de révision associée (en jours).
 * 
 * Système de Leitner :
 * - FIRST: révision tous les jours (fréquence 1)
 * - SECOND: révision tous les 2 jours
 * - THIRD: révision tous les 4 jours
 * - FOURTH: révision tous les 8 jours
 * - FIFTH: révision tous les 16 jours
 * - SIXTH: révision tous les 32 jours
 * - SEVENTH: révision tous les 64 jours
 * - DONE: carte apprise définitivement, sortie du système
 */
public enum Category {

    FIRST(1),
    SECOND(2),
    THIRD(4),
    FOURTH(8),
    FIFTH(16),
    SIXTH(32),
    SEVENTH(64),
    DONE(Integer.MAX_VALUE);

    private final int frequencyInDays;

    Category(int frequencyInDays) {
        this.frequencyInDays = frequencyInDays;
    }

    /**
     * Retourne la fréquence de révision en jours pour cette catégorie.
     */
    public int getFrequencyInDays() {
        return frequencyInDays;
    }

    /**
     * Retourne la catégorie suivante après une bonne réponse.
     * Si la catégorie est SEVENTH, passe à DONE.
     * Si déjà DONE, reste DONE.
     */
    public Category next() {
        return switch (this) {
            case FIRST -> SECOND;
            case SECOND -> THIRD;
            case THIRD -> FOURTH;
            case FOURTH -> FIFTH;
            case FIFTH -> SIXTH;
            case SIXTH -> SEVENTH;
            case SEVENTH, DONE -> DONE;
        };
    }

    /**
     * Retourne la première catégorie (pour les mauvaises réponses).
     */
    public static Category initial() {
        return FIRST;
    }

    /**
     * Vérifie si cette catégorie est la catégorie finale (DONE).
     */
    public boolean isDone() {
        return this == DONE;
    }

    /**
     * Vérifie si une carte de cette catégorie doit être révisée à un jour donné.
     * Basé sur le système calendaire : jour % fréquence == 0
     * 
     * @param dayNumber le numéro du jour (1 = premier jour)
     * @return true si la carte doit être révisée ce jour
     */
    public boolean shouldBeReviewedOnDay(int dayNumber) {
        if (this == DONE) {
            return false;
        }
        return dayNumber % frequencyInDays == 0;
    }
}
