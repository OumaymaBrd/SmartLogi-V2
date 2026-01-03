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
        GIT_CREDENTIALS_ID = "github-credentials"
        PRODUCTION_BRANCH = "product"
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

        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                echo 'Construction de l\'image Docker...'
                script {
                    sh """
                    docker build -t smartlogi-v2:latest .
                    docker tag smartlogi-v2:latest smartlogi-v2:\${BUILD_NUMBER}
                    """
                    echo "Image Docker créée: smartlogi-v2:latest et smartlogi-v2:\${BUILD_NUMBER}"
                }
            }
        }

        stage('Push to Production Branch') {
            when {
                branch 'master'
            }
            steps {
                echo 'Push automatique vers la branche product...'
                script {
                    sh """
                    # Configuration Git
                    git config user.name "Jenkins CI"
                    git config user.email "jenkins@smartlogi.com"

                    # Création ou mise à jour de la branche product
                    git checkout -B ${PRODUCTION_BRANCH}

                    # Tag de version
                    git tag -a v\${BUILD_NUMBER} -m "Version \${BUILD_NUMBER} - Build réussi"

                    # Push vers GitHub
                    git push origin ${PRODUCTION_BRANCH} --force
                    git push origin v\${BUILD_NUMBER}

                    echo "✓ Version \${BUILD_NUMBER} poussée vers ${PRODUCTION_BRANCH}"
                    """
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'product'
            }
            steps {
                echo 'Déploiement de l\'application en production...'
                script {
                    sh """
                    echo "=== Déploiement de SmartLogi-V2 en production ==="

                    # Arrêt des anciens conteneurs
                    docker-compose -f docker-compose.production.yml down || true

                    # Démarrage des nouveaux conteneurs
                    docker-compose -f docker-compose.production.yml up -d

                    # Vérification du déploiement
                    echo "Attente du démarrage de l'application..."
                    sleep 30

                    if curl -f http://localhost:9089/actuator/health; then
                        echo "✓ Application déployée avec succès!"
                    else
                        echo "✗ Échec du déploiement"
                        exit 1
                    fi
                    """
                }
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
            script {
                if (env.BRANCH_NAME == 'master') {
                    echo '✓ Build réussi sur master'
                    echo '✓ Version poussée vers product'
                    echo '✓ Image Docker créée: smartlogi-v2:' + env.BUILD_NUMBER
                } else if (env.BRANCH_NAME == 'product') {
                    echo '✓ Déploiement réussi en production'
                    echo '✓ Application accessible sur http://localhost:9089'
                }
            }
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
