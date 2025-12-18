#!/bin/bash

echo "🧪 Exécution de tous les tests du système Leitner"
echo "=================================================="
echo ""

FAILED=0

# Tests Backend
echo "📦 Tests Backend (Java/Spring Boot)"
echo "------------------------------------"
cd backend
mvn test -q
if [ $? -ne 0 ]; then
    echo "❌ Tests backend échoués"
    FAILED=1
else
    echo "✅ Tests backend réussis"
fi

# Rapport de couverture backend
echo ""
echo "📊 Génération du rapport de couverture backend..."
mvn jacoco:report -q
echo "   Rapport: backend/target/site/jacoco/index.html"

cd ..

echo ""
echo "🎨 Tests Frontend (React/TypeScript)"
echo "-------------------------------------"
cd frontend

# Installer les dépendances si nécessaire
if [ ! -d "node_modules" ]; then
    npm install -q
fi

npm run test:coverage -- --run
if [ $? -ne 0 ]; then
    echo "❌ Tests frontend échoués"
    FAILED=1
else
    echo "✅ Tests frontend réussis"
fi

cd ..

echo ""
echo "=================================================="
if [ $FAILED -eq 0 ]; then
    echo "✅ Tous les tests sont passés avec succès!"
else
    echo "❌ Certains tests ont échoué"
    exit 1
fi
