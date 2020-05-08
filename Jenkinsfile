pipeline {
    agent any

    stages {
        stage('Acceptance Tests') {
            steps {
                sh 'echo "Run Acceptance tests"'
                build job: 'jenkins_receiver', parameters: [string(name: 'TAG_NAME', value: "@acceptance")]
            }
        }

    }
}