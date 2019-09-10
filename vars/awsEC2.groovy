def call() {
 pipeline {
  agent any
  stages {
   stage('Checkout') {
    steps {
     checkout scm
    }
   }
   stage('Call') {
    script {
     def values = terraformAwsEc2()
    }
   }
   stage('Version') {
    if (values.terraformVersion <= 0.10) {

     steps {
      dir(values.ec2Module) {
       echo "Version unsupported!"
       sh values.versionUnsupported
      }
     }
    }
   }
  }
  stage('Init') {
   script {
    if (params.ENVIRONMENT == 'PROD') {
     steps {
      dir(values.ec2Module) {
       sh "terraform init -backend-config='${values.s3Bucket}' -backend-config='key=application/${params.NAME}-${params.TAG}/terraform.tfstate' -backend-config='region=${values.awsRegion}' && sed -i 's/appname/${params.USERDATA}/g' main.tf"
      }
     }
    }
    else {
    dir(values.ec2Module) {
     sh "terraform init -backend-config='bucket=${values.s3BucketDevHom}' -backend-config='key=application/${params.NAME}-${params.TAG}/terraform.tfstate' -backend-config='region=${values.awsRegionDevHom}' && sed -i 's/appname/${params.USERDATA}/g' main.tf"
}
    }
   }
  }
  stage('Plan') {
   steps {
    dir(values.ec2Module) {
     script {
      env.TF_VAR_environment = "${params.ENVIRONMENT}"
      env.TF_VAR_vpc_id = "${params.VPC}"
      env.TF_VAR_tag_description = "${params.TIPO}"
      env.TF_VAR_region = "${params.REGION}"
      env.TF_VAR_owner = "${params.OWNER}"
      env.TF_VAR_count_number = "${params.NUMBER}"
      env.TF_VAR_instance_environment = "${params.TAG}"
      env.TF_VAR_instance_name = "${params.NAME}"
      env.TF_VAR_tag_group = "${params.NAME}-${params.TAG}"
      if ("${params.MEM}" == "2GB" && "${params.ENVIRONMENT}" == "PRD") {
       echo "Don't use t2 family at production environment!"
       sh "sleep 15 && exit 1"
      } else if ("${params.MEM}" == "2GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
       env.TF_VAR_backup_option = "nao"
       env.TF_VAR_instance_type = "t3.small"
       env.TF_VAR_tag_group = "${params.TAG_GROUP}-${params.TAG}"
       sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -out=${params.NAME}-${params.TAG}.tfplan"
      } else if ("${params.MEM}" == "4GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
       env.TF_VAR_backup_option = "nao"
       env.TF_VAR_instance_type = "c5.large"
       env.TF_VAR_tag_group = "${params.TAG_GROUP}-${params.TAG}"
       sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -out=${params.NAME}-${params.TAG}.tfplan"
      } else if ("${params.MEM}" == "4GB" && "${params.ENVIRONMENT}" == "PRD") {
       env.TF_VAR_backup_option = "sim"
       env.TF_VAR_instance_type = "c5.large"
       env.TF_VAR_tag_group = "${params.TAG_GROUP}"
       env.TF_VAR_alarm_name = "${params.NAME}-${params.TAG}-down-recovering"
       sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -target='module.ec2.aws_cloudwatch_metric_alarm.ec2_autorecover' -out=${params.NAME}-${params.TAG}.tfplan"
      } else if ("${params.MEM}" == "8GB" && "${params.ENVIRONMENT}" == "DEVOHOM") {
       env.TF_VAR_backup_option = "nao"
       env.TF_VAR_instance_type = "m5.large"
       env.TF_VAR_tag_group = "${params.TAG_GROUP}-${params.TAG}"
       sh "terraform plan -out=${params.NAME}-${params.TAG}.tfplan"
      } else if ("${params.MEM}" == "8GB" && "${params.AMBIENTE}" == "PRD") {
       env.TF_VAR_backup_option = "sim"
       env.TF_VAR_instance_type = "m5.large"
       env.TF_VAR_tag_group = "${params.TAG_GROUP}"
       env.TF_VAR_alarm_name = "${params.NAME}-${params.TAG}-down-recovering"
       sh "terraform plan -target='module.ec2.aws_instance.generic_ec2' -target='module.ec2.aws_cloudwatch_metric_alarm.ec2_autorecover' -out=${params.NAME}-${params.TAG}.tfplan"
      }
     }
    }
   }
  }
  stage('Destroy') {
   steps {
    dir(values.ec2Module) {
     terraformDestroy()
    }
   }
  }
  stage('Apply') {
   steps {
    dir(values.ec2Module) {
     terraformApply()
    }
   }
  }
 }
}
}