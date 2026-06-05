# Użycie AI w projekcie

W tym projekcie zostały wykorzystane następujące narzędzia AI

* **Claude (Sonnet)**
  Używany do logicznych i trudniejszych zadań:
  * Analiza współbieżności i potencjalnych wyścigów przy aktualizacji salda konta. Użyty ze względu na przystosowanie serwisu do współistnienia wielu instancji.
  * Pomoc przy pisaniu testów jednostkowych, w tym obsłużenie kilku newralgicznych przypadków.
  * Analiza jakości kodu wraz z generowaniem raportu-tutaj sprawdzałem, czy mój kod jest spójny oraz, czy nie krytycznych błędów architektonicznych.

    
* **Gemini 3.5 Flash**
  Używany do powtarzalnego kodu i prostszych, żmudnych zadań:
  * Generowanie adnotacji dokumentacyjnych OpenAPI / Swagger (@Operation, @ApiResponses, @Schema) w DTO i kontrolerach.
  * Wsparcie przy szybkim ostylowaniu frontu w Tailwind CSS i pisaniu skryptu JS do komunikacji z API.
  * Wsparcie przy pisaniu pliku readme

Narzędzia pozwoliły mi także na głębsze poznanie kilku technologi jak: 
* wykorzystanie plików makefile to automatyzacji uruchomienia aplikacji
* nowe mechanizmy jpa, webflux czy resilience