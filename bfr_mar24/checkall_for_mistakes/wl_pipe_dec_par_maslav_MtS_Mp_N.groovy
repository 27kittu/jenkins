pipeline{
    agent{
        label 'nodes'
    }
    tools{
        git "Default"
        maven 'maven'
    }
    parameters{
        choice choices: ['master', 'development'], description: 'Please enter the branch: ', name: 'Branch'
    }
    options{
        timestamps()
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')


    }
    stages{
        stage('checkoutCode'){
            steps{
                
            }
        }
    }
}