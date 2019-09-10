def call() {
 if (params.DELETE) {
  def terraformApprove = input message: 'Do you really want to destroy all resources?',
   parameters: [choice(name: 'Destroy', choices: 'yes\nno', description: 'Enter a value')]
  if (terraformApprove == 'yes') {
   sh "terraform destroy -auto-approve"
  } else {
   echo "Cancelled!"
  }
 }
}