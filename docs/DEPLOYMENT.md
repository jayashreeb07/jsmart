# Deployment
- Build: `mvn -B clean verify` → `target/jsmart.war`
- Tomcat 9.0.x + JDK 17: copy WAR to `$TOMCAT/webapps/` (context `/jsmart`)
- DB persistence: set `db.url=jdbc:h2:tcp://localhost/~/jsmart` (+ user/pass via env `DB_URL/DB_USER/DB_PASSWORD`); run H2 server: `java -cp h2*.jar org.h2.tools.Server`
- Local dev/tests use embedded (`jdbc:h2:mem:test` / `./data/jsmart`)
- Health: `GET /api/v1/health` must return UP/UP
- Backup: copy H2 files or `SCRIPT TO 'backup.sql'`; restore via `RUNSCRIPT FROM`
- Docker: see Dockerfile (tomcat:9-jdk17)
## Demo video checklist (record before review)
1. Register buyer → login → browse/search/filter → product detail
2. Add/update/remove cart → checkout mock payment → order success
3. Buyer history → seller login → create/edit listing → incoming order → advance status
4. Delivered → buyer review → appears on product
5. Admin login → users/orders → remove listing; show 403 for buyer on admin URL
6. Chatbot widget Q&A; health endpoint; `mvn -B clean verify` green
