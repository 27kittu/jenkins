pipeline{
    agent any
    options{
        timestamps()
    }
    tools {
      maven 'maven'
    }
    stages {
      stage('checkoutCode') {
        steps {
          git credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
        }
      }
      stage('Build') {
        steps {
           sh 'mvn clean package' 
        }
    }
      stage('deployToTomcat') {
        steps {
            sshagent(['deployuser']) {
                sh 'scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/walmart_normal_pipe/target/maven-web-application.war ubuntu@18.204.213.43:/opt/tomcat/webapps'
            }
          }
        }   
    }
    post {
  success {
    slackSend color: "#4E4FEB", message: "Build Started: ${env.JOB_NAME} ${env.BUILD_NUMBER}"

  }
  failure {
    slackSend color: "#EF6262", message: "Build Started: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
  }
}
}