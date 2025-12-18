package com.leitner.domain.port.out;

import com.leitner.domain.model.User;
import com.leitner.domain.model.User.UserId;

import java.util.Optional;

/**
 * Port de sortie pour la persistence des utilisateurs.
 * 
 * NOTE: Ce port est prévu pour une future implémentation de l'authentification.
 * Conformément à l'énoncé, seule l'architecture est mise en place.
 * L'implémentation fonctionnelle n'est pas requise.
 */
public interface UserRepository {

    /**
     * Sauvegarde un utilisateur.
     */
    User save(User user);

    /**
     * Recherche un utilisateur par son identifiant.
     */
    Optional<User> findById(UserId id);

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     */
    Optional<User> findByUsername(String username);

    /**
     * Recherche un utilisateur par son email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur existe déjà.
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà.
     */
    boolean existsByEmail(String email);
}
