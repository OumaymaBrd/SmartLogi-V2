pipeline {
    agent any

    environment {
        SPRING_DATASOURCE_URL = "jdbc:postgresql://postgres-db:5432/smartSpring"
        SPRING_DATASOURCE_USERNAME = "admin"
        SPRING_DATASOURCE_PASSWORD = "admin_password"
        SONAR_HOST_URL = "http://sonarqube:9000"
        PRODUCTION_BRANCH = "product"
        // Configuration Docker Hub
        DOCKER_HUB_REPO = "oumaymabramid/smartlogi-v2"
        DOCKER_CREDENTIALS_ID = "docker-hub-credentials"
    }

    stages {
        stage('Preparation') {
            steps {
                sh 'chmod +x mvnw'
            }
        }

        stage('Build & Tests with Coverage') {
            steps {
                sh "./mvnw clean test package -Dspring.datasource.url=${SPRING_DATASOURCE_URL} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} -Dspring.jpa.hibernate.ddl-auto=create-drop"
            }
            post {
                always {
                    jacoco(execPattern: '**/target/jacoco.exec', classPattern: '**/target/classes', sourcePattern: '**/src/main/java', exclusionPattern: '**/test/**')
                }
            }
        }

        stage('Setup SonarQube Project') {
            steps {
                script {
                    sh '''
                    until curl -s http://sonarqube:9000/api/system/status | grep -q '"status":"UP"'; do sleep 5; done
                    PROJECT_EXISTS=$(curl -s -u admin:P@ssWord123! "http://sonarqube:9000/api/projects/search?projects=smartlogi-v2" | grep -c '"key":"smartlogi-v2"' || true)
                    if [ "$PROJECT_EXISTS" -eq 0 ]; then
                        curl -s -u admin:P@ssWord123! -X POST "http://sonarqube:9000/api/projects/create" -d "project=smartlogi-v2" -d "name=SmartLogi-V2"
                    fi
                    '''
                }
            }
        }

        stage('Code Quality - SonarQube') {
            steps {
                sh "./mvnw sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=admin -Dsonar.password=P@ssWord123! -Dsonar.projectKey=smartlogi-v2 -Dsonar.java.binaries=target/classes"
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    echo "Construction et Push de l'image Docker..."
                    sh "docker build -t ${DOCKER_HUB_REPO}:latest ."
                    sh "docker tag ${DOCKER_HUB_REPO}:latest ${DOCKER_HUB_REPO}:${env.BUILD_NUMBER}"

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        sh "docker push ${DOCKER_HUB_REPO}:latest"
                        sh "docker push ${DOCKER_HUB_REPO}:${env.BUILD_NUMBER}"
                    }
                }
            }
        }

        stage('Push to Production Branch') {
            when { branch 'master' }
            steps {
                script {
                    echo "Mise à jour de la branche ${PRODUCTION_BRANCH}..."
                    sh """
                    git config user.name "Jenkins CI"
                    git config user.email "jenkins@smartlogi.com"
                    git checkout -B ${PRODUCTION_BRANCH}
                    git push origin ${PRODUCTION_BRANCH} --force
                    """
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                script {
                    echo "Déploiement immédiat de la version ${DOCKER_HUB_REPO}:latest"
                    sh "docker pull ${DOCKER_HUB_REPO}:latest"
                    sh "docker-compose -f docker-compose.production.yml down || true"
                    sh "docker-compose -f docker-compose.production.yml up -d"
                }
            }
        }
    }
}