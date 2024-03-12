pipeline{
  agent any
  triggers{ 
      githubPush() 
  }
  environment{
    REDKEY=credentials('ubuntu_redkey')
  }
  stages {
    stage('getCode') {
      steps {
          git credentialsId: 'git_token', url: 'https://github.com/trainyou/ansible.git'
      }
    }
    stage('runPlaybooks') {
      steps {
        sh "ansible-playbook -i inventory/test/hosts playbooks/dec23/installTomcat.yml -u ubuntu --private-key=$REDKEY --ssh-common-args='-o StrictHostKeyChecking=no' "
        }
    }
  }
}
