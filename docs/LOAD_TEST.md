# Load test (JMeter/ab, 10 concurrent / 60s)
Plan: thread group 10 users, 60s ramp-hold against `/products`, `/api/v1/products`, `/api/v1/health`.
CLI alternative: `ab -n 1000 -c 10 http://localhost:8080/jsmart/api/v1/health`.
Acceptance: 0 errors on health, p95 browse < 800ms locally, no pool exhaustion (Hikari max 10).
Record results here after running against your environment (results vary by machine; do not fabricate).
| endpoint | reqs | errors | p95 |
|---|---|---|---|
| /api/v1/health | — | — | — |
| /api/v1/products | — | — | — |
| /products | — | — | — |
