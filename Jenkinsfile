pipeline {
  agent any

  tools {
    maven 'Maven 3.8.7' // Must match Jenkins Global Tool Configuration
  }

  environment {
    KUBECONFIG = '/var/lib/jenkins/.kube/config'
    IMAGE_TAG = "v${BUILD_NUMBER}"
    DOCKER_IMAGE = "aakash6012/employeeimg:${IMAGE_TAG}"
    MAVEN_HOME = tool 'Maven 3.8.7'
  }

  stages {
    stage('Checkout Code') {
      steps {
        git 'https://github.com/aakashMediquity/employeeDashboardJava.git'
      }
    }

    stage('List Test Reports') {
      steps {
        sh 'ls -l target/surefire-reports || echo "No test reports found"'
      }
    }
    stage('Build and Test with Maven') {
      steps {
        sh '${MAVEN_HOME}/bin/mvn clean verify'
      }
    }


stage('SonarQube Analysis') {
  steps {
    withSonarQubeEnv('SonarQubeLocal') {
      withCredentials([string(credentialsId: 'sonarqube-token2', variable: 'SONAR_TOKEN')]) {
        sh '${MAVEN_HOME}/bin/mvn sonar:sonar -Dsonar.projectKey=employee-api -Dsonar.token=$SONAR_TOKEN'
      }
    }
  }
}


    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $DOCKER_IMAGE .'
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push $DOCKER_IMAGE
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh 'sed -i "s|image: .*|image: $DOCKER_IMAGE|" deployment.yaml'
        sh 'kubectl apply -f deployment.yaml'
        sh 'kubectl apply -f service.yaml'
      }
    }
  }

  post {
    always {
      // ✅ Publish JUnit test results
      junit 'target/surefire-reports/*.xml'
    }
    success {
      echo "✅ Build #${BUILD_NUMBER} and deployment successful."
    }
    failure {
      echo "❌ Build failed. Please check logs."
    }
  }
}
