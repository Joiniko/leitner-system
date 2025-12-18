import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { CardList } from '../components/CardList';
import { Card } from '../types/Card';

describe('CardList', () => {
  const mockCards: Card[] = [
    {
      id: '1',
      question: 'What is TDD?',
      answer: 'Test Driven Development',
      tag: 'Development',
      category: 'FIRST'
    },
    {
      id: '2',
      question: 'What is DDD?',
      answer: 'Domain Driven Design',
      tag: 'Architecture',
      category: 'THIRD'
    }
  ];

  it('should render title', () => {
    render(<CardList cards={[]} title="Test Cards" />);
    expect(screen.getByText('Test Cards')).toBeInTheDocument();
  });

  it('should display empty message when no cards', () => {
    render(
      <CardList 
        cards={[]} 
        title="Cards" 
        emptyMessage="No cards available" 
      />
    );
    expect(screen.getByText('No cards available')).toBeInTheDocument();
  });

  it('should render all cards', () => {
    render(<CardList cards={mockCards} title="Cards" />);
    
    expect(screen.getByText('What is TDD?')).toBeInTheDocument();
    expect(screen.getByText('What is DDD?')).toBeInTheDocument();
  });

  it('should display card categories', () => {
    render(<CardList cards={mockCards} title="Cards" />);
    
    expect(screen.getByText(/Catégorie 1/)).toBeInTheDocument();
    expect(screen.getByText(/Catégorie 3/)).toBeInTheDocument();
  });

  it('should display card tags', () => {
    render(<CardList cards={mockCards} title="Cards" />);
    
    expect(screen.getByText('Development')).toBeInTheDocument();
    expect(screen.getByText('Architecture')).toBeInTheDocument();
  });

  it('should handle cards without tags', () => {
    const cardsWithoutTags: Card[] = [
      {
        id: '1',
        question: 'Question?',
        answer: 'Answer',
        category: 'FIRST'
      }
    ];

    render(<CardList cards={cardsWithoutTags} title="Cards" />);
    expect(screen.getByText('Question?')).toBeInTheDocument();
  });

  it('should display DONE category correctly', () => {
    const doneCard: Card[] = [
      {
        id: '1',
        question: 'Mastered question',
        answer: 'Answer',
        category: 'DONE'
      }
    ];

    render(<CardList cards={doneCard} title="Cards" />);
    expect(screen.getByText(/Apprise/)).toBeInTheDocument();
  });
});
