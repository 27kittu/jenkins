pipeline{
    agent any
    tools{
        git 'Default'
        maven 'maven'
    }
    options{ 
        timestamps()
    }
    stages {
        stage('getCode') {
            steps {
                git credentialsId: 'git_token', url: 'https://github.com/trainyou/maven-web-application.git'
            }
        }
        stage('packageCode') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('runSonar') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }
        stage('buildImage'){
            steps{
                sh "docker build -t heartocean/trial1/tomcat_maven:${BUILD_NUMBER} ."
            }
        }
        stage('pushimage') {
            steps {
                withCredentials([string(credentialsId: 'docker_pass', variable: 'docker_pass')]) {
                    sh "docker login -u heartocean -p ${docker_pass}"
                } 
                sh "docker push heartocean/trial1/tomcat_maven:${BUILD_NUMBER}"
            }
        }
        stage('buildImage'){
            steps{
                sh "docker run -d --name tomcat_maven_web -p 8080:80 heartocean/trial1/tomcat_maven:${BUILD_NUMBER}"
            }
        }
    }
}
