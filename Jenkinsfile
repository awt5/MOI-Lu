pipeline {
    agent any
    stages {
        stage('Build') {
            stages {
                stage('Build Realise') {
                    when {
                        branch 'master'
                    }
                    steps {
                        sh './gradlew clean build -Pcurrent_version=1.0'
                    }
                }
                stage('Build daily') {
                    when {
                        not { branch 'master' }
                    }
                    steps {
                        sh './gradlew clean build'
                    }
                }
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

        stage('Publish Artifactories') {
            stages {
                stage('Publish when Realise') {
                    when {
                        branch 'master'
                    }
                    steps{
                        sh './gradlew -PcurrentVersion=1.0 artifactoryPublish -Partifactory_repokey=libs-release-local'
                    }
                }
                stage('Publish daily') {
                    when {
                        not { branch 'master' }
                    }
                    steps{
                        sh './gradlew artifactoryPublish'
                    }
                }
            }
        }

        stage('Publish to Docker Hub') {
            steps {
              sh 'echo "Building a new image"'
              sh 'docker-compose down'
              sh 'docker-compose build'

              sh 'echo "Publish to Docker Hub"'
              sh 'docker login -u lucerodocker -p lucerodocker'
              sh 'docker-compose push'
            }
        }

        stage('Deploy to Develop') {
            environment {
                DEV_DIR = './deployments/dev'
            }
            steps {
                sh 'cp docker-compose-go.yml $DEV_DIR'
                sh 'cd $DEV_DIR'
                sh 'docker-compose down'
                sh 'docker-compose -f docker-compose-go.yml up -d --build'
            }
        }

        stage('Deploy to QA') {
            environment {
                QA_DIR = './deployments/qa'
            }
            steps {
                sh 'cp docker-compose-go.yml $QA_DIR'
                sh 'cd $QA_DIR'
                sh 'docker-compose down'
                sh 'docker-compose -f docker-compose-go.yml up -d --build'
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