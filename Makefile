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
	docker-compose -f docker-compose.infra.yml down 2>/dev/null || true

app-down:
	docker-compose -f docker-compose.app.yml down 2>/dev/null || true

obs-down:
	docker-compose -f docker-compose.observability.yml down 2>/dev/null || true

down: obs-down app-down infra-down

erase:
	@printf "WARNING: This will stop all containers, delete the shared network, and prune all volumes. Proceed? [y/N]: " && read ans; \
	if [ "$$ans" = "y" ] || [ "$$ans" = "Y" ]; then \
		echo "Stopping all services..."; \
		$(MAKE) down; \
		echo "Removing shared network..."; \
		docker network rm budget-network 2>/dev/null || true; \
		echo "Pruning docker volumes..."; \
		docker volume prune -f; \
		echo "Erase completed successfully."; \
	else \
		echo "Operation cancelled."; \
	fi

logs:
	docker-compose -f docker-compose.app.yml logs -f

build:
	mvnw clean package -DskipTests -B

clean:
	mvnw clean
