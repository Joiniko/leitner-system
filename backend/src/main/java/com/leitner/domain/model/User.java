package com.leitner.domain.model;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant un utilisateur du système.
 * 
 * NOTE: Cette entité est prévue pour une future implémentation de l'authentification.
 * Actuellement, seule l'architecture est mise en place conformément à l'énoncé.
 * L'implémentation fonctionnelle n'est pas requise.
 * 
 * Un utilisateur peut :
 * - Posséder ses propres cartes
 * - Définir une heure de notification pour les quiz
 * - Avoir un historique de quiz quotidien
 */
public class User {

    private final UserId id;
    private final String username;
    private final String email;
    private LocalTime notificationTime;

    private User(UserId id, String username, String email, LocalTime notificationTime) {
        validateUsername(username);
        validateEmail(email);
        
        this.id = id;
        this.username = username;
        this.email = email;
        this.notificationTime = notificationTime;
    }

    /**
     * Crée un nouvel utilisateur.
     */
    public static User create(String username, String email) {
        return new User(
            UserId.generate(),
            username,
            email,
            null
        );
    }

    /**
     * Reconstruit un utilisateur depuis la persistence.
     */
    public static User reconstitute(UserId id, String username, String email, LocalTime notificationTime) {
        return new User(id, username, email, notificationTime);
    }

    /**
     * Définit l'heure de notification quotidienne.
     */
    public void setNotificationTime(LocalTime time) {
        this.notificationTime = time;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    // Getters
    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalTime getNotificationTime() {
        return notificationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Value Object pour l'identifiant utilisateur.
     */
    public static final class UserId {
        private final String value;

        private UserId(String value) {
            this.value = value;
        }

        public static UserId generate() {
            return new UserId(UUID.randomUUID().toString());
        }

        public static UserId of(String value) {
            return new UserId(value);
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserId userId = (UserId) o;
            return Objects.equals(value, userId.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
