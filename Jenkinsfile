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
        stage('Deploy To DevEnv') {
            steps {
              sh 'echo "Deploying to Develop Environment"'
              sh 'docker-compose down'
              sh 'docker-compose up -d --build'
            }
        }

        stage('Deploy to Client') {
            parallel {
              stage('Deploy To Stage Area') {
                steps {
                  sh 'echo "Deploying to Stage"'
                }
              }
              stage('Deploy To Boss son') {
                steps {
                  sh 'echo "Deploying to Boss son"'
                }
              }
            }
        }

        stage('Publish to Docker Hub') {
            steps{
                sh 'docker login -u lucerodocker -p lucerodocker'
                sh 'docker-compose push'
            }
        }

        stage('Publish Artifactory') {
            when {
                branch 'develop'
            }
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