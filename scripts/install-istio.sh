#!/usr/bin/env bash
# Installe le service mesh Istio dans le cluster (istiod + ingress gateway).
#   ./scripts/install-istio.sh
# istioctl est téléchargé dans le dossier du projet s'il n'est pas déjà installé.
set -euo pipefail
cd "$(dirname "$0")/.."

ISTIO_VERSION="${ISTIO_VERSION:-1.31.4}"

if ! command -v istioctl >/dev/null 2>&1; then
  if [ ! -x "istio-${ISTIO_VERSION}/bin/istioctl" ]; then
    echo "==> Téléchargement d'Istio ${ISTIO_VERSION}"
    curl -sL https://istio.io/downloadIstio | ISTIO_VERSION="$ISTIO_VERSION" sh -
  fi
  export PATH="$PWD/istio-${ISTIO_VERSION}/bin:$PATH"
fi

istioctl x precheck
istioctl install -f k8s/istio/istio-install.yaml -y

kubectl -n istio-system rollout status deployment/istiod --timeout=300s
kubectl -n istio-system rollout status deployment/istio-ingressgateway --timeout=300s
kubectl -n istio-system get pods,svc

echo
echo "Istio installé. Étape suivante : ./scripts/build-image.sh"
