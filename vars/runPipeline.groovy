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
	p.deployCmd()
	  sh 'docker ps && sleep 60'
        }
 }
}
