package com.leitner.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leitner.domain.model.Card;
import com.leitner.domain.model.CardId;
import com.leitner.domain.model.Category;
import com.leitner.domain.port.out.CardRepository;
import com.leitner.infrastructure.adapter.in.rest.dto.AnswerRequest;
import com.leitner.infrastructure.adapter.in.rest.dto.CreateCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CardController Integration Tests")
class CardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        if (cardRepository instanceof com.leitner.infrastructure.adapter.out.persistence.InMemoryCardRepository inMemoryRepo) {
            inMemoryRepo.clear();
        }
    }

    @Nested
    @DisplayName("POST /cards")
    class CreateCard {

        @Test
        @DisplayName("should create card and return 201")
        void shouldCreateCardAndReturn201() throws Exception {
            CreateCardRequest request = new CreateCardRequest(
                "What is SOLID?",
                "Five design principles",
                "OOP"
            );

            mockMvc.perform(post("/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.question").value("What is SOLID?"))
                .andExpect(jsonPath("$.answer").value("Five design principles"))
                .andExpect(jsonPath("$.tag").value("OOP"))
                .andExpect(jsonPath("$.category").value("FIRST"));
        }

        @Test
        @DisplayName("should create card without tag")
        void shouldCreateCardWithoutTag() throws Exception {
            CreateCardRequest request = new CreateCardRequest(
                "Question?",
                "Answer",
                null
            );

            mockMvc.perform(post("/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tag").isEmpty());
        }

        @Test
        @DisplayName("should return 400 when question is missing")
        void shouldReturn400WhenQuestionMissing() throws Exception {
            String json = "{\"answer\": \"Answer\", \"tag\": \"Tag\"}";

            mockMvc.perform(post("/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when answer is missing")
        void shouldReturn400WhenAnswerMissing() throws Exception {
            String json = "{\"question\": \"Question?\", \"tag\": \"Tag\"}";

            mockMvc.perform(post("/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /cards")
    class GetCards {

        @Test
        @DisplayName("should return all cards")
        void shouldReturnAllCards() throws Exception {
            // Given
            cardRepository.save(Card.create("Q1?", "A1", "Tag1"));
            cardRepository.save(Card.create("Q2?", "A2", "Tag2"));

            // When & Then
            mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("should return empty list when no cards")
        void shouldReturnEmptyListWhenNoCards() throws Exception {
            mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should filter cards by tag")
        void shouldFilterCardsByTag() throws Exception {
            // Given
            cardRepository.save(Card.create("Q1?", "A1", "Java"));
            cardRepository.save(Card.create("Q2?", "A2", "Python"));
            cardRepository.save(Card.create("Q3?", "A3", "Java"));

            // When & Then
            mockMvc.perform(get("/cards").param("tags", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].tag", everyItem(equalTo("Java"))));
        }

        @Test
        @DisplayName("should filter cards by multiple tags")
        void shouldFilterCardsByMultipleTags() throws Exception {
            // Given
            cardRepository.save(Card.create("Q1?", "A1", "Java"));
            cardRepository.save(Card.create("Q2?", "A2", "Python"));
            cardRepository.save(Card.create("Q3?", "A3", "JavaScript"));

            // When & Then
            mockMvc.perform(get("/cards").param("tags", "Java", "Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /cards/quizz")
    class GetQuizCards {

        @Test
        @DisplayName("should return cards for today's quiz")
        void shouldReturnCardsForTodaysQuiz() throws Exception {
            // Given - new cards should be in quiz
            cardRepository.save(Card.create("Q1?", "A1", "Tag1"));
            cardRepository.save(Card.create("Q2?", "A2", "Tag2"));

            // When & Then
            mockMvc.perform(get("/cards/quizz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("should not return DONE cards in quiz")
        void shouldNotReturnDoneCardsInQuiz() throws Exception {
            // Given - a card that's DONE
            Card doneCard = Card.reconstitute(
                CardId.generate(), "Q?", "A", null,
                Category.DONE, LocalDate.now()
            );
            cardRepository.save(doneCard);

            // When & Then
            mockMvc.perform(get("/cards/quizz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should accept date parameter")
        void shouldAcceptDateParameter() throws Exception {
            cardRepository.save(Card.create("Q?", "A", null));

            mockMvc.perform(get("/cards/quizz").param("date", "2024-01-15"))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /cards/{cardId}/answer")
    class AnswerCard {

        @Test
        @DisplayName("should return 204 on valid answer")
        void shouldReturn204OnValidAnswer() throws Exception {
            // Given
            Card card = Card.create("Q?", "A", null);
            cardRepository.save(card);
            String cardId = card.getId().getValue();

            AnswerRequest request = new AnswerRequest(true);

            // When & Then
            mockMvc.perform(patch("/cards/{cardId}/answer", cardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should progress card category on correct answer")
        void shouldProgressCardCategoryOnCorrectAnswer() throws Exception {
            // Given
            Card card = Card.create("Q?", "A", null);
            cardRepository.save(card);
            String cardId = card.getId().getValue();

            // When
            mockMvc.perform(patch("/cards/{cardId}/answer", cardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isValid\": true}"))
                .andExpect(status().isNoContent());

            // Then - verify card progressed
            mockMvc.perform(get("/cards"))
                .andExpect(jsonPath("$[0].category").value("SECOND"));
        }

        @Test
        @DisplayName("should reset card to FIRST on incorrect answer")
        void shouldResetCardToFirstOnIncorrectAnswer() throws Exception {
            // Given - card in THIRD category
            Card card = Card.reconstitute(
                CardId.generate(), "Q?", "A", null,
                Category.THIRD, null
            );
            cardRepository.save(card);
            String cardId = card.getId().getValue();

            // When
            mockMvc.perform(patch("/cards/{cardId}/answer", cardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isValid\": false}"))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/cards"))
                .andExpect(jsonPath("$[0].category").value("FIRST"));
        }

        @Test
        @DisplayName("should return 404 when card not found")
        void shouldReturn404WhenCardNotFound() throws Exception {
            AnswerRequest request = new AnswerRequest(true);

            mockMvc.perform(patch("/cards/{cardId}/answer", "non-existent-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when isValid is missing")
        void shouldReturn400WhenIsValidMissing() throws Exception {
            Card card = Card.create("Q?", "A", null);
            cardRepository.save(card);
            String cardId = card.getId().getValue();

            mockMvc.perform(patch("/cards/{cardId}/answer", cardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
        }
    }
}
