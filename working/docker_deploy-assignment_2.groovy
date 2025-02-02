pipeline{
    agent any
    tools {
        git 'Default'
        maven 'maven'                          
    }
    parameters {
        choice choices: ['master', 'prod', 'testing'], description: 'Please select the branch', name: 'Branch'
        text defaultValue: 'container_1', description: 'Please give a container name', name: 'DockerContainerName'
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
        stage('buildDockerImage') {
            steps {
                sh """\
                aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 263487282506.dkr.ecr.us-east-1.amazonaws.com \
                && docker build -t 263487282506.dkr.ecr.us-east-1.amazonaws.com/docraft:maven_app_1 . \
                && docker push 263487282506.dkr.ecr.us-east-1.amazonaws.com/docraft:maven_app_1"""
            }
        }
        stage('provisionRemoteContainer') {
            steps {
                sshagent(['ubuntu_login']) {
                    sh """\
                    ssh -o StrictHostKeyChecking=no ubuntu@54.175.99.238 \
                    '''aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 263487282506.dkr.ecr.us-east-1.amazonaws.com \
                    && docker pull 263487282506.dkr.ecr.us-east-1.amazonaws.com/docraft:maven_app_1 \
                    && docker run --name ${params.DockerContainerName} -d -p 80:8080 263487282506.dkr.ecr.us-east-1.amazonaws.com/docraft:maven_app_1'''"""
                }
            }
        }

    }

}