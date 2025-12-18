package com.leitner.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category Enum - Leitner System")
class CategoryTest {

    @Nested
    @DisplayName("Frequency values")
    class FrequencyValues {

        @Test
        @DisplayName("FIRST category should have frequency of 1 day")
        void firstCategoryShouldHaveFrequencyOf1Day() {
            assertThat(Category.FIRST.getFrequencyInDays()).isEqualTo(1);
        }

        @Test
        @DisplayName("SECOND category should have frequency of 2 days")
        void secondCategoryShouldHaveFrequencyOf2Days() {
            assertThat(Category.SECOND.getFrequencyInDays()).isEqualTo(2);
        }

        @Test
        @DisplayName("THIRD category should have frequency of 4 days")
        void thirdCategoryShouldHaveFrequencyOf4Days() {
            assertThat(Category.THIRD.getFrequencyInDays()).isEqualTo(4);
        }

        @Test
        @DisplayName("FOURTH category should have frequency of 8 days")
        void fourthCategoryShouldHaveFrequencyOf8Days() {
            assertThat(Category.FOURTH.getFrequencyInDays()).isEqualTo(8);
        }

        @Test
        @DisplayName("FIFTH category should have frequency of 16 days")
        void fifthCategoryShouldHaveFrequencyOf16Days() {
            assertThat(Category.FIFTH.getFrequencyInDays()).isEqualTo(16);
        }

        @Test
        @DisplayName("SIXTH category should have frequency of 32 days")
        void sixthCategoryShouldHaveFrequencyOf32Days() {
            assertThat(Category.SIXTH.getFrequencyInDays()).isEqualTo(32);
        }

        @Test
        @DisplayName("SEVENTH category should have frequency of 64 days")
        void seventhCategoryShouldHaveFrequencyOf64Days() {
            assertThat(Category.SEVENTH.getFrequencyInDays()).isEqualTo(64);
        }

        @Test
        @DisplayName("frequencies should double between categories")
        void frequenciesShouldDoubleBetweenCategories() {
            Category[] categories = {
                Category.FIRST, Category.SECOND, Category.THIRD,
                Category.FOURTH, Category.FIFTH, Category.SIXTH, Category.SEVENTH
            };

            for (int i = 1; i < categories.length; i++) {
                int previousFrequency = categories[i - 1].getFrequencyInDays();
                int currentFrequency = categories[i].getFrequencyInDays();
                assertThat(currentFrequency)
                    .as("Category %s frequency should be double of %s", 
                        categories[i], categories[i - 1])
                    .isEqualTo(previousFrequency * 2);
            }
        }
    }

    @Nested
    @DisplayName("Category progression (next)")
    class CategoryProgression {

        @ParameterizedTest
        @CsvSource({
            "FIRST, SECOND",
            "SECOND, THIRD",
            "THIRD, FOURTH",
            "FOURTH, FIFTH",
            "FIFTH, SIXTH",
            "SIXTH, SEVENTH",
            "SEVENTH, DONE",
            "DONE, DONE"
        })
        @DisplayName("should progress to next category correctly")
        void shouldProgressToNextCategoryCorrectly(Category current, Category expected) {
            assertThat(current.next()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Initial category")
    class InitialCategory {

        @Test
        @DisplayName("initial category should be FIRST")
        void initialCategoryShouldBeFirst() {
            assertThat(Category.initial()).isEqualTo(Category.FIRST);
        }
    }

    @Nested
    @DisplayName("isDone check")
    class IsDoneCheck {

        @ParameterizedTest
        @EnumSource(value = Category.class, names = {"FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH", "SIXTH", "SEVENTH"})
        @DisplayName("non-DONE categories should return false for isDone")
        void nonDoneCategoriesShouldReturnFalse(Category category) {
            assertThat(category.isDone()).isFalse();
        }

        @Test
        @DisplayName("DONE category should return true for isDone")
        void doneCategoryShouldReturnTrue() {
            assertThat(Category.DONE.isDone()).isTrue();
        }
    }

    @Nested
    @DisplayName("Review scheduling (calendar system)")
    class ReviewScheduling {

        @Test
        @DisplayName("FIRST category should be reviewed every day")
        void firstCategoryShouldBeReviewedEveryDay() {
            for (int day = 1; day <= 10; day++) {
                assertThat(Category.FIRST.shouldBeReviewedOnDay(day))
                    .as("Day %d", day)
                    .isTrue();
            }
        }

        @Test
        @DisplayName("SECOND category should be reviewed every 2 days")
        void secondCategoryShouldBeReviewedEvery2Days() {
            assertThat(Category.SECOND.shouldBeReviewedOnDay(1)).isFalse();
            assertThat(Category.SECOND.shouldBeReviewedOnDay(2)).isTrue();
            assertThat(Category.SECOND.shouldBeReviewedOnDay(3)).isFalse();
            assertThat(Category.SECOND.shouldBeReviewedOnDay(4)).isTrue();
        }

        @Test
        @DisplayName("THIRD category should be reviewed every 4 days")
        void thirdCategoryShouldBeReviewedEvery4Days() {
            assertThat(Category.THIRD.shouldBeReviewedOnDay(1)).isFalse();
            assertThat(Category.THIRD.shouldBeReviewedOnDay(2)).isFalse();
            assertThat(Category.THIRD.shouldBeReviewedOnDay(3)).isFalse();
            assertThat(Category.THIRD.shouldBeReviewedOnDay(4)).isTrue();
            assertThat(Category.THIRD.shouldBeReviewedOnDay(8)).isTrue();
        }

        @Test
        @DisplayName("DONE category should never be reviewed")
        void doneCategoryShouldNeverBeReviewed() {
            for (int day = 1; day <= 100; day++) {
                assertThat(Category.DONE.shouldBeReviewedOnDay(day))
                    .as("Day %d", day)
                    .isFalse();
            }
        }
    }
}
