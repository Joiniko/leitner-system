package com.leitner.infrastructure.adapter.in.rest.mapper;

import com.leitner.domain.model.Card;
import com.leitner.infrastructure.adapter.in.rest.dto.CardDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper pour convertir entre les entités du domaine et les DTOs REST.
 * Single Responsibility Principle (SRP) : responsable uniquement de la conversion.
 */
@Component
public class CardMapper {

    /**
     * Convertit une entité Card vers un DTO.
     */
    public CardDTO toDTO(Card card) {
        return new CardDTO(
            card.getId().getValue(),
            card.getQuestion(),
            card.getAnswer(),
            card.getTag(),
            card.getCategory().name()
        );
    }

    /**
     * Convertit une liste d'entités Card vers une liste de DTOs.
     */
    public List<CardDTO> toDTOList(List<Card> cards) {
        return cards.stream()
            .map(this::toDTO)
            .toList();
    }
}
