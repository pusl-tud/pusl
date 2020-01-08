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
            def scannerHome = tool 'SonarScanner';
            withSonarQubeEnv('sonar') {
                sh "${scannerHome}/bin/sonar-scanner"
             }
        }

        stage ('Production Build') {
            steps {
                sh 'mvn clean package -Pproduction'
            }
        }

        stage ('Deploy') {
            steps {
                sh 'sudo cp target/*.jar /var/run/zentraldatei/zentraldatei.jar'
            }
        }
    }
}