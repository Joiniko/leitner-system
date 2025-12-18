# Architecture Hexagonale - Système de Leitner

## Vue d'ensemble

```mermaid
graph TB
    subgraph "Interface Utilisateur"
        WEB[Application Web React]
        MOBILE[Application Mobile - Future]
    end

    subgraph "Couche Infrastructure - Adapters Driving"
        REST[REST Controller<br/>CardController]
        CLI[CLI - Future]
    end

    subgraph "Couche Application - Ports Driving"
        UC1[CreateCardUseCase]
        UC2[GetCardsUseCase]
        UC3[GetQuizCardsUseCase]
        UC4[AnswerCardUseCase]
    end

    subgraph "Couche Domaine"
        SVC[CardService]
        subgraph "Entités"
            CARD[Card]
            CAT[Category]
            USER[User]
        end
        subgraph "Value Objects"
            CID[CardId]
            UID[UserId]
        end
    end

    subgraph "Couche Application - Ports Driven"
        REPO[CardRepository]
        UREPO[UserRepository]
        NOTIF[NotificationService - Future]
    end

    subgraph "Couche Infrastructure - Adapters Driven"
        INMEM[InMemoryCardRepository]
        JPA[JPA Repository - Future]
        EMAIL[Email Service - Future]
    end

    subgraph "Stockage"
        DB[(Base de données)]
    end

    WEB --> REST
    MOBILE -.-> REST
    REST --> UC1
    REST --> UC2
    REST --> UC3
    REST --> UC4
    
    UC1 --> SVC
    UC2 --> SVC
    UC3 --> SVC
    UC4 --> SVC
    
    SVC --> CARD
    SVC --> CAT
    CARD --> CID
    USER --> UID
    
    SVC --> REPO
    SVC -.-> UREPO
    SVC -.-> NOTIF
    
    REPO --> INMEM
    REPO -.-> JPA
    UREPO -.-> JPA
    NOTIF -.-> EMAIL
    
    JPA -.-> DB
    INMEM --> DB

    style WEB fill:#e1f5fe
    style REST fill:#fff3e0
    style SVC fill:#e8f5e9
    style CARD fill:#f3e5f5
    style INMEM fill:#fce4ec
```

## Architecture en couches détaillée

```mermaid
graph LR
    subgraph "INFRASTRUCTURE (Externe)"
        subgraph "Adapters IN (Driving)"
            A1[REST API]
            A2[CLI]
            A3[GraphQL]
        end
        
        subgraph "Adapters OUT (Driven)"
            B1[InMemory Repository]
            B2[JPA Repository]
            B3[Notification Service]
        end
    end

    subgraph "APPLICATION"
        subgraph "Ports IN"
            P1[CreateCardUseCase]
            P2[GetCardsUseCase]
            P3[GetQuizCardsUseCase]
            P4[AnswerCardUseCase]
        end
        
        subgraph "Ports OUT"
            P5[CardRepository]
            P6[UserRepository]
            P7[NotificationPort]
        end
    end

    subgraph "DOMAIN (Cœur métier)"
        D1[Card Entity]
        D2[Category Enum]
        D3[User Entity]
        D4[CardService]
        D5[Leitner Rules]
    end

    A1 --> P1
    A1 --> P2
    A1 --> P3
    A1 --> P4
    
    P1 --> D4
    P2 --> D4
    P3 --> D4
    P4 --> D4
    
    D4 --> D1
    D4 --> D2
    D4 --> D5
    
    D4 --> P5
    D4 --> P6
    
    P5 --> B1
    P5 --> B2
    P7 --> B3

    style D1 fill:#c8e6c9
    style D2 fill:#c8e6c9
    style D4 fill:#c8e6c9
    style D5 fill:#c8e6c9
```

## Flux de données

```mermaid
sequenceDiagram
    participant U as Utilisateur
    participant C as Controller
    participant UC as UseCase
    participant S as Service
    participant E as Entity
    participant R as Repository

    Note over U,R: Création d'une carte
    U->>C: POST /cards {question, answer, tag}
    C->>UC: CreateCardCommand
    UC->>S: execute(command)
    S->>E: Card.create(question, answer, tag)
    E-->>S: Card (category=FIRST)
    S->>R: save(card)
    R-->>S: Card saved
    S-->>UC: Card
    UC-->>C: Card
    C-->>U: 201 Created {card}

    Note over U,R: Réponse à une carte
    U->>C: PATCH /cards/{id}/answer {isValid: true}
    C->>UC: AnswerCardCommand
    UC->>S: execute(command)
    S->>R: findById(cardId)
    R-->>S: Card
    S->>E: card.answerCorrectly()
    E->>E: category = category.next()
    S->>R: save(card)
    R-->>S: OK
    S-->>UC: void
    UC-->>C: void
    C-->>U: 204 No Content
```

## Règles métier du système de Leitner

```mermaid
stateDiagram-v2
    [*] --> FIRST: Nouvelle carte
    
    FIRST --> SECOND: Bonne réponse
    SECOND --> THIRD: Bonne réponse
    THIRD --> FOURTH: Bonne réponse
    FOURTH --> FIFTH: Bonne réponse
    FIFTH --> SIXTH: Bonne réponse
    SIXTH --> SEVENTH: Bonne réponse
    SEVENTH --> DONE: Bonne réponse
    
    SECOND --> FIRST: Mauvaise réponse
    THIRD --> FIRST: Mauvaise réponse
    FOURTH --> FIRST: Mauvaise réponse
    FIFTH --> FIRST: Mauvaise réponse
    SIXTH --> FIRST: Mauvaise réponse
    SEVENTH --> FIRST: Mauvaise réponse
    
    DONE --> [*]: Carte apprise

    note right of FIRST
        Fréquence: 1 jour
    end note
    
    note right of SECOND
        Fréquence: 2 jours
    end note
    
    note right of SEVENTH
        Fréquence: 64 jours
    end note
```

## Fréquences de révision

| Catégorie | Fréquence | Description |
|-----------|-----------|-------------|
| FIRST | 1 jour | Révision quotidienne |
| SECOND | 2 jours | Tous les 2 jours |
| THIRD | 4 jours | Tous les 4 jours |
| FOURTH | 8 jours | Toutes les semaines |
| FIFTH | 16 jours | Toutes les 2 semaines |
| SIXTH | 32 jours | Tous les mois |
| SEVENTH | 64 jours | Tous les 2 mois |
| DONE | - | Carte définitivement apprise |
