FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le jar généré par Maven
COPY target/pmt-backend-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
