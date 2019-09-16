def call() {
  def values = terraformAwsEc2()
  if (params.ENVIRONMENT == 'PROD') {
     sh "terraform init -backend-config='${values.s3Bucket}' -backend-config='key=application/${params.NAME}-${params.TAG}/terraform.tfstate' -backend-config='region=${values.awsRegion}' && sed -i 's/appname/${params.USERDATA}/g' main.tf"
  } else {
    sh "terraform init -backend-config='bucket=${values.s3BucketDevHom}' -backend-config='key=application/${params.NAME}-${params.TAG}/terraform.tfstate' -backend-config='region=${values.awsRegionDevHom}' && sed -i 's/appname/${params.USERDATA}/g' main.tf"
  }
}