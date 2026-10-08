# JS Mart - multi-stage build for Render.
# Stage 1 builds the WAR from source so the Maven target/ directory
# never needs to be committed to Git.
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY db ./db
COPY checkstyle.xml .
RUN mvn -B clean package -DskipTests

# Stage 2 runs the WAR on Tomcat 9 with JDK 17.
FROM tomcat:9-jdk17
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=builder /app/target/jsmart.war /usr/local/tomcat/webapps/jsmart.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
