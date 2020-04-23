pipeline {
    agent any

    environment {
        EMAIL_ADMIN = 'luceroqpdb@gmail.com'
        EMAIL_TEAM = 'windyriey@gmail.com, coki.gray@gmail.com'
        BUILD_VERSION = "1.0.$env.BUILD_NUMBER"
        IMAGE_DOCKER = 'lucerodocker/moi'
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                }
            }
        }

        stage('Unit Tests and Analysis') {
            steps {
                sh 'echo "Running tests"'
                sh './gradlew jacocoTestReport'

                sh 'echo "Analyzing Data"'
                sh './gradlew sonarqube'
            }
        }

        stage('PromoteToDevelop') {
            steps {
                sh 'echo "Deploying to develop"'
                sh 'docker-compose down'
                sh 'docker stop $(docker ps -q)'
                sh 'docker-compose up -d --build'
            }
        }

        stage('Acceptance Tests') {
            steps {
                sh 'echo "Run Acceptance tests"'
            }
        }

        stage('PublishArtifacts') {
            stages {
                stage('Publish Release Artifacts') {
                    when {
                        anyOf {
                            branch 'master'
                            branch pattern: "release*", comparator: "GLOB"
                        }
                    }
                    steps{
                        sh 'echo "Publish to artifactory when release"'
                        sh './gradlew -Prelease_version=$BUILD_VERSION artifactoryPublish'
                    }
                }
                stage('Publish SnapShot') {
                    when {
                        branch 'develop'
                    }
                    steps{
                        sh 'echo "Publish to artifactory when develop"'
                        sh './gradlew artifactoryPublish'
                    }
                }
            }
        }

        stage('PublishToDockerHub') {
            stages {
                stage('Publish Latest') {
                    steps {
                      sh 'echo "Publish to Docker Hub"'
                      sh 'docker login -u lucerodocker -p lucerodocker'
                      sh 'docker-compose push'
                    }
                }
                stage('Publish Release') {
                    when {
                        anyOf {
                            branch 'master'
                            branch pattern: "release*", comparator: "GLOB"
                        }
                    }
                    steps {
                      sh 'echo "Publish to Docker Hub"'
                      sh 'docker login -u lucerodocker -p lucerodocker'
                      sh 'docker tag $IMAGE_DOCKER:latest $IMAGE_DOCKER:$BUILD_VERSION'
                      sh 'docker push $IMAGE_DOCKER'
                    }
                }
            }
        }

        stage('Deploy to QA') {
            environment {
                DOC_COMPOSE = 'docker-compose-go.yml'
                DC_DIR = 'docker/compose'
                QA_DIR = '/deployments/qa'
                ENV_DIR = 'env/qa.env'
            }
            when {
                anyOf {
                    branch 'master'
                    branch pattern: "release*", comparator: "GLOB"
                    branch 'develop'
                }
            }
            steps {
                sh 'echo "Deploying to QA"'
                sh 'cp $DC_DIR/$DOC_COMPOSE $QA_DIR'
                sh 'cp $DC_DIR/$ENV_DIR $QA_DIR/.env'
                sh 'ls -la $QA_DIR'
                sh 'docker-compose -f $QA_DIR/$DOC_COMPOSE down'
                sh 'docker-compose -f $QA_DIR/$DOC_COMPOSE up -d'
            }
        }

        stage('Clean WorkSpace') {
            steps {
                sh 'docker image prune -a'
            }
        }
    }

    post {
        always {
            junit 'build/test-results/**/*.xml'
        }

        failure {
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n Pipeline: ${env.BUILD_URL} has been well executed",
                     recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                     subject: "Jenkins Build ${currentBuild.currentResult} # $env.BUILD_NUMBER",
                     attachmentsPattern: 'generatedFile.txt',
                     attachLog: true,
                     to: "$EMAIL_TEAM"
        }
        fixed {
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n Pipeline: ${env.BUILD_URL} has been well executed",
                     recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                     subject: "Fixed Jenkins Build ${currentBuild.currentResult} # $env.BUILD_NUMBER",
                     to: "$EMAIL_TEAM"
        }

        aborted {
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n Pipeline: ${env.BUILD_URL} has been well executed",
                     recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                     subject: "Jenkins Build ${currentBuild.currentResult} # $env.BUILD_NUMBER",
                     to: "$EMAIL_ADMIN"
        }
    }
}