.PHONY: dev infra seed bff portal clean

dev: infra seed          ## Start infra + seed data
	@echo ""
	@echo "Ready. Run 'make bff' and 'make portal' in separate terminals."

infra:                   ## Start LocalStack
	docker compose up -d
	@echo "Waiting for LocalStack..."
	@until curl -s http://localhost:4566/_localstack/health | grep -q '"dynamodb": "running"'; do sleep 1; done
	@echo "LocalStack ready."

seed:                    ## Seed test data into LocalStack
	./dev/seed-data.sh

bff:                     ## Start web-bff on :8080
	cd verigate-web-bff && mvn spring-boot:run -Plocal -Dspring-boot.run.profiles=local

portal:                  ## Start partner portal on :3000
	cd verigate-partner-portal && cp .env.dev-local .env.local && npm run dev

clean:                   ## Stop and remove LocalStack data
	docker compose down -v
