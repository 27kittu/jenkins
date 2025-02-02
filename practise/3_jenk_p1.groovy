pipeline {
  agent any
  tools {
    git 'Default'
  }
  stages {
    stage('gitScm') {
      steps {
        git credentailsId: 'trainyou_cred', url: 'https://github.com/trainyou/spring-boot-mongo-docker-pvt.git' 
      }
    }
  }
}
