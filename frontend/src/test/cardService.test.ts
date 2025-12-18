import { describe, it, expect, vi, beforeEach } from 'vitest';
import { cardService } from '../services/cardService';

// Mock fetch globally
const mockFetch = vi.fn();
global.fetch = mockFetch;

describe('cardService', () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  describe('getCards', () => {
    it('should fetch all cards', async () => {
      const mockCards = [
        { id: '1', question: 'Q1?', answer: 'A1', category: 'FIRST' }
      ];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockCards)
      });

      const result = await cardService.getCards();

      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/cards');
      expect(result).toEqual(mockCards);
    });

    it('should fetch cards with tags filter', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve([])
      });

      await cardService.getCards(['Java', 'Python']);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards?tags=Java&tags=Python'
      );
    });

    it('should throw error on failure', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false
      });

      await expect(cardService.getCards()).rejects.toThrow(
        'Erreur lors de la récupération des cartes'
      );
    });
  });

  describe('createCard', () => {
    it('should create a new card', async () => {
      const newCard = {
        id: '1',
        question: 'Q?',
        answer: 'A',
        tag: 'Test',
        category: 'FIRST'
      };
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(newCard)
      });

      const result = await cardService.createCard({
        question: 'Q?',
        answer: 'A',
        tag: 'Test'
      });

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards',
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ question: 'Q?', answer: 'A', tag: 'Test' })
        }
      );
      expect(result).toEqual(newCard);
    });

    it('should throw error with message on failure', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        json: () => Promise.resolve({ message: 'Validation error' })
      });

      await expect(
        cardService.createCard({ question: '', answer: '' })
      ).rejects.toThrow('Validation error');
    });
  });

  describe('getQuizCards', () => {
    it('should fetch quiz cards for today', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve([])
      });

      await cardService.getQuizCards();

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards/quizz'
      );
    });

    it('should fetch quiz cards for specific date', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve([])
      });

      await cardService.getQuizCards('2024-01-15');

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards/quizz?date=2024-01-15'
      );
    });
  });

  describe('answerCard', () => {
    it('should send correct answer', async () => {
      mockFetch.mockResolvedValueOnce({ ok: true });

      await cardService.answerCard('card-123', true);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards/card-123/answer',
        {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ isValid: true })
        }
      );
    });

    it('should send incorrect answer', async () => {
      mockFetch.mockResolvedValueOnce({ ok: true });

      await cardService.answerCard('card-123', false);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/cards/card-123/answer',
        {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ isValid: false })
        }
      );
    });

    it('should throw error when card not found', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404
      });

      await expect(
        cardService.answerCard('non-existent', true)
      ).rejects.toThrow('Carte non trouvée');
    });
  });
});
