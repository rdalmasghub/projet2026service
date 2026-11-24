#!/usr/bin/env bash
# Crée un cluster Kubernetes local avec minikube.
#   ./scripts/cluster-minikube.sh
# Istio demande plus de ressources qu'un cluster vide : 4 CPU et 8 Go conseillés.
set -euo pipefail

CPUS="${CPUS:-4}"
MEMORY="${MEMORY:-8192}"

minikube start --cpus="$CPUS" --memory="$MEMORY" --driver=docker

kubectl get nodes
echo
echo "Cluster prêt. Étape suivante : ./scripts/install-istio.sh"
