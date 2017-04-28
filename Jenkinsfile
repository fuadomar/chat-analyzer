node {

  checkout scm

  env.PATH = "${tool 'Maven3'}/bin:${env.PATH}"

  stage('Package and Test') {

      sh 'sudo mvn clean package docker:build'
  }

  stage ('Run Application') {

    try {

         sh 'sudo docker-compose up -d'

    } catch (error) {
    } finally {
      //do clean up
    }
  }

}