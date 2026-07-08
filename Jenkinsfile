pipeline {

    agent any

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Job Service') {
            steps {
                dir('job-service') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Candidate Service') {
            steps {
                dir('candidate-service') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build AI Screening Service') {
            steps {
                dir('AI-screening-service') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Notification Service') {
            steps {
                dir('notification-service') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build API Gateway') {
            steps {
                dir('Api-Gateway') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Discovery Server') {
            steps {
                dir('discovery-server') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {

                dir('auth-service') {
                    bat 'docker build -t auth-service .'
                }

                dir('job-service') {
                    bat 'docker build -t job-service .'
                }

                dir('candidate-service') {
                    bat 'docker build -t candidate-service .'
                }

                dir('AI-screening-service') {
                    bat 'docker build -t ai-screening-service .'
                }

                dir('notification-service') {
                    bat 'docker build -t notification-service .'
                }

                dir('Api-Gateway') {
                    bat 'docker build -t api-gateway .'
                }

                dir('discovery-server') {
                    bat 'docker build -t discovery-server .'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                bat 'docker compose down'
                bat 'docker compose up -d'
            }
        }

    }

    post {

        success {
            echo '========================================='
            echo ' SmartHire Pipeline Executed Successfully'
            echo '========================================='
        }

        failure {
            echo '========================================='
            echo ' SmartHire Pipeline Failed'
            echo '========================================='
        }

        always {
            echo 'Pipeline Finished.'
        }
    }
}