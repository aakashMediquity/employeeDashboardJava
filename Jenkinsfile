pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'aakash6012/employeeimg:v1'
        KUBECONFIG = "/var/lib/jenkins/.kube/config"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/aakashMediquity/employee-api.git'
            }
        }

        stage('Build App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh '''
                        echo "$PASSWORD" | docker login -u "$USERNAME" --password-stdin
                        docker push $DOCKER_IMAGE
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl apply -f deployment.yaml --validate=false
                    kubectl apply -f service.yaml --validate=false
                '''
            }
        }
    }
}
