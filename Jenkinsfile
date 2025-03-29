pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'netsong7/netsong7-no-db'
        DOCKER_TAG = 'latest'
    }

    options {
        skipStagesAfterUnstable()
    }

    stages {
        stage('Checkout') {
            steps {
                // GitHub에서 소스코드 가져오기
                git branch: 'main', url: 'https://github.com/netsong7/netsong7-no-db'
            }
        }

        stage('Build with Gradle') {
            steps {
                // gradlew 실행 권한 부여 + 빌드 실행
                sh '''
                    chmod +x gradlew
                    ./gradlew clean build
                '''
            }
        }

        stage('Docker Build & Push') {
            environment {
                // Credentials에서 설정한 Docker Hub 인증 정보 사용
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
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'app1',
                            transfers: [],
                            usePromotionTimestamp: false,
                            execCommand: '''
                                docker stop $(docker ps -q) || true
                                docker rm $(docker ps -a -q) || true
                                docker rmi -f $DOCKER_IMAGE:$DOCKER_TAG || true
                                docker run -d -p 8080:8080 $DOCKER_IMAGE:$DOCKER_TAG
                            ''',
                            verbose: true
                        )
                    ]
                )
            }
        }

        stage('Deploy to app2') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'app2',
                            transfers: [],
                            usePromotionTimestamp: false,
                            execCommand: '''
                                docker stop $(docker ps -q) || true
                                docker rm $(docker ps -a -q) || true
                                docker rmi -f $DOCKER_IMAGE:$DOCKER_TAG || true
                                docker run -d -p 8080:8080 $DOCKER_IMAGE:$DOCKER_TAG
                            ''',
                            verbose: true
                        )
                    ]
                )
            }
        }
    }
}
