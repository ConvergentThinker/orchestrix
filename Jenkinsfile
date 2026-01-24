pipeline {
    agent any
    
    parameters {
        choice(name: 'EXECUTION_MODE', 
               choices: ['parallel', 'sequential'],
               description: 'Test execution mode')
        string(name: 'THREAD_COUNT', 
               defaultValue: '8',
               description: 'Parallel threads')
        choice(name: 'DEVICE_TIER',
               choices: ['all', 'premium', 'standard', 'basic'],
               description: 'Device tier')
    }
    
    environment {
        ANDROID_HOME = '/Users/jenkins/Library/Android/sdk'
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup Environment') {
            steps {
                sh '''
                    chmod +x scripts/*.sh
                    ./scripts/setup-environment.sh || true
                '''
            }
        }
        
        stage('Start Appium Servers') {
            steps {
                sh './scripts/start-appium-nodes.sh'
                sleep 10 // Wait for servers to start
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    if (params.EXECUTION_MODE == 'parallel') {
                        sh """
                            mvn clean test \
                              -Dparallel.threads=${params.THREAD_COUNT} \
                              -Ddevice.tier=${params.DEVICE_TIER}
                        """
                    } else {
                        sh 'mvn clean test -Dparallel.threads=1'
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                publishHTML([
                    reportDir: 'reports/extent',
                    reportFiles: 'ExtentReport*.html',
                    reportName: 'ExtentReport',
                    alwaysLinkToLastBuild: true
                ])
                
                publishHTML([
                    reportDir: 'reports/consolidated',
                    reportFiles: 'DeviceReport*.html',
                    reportName: 'Device-Wise Report',
                    alwaysLinkToLastBuild: true
                ])
            }
        }
    }
    
    post {
        always {
            sh './scripts/stop-appium-nodes.sh'
            
            junit 'target/surefire-reports/*.xml'
            
            archiveArtifacts artifacts: 'reports/**/*', 
                           fingerprint: true,
                           allowEmptyArchive: true
        }
        
        success {
            echo '✓ Tests passed successfully!'
        }
        
        failure {
            echo '✗ Tests failed. Check reports for details.'
            emailext(
                subject: "Test Failure: ${env.JOB_NAME} - Build ${env.BUILD_NUMBER}",
                body: "Tests failed. View reports: ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
    }
}
