import React, { useState } from 'react';
import { Card, CATEGORY_LABELS } from '../types/Card';

interface Props {
  cards: Card[];
  onAnswer: (cardId: string, isValid: boolean) => Promise<void>;
  onComplete: () => void;
}

/**
 * Interface de quiz pour répondre aux cartes
 */
export const Quiz: React.FC<Props> = ({ cards, onAnswer, onComplete }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showAnswer, setShowAnswer] = useState(false);
  const [userAnswer, setUserAnswer] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [results, setResults] = useState<{ correct: number; incorrect: number }>({
    correct: 0,
    incorrect: 0,
  });

  const currentCard = cards[currentIndex];
  const isLastCard = currentIndex === cards.length - 1;

  if (cards.length === 0) {
    return (
      <div className="quiz-complete">
        <h2>🎉 Aucune carte à réviser aujourd'hui !</h2>
        <p>Revenez demain ou créez de nouvelles cartes.</p>
      </div>
    );
  }

  const handleShowAnswer = () => {
    setShowAnswer(true);
  };

  const handleAnswer = async (isValid: boolean) => {
    setIsSubmitting(true);
    try {
      await onAnswer(currentCard.id, isValid);
      
      setResults(prev => ({
        correct: prev.correct + (isValid ? 1 : 0),
        incorrect: prev.incorrect + (isValid ? 0 : 1),
      }));

      if (isLastCard) {
        onComplete();
      } else {
        setCurrentIndex(prev => prev + 1);
        setShowAnswer(false);
        setUserAnswer('');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const progress = ((currentIndex + 1) / cards.length) * 100;

  return (
    <div className="quiz">
      <div className="quiz-progress">
        <div className="progress-bar">
          <div className="progress-fill" style={{ width: `${progress}%` }} />
        </div>
        <span className="progress-text">
          Carte {currentIndex + 1} / {cards.length}
        </span>
      </div>

      <div className="quiz-stats">
        <span className="correct">✓ {results.correct}</span>
        <span className="incorrect">✗ {results.incorrect}</span>
      </div>

      <div className="quiz-card">
        <div className="card-category">
          {CATEGORY_LABELS[currentCard.category]}
        </div>
        
        {currentCard.tag && (
          <div className="card-tag">Tag: {currentCard.tag}</div>
        )}

        <div className="question-section">
          <h3>Question</h3>
          <p className="question-text">{currentCard.question}</p>
        </div>

        {!showAnswer ? (
          <div className="answer-input-section">
            <label htmlFor="user-answer">Votre réponse :</label>
            <textarea
              id="user-answer"
              value={userAnswer}
              onChange={(e) => setUserAnswer(e.target.value)}
              placeholder="Tapez votre réponse..."
              rows={3}
            />
            <button onClick={handleShowAnswer} className="btn-show-answer">
              Voir la réponse
            </button>
          </div>
        ) : (
          <div className="answer-section">
            <h3>Réponse attendue</h3>
            <p className="answer-text">{currentCard.answer}</p>

            {userAnswer && (
              <div className="user-answer-comparison">
                <h4>Votre réponse :</h4>
                <p>{userAnswer}</p>
              </div>
            )}

            <div className="answer-buttons">
              <button
                onClick={() => handleAnswer(false)}
                disabled={isSubmitting}
                className="btn-incorrect"
              >
                ✗ Incorrect
              </button>
              <button
                onClick={() => handleAnswer(true)}
                disabled={isSubmitting}
                className="btn-correct"
              >
                ✓ Correct
              </button>
            </div>
            <p className="hint">
              Vous pouvez valider même si la formulation diffère légèrement
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
