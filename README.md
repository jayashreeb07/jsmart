# JS Mart

Servlet/JSP/JDBC e-commerce capstone — JDK 17, Tomcat 9, H2, HikariCP. Package base `com.js.jsmart`.

## Problem statement
Small sellers need a simple storefront: accounts, listings, search, cart, checkout, orders, reviews, admin moderation, and a help chatbot — without a heavyweight framework.

## Features (F1–F8)
| Spec | Feature | Implementation |
|---|---|---|
| F1 | Auth (buyer/seller register, login/logout, BCrypt, session regen + timeout, seeded admin) | `AuthService`, `RegisterServlet`, `LoginServlet`, `LogoutServlet`, `AuthFilter`, `PasswordUtil` |
| F2 | Seller product CRUD, own-products only | `SellerServlet`, `ProductService` |
| F3 | Browse/search/filter/detail + ratings | `ProductServlet`, JSPs |
| F4 | Cart add/update/remove/total | `CartServlet`, `CartService` |
| F5 | Mock-payment checkout, atomic order+stock+clear | `CheckoutServlet`, `OrderService.checkout` (single txn, rollback) |
| F6 | Buyer history, seller incoming, PENDING→CONFIRMED→SHIPPED→DELIVERED + CANCELLED | `OrderServlet`, `OrderService.updateStatus` |
| F7 | Admin users/orders/moderate | `AdminServlet`, `AuthFilter` role check |
| F8 | Reviews on delivered orders only, 1–5, no duplicates | `ReviewServlet`, `ReviewService` |
| O4/AI | Chatbot (Gemini|mock), rate-limit, cache, fallback | `ChatProvider`, `MockChatProvider`, `GeminiChatProvider`, `ChatService`, `ChatServlet`, `chat.js` |

## Tech stack
JDK 17 · Maven · Tomcat 9.0.x · javax.servlet 4.0.1 · JSP/JSTL 1.2 · vanilla JS fetch · JDBC · H2 · HikariCP · Gson · jBCrypt · JUnit5 · Mockito · SLF4J/Logback · GitHub Actions · Checkstyle · SpotBugs

## Architecture
```
Browser → Filters (Encoding, RequestLogging/MDC, Auth) → Servlets (thin) → Services → DAO (PreparedStatement) → HikariCP → H2
```
Packages: `controller service dao model dto filter listener util exception` under `com.js.jsmart`.

## Setup
```bash
git clone <url> && cd "JS Mart"
cp src/main/resources/app.properties config.properties  # optional overrides (git-ignored)
mvn -B clean verify
cp target/jsmart.war $TOMCAT/webapps/
# H2 file DB at ./data/jsmart by default; server mode: db.url=jdbc:h2:tcp://localhost/~/jsmart
```
Tomcat 9 + JDK 17. App: `http://localhost:8080/jsmart/` · Health: `GET /api/v1/health` → `{"status":"UP","db":"UP"}`.

Demo accounts: `buyer1@jsmart.local / Buyer@123`, `seller1@jsmart.local / Seller@123`, `admin@jsmart.local / Admin@123`.

## API
Versioned `/api/v1/...`, Gson, envelope `{success,data,error{code,message}}`, statuses 200/201/400/401/403/404/409/500. `UserResponseDTO` never contains `passwordHash`.

## Chatbot
`ai.chatbot.provider=mock|gemini` (env `AI_CHATBOT_PROVIDER`). Key via `GEMINI_API_KEY` env only. Widget → `POST /api/chat {message}` → `ChatService` (10 msg/min/session, 500-char cap, per-session repeat cache, fixed prompt, timeout, degraded fallback).

## Testing
`mvn -B clean verify` — 23 tests: DAO (embedded `jdbc:h2:mem:test`), service (Mockito), servlet/filter (Mockito req/resp), security (SQLi/XSS/auth bypass). Manual sheet: `docs/MANUAL_TEST_SHEET.md`. Load: `docs/LOAD_TEST.md` (JMeter/ab, 10 users/60s).

## Diagrams
`docs/diagrams/` (Mermaid): `er.md`, `usecase.md`, `sequence-place-order.md`. Referenced in `docs/FINAL_REPORT.md`.

## Deployment
See `docs/DEPLOYMENT.md` (+ `Dockerfile`). H2 server mode for persistence; health check; backup instructions. Demo-video checklist included. No live URL claimed until actually deployed.

## Known limitations
No real payment (mock), no wishlist/sales dashboard (deferred O1/O3), Gemini needs key (mock default), file-upload avatars not implemented.
