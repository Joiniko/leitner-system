package com.leitner.domain.service;

import com.leitner.domain.exception.CardNotFoundException;
import com.leitner.domain.model.Card;
import com.leitner.domain.model.Category;
import com.leitner.domain.port.in.AnswerCardUseCase;
import com.leitner.domain.port.in.CreateCardUseCase;
import com.leitner.domain.port.in.GetCardsUseCase;
import com.leitner.domain.port.in.GetQuizCardsUseCase;
import com.leitner.domain.port.out.CardRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Service du domaine implémentant tous les use cases liés aux cartes.
 * Single Responsibility Principle (SRP) : ce service gère uniquement la logique métier des cartes.
 * 
 * Cette classe est le cœur du domaine et contient toute la logique métier
 * du système de Leitner.
 */
public class CardService implements CreateCardUseCase, GetCardsUseCase, GetQuizCardsUseCase, AnswerCardUseCase {

    private final CardRepository cardRepository;
    private final LocalDate systemStartDate;

    /**
     * Crée un nouveau service de cartes.
     * 
     * @param cardRepository le repository pour la persistence des cartes
     */
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.systemStartDate = LocalDate.now();
    }

    /**
     * Crée un nouveau service de cartes avec une date de référence spécifique.
     * Utile pour les tests.
     */
    public CardService(CardRepository cardRepository, LocalDate systemStartDate) {
        this.cardRepository = cardRepository;
        this.systemStartDate = systemStartDate;
    }

    @Override
    public Card execute(CreateCardCommand command) {
        Card card = Card.create(
            command.question(),
            command.answer(),
            command.tag()
        );
        return cardRepository.save(card);
    }

    @Override
    public List<Card> execute(GetCardsQuery query) {
        if (query.hasTagFilter()) {
            return cardRepository.findByTags(query.tags());
        }
        return cardRepository.findAll();
    }

    @Override
    public List<Card> execute(GetQuizCardsQuery query) {
        LocalDate quizDate = query.date();
        
        return cardRepository.findAll().stream()
            .filter(card -> !card.getCategory().isDone())
            .filter(card -> shouldCardBeInQuiz(card, quizDate))
            .toList();
    }

    @Override
    public void execute(AnswerCardCommand command) {
        Card card = cardRepository.findById(command.toCardId())
            .orElseThrow(() -> new CardNotFoundException(command.cardId()));

        if (command.isValid()) {
            card.answerCorrectly(LocalDate.now());
        } else {
            card.answerIncorrectly(LocalDate.now());
        }

        cardRepository.save(card);
    }

    /**
     * Détermine si une carte doit être incluse dans le quiz d'une date donnée.
     * Applique les règles du système de Leitner basé sur les fréquences.
     */
    private boolean shouldCardBeInQuiz(Card card, LocalDate quizDate) {
        return card.shouldBeReviewedOn(quizDate, systemStartDate);
    }
}
