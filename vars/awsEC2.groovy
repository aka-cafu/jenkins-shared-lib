def call() {
 node {
     environment {
        REGION = "sa-east-1"
        OWNER = "Prevent"
        TAG_GROUP = 'App'
     }
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['PRD','DEVHOM'], description: 'Choose the environment to use.')
        string(name: 'NAME', defaultValue: '', description: 'Instance name')
        choice(name: 'NUMBER', choices: ['1', '2', '3', '4'], description: 'Number of instances to create')
        choice(name: 'TAG', choices: ['dev', 'hom', 'prod'], description: 'Enviroment tag to include in AWS')
        string(name: 'USERDATA', defaultValue: 'generic', description: 'Script to execute at instance startup')
        choice(name: 'VPC', choices: ['vpc-123', 'vpc-456'], description: '')
        choice(name: 'TIPO', choices: ['Frontend', 'Backend', 'Services'], description: 'Application type')
        choice(name: 'MEM', choices: ['2GB', '4GB', '8GB'], description: 'Instance type')
        booleanParam(name: 'DELETE', defaultValue: false, description: 'Remove resource')
    }
  stage('Checkout') {
   checkout scm
  }
  def values = terraformAwsEc2()
  if (values.terraformVersion < 0.10 ) {
   stage('Version') {
    dir(values.ec2Module) {
       echo "Version unsupported!"
       sh values.versionUnsupported
    }
   }
  }
  if (params.ENVIRONMENT == 'PROD' ) {
   stage('Init') {
    dir(values.ec2Module) {
       sh "terraform init -backend-config='${values.s3Bucket} -backend-config='${values.s3BucketKey}' -backend-config='${values.awsRegion}'"
   }
  } 
  } else {
    dir(values.ec2Module) {
       sh "terraform init -backend-config='${values.s3BucketDevHom} -backend-config='${values.s3BucketKey}' -backend-config='${values.awsRegionDevHom}'"
    }      
   }  
  stage('Plan') {
      dir(values.ec2Module) {
    env.TF_VAR_environment = "${params.ENVIRONMENT}"
     env.TF_VAR_vpc_id = "${params.VPC}"
     env.TF_VAR_tag_description = "${params.TIPO}"
     env.TF_VAR_region = "${REGION}"
     env.TF_VAR_owner = "${OWNER}"
     env.TF_VAR_count_number = "${params.NUMBER}"
     env.TF_VAR_instance_environment = "${params.TAG}"
     env.TF_VAR_instance_name = "${params.NAME}"
     env.TF_VAR_tag_group = "${params.NAME}-${params.TAG}"
     if ("${params.MEM}" == "2GB" && "${params.ENVIRONMENT}" == "PRD") {
      echo "Nao utilizar familia t2 em prod!" 
      sh "sleep 15 && exit 1"
     } else if ("${params.MEM}" == "2GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
      env.TF_VAR_backup_option = "nao"
      env.TF_VAR_instance_type = "t3.small"
      env.TF_VAR_tag_group = "${TAG_GROUP}-${params.TAG}"
      sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -out=${params.NAME}-${params.TAG}.tfplan"
     } else if ("${params.MEM}" == "4GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
      env.TF_VAR_backup_option = "nao"
      env.TF_VAR_instance_type = "c5.large"
      env.TF_VAR_tag_group = "${TAG_GROUP}-${params.TAG}"
      sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -out=${params.NAME}-${params.TAG}.tfplan"
     } else if ("${params.MEM}" == "4GB" && "${params.ENVIRONMENT}" == "PRD") {
      env.TF_VAR_backup_option = "sim"
      env.TF_VAR_instance_type = "c5.large"
      env.TF_VAR_tag_group = "${TAG_GROUP}"
      env.TF_VAR_alarm_name = "${params.NAME}-${params.TAG}-down-recovering"
      sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -target='module.ec2.aws_cloudwatch_metric_alarm.ec2_autorecover' -out=${params.NAME}-${params.TAG}.tfplan"
     } else if ("${params.MEM}" == "8GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
      env.TF_VAR_backup_option = "nao"
      env.TF_VAR_instance_type = "m5.large"
      env.TF_VAR_tag_group = "${TAG_GROUP}-${params.TAG}"
      sh "terraform plan -out=${params.NAME}-${params.TAG}.tfplan"
     } else if ("${params.MEM}" == "8GB"  && "${params.AMBIENTE}" == "PRD") {
      env.TF_VAR_backup_option = "sim" 
      env.TF_VAR_instance_type = "m5.large"
      env.TF_VAR_tag_group = "${TAG_GROUP}"
      env.TF_VAR_alarm_name = "${params.NAME}-${params.TAG}-down-recovering"  
      sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -target='module.ec2.aws_cloudwatch_metric_alarm.ec2_autorecover' -out=${params.NAME}-${params.TAG}.tfplan"
      }
   }
  }
 }
}