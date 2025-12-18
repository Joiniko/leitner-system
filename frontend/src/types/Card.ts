/**
 * Types correspondant au contrat d'interface Swagger
 */

export type Category = 
  | 'FIRST' 
  | 'SECOND' 
  | 'THIRD' 
  | 'FOURTH' 
  | 'FIFTH' 
  | 'SIXTH' 
  | 'SEVENTH' 
  | 'DONE';

export interface Card {
  id: string;
  question: string;
  answer: string;
  tag?: string;
  category: Category;
}

export interface CreateCardRequest {
  question: string;
  answer: string;
  tag?: string;
}

export interface AnswerRequest {
  isValid: boolean;
}

/**
 * Fréquences de révision selon le système de Leitner
 */
export const CATEGORY_FREQUENCIES: Record<Category, number> = {
  FIRST: 1,
  SECOND: 2,
  THIRD: 4,
  FOURTH: 8,
  FIFTH: 16,
  SIXTH: 32,
  SEVENTH: 64,
  DONE: Infinity
};

/**
 * Labels français pour les catégories
 */
export const CATEGORY_LABELS: Record<Category, string> = {
  FIRST: 'Catégorie 1 (tous les jours)',
  SECOND: 'Catégorie 2 (tous les 2 jours)',
  THIRD: 'Catégorie 3 (tous les 4 jours)',
  FOURTH: 'Catégorie 4 (tous les 8 jours)',
  FIFTH: 'Catégorie 5 (tous les 16 jours)',
  SIXTH: 'Catégorie 6 (tous les 32 jours)',
  SEVENTH: 'Catégorie 7 (tous les 64 jours)',
  DONE: 'Apprise ✓'
};
