#!/bin/bash

echo "🚀 Démarrage du frontend Leitner System..."
echo ""

# Vérifier que Node.js est installé
if ! command -v node &> /dev/null; then
    echo "❌ Node.js n'est pas installé. Veuillez l'installer."
    exit 1
fi

# Vérifier que npm est installé
if ! command -v npm &> /dev/null; then
    echo "❌ npm n'est pas installé. Veuillez l'installer."
    exit 1
fi

NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
echo "✅ Node.js v$NODE_VERSION détecté"
echo ""

# Démarrer le frontend
cd frontend

# Installer les dépendances si nécessaire
if [ ! -d "node_modules" ]; then
    echo "📦 Installation des dépendances..."
    npm install
fi

echo ""
echo "▶️  Démarrage du serveur de développement..."
echo "   URL: http://localhost:3000"
echo ""
npm run dev
