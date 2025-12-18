package com.leitner.domain.port.out;

import com.leitner.domain.model.Card;
import com.leitner.domain.model.CardId;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistence des cartes.
 * Interface qui sera implémentée par l'adapter de persistence.
 * Dependency Inversion Principle (DIP) : le domaine définit l'interface,
 * l'infrastructure fournit l'implémentation.
 */
public interface CardRepository {

    /**
     * Sauvegarde une carte (création ou mise à jour).
     *
     * @param card la carte à sauvegarder
     * @return la carte sauvegardée
     */
    Card save(Card card);

    /**
     * Recherche une carte par son identifiant.
     *
     * @param id l'identifiant de la carte
     * @return la carte si trouvée, Optional.empty() sinon
     */
    Optional<Card> findById(CardId id);

    /**
     * Récupère toutes les cartes.
     *
     * @return la liste de toutes les cartes
     */
    List<Card> findAll();

    /**
     * Récupère les cartes ayant un ou plusieurs tags spécifiques.
     *
     * @param tags les tags à rechercher
     * @return la liste des cartes correspondantes
     */
    List<Card> findByTags(List<String> tags);

    /**
     * Supprime une carte par son identifiant.
     *
     * @param id l'identifiant de la carte à supprimer
     */
    void deleteById(CardId id);

    /**
     * Vérifie si une carte existe.
     *
     * @param id l'identifiant de la carte
     * @return true si la carte existe
     */
    boolean existsById(CardId id);
}
