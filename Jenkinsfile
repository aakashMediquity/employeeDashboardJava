pipeline {
    agent any

    stages {
        stage('Clone') {
            steps {
                git 'https://github.com/aakashMediquity/employeeDashboardJava.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t aakash6012/employeeimg:v1 .'
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push aakash6012/employeeimg:v1
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f deployment.yaml'
                sh 'kubectl apply -f service.yaml'
            }
        }
    }
}

