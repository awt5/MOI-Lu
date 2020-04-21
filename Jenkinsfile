pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew jacocoTestReport'
            }
        }
        stage('Sonarqube') {
            steps {
                sh './gradlew sonarqube'
            }
        }
        stage('Deploy') {
            parallel {
              stage('DeployToDevEnv') {
                steps {
                  sh 'docker-compose stop'
                  sh 'docker-compose up --build'
                }
              }
              stage('DeployToQAEnv') {
                steps {
                  sh 'echo "Deploying to QA Enviroment"'
                }
              }
            }
        }
        stage('Publish Binaries') {
            steps{
                sh './gradlew artifactoryPublish'
            }
        }
    }
    environment {
        EMAIL_ME = 'luceroqpdb@gmail.com'
    }
    post {
        failure {
            junit 'build/test-results/**/*.xml'
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL} \n Pipeline: ${env.BUILD_URL} has been well executed",
                 recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                 subject: "Jenkins Build ${currentBuild.currentResult} # {$env.BUILD_NUMBER}: Job ${env.JOB_NAME}!"
        }
        success {
            archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
    }
}