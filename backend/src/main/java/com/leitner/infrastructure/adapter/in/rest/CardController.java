package com.leitner.infrastructure.adapter.in.rest;

import com.leitner.domain.model.Card;
import com.leitner.domain.port.in.AnswerCardUseCase;
import com.leitner.domain.port.in.AnswerCardUseCase.AnswerCardCommand;
import com.leitner.domain.port.in.CreateCardUseCase;
import com.leitner.domain.port.in.CreateCardUseCase.CreateCardCommand;
import com.leitner.domain.port.in.GetCardsUseCase;
import com.leitner.domain.port.in.GetCardsUseCase.GetCardsQuery;
import com.leitner.domain.port.in.GetQuizCardsUseCase;
import com.leitner.domain.port.in.GetQuizCardsUseCase.GetQuizCardsQuery;
import com.leitner.infrastructure.adapter.in.rest.dto.AnswerRequest;
import com.leitner.infrastructure.adapter.in.rest.dto.CardDTO;
import com.leitner.infrastructure.adapter.in.rest.dto.CreateCardRequest;
import com.leitner.infrastructure.adapter.in.rest.mapper.CardMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Adapter REST pour les endpoints des cartes.
 * Implémente le contrat d'interface défini dans le Swagger.
 * 
 * Endpoints :
 * - GET /cards : récupère toutes les cartes (avec filtre par tags optionnel)
 * - POST /cards : crée une nouvelle carte
 * - GET /cards/quizz : récupère les cartes du quiz du jour
 * - PATCH /cards/{cardId}/answer : enregistre une réponse
 */
@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private final CreateCardUseCase createCardUseCase;
    private final GetCardsUseCase getCardsUseCase;
    private final GetQuizCardsUseCase getQuizCardsUseCase;
    private final AnswerCardUseCase answerCardUseCase;
    private final CardMapper cardMapper;

    public CardController(
            CreateCardUseCase createCardUseCase,
            GetCardsUseCase getCardsUseCase,
            GetQuizCardsUseCase getQuizCardsUseCase,
            AnswerCardUseCase answerCardUseCase,
            CardMapper cardMapper) {
        this.createCardUseCase = createCardUseCase;
        this.getCardsUseCase = getCardsUseCase;
        this.getQuizCardsUseCase = getQuizCardsUseCase;
        this.answerCardUseCase = answerCardUseCase;
        this.cardMapper = cardMapper;
    }

    /**
     * GET /cards
     * Récupère toutes les cartes, optionnellement filtrées par tags.
     */
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(
            @RequestParam(name = "tags", required = false) List<String> tags) {
        
        GetCardsQuery query = tags == null || tags.isEmpty() 
            ? GetCardsQuery.all() 
            : GetCardsQuery.withTags(tags);
        
        List<Card> cards = getCardsUseCase.execute(query);
        return ResponseEntity.ok(cardMapper.toDTOList(cards));
    }

    /**
     * POST /cards
     * Crée une nouvelle carte.
     */
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateCardRequest request) {
        CreateCardCommand command = new CreateCardCommand(
            request.question(),
            request.answer(),
            request.tag()
        );
        
        Card card = createCardUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardMapper.toDTO(card));
    }

    /**
     * GET /cards/quizz
     * Récupère les cartes pour le quiz d'une date donnée.
     * Si aucune date n'est fournie, utilise la date du jour.
     */
    @GetMapping("/quizz")
    public ResponseEntity<List<CardDTO>> getQuizCards(
            @RequestParam(name = "date", required = false) String dateParam) {
        
        LocalDate date = parseDate(dateParam);
        GetQuizCardsQuery query = GetQuizCardsQuery.forDate(date);
        
        List<Card> cards = getQuizCardsUseCase.execute(query);
        return ResponseEntity.ok(cardMapper.toDTOList(cards));
    }

    /**
     * PATCH /cards/{cardId}/answer
     * Enregistre une réponse pour une carte.
     */
    @PatchMapping("/{cardId}/answer")
    public ResponseEntity<Void> answerCard(
            @PathVariable String cardId,
            @Valid @RequestBody AnswerRequest request) {
        
        AnswerCardCommand command = new AnswerCardCommand(cardId, request.isValid());
        answerCardUseCase.execute(command);
        
        return ResponseEntity.noContent().build();
    }

    private LocalDate parseDate(String dateParam) {
        if (dateParam == null || dateParam.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateParam);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }
}
