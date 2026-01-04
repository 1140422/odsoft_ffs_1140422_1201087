# CI/CD Pipeline Report – LMS AuthN & Users Service

## Overview
This project implements a fully automated CI/CD pipeline using Jenkins, Maven, Docker, and Docker Compose for the LMS AuthN & Users microservice.

## Pipeline stages

### Source code checkout and Build
The pipeline starts by checking out a Github repository and compiling the project using Maven

### Static Code Analysis
For the static code analysis we used:
- Checkstyle - coding standards enforcement
- SpotBugs - static detect detection

### Unit Testing and Code Coverage 
JUnit tests are executed, and results are published automatically.
Code coverage is measured using JaCoCo, with defined quality gates, like line coverage treshhold and branch coverage threshold

### Mutation Testing
Mutation testing is performed using PIT, which validates the effectiveness of the test suite by introducing artificial faults.
An HTML report is published.

### Docker Image build
In this stage the application is containerized with the latest tag

### Docker Login and Image Push
Through the pipeline we push the image to dockerhub, so it is public and we can retrieve it in the latest stages for production.

### Staging Deployment (Local)
With Docker Compose we deploy the application locally together with its dependencies, like Postgres and RabbitMQ, here we stop the previous containers rebuild and start the new services

### (Books service only) Email and manual approval
In this stage only for Books service an email is sent stating that all tests passed and the application is waiting manual approval to go to production deployment

### Remote Deployment Via SSH
In this stage the group tried to do a deployment in DEI remote servers, however we were not successfull due to permissions on docker, the server denied us to execute any image:
IMAGE

For this reason, we decided to use a home server from one team element, this server is a Ubuntu LTS built on an old laptop, openSSH and docker were installed and in this stage the pipeline copies the dpcker-compose file for the server and pulls the public image from docker hub.
A static IP was given to the server for better traceability 192.168.1.200
