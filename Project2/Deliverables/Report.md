# CI/CD Pipeline Report – LMS AuthN & Users Service

## Overview
This project implements a fully automated CI/CD pipeline using Jenkins, Maven, Docker, and Docker Compose for the LMS AuthN & Users microservice.

## Pipeline stages

### Source code checkout and Build
The pipeline starts by checking out a Github repository and compiling the project using Maven

### Static Code Analysis
Static code analysis is executed during the pipeline to ensure code quality and detect potential issues early.

Checkstyle is used to enforce coding standards and consistency, while SpotBugs performs static analysis to identify common programming errors and potential bugs.

The analysis reports are collected and published by Jenkins.

![StaticAnalysis.png](images/StaticAnalysis.png)

### Unit Testing and Code Coverage 
Unit tests are executed using JUnit, and the test results are automatically published in Jenkins.
Code coverage is measured with JaCoCo, using defined quality gates for line and branch coverage. If the thresholds are not met, the build is marked as unstable.

![Coverage.png](images/Coverage.png)

### Mutation Testing
Mutation testing is performed using PIT, which validates the effectiveness of the test suite by introducing artificial faults.
An HTML report is published.

![PITMutation.png](images/PITMutation.png)

### CDC Testing
During this stage, the CDC Definition Test is executed, where two contract definition tests are successfully validated, ensuring that the expected consumer interactions are correctly defined.

Afterwards, provider-side CDC verification tests are executed, and Pact files are generated based on these interactions.
All generated Pact files are archived as Jenkins artifacts with fingerprinting enabled, ensuring traceability and serving as evidence of successful CDC validation.

![PactFiles.png](images/PactFiles.png)

### Docker Image build
In this stage the application is containerized with the latest tag

### Docker Login and Image Push
Through the pipeline we push the image to dockerhub, so it is public and we can retrieve it in the latest stages for production.

DockerHub lmsbooks
![DockerHub_lmsbooks.png](images/DockerHub_lmsbooks.png)

DockerHub lmsusers
![DockerHub_lmsusers.png](images/DockerHub_lmsusers.png)

### Staging Deployment (Local)
With Docker Compose we deploy the application locally together with its dependencies, like Postgres and RabbitMQ, here we stop the previous containers rebuild and start the new services

### (Books service only) Email and manual approval
An SMTP server was configured in Jenkins using a dedicated email account `isep.odsoft.25.26@gmail.com`, created exclusively for CI/CD notifications.

The email credentials were securely configured in Jenkins and used by the pipeline through the `Email Extension Plugin`.

![SMTPServer.png](images/SMTPServer.png)

After all build, test stages complete successfully, the pipeline sends an automatic email notification to the designated approver with the job details and build link. The pipeline then pauses and waits for manual approval inside Jenkins.

![EmailNotification.png](images/EmailNotification.png)

![](images/ManualGate.png)

---

### Remote Deployment Via SSH
During this stage, the group initially attempted to deploy the application to the DEI remote servers. 
![](images/DEI_Server.png)

However, this deployment was not successful due to insufficient Docker execution permissions on the provided infrastructure. The server explicitly denied the execution of Docker images, which made it impossible to run containerized services in that environment.
![DEIserverDenyErrors.png](images/DEIserverDenyErrors.png)
![DEIServerAppArmorError.png](images/DEIServerAppArmorError.png)

As a result, an alternative solution was adopted. The team deployed the application to a private home server provided by one of the group members. This server runs Ubuntu LTS and is hosted on repurposed hardware (an older laptop). The environment was prepared with OpenSSH for secure remote access and Docker / Docker Compose for container orchestration.
![](images/RemoteHomeServer.png)

In this stage of the pipeline, Jenkins securely connects to the remote server via SSH, transfers the required docker-compose configuration file, and pulls the publicly available Docker images from Docker Hub. The services are then started using Docker Compose, ensuring a reproducible and automated deployment process.
To improve accessibility and traceability during testing and evaluation, the server was assigned a static IP address (192.168.1.200)
![](images/RemoteSystem.png)

---

### Local and Remote applications running

Local Users
![LocalUsers.png](images/LocalUsers.png)

Remote server Books
![](images/RemoteBooks.png)

Remote server Users
![](images/RemoteUsers.png)

## Final Pipeline Overview

Pipeline Users
![PipelineUsers.png](images/PipelineUsers.png)

Pipeline Books
![PipelineBooks.png](images/PipelineBooks.png)
