package com.leitner.domain.exception;

/**
 * Exception levée quand une carte n'est pas trouvée.
 */
public class CardNotFoundException extends RuntimeException {

    private final String cardId;

    public CardNotFoundException(String cardId) {
        super("Card not found with id: " + cardId);
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
