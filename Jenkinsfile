pipeline {
    agent any

    environment {
        GOOGLE_CLIENT_ID = "dummy"
        GOOGLE_CLIENT_SECRET = "dummy"
        SPRING_DATASOURCE_URL = "jdbc:postgresql://postgres-db:5432/smartSpring"
        SPRING_DATASOURCE_USERNAME = "admin"
        SPRING_DATASOURCE_PASSWORD = "admin_password"
        MAVEN_OPTS = "-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300"
        SONAR_HOST_URL = "http://sonarqube:9000"
    }

    stages {
        stage('Preparation') {
            steps {
                echo 'Préparation de l\'environnement...'
                sh 'chmod +x mvnw'
            }
        }

        stage('Build & Tests with Coverage') {
            steps {
                echo 'Construction de l\'application et exécution des tests avec couverture...'
                sh """
                ./mvnw clean test package \
                    -Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
                    -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
                    -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
                    -Dspring.jpa.hibernate.ddl-auto=create-drop
                """
                echo 'Rapport JaCoCo généré dans target/site/jacoco/'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        stage('Setup SonarQube Project') {
            steps {
                echo 'Vérification/Création du projet dans SonarQube...'
                script {
                    sh '''
                    echo "Vérification de la disponibilité de SonarQube..."
                    for i in {1..30}; do
                        if curl -s http://sonarqube:9000/api/system/status | grep -q '"status":"UP"'; then
                            echo "SonarQube est prêt!"
                            break
                        fi
                        echo "Attente de SonarQube... ($i/30)"
                        sleep 10
                    done

                    PROJECT_EXISTS=$(curl -s -u admin:P@ssWord123! "http://sonarqube:9000/api/projects/search?projects=smartlogi-v2" | grep -c '"key":"smartlogi-v2"' || true)

                    if [ "$PROJECT_EXISTS" -eq 0 ]; then
                        echo "Création du projet SmartLogi-V2 dans SonarQube..."
                        curl -s -u admin:P@ssWord123! -X POST "http://sonarqube:9000/api/projects/create" \
                            -d "project=smartlogi-v2" \
                            -d "name=SmartLogi-V2"
                        echo "Projet créé avec succès!"
                    else
                        echo "Le projet existe déjà dans SonarQube"
                    fi
                    '''
                }
            }
        }

        stage('Code Quality - SonarQube') {
            steps {
                echo 'Analyse de la qualité du code avec SonarQube...'
                sh """
                ./mvnw sonar:sonar \
                    -Dsonar.host.url=${SONAR_HOST_URL} \
                    -Dsonar.login=admin \
                    -Dsonar.password=P@ssWord123! \
                    -Dsonar.projectKey=smartlogi-v2 \
                    -Dsonar.projectName='SmartLogi-V2' \
                    -Dsonar.java.binaries=target/classes \
                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                """
            }
        }
    }

    post {
        always {
            echo 'Traitement des rapports de tests...'
        }

        success {
            echo '========================================='
            echo 'PIPELINE RÉUSSI !'
            echo '========================================='
            echo 'Consultez les rapports:'
            echo '   - JaCoCo Coverage: Jenkins > Build > Coverage Report'
            echo '   - SonarQube: http://localhost:9000'
            echo '   - Projet SonarQube: smartlogi-v2'
            echo '   - Identifiants SonarQube: admin/P@ssWord123!'
            echo '========================================='
        }
        failure {
            echo 'ÉCHEC DU PIPELINE. Vérifiez les logs Maven et SonarQube.'
        }
    }
}
