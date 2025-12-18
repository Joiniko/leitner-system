# language: fr
Fonctionnalité: Création de cartes d'apprentissage
  En tant qu'utilisateur du système de Leitner
  Je veux pouvoir créer des cartes d'apprentissage
  Afin de réviser mes connaissances selon le système de répétition espacée

  Contexte:
    Étant donné que l'application est démarrée
    Et que je suis sur la page d'accueil

  Scénario: Créer une carte avec succès
    Quand je clique sur le bouton "Créer une carte"
    Et je remplis le champ "question" avec "Quelle est la capitale de la France ?"
    Et je remplis le champ "answer" avec "Paris"
    Et je remplis le champ "tag" avec "Géographie"
    Et je clique sur le bouton "Créer la carte"
    Alors je suis redirigé vers la liste des cartes
    Et la carte "Quelle est la capitale de la France ?" apparaît dans la liste
    Et la carte a le tag "Géographie"
    Et la carte est dans la catégorie "FIRST"

  Scénario: Créer une carte sans tag
    Quand je clique sur le bouton "Créer une carte"
    Et je remplis le champ "question" avec "Quel est 2 + 2 ?"
    Et je remplis le champ "answer" avec "4"
    Et je clique sur le bouton "Créer la carte"
    Alors je suis redirigé vers la liste des cartes
    Et la carte "Quel est 2 + 2 ?" apparaît dans la liste

  Scénario: Échec de création sans question
    Quand je clique sur le bouton "Créer une carte"
    Et je remplis le champ "answer" avec "Une réponse"
    Et je clique sur le bouton "Créer la carte"
    Alors un message d'erreur s'affiche pour le champ "question"

  Scénario: Échec de création sans réponse
    Quand je clique sur le bouton "Créer une carte"
    Et je remplis le champ "question" avec "Une question ?"
    Et je clique sur le bouton "Créer la carte"
    Alors un message d'erreur s'affiche pour le champ "answer"

  Scénario: Créer plusieurs cartes
    Quand je crée une carte avec la question "Q1?" et la réponse "R1"
    Et je crée une carte avec la question "Q2?" et la réponse "R2"
    Et je crée une carte avec la question "Q3?" et la réponse "R3"
    Et je vais sur la liste des cartes
    Alors je vois 3 cartes dans la liste
