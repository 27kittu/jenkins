pipeline{
    agent any
    tools {
        git 'Default'
        maven 'maven'                          
    }
    parameters {
        choice choices: ['master', 'prod', 'testing'], description: 'Please select the branch', name: 'Branch'
    }
    stages {
        stage('pullCodeSCM') {
            steps {
                git branch: "${params.Branch}", credentialsId: 'git_token', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('buildPackage') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('buildNpushDockerImage') {
            steps{
                withCredentials([string(credentialsId: 'docker_pass', variable: 'docker_login')]) {
                    sh "docker login -u heartocean -p ${docker_login}"
                }
                sh """\
                docker build -t heartocean/working:"${env.BUILD_NUMBER}" . \
                && docker push heartocean/working:"${env.BUILD_NUMBER}" """ 
            }
        }
        stage('modifyImageTag') {
            steps {
                sh "sed -i '4s|TAG|${env.BUILD_NUMBER}|' docker-compose.yml"
            }
        }
        stage('provisionRemoteContainer') {
            steps {
                sshagent(['ubuntu_login']) {
                    sh """\
                    scp docker-compose.yml ubuntu@172.31.34.57:/home/ubuntu/ \
                    && ssh ubuntu@172.31.34.57 docker-compose up -d"""
                }
            }
        }
    }
}