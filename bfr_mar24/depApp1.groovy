pipeline{
  agent any
  tools { 
    git 'Default'
    maven 'maven'
  }
  triggers{ 
      githubPush() 
  }
  stages {
    stage('getCode') {
      steps {
        git credentialsId: 'git_token', url: 'https://github.com/trainyou/maven-web-application.git'
      }
    }
    stage('testPackage') {
      steps {
        sh "mvn clean test"
        }
    }
    stage('buildPackage') {
      steps {
        sh "mvn clean package"
        }
    }
    stage('deployPackageTomcat') {
      steps {
        sshagent(['ubuntu_cred']) {
          sh "scp -o StrictHostKeyChecking=no target/maven-web-application.war ubuntu@10.0.0.123:/opt/tomcat8/webapps/maven-web-application.war"
        }
      }
    }
  }
}
