pipeline{
  agent any
  tools {
    git 'Default'
    maven 'maven'
  }
  stages {
    stage('getCode') {
      steps {
        git credentialsId: 'trainyou-credentials', url: 'https://github.com/trainyou/maven-web-application.git'
      }
    }
    stage('testPackage') {
      steps {
        sh 'mvn clean test'
      }
    }
    stage('buildPackage') {
      steps {
        sh 'mvn clean package'
      }
    }
  }  
}
