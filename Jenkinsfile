pipeline {
    agent any

    environment {
        // Variables pour la connexion à la base de données Docker
        DB_URL = "jdbc:postgresql://postgres-db:5432/smartSpring"
        DB_USER = "admin"
        DB_PASS = "admin_password"
    }

    stages {
        stage('Nettoyage & Préparation') {
            steps {
                echo 'Préparation de l\'environnement...'
                // Donne les droits d'exécution au script Maven Wrapper
                sh 'chmod +x mvnw'
            }
        }

        stage('Compilation & Tests Unitaires') {
            steps {
                echo 'Lancement des tests Maven...'
                // Exécution des tests en injectant les paramètres de la DB Docker
                sh "./mvnw clean test -Dspring.datasource.url=${env.DB_URL} -Dspring.datasource.username=${env.DB_USER} -Dspring.datasource.password=${env.DB_PASS}"
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                echo 'Construction de l\'image Docker de l\'application...'
                // Construit l'image locale en utilisant le Dockerfile présent
                sh 'docker build -t smart-spring-app:latest .'
            }
        }

        stage('Déploiement (Mise à jour)') {
            steps {
                echo 'Redémarrage du service backend...'
                // Met à jour le conteneur backend sans toucher à la base de données
                sh 'docker-compose up -d --no-deps app-backend'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès ! Votre application est à jour.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les erreurs ci-dessus.'
        }
        always {
            // Publication des rapports de tests JUnit dans Jenkins
            junit '**/target/surefire-reports/*.xml'
        }
    }
}