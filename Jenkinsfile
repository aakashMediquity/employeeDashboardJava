pipeline {
  agent any

  tools {
    maven 'Maven 3.8.7' // Must match the configured tool name
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

    stage('Build with Maven') {
      steps {
        sh '${MAVEN_HOME}/bin/mvn clean package'
      }
    }

    stage('Run Unit Tests') {
      steps {
        sh '${MAVEN_HOME}/bin/mvn test'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonarQubeServer') { // 🛠️ Must match the name from Jenkins → Configure System → SonarQube section
          sh '${MAVEN_HOME}/bin/mvn sonar:sonar -Dsonar.projectKey=employee-api'
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
      // ✅ Publishes JUnit test results to Jenkins UI
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
