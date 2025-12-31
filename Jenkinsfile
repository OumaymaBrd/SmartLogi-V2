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
                // On entre dans le dossier où se trouve mvnw
                dir('smart-spring') {
                    echo "Nettoyage dans le dossier smart-spring..."
                    sh 'chmod +x mvnw'
                }
            }
        }

        stage('Compilation & Tests Unitaires') {
            steps {
                dir('smart-spring') {
                    echo 'Lancement des tests avec Maven...'
                    sh "./mvnw clean test -Dspring.datasource.url=${env.DB_URL} -Dspring.datasource.username=${env.DB_USER} -Dspring.datasource.password=${env.DB_PASS}"
                }
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                dir('smart-spring') {
                    echo 'Construction de l\'image...'
                    sh 'docker build -t smart-spring-app:latest .'
                }
            }
        }
    }

    post {
        success { echo '✅ Pipeline réussi !' }
        failure { echo '❌ Pipeline échoué.' }
        always {
            dir('smart-spring') {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}