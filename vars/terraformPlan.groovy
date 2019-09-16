def call() {
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