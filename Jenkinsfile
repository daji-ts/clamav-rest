#!groovy

// If you need help with Jenkins pipeline, check
// Getting started with Pipeline:      https://jenkins.io/doc/book/pipeline/getting-started/
// Pipeline Syntax reference:          https://jenkins.io/doc/book/pipeline/syntax/
// Pipeline Snippet Generator:         https://ci.ts.sv/pipeline-syntax/
// Pipeline Steps/Plugins Reference:   https://jenkins.io/doc/pipeline/steps/
// Github-Pipeline Integration Plugin: https://github.com/jenkinsci/pipeline-github-plugin

pipeline {
    agent {
        node('java8 && docker')
    }
    options {
        ansiColor('xterm')
        timeout(time: 1, unit: 'HOURS')
    }
    triggers {
        issueCommentTrigger('^(retest|docker push)$')
    }
    tools {
        jdk 'oracle-java8u202-jdk'
        maven 'apache-maven-3.6.0'
    }
    stages {
        stage('Checkout') {
            when {
                expression { getTriggerText() != 'docker push' }
            }
            steps {
                checkout scm
            }
        }
        stage('Build & test') {
            when {
                expression { getTriggerText() != 'docker push' }
            }
            steps {
                script {
                    withDockerCompose {
                        sh 'docker-compose run wait -c clamav-server:3310,clamav-rest:8080' // wait for both containers
                        sh 'mvn test'
                    }
                }
            }
        }
        stage('Sonarqube') {
            when {
                expression { getTriggerText() != 'docker push' }
            }
            steps {
                sonarqube()
            }
        }
        stage('Package') {
            when {
                expression { getTriggerText() != 'docker push' }
            }
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        stage('Docker push') {
            when {
                anyOf {
                    branch 'master'                                   // Push `master` build automatically
                    expression { getTriggerText() == 'docker push' }  // Push images from PRs upon request
                }
            }
            steps {
                script {
                    def imageTag = env.CHANGE_ID ? env.GIT_COMMIT : 'latest'
                    sh "docker build -t eu.gcr.io/tradeshift-base/clamav-rest:${imageTag} ."
                    dockerPush name: 'clamav-rest', tag: imageTag
                }
            }
            post {
                success {
                    script {
                        if (env.CHANGE_ID) {
                            pullRequest.comments.each {
                                if (it.body == 'docker push') {
                                    it.delete() // Remove docker push commit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
