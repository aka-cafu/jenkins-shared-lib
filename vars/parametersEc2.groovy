def call () {
  choice(name: 'ENVIRONMENT', choices: ['DEVOHOM', 'PRD'], description: 'Choose the environment to use.')
  string(name: 'NAME', defaultValue: '', description: 'Instance name')
  string(name: 'REGION', defaultValue: 'sa-east-1', description: 'AWS Region')
  string(name: 'OWNER', defaultValue: 'Prevent', description: 'Instance owner')
  string(name: 'TAG_GROUP', defaultValue: 'App', description: 'Instance tag group')
  choice(name: 'NUMBER', choices: ['1', '2', '3', '4'], description: 'Number of instances to create')
  choice(name: 'TAG', choices: ['dev', 'hom', 'prod'], description: 'Enviroment tag to include in AWS')
  string(name: 'USERDATA', defaultValue: 'generic', description: 'Script to execute at instance startup')
  choice(name: 'VPC', choices: ['vpc-8f5c59e6', 'vpc-742d9b10'], description: '[vpc-8f5c59e6: 172.173.0.0/16] or [vpc-742d9b10: 10.34.0.0/16]')
  choice(name: 'TIPO', choices: ['Frontend', 'Backend', 'Services'], description: 'Application type')
  choice(name: 'MEM', choices: ['2GB', '4GB', '8GB'], description: 'Instance type')
  booleanParam(name: 'DELETE', defaultValue: false, description: 'Remove resource')
}