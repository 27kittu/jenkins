pipeline{
    agent { label 'nodes'}
    tools {
    git 'Default'
    maven 'maven'
    }
    options {
        timestamps()
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
    }
    parameters {
        choice choices: ['master', 'dev', 'test'], description: 'Select the branch', name: 'Branch'
    }

    stages {
        stage('getCode') {
            steps {
                git branch: "$params.Branch", credentialsId: 'c3136c60-c715-4d3d-9fdb-4311ee57fb3d', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('testCode') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('buildCode') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('deploy_sonar') {
            parallel {
               stage('deployCode') {
                    steps {
                     sh 'mvn clean deploy'
                    }
                } 
                stage('checkCodeQuality') {
                    steps {
                        sh 'mvn clean sonar:sonar'
                    }
                } 
            }
        }
        stage('deployToTomcat') {
            steps {
                sshagent(['tomcatlogin']) {
                    sh 'scp -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/jenk_app_1_Mtp_Nd_S_T/target/maven-web-application.war ec2-user@192.168.0.133:/opt/tomcat/webapps'
                }
            }
        }
    }
    post {
        success {
            slackSend channel:'#web_app, slack-channel1', color: '#71E600', message: "$env.JOB_NAME-->$env.BUILD_NUMBER"
        }
        failure {
            slackSend channel:'#web_app, slack-channel1', color: '#BF3A00', message: "$env.JOB_NAME-->$env.BUILD_NUMBER"
        }
    }
    
}