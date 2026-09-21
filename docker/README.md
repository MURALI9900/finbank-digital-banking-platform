# FinBank Infrastructure

FinBank uses Docker Compose for local PostgreSQL and Kafka infrastructure.

## Start infrastructure

    docker compose up -d

## Services

| Component | Port | Purpose |
|---|---:|---|
| PostgreSQL | 5432 | Banking database |
| Kafka | 9092 | Event streaming |
| Kafka UI | 8099 | Kafka topic and consumer monitoring |

## Verify

PostgreSQL is configured with database finbank and user finbank.

Open Kafka UI at:

    http://localhost:8099

The transaction event topic used by the backend is:

    finbank.transaction.events

## Stop

    docker compose down

To remove persisted local data:

    docker compose down -v

The infrastructure is intended for local development. Production deployments should use managed or hardened database and Kafka infrastructure with secrets supplied outside source control.
