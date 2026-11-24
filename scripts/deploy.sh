#!/usr/bin/env bash
# Déploie l'application dans Kubernetes (namespace "bibliotheque") :
# Deployment + Service de book-service, Gateway et règles de routage Istio.
#   DOCKERHUB_USER=monpseudo ./scripts/deploy.sh
# Prérequis : Istio installé (./scripts/install-istio.sh).
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && set -a && . ./.env && set +a

DOCKERHUB_USER="${DOCKERHUB_USER:?Définissez DOCKERHUB_USER (votre pseudo Docker Hub) ou un fichier .env}"
TAG="${TAG:-1.0.0}"

if ! kubectl get crd gateways.networking.istio.io >/dev/null 2>&1; then
  echo "Istio n'est pas installé dans ce cluster : lancez d'abord ./scripts/install-istio.sh" >&2
  exit 1
fi

echo "==> Manifests (image : docker.io/$DOCKERHUB_USER/bibliotheque-book-service:$TAG)"
kubectl kustomize k8s \
  | sed -e "s#docker.io/votre-pseudo/#docker.io/${DOCKERHUB_USER}/#g" \
        -e "s#\(docker.io/${DOCKERHUB_USER}/bibliotheque-[a-z-]*\):1.0.0#\1:${TAG}#g" \
  | kubectl apply -f -

echo "==> Attente du démarrage des pods"
kubectl -n bibliotheque rollout status deployment/book-service --timeout=600s
kubectl -n bibliotheque get deployment,pods,service -o wide

cat <<'EOF'

Application déployée (pods 2/2 = book-service + proxy Istio). On y accède par la gateway Istio :
  kubectl -n istio-system port-forward svc/istio-ingressgateway 8080:80
  puis http://localhost:8080/api/books   et   ./scripts/test.sh
(avec `minikube tunnel` : http://<EXTERNAL-IP de svc/istio-ingressgateway>/api/books)
EOF
