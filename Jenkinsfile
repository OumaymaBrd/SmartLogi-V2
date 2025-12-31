pipeline {
    agent any

    environment {
        // Paramètres de connexion au service postgres-db défini dans docker-compose
        DB_URL = "jdbc:postgresql://postgres-db:5432/smartSpring"
        DB_USER = "admin"
        DB_PASS = "admin_password"
    }

    stages {
        stage('Preparation') {
            steps {
                echo 'Vérification des fichiers...'
                // Affiche les fichiers pour confirmer que mvnw est bien présent
                sh 'ls -la'
                sh 'chmod +x mvnw'
            }
        }

        stage('Tests Maven') {
            steps {
                echo 'Lancement des tests unitaires...'
                // On injecte les paramètres de la base de données Docker
                sh "./mvnw clean test -Dspring.datasource.url=${env.DB_URL} -Dspring.datasource.username=${env.DB_USER} -Dspring.datasource.password=${env.DB_PASS}"
            }
        }

        stage('Build Image Docker') {
            steps {
                echo 'Création de l\'image de l\'application...'
                // Utilise le Dockerfile à la racine
                sh 'docker build -t smart-spring-app:latest .'
            }
        }
    }

    post {
        always {
            // Publie les résultats des tests dans l'interface Jenkins
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo ' Pipeline terminé avec succès !'
        }
        failure {
            echo ' Le pipeline a échoué. Vérifiez les fichiers manquants dans Git.'
        }
    }
}