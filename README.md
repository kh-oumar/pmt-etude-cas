# Project Management Tool (PMT)

Ce projet est une application de gestion de projets collaboratifs :

- **Authentification** : Inscription et connexion des utilisateurs.  
- **Gestion des Projets** : Création de projets, gestion des membres et attribution de rôles (ADMIN, MEMBER, OBSERVER).  
- **Gestion des Tâches** : Création, modification, assignation et suivi des tâches, avec historique de modifications.  
- **Notifications** : Envoi de notifications par e-mail lors de l'assignation des tâches.  
- **Tests & Industrialisation** : Tests unitaires et d'intégration côté backend et frontend, avec Dockerisation et pipeline CI/CD sur GitHub Actions.

---

## Table des matières

- [Configuration du Projet](#configuration-du-projet)
- [Déploiement Local](#déploiement-local)
  - [Avec Docker et Docker Compose](#avec-docker-et-docker-compose)
- [Exécution des Tests](#exécution-des-tests)
  - [Backend](#tests-backend)
  - [Frontend](#tests-frontend)
- [Pipeline CI/CD avec GitHub Actions](#pipeline-cicd-avec-github-actions)
- [Structure du Projet](#structure-du-projet)
- [Utilisation](#utilisation)

---

## Configuration du Projet

### Prérequis

- **Java** : Version 17 (ou plus récente)  
- **Node.js et npm** : Version Node 22 et npm 10  
- **Angular CLI** : Version 18  
- **Maven**  
- **Docker et Docker Compose**  

### Installation du Projet

1. **Cloner le dépôt**  
   git clone https://github.com/kh-oumar/pmt-etude-cas.git

2. **Configurer le backend**  
   Placez-vous dans le dossier `pmt-backend`  
   Installez les dépendances et compilez le projet avec Maven :
   ```sh
   mvn clean package
   ```
  et pour voir le rapport de couverture des tests coté backend faire :
   ```sh
   mvn clean verify
   ```
  puis ouvrir sur le web le ficher qui se trouve dans : pmt-etude-cas/pmt-backend/target/site/jacoco/index.html

3. **Configurer le frontend**  
   Placez-vous dans le dossier `pmt-frontend` et installez les dépendances npm :
   ```sh
   npm install
   ```
   et pour voir le rapport de couverture des tests coté frontend :
   ```sh
    npm run test:coverage
   ```
  le rapport sera visible dans la console.

---

## Déploiement Local

### Avec Docker et Docker Compose

Ce projet inclut des Dockerfile pour le backend et le frontend et un fichier `docker-compose.yml` pour orchestrer les conteneurs.

#### Structure des Dockerfiles

**Backend (pmt-backend/Dockerfile) :**
```Dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copier le jar généré par Maven dans le conteneur
COPY target/pmt-backend-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend (pmt-frontend/Dockerfile) :**
```Dockerfile
# Étape 1 : Build de l'application Angular
FROM node:22 as build

WORKDIR /app

# Copier le package.json et le package-lock.json
COPY package*.json ./
RUN npm install

# Copier le reste du code source
COPY . .

# Construire l'application Angular en mode production
RUN npm run build -- --configuration production

# Étape 2 : Serve avec Nginx
FROM nginx:1.25-alpine

# Copier les fichiers buildés Angular depuis l'étape de build vers le répertoire par défaut de Nginx
COPY --from=build /app/dist/pmt-frontend /usr/share/nginx/html

# Exposer le port 80
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
```

#### Fichier docker-compose.yml (à la racine du projet)

```yaml
version: '3.8'
services:
  backend:
    build:
      context: ./pmt-backend   # Chemin vers votre dossier backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=default

  frontend:
    build:
      context: ./pmt-frontend   # Chemin vers votre dossier frontend
      dockerfile: Dockerfile
    ports:
      - "4200:80"
```

#### Lancement du Projet en Local avec Docker Compose

Dans la racine du projet (là où se trouve le fichier `docker-compose.yml`), exécutez :
```sh
docker-compose up --build
```
Cela va construire et lancer les conteneurs pour le frontend et le backend.  
L'application frontend sera accessible sur [http://localhost:4200](http://localhost:4200) et le backend sur [http://localhost:8080](http://localhost:8080).


## Pipeline CI/CD avec GitHub Actions

Le pipeline CI/CD est configuré pour :

- Exécuter les tests du backend et du frontend.
- Construire les images Docker pour le backend et le frontend.
- Publier ces images sur Docker Hub.

### Exemple de fichier GitHub Actions Workflow (ci-cd.yml)

Créez un fichier `.github/workflows/ci-cd.yml` dans votre dépôt :

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: 21
          distribution: temurin
      - name: Build and Test Backend
        run: mvn clean test package

      - name: Build Docker Image for Backend
        run: docker build -t yourusername/pmt-backend:latest -f pmt-backend/Dockerfile .

  build-and-test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Use Node.js 22
        uses: actions/setup-node@v3
        with:
          node-version: 22
      - name: Install Dependencies and Test Frontend
        working-directory: pmt-frontend
        run: |
          npm install
          npm run test -- --ci
      - name: Build Frontend for Production
        working-directory: pmt-frontend
        run: npm run build -- --configuration production
      - name: Build Docker Image for Frontend
        run: docker build -t yourusername/pmt-frontend:latest -f pmt-frontend/Dockerfile .
```

## Structure du Projet

Voici un aperçu rapide de la structure du projet :

```
pmt-project/
├── pmt-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/codesolutions/pmt_backend/
│   │   │   └── resources/
│   │   └── test/ (tests backend)
│   ├── pom.xml
│   └── Dockerfile
├── pmt-frontend/
│   ├── src/
│   │   ├── app/ (composants Angular, services, etc.)
│   │   ├── index.html
│   │   └── setup-jest.ts
│   ├── angular.json
│   ├── package.json
│   ├── tsconfig.json
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Utilisation

- **Accès à l'application** :  
  - Frontend : [http://localhost:4200](http://localhost:4200)  
  - Backend API : [http://localhost:8080](http://localhost:8080)

- **Déploiement local avec Docker Compose** :  
  Dans la racine du projet, exécutez :
  ```sh
  docker-compose up --build
  ```

- **Exécution des tests** :
  - Backend :  
    ```sh
    mvn clean test
    ```
  - Frontend :  
    ```sh
    npm run test
    ```

- **Pipeline CI/CD** :  
  Lorsqu'une modification est poussée sur la branche principale ou dans une pull request, le pipeline GitHub Actions s'exécute pour tester, construire et (optionnellement) déployer les images Docker.

---

Principaux Endpoints de l’API
Voici une liste des principales routes REST exposées par le backend, avec leur méthode HTTP et leur fonctionnalité :
POST /api/auth/register – Création d’un nouveau compte utilisateur. Le corps de la requête contient les informations d’inscription (username, email, mot de passe). Réponses : 201 Created si succès, 400 si email déjà utilisé par exemple.
POST /api/auth/login – Authentification de l’utilisateur. Le corps contient email/username et mot de passe. Si validé, le serveur renvoie un token JWT (dans le corps JSON ou l’en-tête Authorization). Réponses : 200 OK + token si succès, 401 Unauthorized si échec (identifiants invalides).
GET /api/users/me – (éventuel) Récupère les informations du profil de l’utilisateur connecté (d’après le token fourni). Utile pour afficher le nom de l’utilisateur courant dans l’UI et vérifier son rôle global.
GET /api/projects – Récupère la liste des projets dont l’utilisateur connecté est membre. Permet d’afficher le dashboard des projets. Réponse: 200 OK avec la liste des projets (id, nom, description, rôle de l’utilisateur dans chaque projet).
POST /api/projects – Crée un nouveau projet. Le corps JSON doit contenir au minimum le nom (et éventuellement description, dates…). L’utilisateur auteur de la requête sera automatiquement attaché comme ADMIN du projet. Réponse : 201 Created avec les détails du projet créé (y compris son id généré).
GET /api/projects/{projectId} – Récupère les détails d’un projet spécifique, incluant la liste des membres (utilisateurs + leur rôle) et possiblement la liste des tâches associées. Nécessite que l’utilisateur appelant soit membre du projet ciblé, sinon 403 Forbidden. Réponse : 200 OK avec l’objet projet complet.
PUT /api/projects/{projectId} – (éventuel) Met à jour les informations d’un projet (ex: changer le nom ou description). Autorisé uniquement à un ADMIN du projet.
DELETE /api/projects/{projectId} – (éventuel) Supprime un projet et toutes ses données associées. Autorisation ADMIN requise. Réponse : 204 No Content si réussite.
POST /api/projects/{projectId}/invite – Envoie une invitation à un nouvel utilisateur pour rejoindre le projet. Le corps JSON contient l’email de la personne à inviter et éventuellement le rôle à lui attribuer (par défaut MEMBER si non précisé). Le serveur crée une entité Invitation associée au projet. Un email pourrait être envoyé en arrière-plan. Réponse : 200 OK (ou 201 Created) si l’invitation est bien créée. (Note : Pas de doublon – si la personne est déjà invitée ou déjà membre, le serveur peut retourner une erreur 400).
POST /api/invitations/accept – Valide une invitation. Cette route peut être appelée sans être authentifié préalablement (ou par un utilisateur connecté correspondant à l’email invité). Le corps ou les paramètres contiennent le token d’invitation reçu. Le backend vérifie le token, ajoute l’utilisateur correspondant comme membre du projet avec le rôle spécifié, marque l’invitation comme “acceptée”, et retourne éventuellement le projet rejoint. Réponses : 200 OK si succès (+ peut-être le projet ou un message), 400 Bad Request si token invalide ou expiré, 409 Conflict si l’utilisateur est déjà membre.
GET /api/projects/{projectId}/members – (éventuel) Liste les membres d’un projet avec leurs rôles. Réservé aux membres du projet.
PUT /api/projects/{projectId}/members/{userId} – (éventuel) Change le rôle d’un membre existant (par exemple promouvoir un MEMBER en ADMIN ou inversement). Seulement l’ADMIN actuel peut effectuer cette action.
DELETE /api/projects/{projectId}/members/{userId} – (éventuel) Retire un membre du projet (ou refuse une invitation en attente). Là encore, nécessite les droits d’admin ou le userId qui se retire lui-même.
GET /api/projects/{projectId}/tasks – Récupère la liste des tâches du projet spécifié. Inclut pour chaque tâche ses propriétés (statut, priorité…) et éventuellement les informations basiques de la personne assignée.
POST /api/projects/{projectId}/tasks – Crée une nouvelle tâche dans le projet donné. Le corps JSON doit contenir au moins un nom de tâche, et peut inclure description, due_date, priority, assigned_to (id d’utilisateur du projet). Réponse : 201 Created avec l’objet tâche complet (id généré, etc.). Le backend ajoute aussi une entrée dans l’historique indiquant la création de la tâche.
GET /api/tasks/{taskId} – Récupère les détails d’une tâche particulière (peut aussi être obtenu via le projet, donc pas toujours nécessaire séparément).
PUT /api/tasks/{taskId} – Modifie une tâche existante (changement de statut, réassignation, mise à jour du titre/description/priorité…). Le corps JSON ne contient que les champs modifiés. Chaque modification valide entraîne l’ajout d’une entrée dans TaskHistory côté serveur. Réponse : 200 OK avec la tâche mise à jour. Erreurs possibles : 400 si données invalides, 403 si l’utilisateur n’a pas le droit (par ex. un observateur tentant de modifier une tâche).
DELETE /api/tasks/{taskId} – Supprime une tâche. Autorisé aux membres ayant créé la tâche ou aux Admin du projet, selon règles définies. Réponse : 204 No Content.
GET /api/tasks/{taskId}/history – Renvoie l’historique des modifications de la tâche (liste des entrées avec date, auteur du changement, description du changement). Réservé aux membres du projet. Réponse : 200 OK avec la liste triée du plus ancien au plus récent.
GET /api/users – (optionnel/admin global) Liste tous les utilisateurs de la plateforme (peut ne pas exister si pas de notion d’admin global).
GET /api/health – (optionnel) Endpoint de santé pour vérifier que l’API fonctionne (utilisé en dev/monitoring, renvoyant juste "OK").


possibilité de mettre en place une base mySql pour la production : 

# application.properties pour MySQL en production
spring.datasource.url=jdbc:mysql://localhost:3306/pmt_db?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none 
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

-- Création de la base de données PMT
CREATE DATABASE IF NOT EXISTS pmt_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pmt_db;

--------------------------------------------------------
-- Table des Utilisateurs (Users)
--------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table des Projets (Project)
--------------------------------------------------------
CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    start_date DATE
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table des Rôles (Role)
--------------------------------------------------------
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table d'Association Projet-Membre (Project_Member)
-- Clé composite composée de user_id et project_id
--------------------------------------------------------
CREATE TABLE project_member (
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table des Tâches (Task)
--------------------------------------------------------
CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    due_date DATE,
    priority INT,
    status VARCHAR(20),
    assigned_to BIGINT,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table de l'Historique des Tâches (Task_History)
--------------------------------------------------------
CREATE TABLE task_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT,
    change_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    change_description VARCHAR(500),
    CONSTRAINT fk_history_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

--------------------------------------------------------
-- Table des Invitations (Invitation)
--------------------------------------------------------
CREATE TABLE invitation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(50) NOT NULL,
    status VARCHAR(20),
    invited_by BIGINT NOT NULL,
    CONSTRAINT fk_invitation_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_invited_by FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

