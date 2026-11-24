#!/usr/bin/env bash
# Construit l'image Docker de book-service (build multi-étapes : ni Java ni Gradle à installer).
#   DOCKERHUB_USER=monpseudo ./scripts/build-image.sh
# Option LOAD=minikube|kind : charge aussi l'image dans le cluster local (pas besoin de Docker Hub).
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && set -a && . ./.env && set +a

DOCKERHUB_USER="${DOCKERHUB_USER:?Définissez DOCKERHUB_USER (votre pseudo Docker Hub) ou un fichier .env}"
TAG="${TAG:-1.0.0}"
IMAGE="docker.io/${DOCKERHUB_USER}/bibliotheque-book-service:${TAG}"

echo "==> $IMAGE"
docker build -t "$IMAGE" book-service

case "${LOAD:-}" in
  minikube) minikube image load "$IMAGE" ;;
  kind)     kind load docker-image "$IMAGE" ;;
esac

docker images | grep bibliotheque-book-service
echo
echo "Essai en local : docker run --rm -p 8080:8080 $IMAGE   puis http://localhost:8080/api/books"
echo "Étape suivante : ./scripts/push-dockerhub.sh  (ou LOAD=minikube pour rester en local)"
