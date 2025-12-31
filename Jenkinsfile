pipeline {
    agent any

    environment {
        DB_URL = "jdbc:postgresql://postgres-db:5432/smartSpring"
        DB_USER = "admin"
        DB_PASS = "admin_password"
    }

    stages {
        stage('Nettoyage & Préparation') {
            steps {
                echo 'Nettoyage de l\'espace de travail...'
                sh 'chmod +x mvnw'
            }
        }

        stage('Compilation & Tests Unitaires') {
            steps {
                echo 'Lancement des tests avec Maven...'
                sh "./mvnw clean test -Dspring.datasource.url=${env.DB_URL} -Dspring.datasource.username=${env.DB_USER} -Dspring.datasource.password=${env.DB_PASS}"
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                echo 'Construction de la nouvelle image de l\'application...'
                sh 'docker build -t smart-spring-app:latest .'
            }
        }

        stage('Déploiement (Mise à jour)') {
            steps {
                echo 'Redémarrage du service app-backend avec la nouvelle image...'
                sh 'docker-compose up -d --no-deps app-backend'
            }
        }
    }

    post {
        success {
            echo ' Félicitations ! Le pipeline a réussi et l\'application est à jour.'
        }
        failure {
            echo ' Échec du pipeline. Vérifiez les logs de la console Jenkins.'
        }
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}