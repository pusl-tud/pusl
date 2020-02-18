pipeline {
    agent any
    tools {
        maven 'Maven 3.6.2'
        jdk 'jdk11'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn clean install' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }

        stage('SonarQube analysis') {
            environment {
                scannerHome = tool 'SonarScanner 4.2'
            }
            steps {
                withSonarQubeEnv('SonarServer_bp2019') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage ('Production Build') {
            steps {
                sh 'mvn clean package -Pproduction'
            }
        }
    }
}