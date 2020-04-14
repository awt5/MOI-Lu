pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Start Building app"'
                sh 'echo "Continue building"'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'echo "Running Tests"'
            }
        }
        stage('Publish Artifact') {
            steps {
                sh 'echo $?'
            }
        }
        stage('Deploy') {
            parallel {
              stage('DeployToDevEnv') {
                steps {
                  sh 'echo "Deploying to Dev Enviroment"'
                }
              }
              stage('DeployToQAEnv') {
                steps {
                  sh 'echo "Deploying to QA Enviroment"'
                }
              }
            }
        }
    }
    environment {
        EMAIL_ME = 'coki.gray@gmail.com'
    }
    post {
        always {
            emailext to: "${EMAIL_ME}",
                 subject: "${DEFAULT_SUBJECT}",
                 body: "${DEFAULT_CONTENT}",
                 recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']]
        }
    }
}