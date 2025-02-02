pipeline{
	agent any
	tools {
		git 'Default'
		maven 'maven'
	}
	stages {
    stage('getCode') {
      steps {
        git credentialsId: 'trainyoucred', url: 'https://github.com/trainyou/java-web-app-docker.git'
      }
    }
	  stage('validatePackage') {
	    steps {
        sh 'mvn clean validate'
	    }
    }
    stage('compilePackage'){
      steps{
        sh 'mvn clean compile'
      }
    }
    stage('buildPackage'){
      steps{
        sh 'mvn clean package -DskipTests'
      }
    }
    stage('installPackage'){
      steps{
        sh 'mvn clean install'
      }
    }
	}
}
