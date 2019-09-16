def call() {
 def values = terraformAwsEc2()
 dir(values.ec2Module) {
  if !(params.DELETE) {
   def terraformApprove = input message: 'Do you really want to create the resources described above?',
    parameters: [choice(name: 'Apply', choices: 'yes\nno', description: 'Enter a value')]
   if (terraformApprove == "yes") {
    sh "terraform apply ${params.NAME}-${params.TAG}.tfplan"
   } else {
    echo "Apply cancelled."
   }
  }
 }
}