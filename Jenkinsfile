pipeline {
    agent any

environment {
    DB_PASSWORD    = credentials('DB_PASSWORD')
    JWT_SECRET     = credentials('JWT_SECRET')
    ADMIN_EMAIL    = credentials('ADMIN_EMAIL')
    ADMIN_PASSWORD = credentials('ADMIN_PASSWORD')

    PATH+POWERSHELL = 'C:\\Windows\\System32\\WindowsPowerShell\\v1.0'
}
    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend: Build & Test') {
            steps {
                dir('finance-dashboard') {
                    bat 'mvnw.cmd clean verify'
                }
            }
        }

        stage('Frontend: Install & Build') {
            steps {
                dir('finance-ui') {
                    bat 'npm install'
                    bat 'npm run build'
                }
            }
        }

        stage('Docker: Build Images') {
            steps {
                bat 'docker compose build'
            }
        }

        stage('Deploy') {
            steps {
                bat 'docker compose down'
                bat 'docker compose up -d'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully — app redeployed.'
        }
        failure {
            echo 'Pipeline failed — check the stage logs above.'
        }
    }
}