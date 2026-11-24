#!/usr/bin/env bash
# Supprime l'application du cluster (et, avec ISTIO=1, Istio lui-même).
#   ./scripts/nettoyage.sh          ISTIO=1 ./scripts/nettoyage.sh
set -uo pipefail
kubectl delete namespace bibliotheque --ignore-not-found
if [ "${ISTIO:-0}" = "1" ]; then
  istioctl uninstall --purge -y
  kubectl delete namespace istio-system --ignore-not-found
fi
