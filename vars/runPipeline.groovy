def call() {
  node {
    stage('Checkout') {
      checkout scm
    }
    def p = pipelineCfg()

    if (p.buildImage == true) {
        stage('Build') {
         docker.build(p.imageName:"${env.BUILD_ID}")
          sh 'docker images'
       }       
  }
 }
}
