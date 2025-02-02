pipeline{
    agent any
    tools {
        git 'Default'
        maven 'maven'
    }
    parameters {
        choice choices: ['master', 'prod', 'testing'], description: 'Please select the branch', name: 'Branch'
    }
    options {
        timestamps()
    }

    stages {
        stage('pullCode') {
            steps {
                git branch: "${params.Branch}", credentialsId: 'trainyou_pass', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('buildPackage') {
            steps {
                sh "mvn clean package"
            }
        }
        stage('login&formDockerImage&Push') {
            steps {
                withCredentials([string(credentialsId: 'docker_sec', variable: 'docker_password')]) {
                    sh "docker login -u heartocean -p ${docker_password}"
                }
                sh """docker build -t heartocean/k8s:${env.BUILD_NUMBER} . \
                && docker push heartocean/k8s:${env.BUILD_NUMBER}"""
            }
        }
        stage('manipulateManifestFile') {
            steps {
                sh "sed -i '18s|Tag|${env.BUILD_NUMBER}|' k8s_pipe1.yaml"
            }
        }
        stage('manifestCluster') {
            steps {
                sh "kubectl apply -f k8s_pipe1.yaml"
            }
        }
    }
}
