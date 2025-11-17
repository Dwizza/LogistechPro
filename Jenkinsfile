pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        SONAR_AUTH_TOKEN = credentials('sonar-token')

        SONAR_PROJECT_KEY = 'LogistechPro2'
        SONAR_PROJECT_NAME = 'LogistechPro2'
        SONAR_PROJECT_VERSION = '1.0'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Dwizza/LogistechPro.git',
                        credentialsId: 'github-user'
                    ]]
                ])
            }
        }

        stage('Fix mvnw permissions') {
            steps {
                sh 'chmod +x mvnw'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis & Quality Gate') {
                    steps {
                        // 'SonarQube' est le nom du serveur que vous avez configuré
                        withSonarQubeEnv('SonarQube') {
                            // 'verify' a déjà créé le rapport jacoco.exec
                            // 'sonar:sonar' va l'envoyer à SonarQube
                            sh "mvn sonar:sonar -Dsonar.qualitygate.wait=true -Dsonar.qualitygate.timeout=300"
                        }

                        echo "SonarQube analysis completed successfully"
                    }
        }


        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/**/*.jar', fingerprint: true
                archiveArtifacts artifacts: 'target/site/jacoco/*', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "Build SUCCESSFUL!"
        }
        failure {
            echo "Build FAILED!"
        }
    }
}
