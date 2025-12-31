pipeline {
    agent any

    environment {
        // Ces variables évitent que Spring ne s'arrête à cause de config manquantes
        GOOGLE_CLIENT_ID = "dummy-id"
        GOOGLE_CLIENT_SECRET = "dummy-secret"
    }

    stages {
        stage('Preparation') {
            steps {
                echo '🔧 Nettoyage et préparation des permissions...'
                // Supprime les résidus des builds précédents et rend le wrapper exécutable
                sh 'chmod +x mvnw'
            }
        }

        stage('Tests Maven (Mode Isolé)') {
            steps {
                echo '🧪 Exécution des tests en mode isolation (Sans DB)...'

                /* -Dspring.liquibase.enabled=false : Règle le problème du fichier changelog manquant.
                   -Dspring.autoconfigure.exclude=... : Empêche Spring de chercher PostgreSQL ou Hibernate.
                   Cela permet aux tests de tourner sans base de données réelle.
                */
                sh """
                ./mvnw clean test \
                -Dspring.liquibase.enabled=false \
                -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration \
                -Dspring.security.oauth2.client.registration.google.client-id=${env.GOOGLE_CLIENT_ID} \
                -Dspring.security.oauth2.client.registration.google.client-secret=${env.GOOGLE_CLIENT_SECRET}
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '📦 Construction de l\'image Docker...'
                // Utilise le nom d'image que vous avez défini dans votre docker-compose ou backend
                sh 'docker build -t smart-spring-app-backend:latest .'
            }
        }
    }

    post {
        always {
            // Publie les résultats des tests dans l'interface Jenkins
            script {
                try {
                    junit '**/target/surefire-reports/*.xml'
                } catch (Exception e) {
                    echo '⚠️ Aucun rapport de test trouvé (normal si la compilation a échoué).'
                }
            }
        }
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs ci-dessus.'
        }
    }
}