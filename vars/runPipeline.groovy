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
	  sh docker.image(p.imageName).withRun('-d -p 8091:80')
        }
 }
}
