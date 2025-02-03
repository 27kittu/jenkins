pipeline {
  agent any
  tools {
    git 'Default'
    maven 'maven_399'
  }
  stages {
    stage('gitScm') {
      steps {
        git credentialsId: 'trainyou_cred', url: 'https://github.com/trainyou/spring-boot-mongo-docker-pvt.git'
      }
    }

    stage('buildPkg') {
      steps {
        sh "mvn clean validate \
        && mvn clean compile \
        && mvn clean test \
        && mvn clean package"
      }
    }

    stage('buildDockImage') {
      steps {
        sh "docker build -t heartocean/cat:$BUILD_NUMBER ."
      }
    }

    stage('imageUploadReg') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker_cred', passwordVariable: 'docker_pass', usernameVariable: 'docker_username')]) {
          sh "docker push heartocean/cat:$BUILD_NUMBER"
        }
      }
    }
  }
}
