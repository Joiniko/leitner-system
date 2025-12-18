import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Quiz } from '../components/Quiz';
import { Card } from '../types/Card';

describe('Quiz', () => {
  const mockCards: Card[] = [
    {
      id: '1',
      question: 'What is React?',
      answer: 'A JavaScript library for building user interfaces',
      tag: 'Frontend',
      category: 'FIRST'
    },
    {
      id: '2',
      question: 'What is TypeScript?',
      answer: 'A typed superset of JavaScript',
      category: 'SECOND'
    }
  ];

  it('should display message when no cards', () => {
    render(
      <Quiz 
        cards={[]} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    expect(screen.getByText(/Aucune carte à réviser/)).toBeInTheDocument();
  });

  it('should display first card question', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    expect(screen.getByText('What is React?')).toBeInTheDocument();
  });

  it('should display progress', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    expect(screen.getByText('Carte 1 / 2')).toBeInTheDocument();
  });

  it('should show answer when clicking show button', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    
    expect(screen.getByText('A JavaScript library for building user interfaces')).toBeInTheDocument();
  });

  it('should show correct and incorrect buttons after revealing answer', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    
    expect(screen.getByText(/Correct/)).toBeInTheDocument();
    expect(screen.getByText(/Incorrect/)).toBeInTheDocument();
  });

  it('should call onAnswer with true when correct clicked', async () => {
    const onAnswer = vi.fn().mockResolvedValue(undefined);
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={onAnswer} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    fireEvent.click(screen.getByText(/✓ Correct/));
    
    await waitFor(() => {
      expect(onAnswer).toHaveBeenCalledWith('1', true);
    });
  });

  it('should call onAnswer with false when incorrect clicked', async () => {
    const onAnswer = vi.fn().mockResolvedValue(undefined);
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={onAnswer} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    fireEvent.click(screen.getByText(/✗ Incorrect/));
    
    await waitFor(() => {
      expect(onAnswer).toHaveBeenCalledWith('1', false);
    });
  });

  it('should move to next card after answering', async () => {
    const onAnswer = vi.fn().mockResolvedValue(undefined);
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={onAnswer} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    fireEvent.click(screen.getByText(/✓ Correct/));
    
    await waitFor(() => {
      expect(screen.getByText('What is TypeScript?')).toBeInTheDocument();
      expect(screen.getByText('Carte 2 / 2')).toBeInTheDocument();
    });
  });

  it('should call onComplete after last card', async () => {
    const onComplete = vi.fn();
    const onAnswer = vi.fn().mockResolvedValue(undefined);
    const singleCard = [mockCards[0]];
    
    render(
      <Quiz 
        cards={singleCard} 
        onAnswer={onAnswer} 
        onComplete={onComplete} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    fireEvent.click(screen.getByText(/✓ Correct/));
    
    await waitFor(() => {
      expect(onComplete).toHaveBeenCalled();
    });
  });

  it('should update stats after correct answer', async () => {
    const onAnswer = vi.fn().mockResolvedValue(undefined);
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={onAnswer} 
        onComplete={vi.fn()} 
      />
    );
    
    fireEvent.click(screen.getByText(/Voir la réponse/));
    fireEvent.click(screen.getByText(/✓ Correct/));
    
    await waitFor(() => {
      expect(screen.getByText('✓ 1')).toBeInTheDocument();
    });
  });

  it('should display user answer comparison', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    const textarea = screen.getByPlaceholderText(/Tapez votre réponse/);
    fireEvent.change(textarea, { target: { value: 'My answer' } });
    fireEvent.click(screen.getByText(/Voir la réponse/));
    
    expect(screen.getByText('My answer')).toBeInTheDocument();
  });

  it('should display card tag when present', () => {
    render(
      <Quiz 
        cards={mockCards} 
        onAnswer={vi.fn()} 
        onComplete={vi.fn()} 
      />
    );
    
    expect(screen.getByText(/Frontend/)).toBeInTheDocument();
  });
});
