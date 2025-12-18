package com.leitner.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CardId Value Object")
class CardIdTest {

    @Nested
    @DisplayName("Generation")
    class Generation {

        @Test
        @DisplayName("should generate unique identifiers")
        void shouldGenerateUniqueIds() {
            CardId id1 = CardId.generate();
            CardId id2 = CardId.generate();

            assertThat(id1).isNotEqualTo(id2);
            assertThat(id1.getValue()).isNotEqualTo(id2.getValue());
        }

        @Test
        @DisplayName("should generate non-empty identifier")
        void shouldGenerateNonEmptyId() {
            CardId id = CardId.generate();

            assertThat(id.getValue()).isNotNull().isNotBlank();
        }
    }

    @Nested
    @DisplayName("Creation from value")
    class CreationFromValue {

        @Test
        @DisplayName("should create from valid value")
        void shouldCreateFromValidValue() {
            String value = "test-id-123";
            CardId id = CardId.of(value);

            assertThat(id.getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> CardId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> CardId.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("should reject empty value")
        void shouldRejectEmptyValue() {
            assertThatThrownBy(() -> CardId.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when values are the same")
        void shouldBeEqualWhenValuesAreSame() {
            CardId id1 = CardId.of("same-value");
            CardId id2 = CardId.of("same-value");

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values are different")
        void shouldNotBeEqualWhenValuesDifferent() {
            CardId id1 = CardId.of("value-1");
            CardId id2 = CardId.of("value-2");

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("toString should return the value")
        void toStringShouldReturnValue() {
            String value = "my-card-id";
            CardId id = CardId.of(value);

            assertThat(id.toString()).isEqualTo(value);
        }
    }
}
