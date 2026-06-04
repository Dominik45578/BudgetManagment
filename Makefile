.PHONY: network infra app obs up down build-up infra-down app-down obs-down erase logs clean build tests

network:
	docker network create budget-network

infra:
	docker-compose -f docker-compose.infra.yml up -d

app:
	docker-compose -f docker-compose.app.yml up -d --build

obs:
	docker-compose -f docker-compose.observability.yml up -d

build-up:
	@echo "Starting app building .."
	mvnw clean package -DskipTests -B
	@echo "Starting infra .."
	docker-compose -f docker-compose.infra.yml up -d
	@echo "Starting app .."
	docker-compose -f docker-compose.app.yml up -d --build
	@echo "Starting observability .."
	docker-compose -f docker-compose.observability.yml up -d
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

up:
	@echo "Starting infra .."
	docker-compose -f docker-compose.infra.yml up -d
	@echo "Starting app .."
	docker-compose -f docker-compose.app.yml up -d
	@echo "Starting observability .."
	docker-compose -f docker-compose.observability.yml up -d
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
	-docker-compose -f docker-compose.observability.yml down
	-docker-compose -f docker-compose.app.yml down
	-docker-compose -f docker-compose.infra.yml down
	-docker network rm budget-network
	-docker volume prune -f

tests:
	mvnw test

logs:
	docker-compose -f docker-compose.app.yml logs -f

build:
	mvnw clean package -DskipTests -B

clean:
	mvnw clean
