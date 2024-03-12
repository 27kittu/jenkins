pipeline{
    agent any
    tools {
        maven 'maven'
        git 'Default'
        }
    options{timestamps()}
    parameters {
        choice choices: ['master', 'development', 'stage', 'test', 'uat'], description: 'Please select the branch to run pipeline', name: 'BranchName'
    }

    stages {
        stage('checkoutCode') {
            steps {
            git branch: "$params.BranchName", credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
            sh "echo $params.BranchName"
            }
            
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }  

        stage('deploytoTomcat') {
            steps {
                sshagent(['deployuser']) {
                sh 'scp -o StrictHostKeyChecking=no  /var/lib/jenkins/workspace/walmart_decl_parameters/target/maven-web-application.war ubuntu@54.158.53.78:/opt/tomcat/webapps'
            }
            }

        }
    }
}