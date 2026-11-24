#!/usr/bin/env bash
# Teste l'API REST de book-service de bout en bout.
#   ./scripts/test.sh                          # http://localhost:8080 : port-forward de la gateway,
#                                              # bootRun ou docker run
#   BASE_URL=http://<EXTERNAL-IP> ./scripts/test.sh    # avec `minikube tunnel`
set -uo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
CURL=(curl -s --max-time 15)
echecs=0

check() { # check <description> <attendu> <obtenu>
  if [ "$2" = "$3" ]; then printf '  \033[32m✔\033[0m %s\n' "$1"; else printf '  \033[31m✘\033[0m %s (attendu %s, obtenu %s)\n' "$1" "$2" "$3"; echecs=$((echecs+1)); fi
}
code() { "${CURL[@]}" -o /dev/null -w '%{http_code}' "$@"; }
json() { python3 -c "import json,sys; d=json.load(sys.stdin); print($1)"; }

echo "Tests sur $BASE_URL"
check "liste des livres"                 200 "$(code "$BASE_URL/api/books")"
check "recherche « hugo »"               "Les Misérables" "$("${CURL[@]}" "$BASE_URL/api/books?q=hugo" | json "d[0]['title']")"
check "livre inexistant -> 404"          404 "$(code "$BASE_URL/api/books/999999")"
check "création invalide -> 400"         400 "$(code -X POST -H 'Content-Type: application/json' -d '{"title":""}' "$BASE_URL/api/books")"

isbn="979$(date +%s)"
body="{\"isbn\":\"$isbn\",\"title\":\"Livre de test\",\"author\":\"Script\",\"totalCopies\":1}"
book_id=$("${CURL[@]}" -X POST -H 'Content-Type: application/json' -d "$body" "$BASE_URL/api/books" | json "d['id']")
check "création d'un livre"              yes "$([ -n "$book_id" ] && echo yes)"
check "ISBN déjà utilisé -> 409"         409 "$(code -X POST -H 'Content-Type: application/json' -d "$body" "$BASE_URL/api/books")"
check "modification (3 exemplaires)"     3 "$("${CURL[@]}" -X PUT -H 'Content-Type: application/json' \
  -d "${body/\"totalCopies\":1/\"totalCopies\":3}" "$BASE_URL/api/books/$book_id" | json "d['availableCopies']")"
check "suppression"                      204 "$(code -X DELETE "$BASE_URL/api/books/$book_id")"

# Derrière la gateway Istio, les réponses viennent du proxy Envoy et /actuator n'est pas exposé
if "${CURL[@]}" -D - -o /dev/null "$BASE_URL/api/books" | grep -qi '^server: istio-envoy'; then
  echo "  (requêtes passées par la gateway Istio)"
  check "/actuator non exposé par la gateway"  404 "$(code "$BASE_URL/actuator/health")"
else
  check "santé (Actuator)"               UP "$("${CURL[@]}" "$BASE_URL/actuator/health" | json "d['status']")"
fi

echo
echo "Pod qui a répondu (en-tête X-Served-By sur 6 appels) :"
for _ in 1 2 3 4 5 6; do "${CURL[@]}" -D - -o /dev/null "$BASE_URL/api/books" | grep -i x-served-by; done | sort | uniq -c

echo
[ "$echecs" -eq 0 ] && echo "Tous les tests sont passés." || { echo "$echecs test(s) en échec."; exit 1; }
