# Utiliser une image de base Ubuntu
FROM ubuntu:latest AS build

# Installer Java et Maven
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk maven

# Créer un répertoire de travail dans le conteneur
WORKDIR /app

# Copier l'ensemble du projet dans le conteneur
COPY . .

# Exécuter la commande mvn spring-boot:run pour démarrer l'application
CMD [ "mvn", "spring-boot:run" ]
