# Budget Service & Gateway

Aplikacja do zarządzania budżetem osobistym oparta na architekturze mikroserwisów (Spring Boot 3.5.7, Spring Cloud) wraz ze stosem monitorującym (Prometheus, Grafana, Loki) i prostym interfejsem użytkownika w Tailwind CSS.

---

## Wymagania
* Java 21
* Docker + Docker Compose
* Narzędzie make (np. zainstalowane na Windowsie za pomocą Chocolatey: `choco install make`)

---

## Konfiguracja (.env)
Zachowanie całej aplikacji można dowolnie modyfikować w pliku `.env` utworzonym na bazie `.env.example`.

---

## Architektura i Funkcjonalność

* **eureka-server:** Service Discovery dla rejestracji i wyszukiwania instancji usług.
* **api-gateway:** Bramka reaktywna realizująca centralny routing, obsługę nagłówka korelacji (Correlation ID) oraz konfigurację CORS. Posiada wdrożony failback oraz limiter czasu(3 sekundy) przekierowujące ruch do reaktywnego endpointu fallback w razie awarii.
* **budget-service:** Główny serwis biznesowy. Wdrożono w nim:
  * Blokowanie pesymistyczne przy aktualizacji sald kont w transakcjach, co zabezpiecza przed wyścigami w środowisku wielowątkowym/wiele instancyjnym
  * Walidację limitów wydatków per kategoria (informacja o przekroczeniu limitu zwracana jest w odpowiedzi transakcji).
  * Eksport transakcji do formatu CSV
  * Obsługę błędów

---

## Uruchamianie

Głównym środowiskiem uruchomieniowym jest Windows. Przed startem skopiuj plik środowiskowy `.env.example` do `.env`.

W klasycznej konsoli CMD:
```cmd
copy .env.example .env
```

Lub w PowerShell:
```powershell
Copy-Item .env.example .env
```

### Za pomocą make (pełna lista komend):
* `make network` – Tworzy zewnętrzną sieć Docker (`budget-network`) – wymagane przed pierwszym startem.
* `make infra` – Uruchamia tylko bazę danych PostgreSQL.
* `make app` – Buduje obrazy i uruchamia usługi aplikacyjne (Eureka, Gateway, Budget Service).
* `make obs` – Uruchamia stos monitoringu (Prometheus, Grafana, Loki).
* `make build-up` – Kompiluje kod źródłowy, uruchamia bazę, buduje i odpala aplikacje oraz stos monitorowania.
* `make up` – Uruchamia bazę, aplikacje i monitoring (bez ponownej kompilacji kodu).
* `make infra-down` – Zatrzymuje bazę danych.
* `make app-down` – Zatrzymuje usługi aplikacyjne.
* `make obs-down` – Zatrzymuje monitoring.
* `make down` – Zatrzymuje wszystkie kontenery w projekcie (baza, aplikacje, monitoring).
* `make erase` – Zatrzymuje kontenery, usuwa sieć `budget-network` oraz czyści wolumeny.
* `make tests` – Uruchamia testy jednostkowe i integracyjne w budget-service.
* `make logs` – Podgląd logów kontenerów aplikacyjnych na żywo.
* `make build` – Buduje lokalne pliki jar za pomocą Maven (pomijając testy).
* `make clean` – Czyści foldery kompilacji target w Maven.

### Komendy ręczne (bez make):
```bash
# Tworzenie sieci
docker network create budget-network

# Uruchomienie bazy danych
docker-compose -f docker-compose.infra.yml up -d

# Budowanie paczek jar
./mvnw clean package -DskipTests   # Windows: .\mvnw.cmd clean package -DskipTests

# Uruchomienie aplikacji
docker-compose -f docker-compose.app.yml up -d --build

# Uruchomienie monitoringu
docker-compose -f docker-compose.observability.yml up -d
```

---

## Adresy usług
* Frontend GUI: http://localhost:3000
* API Gateway (Entrypoint): http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui.html
* Eureka Dashboard: http://localhost:8761
* Grafana: http://localhost:3001 (hasło admin/admin, dashboard "Budget Application Overview")
* Prometheus: http://localhost:9090

---

## Endpointy API (Bramka: http://localhost:8080)

### Konta (Accounts)
* `GET /api/v1/accounts` – Lista kont (paginowana)
* `POST /api/v1/accounts` – Tworzenie nowego konta (`{"name": "Nazwa"}`)
* `GET /api/v1/accounts/{id}` – Szczegóły konta z saldem
* `DELETE /api/v1/accounts/{id}` – Usuwanie konta (blokowane gdy konto ma powiązane transakcje)
* `GET /api/v1/accounts/{id}/transactions/export` – Eksport transakcji konta do CSV

### Transakcje (Transactions)
* `GET /api/v1/transactions` – Lista transakcji z filtrami (`?accountId=`, `?category=`, `?from=`, `?to=`)
* `POST /api/v1/transactions` – Dodawanie transakcji (aktualizuje saldo konta; zwraca ostrzeżenie w `budgetWarning` przy przekroczeniu limitu)
* `GET /api/v1/transactions/{id}` – Szczegóły transakcji
* `DELETE /api/v1/transactions/{id}` – Usuwanie transakcji (cofa jej wpływ na saldo konta)

### Limity budżetowe (Budgets)
* `POST /api/v1/budgets` – Tworzenie limitu miesięcznego na kategorię (`{"accountId": "...", "category": "FOOD", "monthlyLimit": 500.00}`)
* `GET /api/v1/budgets/{id}` – Szczegóły limitu
* `GET /api/v1/budgets/account/{accountId}` – Pobranie wszystkich limitów dla wybranego konta
* `DELETE /api/v1/budgets/{id}` – Usuwanie limitu

### Podsumowania (Summaries)
* `GET /api/v1/summaries/account/{accountId}?period=yyyy-MM` – Raport miesięczny (suma przychodów, wydatków, saldo netto, zestawienie wydatków wg kategorii)
