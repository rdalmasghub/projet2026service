# syntax=docker/dockerfile:1
# Image Docker de book-service :
#   docker build -t <pseudo-docker-hub>/bibliotheque-book-service:1.0.0 book-service
#
# Build "multi-étapes" : l'étape 1 compile avec un JDK complet, l'étape 2 ne garde que le JRE
# et le jar. Aucune installation de Java n'est nécessaire sur la machine qui construit l'image.

# ---------- Étape 1 : compilation (JDK + Gradle), jetée après le build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
# Téléchargement des dépendances dans une couche à part : elle reste en cache tant que build.gradle ne change pas
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# ---------- Étape 2 : image d'exécution minimale ----------
FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.title="bibliotheque-book-service" \
      org.opencontainers.image.description="Catalogue de livres : API REST sur le port 8080"
# Correctifs de sécurité des paquets Alpine publiés depuis la sortie de l'image de base
RUN apk upgrade --no-cache
# Utilisateur sans privilèges : le conteneur ne tourne jamais en root
RUN addgroup -S -g 10001 app && adduser -S -D -H -u 10001 -G app app
WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/build/libs/app.jar app.jar
USER 10001:10001
EXPOSE 8080
# MaxRAMPercentage : la JVM s'adapte à la limite mémoire du conteneur (resources.limits dans Kubernetes)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/app.jar"]
