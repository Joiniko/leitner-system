package com.leitner.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Card Entity")
class CardTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create card with valid data")
        void shouldCreateCardWithValidData() {
            Card card = Card.create("What is TDD?", "Test Driven Development", "Development");

            assertThat(card.getId()).isNotNull();
            assertThat(card.getQuestion()).isEqualTo("What is TDD?");
            assertThat(card.getAnswer()).isEqualTo("Test Driven Development");
            assertThat(card.getTag()).isEqualTo("Development");
            assertThat(card.getCategory()).isEqualTo(Category.FIRST);
            assertThat(card.getLastAnswerDate()).isNull();
        }

        @Test
        @DisplayName("should create card without tag")
        void shouldCreateCardWithoutTag() {
            Card card = Card.create("Question?", "Answer", null);

            assertThat(card.getTag()).isNull();
        }

        @Test
        @DisplayName("should reject null question")
        void shouldRejectNullQuestion() {
            assertThatThrownBy(() -> Card.create(null, "Answer", "Tag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
        }

        @Test
        @DisplayName("should reject blank question")
        void shouldRejectBlankQuestion() {
            assertThatThrownBy(() -> Card.create("   ", "Answer", "Tag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
        }

        @Test
        @DisplayName("should reject null answer")
        void shouldRejectNullAnswer() {
            assertThatThrownBy(() -> Card.create("Question?", null, "Tag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Answer cannot be null or blank");
        }

        @Test
        @DisplayName("should reject blank answer")
        void shouldRejectBlankAnswer() {
            assertThatThrownBy(() -> Card.create("Question?", "  ", "Tag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Answer cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Reconstitution")
    class Reconstitution {

        @Test
        @DisplayName("should reconstitute card from persistence")
        void shouldReconstituteCardFromPersistence() {
            CardId id = CardId.of("existing-id");
            LocalDate lastAnswer = LocalDate.of(2024, 1, 15);

            Card card = Card.reconstitute(
                id, "Question?", "Answer", "Tag",
                Category.THIRD, lastAnswer
            );

            assertThat(card.getId()).isEqualTo(id);
            assertThat(card.getCategory()).isEqualTo(Category.THIRD);
            assertThat(card.getLastAnswerDate()).isEqualTo(lastAnswer);
        }
    }

    @Nested
    @DisplayName("Answering correctly")
    class AnsweringCorrectly {

        @Test
        @DisplayName("should progress to next category on correct answer")
        void shouldProgressToNextCategoryOnCorrectAnswer() {
            Card card = Card.create("Q?", "A", null);
            LocalDate today = LocalDate.now();

            card.answerCorrectly(today);

            assertThat(card.getCategory()).isEqualTo(Category.SECOND);
            assertThat(card.getLastAnswerDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("should progress through all categories")
        void shouldProgressThroughAllCategories() {
            Card card = Card.create("Q?", "A", null);
            LocalDate date = LocalDate.now();

            assertThat(card.getCategory()).isEqualTo(Category.FIRST);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.SECOND);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.THIRD);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.FOURTH);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.FIFTH);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.SIXTH);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.SEVENTH);

            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.DONE);
            assertThat(card.isDone()).isTrue();
        }
    }

    @Nested
    @DisplayName("Answering incorrectly")
    class AnsweringIncorrectly {

        @Test
        @DisplayName("should return to FIRST category on incorrect answer")
        void shouldReturnToFirstCategoryOnIncorrectAnswer() {
            Card card = Card.create("Q?", "A", null);
            LocalDate date = LocalDate.now();

            // Progress to THIRD
            card.answerCorrectly(date);
            card.answerCorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.THIRD);

            // Wrong answer
            card.answerIncorrectly(date);
            assertThat(card.getCategory()).isEqualTo(Category.FIRST);
            assertThat(card.getLastAnswerDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("card in FIRST category stays in FIRST on incorrect answer")
        void cardInFirstStaysInFirstOnIncorrectAnswer() {
            Card card = Card.create("Q?", "A", null);

            card.answerIncorrectly(LocalDate.now());

            assertThat(card.getCategory()).isEqualTo(Category.FIRST);
        }
    }

    @Nested
    @DisplayName("Check answer")
    class CheckAnswer {

        @Test
        @DisplayName("should return true for exact match")
        void shouldReturnTrueForExactMatch() {
            Card card = Card.create("Q?", "Test Driven Development", null);

            assertThat(card.checkAnswer("Test Driven Development")).isTrue();
        }

        @Test
        @DisplayName("should return true for case-insensitive match")
        void shouldReturnTrueForCaseInsensitiveMatch() {
            Card card = Card.create("Q?", "Test Driven Development", null);

            assertThat(card.checkAnswer("test driven development")).isTrue();
            assertThat(card.checkAnswer("TEST DRIVEN DEVELOPMENT")).isTrue();
        }

        @Test
        @DisplayName("should return true with extra whitespace")
        void shouldReturnTrueWithExtraWhitespace() {
            Card card = Card.create("Q?", "TDD", null);

            assertThat(card.checkAnswer("  TDD  ")).isTrue();
        }

        @Test
        @DisplayName("should return false for different answer")
        void shouldReturnFalseForDifferentAnswer() {
            Card card = Card.create("Q?", "Correct Answer", null);

            assertThat(card.checkAnswer("Wrong Answer")).isFalse();
        }

        @Test
        @DisplayName("should return false for null answer")
        void shouldReturnFalseForNullAnswer() {
            Card card = Card.create("Q?", "Answer", null);

            assertThat(card.checkAnswer(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Review scheduling")
    class ReviewScheduling {

        @Test
        @DisplayName("new card should always be reviewed")
        void newCardShouldAlwaysBeReviewed() {
            Card card = Card.create("Q?", "A", null);
            LocalDate today = LocalDate.now();

            assertThat(card.shouldBeReviewedOn(today, today)).isTrue();
        }

        @Test
        @DisplayName("DONE card should never be reviewed")
        void doneCardShouldNeverBeReviewed() {
            Card card = Card.create("Q?", "A", null);
            LocalDate date = LocalDate.now();

            // Progress to DONE
            for (int i = 0; i < 7; i++) {
                card.answerCorrectly(date);
            }

            assertThat(card.isDone()).isTrue();
            assertThat(card.shouldBeReviewedOn(date.plusDays(100), date)).isFalse();
        }

        @Test
        @DisplayName("SECOND category card should be reviewed after 2 days")
        void secondCategoryCardShouldBeReviewedAfter2Days() {
            LocalDate answerDate = LocalDate.of(2024, 1, 1);
            Card card = Card.create("Q?", "A", null);
            card.answerCorrectly(answerDate); // Now in SECOND

            assertThat(card.shouldBeReviewedOn(answerDate.plusDays(1), answerDate)).isFalse();
            assertThat(card.shouldBeReviewedOn(answerDate.plusDays(2), answerDate)).isTrue();
            assertThat(card.shouldBeReviewedOn(answerDate.plusDays(3), answerDate)).isTrue();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("cards with same id should be equal")
        void cardsWithSameIdShouldBeEqual() {
            CardId id = CardId.of("same-id");
            Card card1 = Card.reconstitute(id, "Q1?", "A1", null, Category.FIRST, null);
            Card card2 = Card.reconstitute(id, "Q2?", "A2", null, Category.SECOND, null);

            assertThat(card1).isEqualTo(card2);
            assertThat(card1.hashCode()).isEqualTo(card2.hashCode());
        }

        @Test
        @DisplayName("cards with different ids should not be equal")
        void cardsWithDifferentIdsShouldNotBeEqual() {
            Card card1 = Card.create("Q?", "A", null);
            Card card2 = Card.create("Q?", "A", null);

            assertThat(card1).isNotEqualTo(card2);
        }
    }
}
