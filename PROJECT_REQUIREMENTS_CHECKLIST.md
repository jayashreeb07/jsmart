# Requirements checklist (PDF §§1–19 → implementation)
| Req | Implementation | Status |
|---|---|---|
| Stack: JDK17/Maven/Tomcat9/javax/JSP/JSTL/vanillaJS/fetch/JDBC/H2/HikariCP/Gson/jBCrypt/JUnit5/Mockito/SLF4J/Logback/Actions/Checkstyle/SpotBugs | pom.xml, web.xml, servlets/JSP | PASS |
| MVC Front Controller, packages controller/service/dao/model/dto/filter/listener/util/exception | full tree | PASS |
| Thin servlets; logic in services; SQL in DAO; DTOs separate; no passwordHash | enforced | PASS |
| All SQL PreparedStatement; no concat; BCrypt; HttpSession regen+timeout; c:out; single listener pool; no DriverManager; try-with-resources; no stack traces; no secret logs | code + web.xml errors | PASS |
| H2 server deploy / embedded test; 6 tables; roles; FK indexed; UNIQUE email; DECIMAL; created_at; schema+seed; db/migrations/V1__ | V1 + seed | PASS |
| F1 auth + UserDAO/AuthService/PasswordUtil/Register/Login/Logout + AuthFilter + seeded admin | done | PASS |
| F2 seller CRUD own-only, no browser seller_id | SellerServlet/ProductService | PASS |
| F3 browse/filter/search/detail, safe search, bad-id handling | ProductServlet | PASS |
| F4 cart rules | CartServlet/Service | PASS |
| F5 mock payment, atomic txn rollback | CheckoutServlet/OrderService | PASS |
| F6 history + seller incoming + workflow | OrderServlet/Service | PASS |
| F7 admin users/orders/moderate, server-side 403 | AdminServlet/AuthFilter | PASS |
| F8 delivered-only reviews, range/duplicates | ReviewServlet/Service | PASS |
| O4 chatbot mandatory: interface, Gemini+Mock, provider key, ChatService/Servlet/widget, server key, domain prompt, fallback, 10 FAQs, rate-limit, length cap, timeout, prompt template, session cache | done (mock default) | PASS |
| UI JSP+JSTL+CSS+vanillaJS+fetch, all areas incl. chatbot | done | PASS |
| JSON /api/v1 envelope + statuses; validation fields | ApiResponse/JsonUtil | PASS |
| GET /api/v1/health {UP,UP} with real DB check | HealthServlet | PASS |
| SLF4J/Logback + request filter MDC requestId + cleanup | RequestLoggingFilter | PASS |
| Tests: DAO embedded H2, service Mockito, servlet Mockito, security, manual sheet, load doc | 23 tests PASS | PASS |
| Security checklist 16 items | verified in code/tests | PASS |
| 404/500 pages, no traces | web.xml + JSPs | PASS |
| Naming PascalCase/camelCase/UPPER_SNAKE/lowercase; Javadoc service/dao; Checkstyle+SpotBugs in CI | done | PASS |
| Patterns DAO/FrontController/Singleton-pool/Factory/Strategy/Builder documented | FINAL_REPORT | PASS |
| CI build.yml checkout@v4/setup-java@v4/Temurin17/mvn verify | done | PASS |
| Git: conventional commits; issue templates; no fake history | templates present | PASS |
| SemVer CHANGELOG; RETRO; CONTRIBUTING; README; D1–D3 diagrams | done | PASS |
| Deployment WAR/H2-server prep; no fake URL; video checklist | DEPLOYMENT.md | PASS |
| Final report 19 sections | docs/FINAL_REPORT.md | PASS |
| O1 wishlist / O3 dashboard | deferred, documented | PARTIAL (optional, honestly deferred) |
| Live public URL + Gemini key + demo video + 33 historic commits | need user action/creds/time | BLOCKED (documented, not faked) |
