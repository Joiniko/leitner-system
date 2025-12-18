# 🧠 Système de Leitner - Application d'Apprentissage par Répétition Espacée

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg)](https://www.typescriptlang.org/)

Application web permettant l'apprentissage par répétition espacée selon le système de Leitner.

## 📋 Table des matières

- [Présentation](#-présentation)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Démarrage](#-démarrage)
- [Tests](#-tests)
- [API](#-api)
- [Structure du projet](#-structure-du-projet)
- [Principes SOLID et DDD](#-principes-solid-et-ddd)

## 🎯 Présentation

Le système de Leitner est une méthode d'apprentissage par répétition espacée. Les cartes d'apprentissage passent par 7 catégories avec des fréquences de révision croissantes :

| Catégorie | Fréquence |
|-----------|-----------|
| 1 | Tous les jours |
| 2 | Tous les 2 jours |
| 3 | Tous les 4 jours |
| 4 | Tous les 8 jours |
| 5 | Tous les 16 jours |
| 6 | Tous les 32 jours |
| 7 | Tous les 64 jours |

- ✅ **Bonne réponse** : la carte passe à la catégorie suivante
- ❌ **Mauvaise réponse** : la carte retourne en catégorie 1
- 🎉 **Catégorie 7 réussie** : la carte est définitivement apprise (DONE)

## 🏗 Architecture

Le projet suit une **architecture hexagonale** (Ports & Adapters) avec les principes **DDD** (Domain-Driven Design).

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                            │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │  REST Controller │              │ InMemory Repo   │       │
│  │  (Adapter IN)    │              │ (Adapter OUT)   │       │
│  └────────┬────────┘              └────────▲────────┘       │
│           │                                 │                │
│  ┌────────▼────────┐              ┌────────┴────────┐       │
│  │   Use Cases      │              │  Repository     │       │
│  │   (Ports IN)     │              │  (Ports OUT)    │       │
│  └────────┬────────┘              └────────▲────────┘       │
│           │                                 │                │
│  ┌────────┴─────────────────────────────────┴────────┐      │
│  │                      DOMAIN                        │      │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────┐    │      │
│  │  │  Card    │  │ Category │  │  CardService │    │      │
│  │  │ (Entity) │  │  (Enum)  │  │   (Service)  │    │      │
│  │  └──────────┘  └──────────┘  └──────────────┘    │      │
│  └───────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

📄 Voir [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) pour les diagrammes détaillés.

## 📦 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8+**
- **Node.js 18+** et **npm 9+**

## 🚀 Installation

### Backend (Java/Spring Boot)

```bash
cd backend
mvn clean install
```

### Frontend (React/TypeScript)

```bash
cd frontend
npm install
```

### Tests E2E (Playwright/Cucumber) - Bonus

```bash
cd e2e
npm install
npx playwright install
```

## ▶️ Démarrage

### Démarrer le Backend

```bash
cd backend
mvn spring-boot:run
```

Le serveur démarre sur `http://localhost:8080`

### Démarrer le Frontend

```bash
cd frontend
npm run dev
```

L'application est accessible sur `http://localhost:3000`

### Script de démarrage complet

```bash
# Terminal 1 - Backend
cd backend && mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend && npm run dev
```

## 🧪 Tests

### Tests Backend

```bash
cd backend

# Exécuter tous les tests
mvn test

# Avec rapport de couverture
mvn test jacoco:report

# Voir le rapport de couverture
open target/site/jacoco/index.html
```

### Tests Frontend

```bash
cd frontend

# Exécuter les tests
npm test

# Avec couverture
npm run test:coverage
```

### Tests E2E (Bonus 2)

```bash
# Assurez-vous que le backend et frontend sont démarrés

cd e2e

# Exécuter les tests Cucumber/Playwright
npm test

# Mode headed (visible dans le navigateur)
npm run test:headed
```

### Couverture de tests globale

| Composant | Couverture cible |
|-----------|------------------|
| Domain (Entities, Services) | > 90% |
| Use Cases | > 85% |
| Controllers | > 80% |
| Frontend Components | > 80% |

## 📡 API

L'API REST suit le contrat d'interface Swagger fourni.

### Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/cards` | Liste toutes les cartes (filtre par tags optionnel) |
| `POST` | `/cards` | Crée une nouvelle carte |
| `GET` | `/cards/quizz` | Récupère les cartes du quiz du jour |
| `PATCH` | `/cards/{cardId}/answer` | Enregistre une réponse |

### Exemples

#### Créer une carte

```bash
curl -X POST http://localhost:8080/cards \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Quelle est la capitale de la France ?",
    "answer": "Paris",
    "tag": "Géographie"
  }'
```

#### Récupérer les cartes du quiz

```bash
curl http://localhost:8080/cards/quizz

# Pour une date spécifique
curl http://localhost:8080/cards/quizz?date=2024-01-15
```

#### Répondre à une carte

```bash
# Bonne réponse
curl -X PATCH http://localhost:8080/cards/{cardId}/answer \
  -H "Content-Type: application/json" \
  -d '{"isValid": true}'

# Mauvaise réponse
curl -X PATCH http://localhost:8080/cards/{cardId}/answer \
  -H "Content-Type: application/json" \
  -d '{"isValid": false}'
```

## 📁 Structure du projet

```
leitner-system/
├── backend/
│   ├── src/main/java/com/leitner/
│   │   ├── domain/
│   │   │   ├── model/          # Entités DDD
│   │   │   │   ├── Card.java
│   │   │   │   ├── CardId.java
│   │   │   │   ├── Category.java
│   │   │   │   └── User.java
│   │   │   ├── port/
│   │   │   │   ├── in/         # Ports d'entrée (Use Cases)
│   │   │   │   └── out/        # Ports de sortie (Repositories)
│   │   │   ├── service/        # Services du domaine
│   │   │   └── exception/      # Exceptions métier
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/rest/    # Contrôleurs REST
│   │       │   └── out/persistence/  # Implémentations repositories
│   │       └── config/         # Configuration Spring
│   └── src/test/java/          # Tests unitaires et intégration
│
├── frontend/
│   ├── src/
│   │   ├── components/         # Composants React
│   │   ├── services/           # Services API
│   │   ├── types/              # Types TypeScript
│   │   └── test/               # Tests unitaires
│   └── package.json
│
├── e2e/
│   ├── features/               # Scénarios Gherkin
│   ├── steps/                  # Step definitions Playwright
│   └── package.json
│
├── docs/
│   └── ARCHITECTURE.md         # Diagrammes d'architecture
│
└── README.md
```

## ✨ Principes SOLID et DDD

### SOLID

| Principe | Application |
|----------|-------------|
| **S**ingle Responsibility | Chaque classe a une seule responsabilité (CardService, CardMapper, etc.) |
| **O**pen/Closed | Architecture extensible via les ports (nouveau repository = nouvelle implémentation) |
| **L**iskov Substitution | Les implémentations de repository sont interchangeables |
| **I**nterface Segregation | Use cases séparés (CreateCardUseCase, GetCardsUseCase, etc.) |
| **D**ependency Inversion | Le domaine dépend d'abstractions (ports), pas d'implémentations |

### DDD (Domain-Driven Design)

| Concept | Implémentation |
|---------|----------------|
| **Entités** | `Card`, `User` - identité unique, cycle de vie |
| **Value Objects** | `CardId`, `UserId` - immutables, comparés par valeur |
| **Agrégats** | `Card` est l'agrégat racine |
| **Services de domaine** | `CardService` - logique métier Leitner |
| **Repositories** | `CardRepository` - abstraction de la persistence |
| **Bounded Context** | Module cards isolé avec son propre modèle |

### Architecture Hexagonale

- **Ports d'entrée** : Interfaces use cases appelées par les controllers
- **Ports de sortie** : Interfaces repositories implémentées par l'infrastructure
- **Adapters d'entrée** : REST Controllers (driving adapters)
- **Adapters de sortie** : InMemoryRepository, JPA (driven adapters)

## 🎁 Bonus implémentés

### Bonus 1 : Système basé sur les dates (+1 point)

Le système calcule les révisions basées sur la date de la dernière réponse plutôt que sur un calendrier fixe. Une carte en catégorie 2 sera révisée 2 jours après sa dernière réponse, pas simplement les jours pairs.

### Bonus 2 : Tests E2E Playwright + Gherkin (+2 points)

Tests end-to-end écrits en format Gherkin (Cucumber) avec Playwright :

```gherkin
Scénario: Créer une carte avec succès
  Quand je clique sur le bouton "Créer une carte"
  Et je remplis le champ "question" avec "Quelle est la capitale de la France ?"
  Et je remplis le champ "answer" avec "Paris"
  Et je clique sur le bouton "Créer la carte"
  Alors la carte apparaît dans la liste
```

## 👥 Équipe

Projet réalisé dans le cadre du module d'architecture logicielle.

## 📄 Licence

Projet académique - Tous droits réservés.
