package com.leitner.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entité représentant une carte d'apprentissage dans le système de Leitner.
 * C'est l'agrégat principal du domaine.
 * 
 * Une carte contient :
 * - Une question (recto)
 * - Une réponse (verso)
 * - Un tag optionnel pour le regroupement
 * - Une catégorie indiquant le niveau de maîtrise
 * - La date de dernière réponse pour calculer les révisions
 */
public class Card {

    private final CardId id;
    private final String question;
    private final String answer;
    private final String tag;
    private Category category;
    private LocalDate lastAnswerDate;

    private Card(CardId id, String question, String answer, String tag, Category category, LocalDate lastAnswerDate) {
        validateQuestion(question);
        validateAnswer(answer);
        
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.tag = tag;
        this.category = category;
        this.lastAnswerDate = lastAnswerDate;
    }

    /**
     * Crée une nouvelle carte avec les données fournies.
     * La carte est automatiquement placée en catégorie FIRST.
     */
    public static Card create(String question, String answer, String tag) {
        return new Card(
            CardId.generate(),
            question,
            answer,
            tag,
            Category.initial(),
            null
        );
    }

    /**
     * Reconstruit une carte existante à partir de données persistées.
     */
    public static Card reconstitute(CardId id, String question, String answer, 
                                     String tag, Category category, LocalDate lastAnswerDate) {
        return new Card(id, question, answer, tag, category, lastAnswerDate);
    }

    /**
     * Enregistre une réponse correcte.
     * La carte passe à la catégorie suivante.
     */
    public void answerCorrectly(LocalDate answerDate) {
        this.category = this.category.next();
        this.lastAnswerDate = answerDate;
    }

    /**
     * Enregistre une réponse incorrecte.
     * La carte revient en catégorie FIRST.
     */
    public void answerIncorrectly(LocalDate answerDate) {
        this.category = Category.initial();
        this.lastAnswerDate = answerDate;
    }

    /**
     * Détermine si cette carte doit être révisée à une date donnée.
     * Utilise le système calendaire basé sur les fréquences de Leitner.
     * 
     * @param date la date pour laquelle vérifier
     * @param referenceDate la date de référence (début du système)
     * @return true si la carte doit être révisée
     */
    public boolean shouldBeReviewedOn(LocalDate date, LocalDate referenceDate) {
        if (category.isDone()) {
            return false;
        }
        
        // Nouvelle carte jamais répondue : toujours à réviser
        if (lastAnswerDate == null) {
            return true;
        }
        
        // Calcul du nombre de jours depuis la dernière réponse
        long daysSinceLastAnswer = java.time.temporal.ChronoUnit.DAYS.between(lastAnswerDate, date);
        
        // La carte doit être révisée si le délai depuis la dernière réponse >= fréquence de la catégorie
        return daysSinceLastAnswer >= category.getFrequencyInDays();
    }

    /**
     * Vérifie si la réponse fournie correspond à la réponse attendue.
     * Comparaison insensible à la casse et aux espaces superflus.
     */
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) {
            return false;
        }
        return normalizeAnswer(answer).equals(normalizeAnswer(userAnswer));
    }

    private String normalizeAnswer(String text) {
        return text.trim().toLowerCase();
    }

    private void validateQuestion(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank");
        }
    }

    private void validateAnswer(String answer) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer cannot be null or blank");
        }
    }

    // Getters
    public CardId getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTag() {
        return tag;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getLastAnswerDate() {
        return lastAnswerDate;
    }

    public boolean isDone() {
        return category.isDone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", category=" + category +
                ", tag='" + tag + '\'' +
                '}';
    }
}
