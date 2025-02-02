pipeline{
    options{timestamps()}
    agent any
    tools{
        maven 'maven'
        git 'Default'
    }
    parameters{
        choice choices: ['master', 'development', 'stage', 'test', 'uat'], description: 'Please select the branch to run pipeline', name: 'BranchName'
    }
    stages{
        stage('checkoutCode'){
            steps{
                git branch: "$params.BranchName", credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('Test'){
            steps{
                sh 'mvn clean test'
            }
        }
        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
        }
        stage('uploadToNexus'){
            steps{
                sh 'mvn clean deploy'
            }
        }
        stage('deployToTomcat'){
            steps{
                sshagent(['deploy_tomcat']) {
                    sh 'scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/walmart_new_pipe1/target/maven-web-application.war ubuntu@44.211.45.246:/opt/tomcat/webapps' 

                }

            }
        }
    }
    post{
        success{
            slackSend color:'#5FF500', message: "Build started: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
        failure{
            slackSend color: '#BB004D', message: "Build started: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
    }

}