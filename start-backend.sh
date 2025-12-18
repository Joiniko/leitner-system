#!/bin/bash

echo "🚀 Démarrage du backend Leitner System..."
echo ""

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez l'installer."
    exit 1
fi

# Vérifier que Java est installé
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java 17+."
    exit 1
fi

# Vérifier la version de Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ est requis. Version actuelle: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION détecté"
echo ""

# Démarrer le backend
cd backend
echo "📦 Installation des dépendances..."
mvn clean install -DskipTests -q

echo ""
echo "▶️  Démarrage du serveur Spring Boot..."
echo "   URL: http://localhost:8080"
echo ""
mvn spring-boot:run
