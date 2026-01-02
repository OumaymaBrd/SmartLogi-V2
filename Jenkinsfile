pipeline {
    agent any

    environment {
        // Variables dummy pour éviter les erreurs d'initialisation Spring context
        GOOGLE_CLIENT_ID = "dummy"
        GOOGLE_CLIENT_SECRET = "dummy"
        // Configuration de la DB de test pour Maven
        SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/smartSpring"
        SPRING_DATASOURCE_USERNAME = "admin"
        SPRING_DATASOURCE_PASSWORD = "admin_password"
    }

    stages {
        stage('Preparation') {
            steps {
                echo '🔧 Nettoyage des anciens conteneurs et préparation...'
                sh """
                chmod +x mvnw
                docker stop test-postgres || true
                docker rm test-postgres || true
                """
            }
        }

        stage('Start Test Database') {
            steps {
                echo '🐘 Démarrage de PostgreSQL pour les tests...'
                sh """
                docker run -d \
                  --name test-postgres \
                  -p 5433:5432 \
                  -e POSTGRES_DB=smartSpring \
                  -e POSTGRES_USER=admin \
                  -e POSTGRES_PASSWORD=admin_password \
                  postgres:15

                echo 'Attente du démarrage (15s)...'
                sleep 15

                # Vérifie si la DB est prête à accepter des connexions
                docker exec test-postgres pg_isready -U admin
                """
            }
        }

        stage('Tests Maven') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                // Comme vous avez supprimé les méthodes problématiques,
                // nous n'utilisons plus "ignore failure", le build échouera s'il reste une vraie erreur.
                sh """
                ./mvnw clean test \
                    -Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
                    -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
                    -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
                    -Dspring.jpa.hibernate.ddl-auto=create-drop
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '📦 Construction de l\'image Docker Backend...'
                /* On utilise -DskipTests ici car les tests ont déjà été validés
                   à l'étape précédente du pipeline. Cela gagne du temps. */
                sh 'docker build -t smart-spring-app-backend:latest .'
            }
        }
    }

    post {
        always {
            echo '📊 Traitement des rapports de tests...'
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true

            echo '🧹 Arrêt de la base de données de test...'
            sh """
            docker stop test-postgres || true
            docker rm test-postgres || true
            """
        }
        success {
            echo '✅ PIPELINE RÉUSSI ! L\'image Docker est prête et les tests sont validés.'
        }
        failure {
            echo '❌ ÉCHEC DU PIPELINE. Vérifiez les logs Maven ou Docker.'
        }
    }
}