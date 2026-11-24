#!/usr/bin/env bash
# Crée un cluster Kubernetes local avec minikube.
#   ./scripts/cluster-minikube.sh
set -euo pipefail

CPUS="${CPUS:-2}"
MEMORY="${MEMORY:-4096}"

minikube start --cpus="$CPUS" --memory="$MEMORY" --driver=docker

kubectl get nodes
echo
echo "Cluster prêt. Étape suivante : ./scripts/build-image.sh"
