pipeline{
    agent any
    options{ timestamps()}
    parameters {
        choice choices: ['master', 'development'], description: 'Need to select atleast one branch', name: 'Branch'
    }

    tools {
      git 'Default'
      maven 'maven'
    }
    stages {
        stage('checkoutCode') {
            steps {
                git branch: 'development', credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
            }
         }
        stage('validate_compile_package') {
            parallel {
                stage('validateCode') {
                    steps {
                        sh 'mvn clean validate'
                    }
                }
        
                stage('compileCode'){
                    steps{
                        sh 'mvn clean compile'
                    }
                }
                
                stage('testCode'){
                    steps{
                        sh 'mvn clean test'
                    }
                }
                
            }
        }
        stage('createPackage'){
            steps{
                sh 'mvn clean package'
            }
        }
        stage('installPackage'){
            steps{
                sh 'mvn clean install'
            }
        }
        stage('deployToNexus'){
            steps{
                sh 'mvn clean deploy'
            }
        }
        stage('uploadToTomcat'){
            steps{
                sshagent(['deploy_tomcat']) {
                    sh 'scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/wl_pipe_decl2_MvctpidNT_SL/target/ maven-web-application.war ubuntu@172.31.90.50:/opt/tomcat/webapps'
                }
            }
        }

    }
    post{
        success{
            slackSend color:'#5FF500', message:"Build Started: ${env.JOB_NAME} with Build Number: ${env.BUILD_NUMBER}"
        }
        failure{
            slackSend color: '#BB004D', message:"Build Started: ${env.JOB_NAME} with Build Number: ${env.BUILD_NUMBER}"
        }
    }
}