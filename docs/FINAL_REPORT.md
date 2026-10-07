# Final report — JS Mart (matches actual implementation)
1. Problem: small sellers need a simple Java storefront without framework lock-in.
2. Objective: F1–F8 + mandatory chatbot on Servlet/JSP/JDBC/Tomcat.
3. Requirements: PDF is source of truth; naming `JS Mart/jsmart/com.js.jsmart`.
4. Architecture: Browser→Filters→Servlets(thin)→Services→DAO→HikariCP→H2. See README diagram.
5. Stack: JDK17, Maven, Tomcat 9, javax.servlet, JSP/JSTL, vanilla JS fetch, JDBC, H2, HikariCP, Gson, jBCrypt, JUnit5, Mockito, SLF4J/Logback, Actions, Checkstyle, SpotBugs.
6. Database: 6 tables (users/products/orders/order_items/cart_items/reviews), FKs indexed, email UNIQUE, DECIMAL(10,2), created_at everywhere; `db/migrations/V1__init_schema.sql`; seed separate.
7–9. Diagrams: `docs/diagrams/er.md`, `usecase.md`, `sequence-place-order.md`.
10. Patterns: DAO (Jdbc*DAO), Front Controller (servlets+AuthFilter dispatch), Singleton/managed pool (AppContextListener+DbUtil), Factory (DAOFactory), Strategy (ChatProvider), Builder (Gson/envelope via ApiResponse factories).
11. Security: PreparedStatement only; BCrypt; session regen+timeout; c:out escaping; single listener pool; no DriverManager outside pool; try-with-resources; thin servlets; generic error pages; no secrets in logs/git. Checklist in PROJECT_REQUIREMENTS_CHECKLIST.md.
12. API: `/api/v1/...`, fixed envelope, correct statuses, DTOs separate, no passwordHash.
13. Chatbot: ChatProvider interface; Mock+Gemini; `ai.chatbot.provider`; server-side key; domain prompt; try/catch fallback; 10 FAQs; 10/min rate-limit; 500-char cap; timeout; fixed prompt; per-session repeat cache.
14. Testing: 23 tests (DAO embedded H2, service Mockito, servlet/filter Mockito, security). Manual sheet + load docs.
15. CI/CD: `.github/workflows/build.yml` (checkout@v4, setup-java@v4, Temurin 17, `mvn -B clean verify`).
16. Deployment: `docs/DEPLOYMENT.md`; WAR to Tomcat 9; H2 server mode persistent; no live URL claimed.
17. Decisions: mock-first chatbot (offline-safe); single-txn checkout; forward-only status table; no Flyway (numbered SQL suffices).
18. Limitations: mock payment; no wishlist/dashboard; Gemini needs key; no uploads.
19. Future: wishlist, sales dashboard, real gateway, image upload, pagination polish.
