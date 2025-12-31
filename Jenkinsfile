pipeline {
    agent any
    environment {
        GOOGLE_CLIENT_ID = "dummy"
        GOOGLE_CLIENT_SECRET = "dummy"
    }
    stages {
        stage('Preparation') {
            steps { sh 'chmod +x mvnw' }
        }
        stage('Tests Maven') {
            steps {
                // ignoreTestFailure=true permet de continuer le build même si un test échoue
                sh """
                ./mvnw clean test \
                -Dspring.liquibase.enabled=false \
                -Dmaven.test.failure.ignore=true \
                -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
                """
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t smart-spring-app-backend:latest .'
            }
        }
    }
}