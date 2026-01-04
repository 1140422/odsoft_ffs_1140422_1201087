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