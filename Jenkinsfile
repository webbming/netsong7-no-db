pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'netsong7/netsong7-no-db'
        DOCKER_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/netsong7/netsong7-no-db'
            }
        }

        stage('Build with Gradle') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean build
                '''
            }
        }

        stage('Docker Build & Push') {
            environment {
                DOCKER_CREDENTIALS_ID = 'docker-hub-credential'
            }
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                        docker build --no-cache -t $DOCKER_IMAGE:$DOCKER_TAG .
                        docker push $DOCKER_IMAGE:$DOCKER_TAG
                    '''
                }
            }
        }

        stage('Deploy to app1') {
            steps {
                sshagent (credentials: ['app-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@10.1.1.61 <<'ENDSSH'
                    PID=$(lsof -ti:8080)
                    if [ ! -z "$PID" ]; then
                        echo "Killing process using port 8080: $PID"
                        kill -9 "$PID"
                        sleep 2
                    fi
                    docker rm -f app || true
                    docker rmi -f netsong7/netsong7-no-db || true
                    sleep 2
                    docker run -d --name app -p 8080:8080 netsong7/netsong7-no-db
                    ENDSSH
                    '''
                }    
            }
        }

        stage('Deploy to app2') {
            steps {
                sshagent (credentials: ['app-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@10.1.1.40 <<'ENDSSH'
                    PID=$(lsof -ti:8080)
                    if [ ! -z "$PID" ]; then
                        echo "Killing process using port 8080: $PID"
                        kill -9 "$PID"
                        sleep 2
                    fi
                    docker rm -f app || true
                    docker rmi -f netsong7/netsong7-no-db || true
                    sleep 2
                    docker run -d --name app -p 8080:8080 netsong7/netsong7-no-db
                    ENDSSH
                    '''
                }
            }
        }
    }
}
