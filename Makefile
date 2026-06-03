.PHONY: network infra app obs up down infra-down app-down obs-down erase logs clean build

network:
	docker network create budget-network

infra:
	docker-compose -f docker-compose.infra.yml up -d

app:
	docker-compose -f docker-compose.app.yml up -d --build

obs:
	docker-compose -f docker-compose.observability.yml up -d

up: infra app obs
	@echo ""
	@echo "========================================"
	@echo "  All services started successfully!"
	@echo "========================================"
	@echo "  API Gateway:     http://localhost:8080"
	@echo "  Swagger UI:      http://localhost:8080/swagger-ui.html"
	@echo "  Frontend GUI:    http://localhost:3000"
	@echo "  Eureka:          http://localhost:8761"
	@echo "  Grafana:         http://localhost:3001"
	@echo "  Prometheus:      http://localhost:9090"
	@echo "========================================"
	@echo ""

infra-down:
	docker-compose -f docker-compose.infra.yml down

app-down:
	docker-compose -f docker-compose.app.yml down

obs-down:
	docker-compose -f docker-compose.observability.yml down

down: obs-down app-down infra-down

erase:
	@powershell -Command "$$ans = Read-Host 'WARNING: This will stop all containers, delete the shared network, and prune all volumes. Proceed? (y/N)'; if ($$ans -match '^[yY]$') { Write-Host 'Stopping all services...'; & '$(MAKE)' down; Write-Host 'Removing shared network...'; & docker network rm budget-network; Write-Host 'Pruning docker volumes...'; & docker volume prune -f; Write-Host 'Erase completed successfully.' } else { Write-Host 'Operation cancelled.' }"


logs:
	docker-compose -f docker-compose.app.yml logs -f

build:
	mvnw clean package -DskipTests -B

clean:
	mvnw clean
