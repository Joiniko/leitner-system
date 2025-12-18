import React, { useState, useEffect, useCallback } from 'react';
import { Card } from './types/Card';
import { cardService } from './services/cardService';
import { CreateCardForm } from './components/CreateCardForm';
import { CardList } from './components/CardList';
import { Quiz } from './components/Quiz';
import './App.css';

type View = 'home' | 'create' | 'quiz' | 'cards';

/**
 * Application principale du système de Leitner
 */
const App: React.FC = () => {
  const [view, setView] = useState<View>('home');
  const [cards, setCards] = useState<Card[]>([]);
  const [quizCards, setQuizCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [tagFilter, setTagFilter] = useState('');
  const [quizCompleted, setQuizCompleted] = useState(false);

  const loadCards = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const tags = tagFilter ? tagFilter.split(',').map(t => t.trim()) : undefined;
      const data = await cardService.getCards(tags);
      setCards(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur de chargement');
    } finally {
      setLoading(false);
    }
  }, [tagFilter]);

  const loadQuizCards = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await cardService.getQuizCards();
      setQuizCards(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur de chargement');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (view === 'cards') {
      loadCards();
    } else if (view === 'quiz') {
      loadQuizCards();
    }
  }, [view, loadCards, loadQuizCards]);

  const handleCreateCard = async (data: { question: string; answer: string; tag?: string }) => {
    await cardService.createCard(data);
    setView('cards');
  };

  const handleAnswer = async (cardId: string, isValid: boolean) => {
    await cardService.answerCard(cardId, isValid);
  };

  const handleQuizComplete = () => {
    setQuizCompleted(true);
  };

  const renderContent = () => {
    if (loading) {
      return <div className="loading">Chargement...</div>;
    }

    if (error) {
      return <div className="error">{error}</div>;
    }

    switch (view) {
      case 'create':
        return <CreateCardForm onSubmit={handleCreateCard} />;
      
      case 'quiz':
        if (quizCompleted) {
          return (
            <div className="quiz-complete">
              <h2>🎉 Quiz terminé !</h2>
              <p>Bravo, vous avez révisé toutes vos cartes du jour.</p>
              <button onClick={() => { setQuizCompleted(false); setView('home'); }}>
                Retour à l'accueil
              </button>
            </div>
          );
        }
        return (
          <Quiz
            cards={quizCards}
            onAnswer={handleAnswer}
            onComplete={handleQuizComplete}
          />
        );
      
      case 'cards':
        return (
          <div>
            <div className="filter-section">
              <input
                type="text"
                value={tagFilter}
                onChange={(e) => setTagFilter(e.target.value)}
                placeholder="Filtrer par tags (séparés par des virgules)"
              />
              <button onClick={loadCards}>Filtrer</button>
            </div>
            <CardList
              cards={cards}
              title="Toutes les cartes"
              emptyMessage="Aucune carte. Créez-en une !"
            />
          </div>
        );
      
      default:
        return (
          <div className="home">
            <h1>🧠 Système de Leitner</h1>
            <p className="subtitle">Apprenez efficacement grâce à la répétition espacée</p>
            
            <div className="home-actions">
              <button onClick={() => setView('quiz')} className="btn-primary">
                📝 Commencer le Quiz du jour
              </button>
              <button onClick={() => setView('create')} className="btn-secondary">
                ➕ Créer une carte
              </button>
              <button onClick={() => setView('cards')} className="btn-secondary">
                📚 Voir toutes les cartes
              </button>
            </div>

            <div className="info-box">
              <h3>Comment ça marche ?</h3>
              <ol>
                <li>Créez des cartes avec une question et une réponse</li>
                <li>Révisez quotidiennement avec le quiz</li>
                <li>Les cartes réussies passent en catégorie supérieure</li>
                <li>Les cartes ratées reviennent en catégorie 1</li>
                <li>Après 7 catégories réussies, la carte est apprise !</li>
              </ol>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="app">
      <nav className="navbar">
        <span className="logo" onClick={() => setView('home')}>🧠 Leitner</span>
        <div className="nav-links">
          <button onClick={() => setView('home')}>Accueil</button>
          <button onClick={() => setView('quiz')}>Quiz</button>
          <button onClick={() => setView('create')}>Créer</button>
          <button onClick={() => setView('cards')}>Cartes</button>
        </div>
      </nav>

      <main className="main-content">
        {renderContent()}
      </main>

      <footer className="footer">
        <p>Système de Leitner - Apprentissage par répétition espacée</p>
      </footer>
    </div>
  );
};

export default App;
