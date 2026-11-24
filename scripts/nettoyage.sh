#!/usr/bin/env bash
# Supprime l'application du cluster.
set -uo pipefail
kubectl delete namespace bibliotheque --ignore-not-found
