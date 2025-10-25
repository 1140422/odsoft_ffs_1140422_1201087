pipeline {
	agent any

	tools {
		maven 'Maven3'
		jdk 'Java21'
	}

    environment {
        APP_NAME = "psoft-g1"
        DOCKER_IMAGE = "psoft-g1-app"
        DOCKER_TAG = "latest"
    }

    parameters {
        choice(
            name: 'DEPLOY_MODE',
            choices: ['dev', 'staging', 'prod', 'all'],
            description: 'Choose which environment(s) to deploy to'
        )
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

		stage('Static Code Analysis') {
            steps {
                echo 'Running Checkstyle and SpotBugs...'
                dir('psoft-project-2024-g1') {
                    bat 'mvn checkstyle:check spotbugs:spotbugs -DskipTests'
                }
            }
            post {
                always {
                    recordIssues tools: [
                        checkStyle(pattern: 'psoft-project-2024-g1/target/checkstyle-result.xml'),
                        spotBugs(pattern: 'psoft-project-2024-g1/target/spotbugsXml.xml')
                    ]
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

        stage('Deploy DEV') {
            when {
                expression { params.ENVIRONMENT == 'dev' || params.DEPLOY_MODE == 'all' }
            }
            steps {
                echo "Starting Spring Boot app locally in DEV mode..."
                // Stop any previously running app
                bat 'taskkill /F /IM java.exe || echo No running app'
                // Start in background
                bat 'start cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=dev"'
                echo "App started at http://localhost:8080"
            }
        }

		stage('Build Docker Image') {
            when {
                expression { params.ENVIRONMENT == 'staging' }
            }
            steps {
                bat "docker build -t %DOCKER_IMAGE%:%DOCKER_TAG% ."
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
