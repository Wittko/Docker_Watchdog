# Basis-Image mit Java 17
FROM openjdk:17-jdk-slim

# Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere die gebaute JAR-Datei aus dem target-Ordner in den Container
# Hinweis: Der Name der JAR muss dem in deiner pom.xml entsprechen
COPY target/*.jar app.jar

# Starte die Anwendung
ENTRYPOINT ["java", "-jar", "app.jar"]
