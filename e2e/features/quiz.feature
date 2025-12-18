# language: fr
Fonctionnalité: Quiz quotidien
  En tant qu'utilisateur du système de Leitner
  Je veux pouvoir passer un quiz quotidien
  Afin de réviser mes cartes selon le système de répétition espacée

  Contexte:
    Étant donné que l'application est démarrée
    Et que des cartes existent dans le système

  Scénario: Démarrer un quiz
    Quand je clique sur "Commencer le Quiz du jour"
    Alors je vois la première carte du quiz
    Et je vois la progression "Carte 1"

  Scénario: Répondre correctement à une carte
    Étant donné que je suis dans le quiz
    Et je vois une question
    Quand je tape ma réponse
    Et je clique sur "Voir la réponse"
    Et je clique sur "Correct"
    Alors la carte passe à la catégorie suivante
    Et le compteur de bonnes réponses augmente

  Scénario: Répondre incorrectement à une carte
    Étant donné que je suis dans le quiz
    Et je vois une question
    Quand je clique sur "Voir la réponse"
    Et je clique sur "Incorrect"
    Alors la carte retourne en catégorie "FIRST"
    Et le compteur de mauvaises réponses augmente

  Scénario: Comparer ma réponse avec la bonne réponse
    Étant donné que je suis dans le quiz
    Quand je tape "ma réponse personnelle" dans le champ de réponse
    Et je clique sur "Voir la réponse"
    Alors je vois ma réponse "ma réponse personnelle"
    Et je vois la réponse attendue

  Scénario: Forcer la validation malgré une formulation différente
    Étant donné que je suis dans le quiz
    Et la réponse attendue est "Domain Driven Design"
    Quand je tape "DDD - Domain-Driven Design" dans le champ de réponse
    Et je clique sur "Voir la réponse"
    Et je clique sur "Correct"
    Alors la carte est validée

  Scénario: Terminer le quiz
    Étant donné que je suis dans le quiz avec une seule carte
    Quand je réponds à la carte
    Alors je vois le message "Quiz terminé"
    Et je peux retourner à l'accueil

  Scénario: Aucune carte à réviser
    Étant donné qu'aucune carte n'est à réviser aujourd'hui
    Quand je clique sur "Commencer le Quiz du jour"
    Alors je vois le message "Aucune carte à réviser"
