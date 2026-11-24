#!/usr/bin/env bash
# Publie l'image sur le Docker Hub.
#   DOCKERHUB_USER=monpseudo ./scripts/push-dockerhub.sh
#
# Connectez-vous avec un "Personal Access Token" (Docker Hub > Account settings > Personal access
# tokens) plutôt qu'avec votre mot de passe, et activez la double authentification.
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && set -a && . ./.env && set +a

DOCKERHUB_USER="${DOCKERHUB_USER:?Définissez DOCKERHUB_USER (votre pseudo Docker Hub) ou un fichier .env}"
TAG="${TAG:-1.0.0}"

if [ -n "${DOCKERHUB_TOKEN:-}" ]; then
  echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
else
  docker login -u "$DOCKERHUB_USER"   # saisissez votre access token quand le mot de passe est demandé
fi

docker push "docker.io/${DOCKERHUB_USER}/bibliotheque-book-service:${TAG}"

echo
echo "Image publiée : https://hub.docker.com/r/${DOCKERHUB_USER}/bibliotheque-book-service"
echo "Étape suivante : ./scripts/deploy.sh"
