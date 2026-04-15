FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system rag && adduser --system --ingroup rag rag
COPY --from=build /workspace/target/document-qa-rag-system-*.jar app.jar
USER rag
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
