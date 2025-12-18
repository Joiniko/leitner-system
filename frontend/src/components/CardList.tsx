import React from 'react';
import { Card, CATEGORY_LABELS } from '../types/Card';

interface Props {
  cards: Card[];
  title: string;
  emptyMessage?: string;
}

/**
 * Liste des cartes avec leur catégorie
 */
export const CardList: React.FC<Props> = ({ 
  cards, 
  title, 
  emptyMessage = 'Aucune carte' 
}) => {
  return (
    <div className="card-list">
      <h2>{title}</h2>
      
      {cards.length === 0 ? (
        <p className="empty-message">{emptyMessage}</p>
      ) : (
        <ul>
          {cards.map((card) => (
            <li key={card.id} className="card-item">
              <div className="card-question">{card.question}</div>
              <div className="card-meta">
                <span className={`category category-${card.category.toLowerCase()}`}>
                  {CATEGORY_LABELS[card.category]}
                </span>
                {card.tag && <span className="tag">{card.tag}</span>}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};
