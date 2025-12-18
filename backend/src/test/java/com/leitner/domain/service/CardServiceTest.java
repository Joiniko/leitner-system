package com.leitner.domain.service;

import com.leitner.domain.exception.CardNotFoundException;
import com.leitner.domain.model.Card;
import com.leitner.domain.model.CardId;
import com.leitner.domain.model.Category;
import com.leitner.domain.port.in.AnswerCardUseCase.AnswerCardCommand;
import com.leitner.domain.port.in.CreateCardUseCase.CreateCardCommand;
import com.leitner.domain.port.in.GetCardsUseCase.GetCardsQuery;
import com.leitner.domain.port.in.GetQuizCardsUseCase.GetQuizCardsQuery;
import com.leitner.domain.port.out.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CardService - Use Cases")
class CardServiceTest {

    private CardRepository cardRepository;
    private CardService cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        cardService = new CardService(cardRepository, LocalDate.of(2024, 1, 1));
    }

    @Nested
    @DisplayName("CreateCardUseCase")
    class CreateCardUseCaseTest {

        @Test
        @DisplayName("should create a new card in FIRST category")
        void shouldCreateNewCardInFirstCategory() {
            when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

            CreateCardCommand command = new CreateCardCommand(
                "What is DDD?",
                "Domain Driven Design",
                "Architecture"
            );

            Card result = cardService.execute(command);

            assertThat(result.getQuestion()).isEqualTo("What is DDD?");
            assertThat(result.getAnswer()).isEqualTo("Domain Driven Design");
            assertThat(result.getTag()).isEqualTo("Architecture");
            assertThat(result.getCategory()).isEqualTo(Category.FIRST);
            verify(cardRepository).save(any(Card.class));
        }

        @Test
        @DisplayName("should create card without tag")
        void shouldCreateCardWithoutTag() {
            when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

            CreateCardCommand command = new CreateCardCommand("Q?", "A", null);

            Card result = cardService.execute(command);

            assertThat(result.getTag()).isNull();
        }

        @Test
        @DisplayName("should reject invalid command with null question")
        void shouldRejectInvalidCommand() {
            assertThatThrownBy(() -> new CreateCardCommand(null, "Answer", "Tag"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("GetCardsUseCase")
    class GetCardsUseCaseTest {

        @Test
        @DisplayName("should return all cards when no tags filter")
        void shouldReturnAllCardsWhenNoTagsFilter() {
            List<Card> allCards = List.of(
                Card.create("Q1?", "A1", "Tag1"),
                Card.create("Q2?", "A2", "Tag2")
            );
            when(cardRepository.findAll()).thenReturn(allCards);

            List<Card> result = cardService.execute(GetCardsQuery.all());

            assertThat(result).hasSize(2);
            verify(cardRepository).findAll();
            verify(cardRepository, never()).findByTags(any());
        }

        @Test
        @DisplayName("should return filtered cards when tags provided")
        void shouldReturnFilteredCardsWhenTagsProvided() {
            List<Card> filteredCards = List.of(Card.create("Q1?", "A1", "Java"));
            when(cardRepository.findByTags(List.of("Java"))).thenReturn(filteredCards);

            List<Card> result = cardService.execute(GetCardsQuery.withTags(List.of("Java")));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTag()).isEqualTo("Java");
            verify(cardRepository).findByTags(List.of("Java"));
        }
    }

    @Nested
    @DisplayName("GetQuizCardsUseCase")
    class GetQuizCardsUseCaseTest {

        @Test
        @DisplayName("should return new cards for quiz")
        void shouldReturnNewCardsForQuiz() {
            Card newCard = Card.create("Q?", "A", null);
            when(cardRepository.findAll()).thenReturn(List.of(newCard));

            List<Card> result = cardService.execute(GetQuizCardsQuery.forDate(LocalDate.of(2024, 1, 1)));

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should not return DONE cards")
        void shouldNotReturnDoneCards() {
            Card doneCard = createCardInCategory(Category.DONE);
            when(cardRepository.findAll()).thenReturn(List.of(doneCard));

            List<Card> result = cardService.execute(GetQuizCardsQuery.forDate(LocalDate.of(2024, 1, 1)));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return FIRST category cards every day")
        void shouldReturnFirstCategoryCardsEveryDay() {
            Card card = createCardWithLastAnswer(Category.FIRST, LocalDate.of(2024, 1, 1));
            when(cardRepository.findAll()).thenReturn(List.of(card));

            // Day 2 - 1 day since last answer, frequency is 1, should be included
            List<Card> result = cardService.execute(
                GetQuizCardsQuery.forDate(LocalDate.of(2024, 1, 2))
            );

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return SECOND category cards after 2 days")
        void shouldReturnSecondCategoryCardsAfter2Days() {
            Card card = createCardWithLastAnswer(Category.SECOND, LocalDate.of(2024, 1, 1));
            when(cardRepository.findAll()).thenReturn(List.of(card));

            // Day 2 - only 1 day since answer, frequency is 2, should NOT be included
            List<Card> result1 = cardService.execute(
                GetQuizCardsQuery.forDate(LocalDate.of(2024, 1, 2))
            );
            assertThat(result1).isEmpty();

            // Day 3 - 2 days since answer, frequency is 2, should be included
            List<Card> result2 = cardService.execute(
                GetQuizCardsQuery.forDate(LocalDate.of(2024, 1, 3))
            );
            assertThat(result2).hasSize(1);
        }

        private Card createCardInCategory(Category category) {
            return Card.reconstitute(
                CardId.generate(), "Q?", "A", null,
                category, LocalDate.of(2024, 1, 1)
            );
        }

        private Card createCardWithLastAnswer(Category category, LocalDate lastAnswer) {
            return Card.reconstitute(
                CardId.generate(), "Q?", "A", null,
                category, lastAnswer
            );
        }
    }

    @Nested
    @DisplayName("AnswerCardUseCase")
    class AnswerCardUseCaseTest {

        @Test
        @DisplayName("should progress card on correct answer")
        void shouldProgressCardOnCorrectAnswer() {
            CardId cardId = CardId.of("card-1");
            Card card = Card.reconstitute(cardId, "Q?", "A", null, Category.FIRST, null);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
            when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

            cardService.execute(new AnswerCardCommand("card-1", true));

            assertThat(card.getCategory()).isEqualTo(Category.SECOND);
            verify(cardRepository).save(card);
        }

        @Test
        @DisplayName("should reset card to FIRST on incorrect answer")
        void shouldResetCardToFirstOnIncorrectAnswer() {
            CardId cardId = CardId.of("card-1");
            Card card = Card.reconstitute(cardId, "Q?", "A", null, Category.THIRD, null);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
            when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

            cardService.execute(new AnswerCardCommand("card-1", false));

            assertThat(card.getCategory()).isEqualTo(Category.FIRST);
            verify(cardRepository).save(card);
        }

        @Test
        @DisplayName("should throw exception when card not found")
        void shouldThrowExceptionWhenCardNotFound() {
            CardId cardId = CardId.of("non-existent");
            when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> 
                cardService.execute(new AnswerCardCommand("non-existent", true))
            ).isInstanceOf(CardNotFoundException.class)
             .hasMessageContaining("non-existent");
        }
    }
}
