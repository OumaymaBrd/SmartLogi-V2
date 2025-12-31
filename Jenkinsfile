pipeline {
    agent any

    environment {
        // Paramètres pour éviter les erreurs de contexte Spring
        GOOGLE_CLIENT_ID = "dummy-id"
        GOOGLE_CLIENT_SECRET = "dummy-secret"
    }

    stages {
        stage('Preparation') {
            steps {
                echo 'Nettoyage et permissions...'
                sh 'chmod +x mvnw'
            }
        }

        stage('Tests Maven') {
            steps {
                echo 'Exécution des tests en ignorant Liquibase et DB...'
                // On force la désactivation de Liquibase et on simule les secrets Google
                sh """
                ./mvnw clean test \
                -Dspring.liquibase.enabled=false \
                -Dspring.security.oauth2.client.registration.google.client-id=${env.GOOGLE_CLIENT_ID} \
                -Dspring.security.oauth2.client.registration.google.client-secret=${env.GOOGLE_CLIENT_SECRET}
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Construction de l\'image Docker...'
                sh 'docker build -t smart-spring-app:latest .'
            }
        }
    }

    post {
        always {
            script {
                try {
                    junit '**/target/surefire-reports/*.xml'
                } catch (Exception e) {
                    echo 'Aucun rapport de test trouvé.'
                }
            }
        }
        success { echo '✅ Build et Tests terminés avec succès !' }
        failure { echo '❌ Échec du pipeline.' }
    }
}