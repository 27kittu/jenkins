pipeline{
    agent any
    tools{
        git 'Default'
        maven 'maven'
    }
    parameters {
        choice choices: ['master', 'development'], description: 'Please select the Branch name', name: 'Branch'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
        timestamps()
    }
    stages {
        stage('checkoutCode') {
            steps {
                git branch:"${params.Branch}", credentialsId: 'ccbc4957-1a1d-4198-83bb-c06625b2c391', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('testCode_exexuteSonar') {
            parallel {
                stage('testCode'){
                    steps{
                        sh 'mvn clean test'
                    }
                }
                stage('executeSonar'){
                    steps{
                        sh 'mvn clean sonar:sonar'
                    }
                }

            }
        }
        stage('uploadToNexus'){
            steps{
                sh 'mvn clean deploy'
            }
        }
    }

    post {
        success {
            slackSend color: '#001eff', message: "Build started : $env.JOB_NAME, Build number: $env.BUILD_NUMBER"
        }
        failure {
            slackSend color: '#ff5400', message:"Build started : $env.JOB_NAME, Build number: $env.BUILD_NUMBER"
        }
    }
}