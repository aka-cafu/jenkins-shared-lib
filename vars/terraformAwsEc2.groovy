def call() {
  Map terraformAwsEc2 = readYaml(file: "${WORKSPACE}/values.yaml")
  return terraformAwsEc2
}
