# Users API - Configuration Production

##  Variables d'environnement requises

### Configuration de base
Copiez le fichier `.env.example` vers `.env` et configurez les valeurs :

``bash
cp .env.example .env
``

### Variables obligatoires

#### 1. Base de données
``bash
DB_HOST=localhost                    # Hôte de la base de données
DB_PORT=5432                         # Port PostgreSQL
DB_NAME=postgres                     # Nom de la base
DB_USERNAME=postgres                 # Utilisateur de la base
DB_PASSWORD=postgres      # Mot de passe de la base
``

#### 2. JWT (IMPORTANT)
``bash
# GÉNÉRER UNE CLÉ FORTE :
# Option 1: openssl rand -base64 64
# Option 2: https://generate-random.org/api-key-generator
JWT_SECRET=votre_cle_ultra_secrete_minimum_32_caracteres
JWT_EXPIRATION=86400000              # 24h en millisecondes
``

#### 3. Serveur
``bash
SERVER_PORT=8081                     # Port de l'application
SPRING_PROFILES_ACTIVE=prod          # Profil (dev ou prod)
``

#### 4. CORS
``bash
# DÉVELOPPEMENT
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

# PRODUCTION
CORS_ALLOWED_ORIGINS=https://votre-domaine.com,https://www.votre-domaine.com
``

#### 5. Logging
``bash
LOG_LEVEL=INFO                       # DEBUG (dev) ou INFO/WARN (prod)
``

---

##  Déploiement

### Développement local
``bash
# 1. Copier et configurer .env
cp .env.example .env

# 2. Démarrer PostgreSQL (via Docker par exemple)
docker run --name postgres-dev -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres

# 3. Compiler et lancer
mvn clean install
mvn spring-boot:run
``

### Production avec Docker

#### Dockerfile (déjà présent)
``dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
``

#### Build et déploiement
``bash
# 1. Builder l'application
mvn clean package -DskipTests

# 2. Builder l'image Docker
docker build -t users-api:latest .

# 3. Lancer avec les variables d'environnement
docker run -d \
  --name users-api \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=your-db-host \
  -e DB_PORT=5432 \
  -e DB_NAME=postgres \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your-secure-password \
  -e JWT_SECRET=your-super-secret-jwt-key-minimum-32-chars \
  -e JWT_EXPIRATION=86400000 \
  -e CORS_ALLOWED_ORIGINS=https://your-domain.com \
  -e LOG_LEVEL=INFO \
  users-api:latest
``

### Production avec docker-compose

Créer un fichier `docker-compose.yml` :

``yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: postgres
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network

  users-api:
    build: .
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: postgres
      DB_USERNAME: postgres
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION:-86400000}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      LOG_LEVEL: ${LOG_LEVEL:-INFO}
    depends_on:
      - postgres
    networks:
      - app-network

volumes:
  postgres_data:

networks:
  app-network:
    driver: bridge
``

Lancer avec :
``bash
docker-compose up -d
``

---

##  Sécurité en production

### Checklist avant déploiement

- [ ] **JWT_SECRET** : Générer une clé forte et unique
- [ ] **DB_PASSWORD** : Utiliser un mot de passe fort
- [ ] **CORS** : Configurer uniquement vos domaines autorisés
- [ ] **SPRING_PROFILES_ACTIVE=prod** : Activer le profil production
- [ ] **spring.jpa.hibernate.ddl-auto=validate** : Éviter la modification auto du schéma
- [ ] Ne JAMAIS committer le fichier `.env`
- [ ] Utiliser des secrets managers (AWS Secrets Manager, Azure Key Vault, etc.)

### Générer une clé JWT sécurisée

``bash
# Linux/Mac
openssl rand -base64 64

# PowerShell
$bytes = New-Object byte[] 64
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
``

---

##  Tests

``bash
# Tous les tests
mvn test

# Tests avec couverture
mvn test jacoco:report
``

---

##  Troubleshooting

### Erreur : "JWT secret is too short"
 La clé JWT doit faire au minimum 256 bits (32 caractères)

### Erreur : "CORS policy"
 Vérifier que l'origine du frontend est dans `CORS_ALLOWED_ORIGINS`

### Erreur : "Connection refused to PostgreSQL"
 Vérifier que PostgreSQL est démarré et accessible avec les bons identifiants

---

##  Documentation API

Une fois l'application lancée :
- **Swagger UI** : http://localhost:8081/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8081/v3/api-docs

---

##  Rôles utilisateurs

- **USER** : Peut lire les utilisateurs
- **ADMIN** : Peut modifier et supprimer les utilisateurs

Par défaut, les nouveaux utilisateurs ont le rôle `USER`.
