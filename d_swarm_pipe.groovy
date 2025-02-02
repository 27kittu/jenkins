pipeline{
    agent any
    tools {
        git 'Default'
        maven 'maven'                          
    }
    parameters {
        choice choices: ['master', 'prod', 'testing'], description: 'Please select the branch', name: 'Branch'
    }
    options{timestamps()}
    stages{
        stage("pullCodeSCM"){
            steps{
                git branch: "${params.Branch}", credentialsId: 'trainyou_token', url: 'https://github.com/trainyou/spring-boot-mongo-docker.git'
            }
        }
        stage("buildPackage"){
            steps{
                sh 'mvn clean package'
            }
        }
        stage("build&pushImage"){
            steps{
                withCredentials([string(credentialsId: 'docker_password', variable: 'docker_repo_cred')]) {
                    sh """\
                    docker login -u heartocean -p $docker_repo_cred \
                    && docker build -t heartocean/tral1:${env.BUILD_NUMBER} . \
                    && docker push heartocean/tral1:${env.BUILD_NUMBER}"""
                }
            }
        }
        stage("modifyComposeFile"){
            steps{
                sh "sed -i '5s|TAG|${env.BUILD_NUMBER}|' docker-compose.yml"
            }
        }
        stage("createSwarmRemotely"){
            steps{
                sshagent(['ubuntu_cred']) {
                    sh """scp -o StrictHostKeyChecking=no docker-compose.yml ubuntu@172.31.39.53:/home/ubuntu \
                    && ssh ubuntu@172.31.39.53 docker stack deploy --compose-file docker-compose.yml spring_1"""
                }
            }
        }
    }
}