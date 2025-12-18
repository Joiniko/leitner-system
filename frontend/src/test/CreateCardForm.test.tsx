import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CreateCardForm } from '../components/CreateCardForm';

describe('CreateCardForm', () => {
  it('should render form fields', () => {
    render(<CreateCardForm onSubmit={vi.fn()} />);

    expect(screen.getByLabelText(/question/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/réponse/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/tag/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /créer/i })).toBeInTheDocument();
  });

  it('should call onSubmit with form data', async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<CreateCardForm onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText(/question/i), {
      target: { value: 'Test question?' }
    });
    fireEvent.change(screen.getByLabelText(/réponse/i), {
      target: { value: 'Test answer' }
    });
    fireEvent.change(screen.getByLabelText(/tag/i), {
      target: { value: 'TestTag' }
    });

    fireEvent.click(screen.getByRole('button', { name: /créer/i }));

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({
        question: 'Test question?',
        answer: 'Test answer',
        tag: 'TestTag'
      });
    });
  });

  it('should reset form after successful submission', async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<CreateCardForm onSubmit={onSubmit} />);

    const questionInput = screen.getByLabelText(/question/i);
    const answerInput = screen.getByLabelText(/réponse/i);

    fireEvent.change(questionInput, { target: { value: 'Question?' } });
    fireEvent.change(answerInput, { target: { value: 'Answer' } });
    fireEvent.click(screen.getByRole('button', { name: /créer/i }));

    await waitFor(() => {
      expect(questionInput).toHaveValue('');
      expect(answerInput).toHaveValue('');
    });
  });

  it('should display error on submission failure', async () => {
    const onSubmit = vi.fn().mockRejectedValue(new Error('Network error'));
    render(<CreateCardForm onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText(/question/i), {
      target: { value: 'Question?' }
    });
    fireEvent.change(screen.getByLabelText(/réponse/i), {
      target: { value: 'Answer' }
    });
    fireEvent.click(screen.getByRole('button', { name: /créer/i }));

    await waitFor(() => {
      expect(screen.getByText(/network error/i)).toBeInTheDocument();
    });
  });

  it('should submit without tag if empty', async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<CreateCardForm onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText(/question/i), {
      target: { value: 'Question?' }
    });
    fireEvent.change(screen.getByLabelText(/réponse/i), {
      target: { value: 'Answer' }
    });
    // Don't fill tag

    fireEvent.click(screen.getByRole('button', { name: /créer/i }));

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({
        question: 'Question?',
        answer: 'Answer',
        tag: undefined
      });
    });
  });
});
