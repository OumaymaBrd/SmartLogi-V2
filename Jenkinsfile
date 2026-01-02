pipeline {
    agent any

    environment {
        GOOGLE_CLIENT_ID = "dummy"
        GOOGLE_CLIENT_SECRET = "dummy"
        SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/smartSpring"
        SPRING_DATASOURCE_USERNAME = "admin"
        SPRING_DATASOURCE_PASSWORD = "admin_password"
        MAVEN_OPTS = "-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300"
        SONAR_HOST_URL = "http://sonarqube:9000"
        SONAR_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Preparation') {
            steps {
                echo '🔧 Nettoyage des anciens conteneurs et préparation...'
                sh """
                chmod +x mvnw
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

                echo 'Attente du démarrage (15s)...'
                sleep 15

                # Vérifie si la DB est prête à accepter des connexions
                docker exec test-postgres pg_isready -U admin
                """
            }
        }

        stage('Tests Maven') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                sh """
                ./mvnw clean test \
                    -Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
                    -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
                    -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
                    -Dspring.jpa.hibernate.ddl-auto=create-drop
                """
            }
        }

        stage('Code Coverage - JaCoCo') {
            steps {
                echo '📊 Génération du rapport de couverture de code JaCoCo...'
                sh './mvnw jacoco:report'
            }
            post {
                always {
                    // Publication du rapport JaCoCo dans Jenkins
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
                echo '🔧 Vérification/Création du projet dans SonarQube...'
                script {
                    sh '''
                    # Attendre que SonarQube soit prêt
                    echo "Vérification de la disponibilité de SonarQube..."
                    for i in {1..30}; do
                        if curl -s http://sonarqube:9000/api/system/status | grep -q '"status":"UP"'; then
                            echo "✅ SonarQube est prêt!"
                            break
                        fi
                        echo "   Attente de SonarQube... ($i/30)"
                        sleep 10
                    done

                    # Vérifier si le projet existe
                    PROJECT_EXISTS=$(curl -s -u admin:admin "http://sonarqube:9000/api/projects/search?projects=smartlogi-v2" | grep -c '"key":"smartlogi-v2"' || true)

                    if [ "$PROJECT_EXISTS" -eq 0 ]; then
                        echo "📝 Création du projet SmartLogi-V2 dans SonarQube..."
                        curl -s -u admin:admin -X POST "http://sonarqube:9000/api/projects/create" \
                            -d "project=smartlogi-v2" \
                            -d "name=SmartLogi-V2"
                        echo "✅ Projet créé avec succès!"
                    else
                        echo "✅ Le projet existe déjà dans SonarQube"
                    fi
                    '''
                }
            }
        }

        stage('Code Quality - SonarQube') {
            steps {
                echo '🔍 Analyse de la qualité du code avec SonarQube...'
                script {
                    sh """
                    ./mvnw sonar:sonar \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.token=${SONAR_TOKEN} \
                        -Dsonar.projectKey=smartlogi-v2 \
                        -Dsonar.projectName='SmartLogi-V2' \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '📦 Construction de l\'image Docker Backend...'
                timeout(time: 20, unit: 'MINUTES') {
                    sh '''
                    docker build -t smart-spring-app-backend:latest . 2>&1 | while IFS= read -r line; do
                        echo "$line"
                        sleep 0.1
                    done
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '📊 Traitement des rapports de tests...'
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true

            // Publication du rapport JaCoCo dans Jenkins
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage Report',
                reportTitles: 'Code Coverage'
            ])

            echo '🧹 Arrêt de la base de données de test...'
            sh """
            docker stop test-postgres || true
            docker rm test-postgres || true
            """
        }

        success {
            echo '✅ PIPELINE RÉUSSI ! L\'image Docker est prête et les tests sont validés.'
            echo '📊 Consultez les rapports:'
            echo '   - JaCoCo: Jenkins → JaCoCo Coverage Report'
            echo '   - SonarQube: http://localhost:9000'
        }
        failure {
            echo '❌ ÉCHEC DU PIPELINE. Vérifiez les logs Maven ou Docker.'
        }
    }
}
