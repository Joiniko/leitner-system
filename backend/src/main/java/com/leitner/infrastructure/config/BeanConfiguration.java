package com.leitner.infrastructure.config;

import com.leitner.domain.port.in.AnswerCardUseCase;
import com.leitner.domain.port.in.CreateCardUseCase;
import com.leitner.domain.port.in.GetCardsUseCase;
import com.leitner.domain.port.in.GetQuizCardsUseCase;
import com.leitner.domain.port.out.CardRepository;
import com.leitner.domain.service.CardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des beans Spring.
 * Permet l'injection de dépendances tout en gardant le domaine indépendant du framework.
 * 
 * Les interfaces (ports) sont définies dans le domaine.
 * Les implémentations (adapters) sont configurées ici.
 */
@Configuration
public class BeanConfiguration {

    /**
     * Crée le service de cartes qui implémente tous les use cases.
     * Injection du repository (port de sortie) fourni par l'infrastructure.
     */
    @Bean
    public CardService cardService(CardRepository cardRepository) {
        return new CardService(cardRepository);
    }

    /**
     * Expose le service comme implémentation du use case de création.
     */
    @Bean
    public CreateCardUseCase createCardUseCase(CardService cardService) {
        return cardService;
    }

    /**
     * Expose le service comme implémentation du use case de récupération.
     */
    @Bean
    public GetCardsUseCase getCardsUseCase(CardService cardService) {
        return cardService;
    }

    /**
     * Expose le service comme implémentation du use case du quiz.
     */
    @Bean
    public GetQuizCardsUseCase getQuizCardsUseCase(CardService cardService) {
        return cardService;
    }

    /**
     * Expose le service comme implémentation du use case de réponse.
     */
    @Bean
    public AnswerCardUseCase answerCardUseCase(CardService cardService) {
        return cardService;
    }
}
