def call() {
  node {
    stage('Checkout') {
      checkout scm
    }
    def p = pipelineCfg()

    if (p.buildImage == true) {
        stage('Build') {
         docker.build(p.imageName)
          sh 'docker images'
        }       
  }
        stage('Deploy') {
	sh p.deployCmd felipe/website
	  sh 'docker ps && sleep 60'
        }
 }
}
