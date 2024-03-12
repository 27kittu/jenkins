pipeline {
    agent any
    tools {
    maven 'maven'
	}
    options {
        timestamps()
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
    }
	stages{
	    stage('checkoutCode') {
            steps {
		        git credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
		    }
	    }

        stage('buildPackage') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('deployTomcat') {
            steps {
                sshagent(['d7a406da-1788-4f6d-80cb-47c37723093d']) {
                    sh "scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/wal_pipe_file/target/maven-web-application.war ubuntu@172.31.90.50:/opt/tomcat/webapps/"
                }
            } 
        }
	}
    post {
        success{
            slackSend color: '', message: "Build Started: ${env.JOB_NAME} Build Number: ${env.BUILD_NUMBER}"
        }
        failure{
            slackSend color: '', message: "Build Started: ${env.JOB_NAME} Build Number: ${env.BUILD_NUMBER}"
        }
    }

}
