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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                        -Dsonar.projectVersion=${SONAR_PROJECT_VERSION} \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=${SONAR_AUTH_TOKEN}
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "❌ Quality Gate Failed: ${qg.status}"
                        }
                    }
                }
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
            echo "🔥 Build SUCCESSFUL!"
        }
        failure {
            echo "❌ Build FAILED!"
        }
    }
}
