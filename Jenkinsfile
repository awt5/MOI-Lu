pipeline {
    agent any

    environment {
        EMAIL_ME = 'luceroqpdb@gmail.com'
        BUILD_VERSION = "1.0.$env.BUILD_NUMBER"
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
                        sh './gradlew -PcurrentVersion=$BUILD_VERSION -Partifactory_repokey=libs-release-local artifactoryPublish'
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

        stage('PromoteToDevelop') {
            environment {
                DC_DIR = 'docker/compose'
                DOC_COMPOSE = 'docker-compose.yml'
            }
            steps {
                sh 'echo "Deploying to develop"'
                sh 'ls $DC_DIR'
                sh 'docker-compose -f $DC_DIR/$DOC_COMPOSE down'
                sh 'docker-compose -f $DC_DIR/$DOC_COMPOSE up -d --build'
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
                      sh 'docker tag moi:latest lucerodocker/moi:$BUILD_VERSION'
                      sh 'docker-compose push'
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
        aborted {
            sh 'echo Aborted'
        }
        failure {
            emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL} \n Pipeline: ${env.BUILD_URL} has been well executed",
                 recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                 subject: "Jenkins Build ${currentBuild.currentResult} # {$env.BUILD_NUMBER}: Job ${env.JOB_NAME}!"
        }
        fixed {
            sh 'echo Fixed'
        }
    }
}