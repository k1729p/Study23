@echo off
set REST_CLIENT_DOCKER_IMAGE=eeengcs/rest-client:1.0.0-SNAPSHOT
set REST_CLIENT_DOCKER_FILE=docker-config\tests\Rest-Client.Dockerfile
cd ..
docker image rm --force %REST_CLIENT_DOCKER_IMAGE% > nul 2>&1
docker build --file %REST_CLIENT_DOCKER_FILE% --tag %REST_CLIENT_DOCKER_IMAGE% .
docker push %REST_CLIENT_DOCKER_IMAGE%
echo ------------------------------------------------------------------------------------------
docker run --name rest-client --network study23_net --rm %REST_CLIENT_DOCKER_IMAGE%
pause