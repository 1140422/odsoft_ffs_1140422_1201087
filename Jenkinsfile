pipeline {
	agent any

	tools {
		maven 'Maven3'
		jdk 'Java21'
	}

	stages {
		stage('Checkout') {
			steps {
				echo 'Checking out source code...'
				checkout scm
			}
		}

		stage('Build') {
			steps {
				echo 'Building the project...'
				dir('psoft-project-2024-g1') {
					bat 'mvn clean package -DskipTests'
				}
			}
		}

		stage('Test') {
			steps {
				echo 'Running tests...'
				dir('psoft-project-2024-g1') {
					bat 'mvn test'
				}
			}
			post {
				always {
					junit 'psoft-project-2024-g1/target/surefire-reports/*.xml'
				}
			}
		}
	}

	post {
		success {
			echo 'Pipeline completed successfully!'
		}
		failure {
			echo 'Pipeline failed. Please check the logs.'
		}
	}
}
