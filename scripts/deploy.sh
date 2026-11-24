#!/usr/bin/env bash
# Déploie book-service dans Kubernetes (namespace "bibliotheque") : Deployment + Service.
#   DOCKERHUB_USER=monpseudo ./scripts/deploy.sh
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && set -a && . ./.env && set +a

DOCKERHUB_USER="${DOCKERHUB_USER:?Définissez DOCKERHUB_USER (votre pseudo Docker Hub) ou un fichier .env}"
TAG="${TAG:-1.0.0}"

echo "==> Manifests (image : docker.io/$DOCKERHUB_USER/bibliotheque-book-service:$TAG)"
kubectl kustomize k8s \
  | sed -e "s#docker.io/votre-pseudo/#docker.io/${DOCKERHUB_USER}/#g" \
        -e "s#\(docker.io/${DOCKERHUB_USER}/bibliotheque-[a-z-]*\):1.0.0#\1:${TAG}#g" \
  | kubectl apply -f -

echo "==> Attente du démarrage des pods"
kubectl -n bibliotheque rollout status deployment/book-service --timeout=600s
kubectl -n bibliotheque get deployment,pods,service -o wide

cat <<'EOF'

book-service est déployé. Pour l'appeler :
  minikube : minikube service book-service -n bibliotheque --url
  partout  : kubectl -n bibliotheque port-forward svc/book-service 8080:8080
             puis http://localhost:8080/api/books
Tests : ./scripts/test.sh
EOF
