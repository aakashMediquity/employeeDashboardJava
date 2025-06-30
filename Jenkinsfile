pipeline {
  agent any

  environment {
    KUBECONFIG = '/var/lib/jenkins/.kube/config'
    IMAGE_TAG = "v${BUILD_NUMBER}"
    DOCKER_IMAGE = "aakash6012/employeeimg:${IMAGE_TAG}"
  }

  stages {
    stage('Clone') {
      steps {
        git 'https://github.com/aakashMediquity/employeeDashboardJava.git'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $DOCKER_IMAGE .'
      }
    }

    stage('Push to DockerHub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push $DOCKER_IMAGE
          '''
        }
      }
    }

    stage('Update Deployment with New Image') {
      steps {
        // Replace image in deployment.yaml with the current build tag
        sh 'sed -i "s|image: .*|image: $DOCKER_IMAGE|" deployment.yaml'
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh 'kubectl apply -f deployment.yaml --validate=false'
        sh 'kubectl apply -f service.yaml --validate=false'
      }
    }
  }
}
