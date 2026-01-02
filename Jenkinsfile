pipeline {
    agent any

    environment {
        GOOGLE_CLIENT_ID = "dummy"
        GOOGLE_CLIENT_SECRET = "dummy"
        SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/smartSpring"
        SPRING_DATASOURCE_USERNAME = "admin"
        SPRING_DATASOURCE_PASSWORD = "admin_password"
    }

    stages {
        stage('Preparation') {
            steps {
                echo '🔧 Nettoyage et préparation...'
                sh """
                chmod +x mvnw

                # Nettoyage des conteneurs précédents
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

                # Attendre que PostgreSQL soit prêt
                echo 'Attente du démarrage de PostgreSQL...'
                sleep 15

                # Vérifier que PostgreSQL est accessible
                docker exec test-postgres pg_isready -U admin
                """
            }
        }

        stage('Tests Maven') {
            steps {
                echo '🧪 Exécution des tests avec PostgreSQL...'
                sh """
                ./mvnw clean test \
                    -Dspring.datasource.url=jdbc:postgresql://localhost:5433/smartSpring \
                    -Dspring.datasource.username=admin \
                    -Dspring.datasource.password=admin_password \
                    -Dspring.jpa.hibernate.ddl-auto=create-drop
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '📦 Construction de l\'image Docker...'
                sh 'docker build -t smart-spring-app-backend:latest .'
            }
        }
    }

    post {
        always {
            script {
                echo '📊 Collecte des résultats de test...'
                junit testResults: '**/target/surefire-reports/*.xml',
                      allowEmptyResults: true

                echo '🧹 Nettoyage de la base de données de test...'
                sh """
                docker stop test-postgres || true
                docker rm test-postgres || true
                """
            }
        }
        success {
            echo '✅ PIPELINE RÉUSSI ! Tous les tests sont passés.'
        }
        failure {
            echo '❌ PIPELINE ÉCHOUÉ. Vérifiez les logs des tests.'
        }
    }
}
