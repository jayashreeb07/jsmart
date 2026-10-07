# Contributing
1. `git clone <url>` and `cd "JS Mart"`
2. JDK 17 (Temurin) + Maven 3.9+
3. Configure: copy `src/main/resources/app.properties` to `config.properties` for local secrets (ignored by git); or set env `DB_URL`, `DB_USER`, `DB_PASSWORD`, `GEMINI_API_KEY`
4. Database: auto-migrated by `AppContextListener` from `db/migrations/V1__init_schema.sql`; new changes = new `V2__...` files, never edit V1
5. Build: `mvn -B clean verify` (tests + Checkstyle + SpotBugs)
6. Run: `cp target/jsmart.war $TOMCAT_HOME/webapps/` and start Tomcat 9; open `http://localhost:8080/jsmart/`
7. Branches: `feature/<name>`; conventional commits (`feat:`, `fix:`, `test:`, `docs:`); self-reviewed PRs to `main`
