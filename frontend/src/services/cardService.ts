import { Card, CreateCardRequest, AnswerRequest } from '../types/Card';

const API_BASE_URL = 'http://localhost:8080';

/**
 * Service pour communiquer avec l'API backend
 */
export const cardService = {
  /**
   * Récupère toutes les cartes, optionnellement filtrées par tags
   */
  async getCards(tags?: string[]): Promise<Card[]> {
    const url = new URL(`${API_BASE_URL}/cards`);
    if (tags && tags.length > 0) {
      tags.forEach(tag => url.searchParams.append('tags', tag));
    }
    
    const response = await fetch(url.toString());
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération des cartes');
    }
    return response.json();
  },

  /**
   * Crée une nouvelle carte
   */
  async createCard(data: CreateCardRequest): Promise<Card> {
    const response = await fetch(`${API_BASE_URL}/cards`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Erreur lors de la création de la carte');
    }
    return response.json();
  },

  /**
   * Récupère les cartes pour le quiz d'une date donnée
   */
  async getQuizCards(date?: string): Promise<Card[]> {
    const url = new URL(`${API_BASE_URL}/cards/quizz`);
    if (date) {
      url.searchParams.append('date', date);
    }
    
    const response = await fetch(url.toString());
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération du quiz');
    }
    return response.json();
  },

  /**
   * Enregistre une réponse pour une carte
   */
  async answerCard(cardId: string, isValid: boolean): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/cards/${cardId}/answer`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ isValid } as AnswerRequest),
    });
    
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('Carte non trouvée');
      }
      throw new Error('Erreur lors de l\'enregistrement de la réponse');
    }
  },
};
