pipeline {
    agent any

    environment {
        EMAIL_ADMIN = 'luceroqpdb@gmail.com'
        EMAIL_TEAM = 'windyriey@gmail.com, coki.gray@gmail.com'
        BUILD_VERSION = "1.0.$env.BUILD_NUMBER"
        IMAGE_DOCKER = 'lucerodocker/moi'
    }

    stages {
        stage('Acceptance Tests') {
            steps {
                sh 'echo "Run Acceptance tests"'
                build job: 'jenkins_receiver', parameters: [string(name: 'tagName', value: "@acceptance")]
            }
        }

    }
}