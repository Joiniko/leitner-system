package com.leitner.infrastructure.adapter.out.persistence;

import com.leitner.domain.model.Card;
import com.leitner.domain.model.CardId;
import com.leitner.domain.port.out.CardRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation en mémoire du repository de cartes.
 * Utilise une ConcurrentHashMap pour la thread-safety.
 * 
 * Note: Cette implémentation peut être facilement remplacée par une
 * implémentation JPA sans modifier le domaine (Open/Closed Principle).
 */
@Repository
public class InMemoryCardRepository implements CardRepository {

    private final Map<String, Card> cards = new ConcurrentHashMap<>();

    @Override
    public Card save(Card card) {
        cards.put(card.getId().getValue(), card);
        return card;
    }

    @Override
    public Optional<Card> findById(CardId id) {
        return Optional.ofNullable(cards.get(id.getValue()));
    }

    @Override
    public List<Card> findAll() {
        return List.copyOf(cards.values());
    }

    @Override
    public List<Card> findByTags(List<String> tags) {
        return cards.values().stream()
            .filter(card -> card.getTag() != null && tags.contains(card.getTag()))
            .toList();
    }

    @Override
    public void deleteById(CardId id) {
        cards.remove(id.getValue());
    }

    @Override
    public boolean existsById(CardId id) {
        return cards.containsKey(id.getValue());
    }

    /**
     * Vide le repository (utile pour les tests).
     */
    public void clear() {
        cards.clear();
    }
}
