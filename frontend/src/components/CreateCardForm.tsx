import React, { useState } from 'react';
import { CreateCardRequest } from '../types/Card';

interface Props {
  onSubmit: (data: CreateCardRequest) => Promise<void>;
}

/**
 * Formulaire de création d'une nouvelle carte
 */
export const CreateCardForm: React.FC<Props> = ({ onSubmit }) => {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [tag, setTag] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await onSubmit({
        question: question.trim(),
        answer: answer.trim(),
        tag: tag.trim() || undefined,
      });
      
      // Reset form
      setQuestion('');
      setAnswer('');
      setTag('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="create-card-form">
      <h2>Créer une nouvelle carte</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="form-group">
        <label htmlFor="question">Question *</label>
        <textarea
          id="question"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Entrez votre question..."
          required
          rows={3}
        />
      </div>

      <div className="form-group">
        <label htmlFor="answer">Réponse *</label>
        <textarea
          id="answer"
          value={answer}
          onChange={(e) => setAnswer(e.target.value)}
          placeholder="Entrez la réponse attendue..."
          required
          rows={3}
        />
      </div>

      <div className="form-group">
        <label htmlFor="tag">Tag (optionnel)</label>
        <input
          type="text"
          id="tag"
          value={tag}
          onChange={(e) => setTag(e.target.value)}
          placeholder="Ex: JavaScript, Histoire, etc."
        />
      </div>

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Création...' : 'Créer la carte'}
      </button>
    </form>
  );
};
