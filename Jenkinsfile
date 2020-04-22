pipeline {
    agent any
    stages {
        stage('Build') {
            parallel {
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

        stage('Tests') {
            steps {
                sh 'echo Running tests'
                sh './gradlew jacocoTestReport'
            }
        }

        stage('Sonarqube') {
            steps {
                sh './gradlew sonarqube'
            }
        }

        stage('Publish Artifacts') {
            parallel {
                stage('Publish when Realise') {
                    when {
                        branch 'master'
                    }
                    steps{
                        sh 'echo "Publish to artifactory"'
                        sh './gradlew -PcurrentVersion=1.0 -Partifactory_repokey=libs-release-local artifactoryPublish'
                    }
                }
                stage('Publish daily') {
                    when {
                        not { branch 'master' }
                    }
                    steps{
                        sh 'echo "Publish to artifactory"'
                        sh './gradlew artifactoryPublish'
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
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
                sh 'echo "Deploying to develop"'
                sh 'cp docker-compose-go.yml $DEV_DIR'
                sh 'ls $DEV_DIR'
                sh 'docker-compose down'
                sh 'docker-compose -f $DEV_DIR/docker-compose-go.yml up -d --build'
            }
        }

        stage('Deploy to QA') {
            environment {
                QA_DIR = './deployments/qa'
            }
            steps {
                sh 'echo "Deploying to QA"'
                sh 'cp docker-compose-go.yml $QA_DIR'
                sh 'ls $QA_DIR'
                sh 'docker-compose down'
                sh 'docker-compose -f $QA_DIR/docker-compose-go.yml up -d --build'
            }
        }

        stage('Clean WorkSpace') {
            steps {
                sh 'docker image prune -a'
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
    }
}