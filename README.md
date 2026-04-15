# Partner Onboarding Copilot

A conversational multi-agent system that helps partner engineers integrate with the R2 platform. Instead of waiting on Slack threads, partners get an AI copilot that reasons over API documentation, diagnoses integration errors from real logs, and guides them through each stage of the onboarding process.

---

## What it does

Partners at companies like UberEats, Rappi, MercadoLibre, DoorDash, and PedidosYa need to implement R2 APIs, configure webhooks, and test the full lending flow. This chatbot replaces repetitive back-and-forth with R2 engineers by:

- Answering API and integration questions using RAG over platform documentation
- Diagnosing errors by fetching and analyzing real integration logs from Loki
- Tracking progress through onboarding stages and advancing partners automatically when milestones are met

---

## Architecture

Multi-agent system built on Spring Boot 4 with Java 21 virtual threads.

```
Partner browser
    ↓ JWT auth
Spring Security (PartnerContextFilter → partner.id in Baggage + MDC)
    ↓
ChatController  POST /api/v1/chat
    ↓
OrchestratorService  (LLM-as-router)
    ↓ ToolContext carries partnerId — never from LLM
    ├── DocAgentService        RAG over API docs via Qdrant
    ├── DiagnosticAgentService Logs via LokiLogProvider / MockLogProvider
    └── StatusAgentService     Advance onboarding stage
```

Onboarding stages: `START → AUTH_CONFIGURED → WEBHOOK_SET → LIVE`

See [ARCHITECTURE.md](ARCHITECTURE.md) for full design details.

---

## Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 4, virtual threads |
| LLM | Anthropic Claude Haiku (`claude-haiku-4-5-20251001`) |
| Embeddings | Ollama (`mxbai-embed-large`) |
| Vector store | Qdrant |
| Database | PostgreSQL 16 |
| Observability | OTel → Jaeger + Prometheus + Loki → Grafana |
| Auth | JWT via mock-oauth2-server (local dev) |

---

## Prerequisites

- Docker Desktop with Docker Compose
- Java 21
- Gradle
- An Anthropic API key

---

## Getting started

**1 — Clone and configure**

```bash
git clone <repo>
cd onboarding-copilot
```

Create a `.env` file in the project root:

```env
ANTHROPIC_API_KEY=sk-ant-your-key-here
GROQ_API_KEY=your-groq-key-here
```

**2 — Start infrastructure**

```bash
docker-compose up -d
```

**3 — Pull Ollama models** (first time only)

```bash
docker-compose exec ollama ollama pull mxbai-embed-large
```

**4 — Run the app**

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`. Open `http://localhost:8080/index.html` to use the chat interface.

---

## Services

| Service | URL | Credentials |
|---|---|---|
| Chat UI | http://localhost:8080 | JWT via mock-auth |
| Grafana | http://localhost:3000 | admin / admin |
| Jaeger | http://localhost:16686 | — |
| Prometheus | http://localhost:9090 | — |
| Qdrant | http://localhost:6333 | — |
| Mock Auth | http://localhost:9999 | — |

---

## Getting a JWT for API calls

```powershell
$token = (Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:9999/default/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=client_credentials&client_id=mercadolibre&client_secret=secret"
).access_token
```

Available partners: `ubereats`, `rappi`, `doordash`, `pedidosya`, `mercadolibre`

**Send a chat message:**

```powershell
curl -X POST http://localhost:8080/api/v1/chat `
  -H "Authorization: Bearer $token" `
  -H "Content-Type: text/plain" `
  -d "How do I authenticate with the API?"
```

**Simulate integration events:**

```powershell
# auth configured
curl -X POST http://localhost:8080/v1/auth -H "Authorization: Bearer $token"

# webhook configured
curl -X POST http://localhost:8080/v1/webhooks -H "Authorization: Bearer $token"
```

---

## Running tests

Requires `docker-compose up -d postgres qdrant ollama` to be running.

```powershell
.\gradlew.bat test --tests "org.example.onboardingcopilot.SetEvaluationTest" `
  "-Dspring.profiles.active=test" --rerun-tasks
```

The golden set runs 4 test cases covering each onboarding stage. Each case calls the full HTTP stack with a real JWT, exercises the complete filter chain, and evaluates the LLM response for expected keywords.

---

## Project structure

```
src/main/
  java/org/example/onboardingcopilot/
    config/          Spring config, AOP aspects, security
    controller/      ChatController, MockedOnboardingAPIController
    filter/          PartnerContextFilter
    model/           PartnerOnboarding, ChatSession, ChatMessage, OnboardingStatus
    repository/      JPA repositories + PartnerChatMemoryRepository
    service/         OrchestratorService, GuardrailService, ChatSessionService
    tools/           AgentTool interface, DocAgent, DiagnosticAgent, StatusAgent
    tools/provider/  LokiLogProvider
  resources/
    db/migration/    Flyway schema
    db/seed/         Partner seed data
    static/          Chat UI (index.html)

src/test/
  java/.../
    config/          TestSecurityConfig, TestMetricsConfig, TestFlywayConfig, TestDatabaseInitializer
    tools/           MockLogProvider
  resources/
    db/testdata/     R__test_partners.sql
    Test-set.json    Golden set evaluation cases

grafana/provisioning/
  datasources/       prometheus.yml (Prometheus + Loki)
  dashboards/        dashboard.yml + onboarding.json

infra/
  prometheus.yml
  loki-config.yaml
  promtail-config.yaml
  otel-collector-config.yaml

docs_folder/     Markdown API documentation for RAG
```

---

## Observability

After sending a few chat messages, open Grafana at `http://localhost:3000` (admin/admin) and navigate to **Dashboards → Onboarding Copilot**. Panels show:

- LLM p95 latency by onboarding stage
- Doc agent miss rate
- Stage advancement over time
- Guardrail blocks by direction

Traces are available in Jaeger at `http://localhost:16686` — search by service `r2-copilot`. Every trace includes `partner.id` and `session.id` as baggage so you can follow a full conversation across all agent calls.

---

## Deployment

Docker Compose is the primary development environment.