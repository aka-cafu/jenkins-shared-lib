def call() {
  node {
    stage('Checkout') {
      checkout scm
    }
    def p = pipelineCfg()

    if (p.buildImage == true) {
        stage('Build') {
         docker.build(p.imageName:"${JOB_NUMBER}")
          sh 'docker images'
       }       
  }
 }
}
