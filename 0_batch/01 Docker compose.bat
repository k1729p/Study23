@echo off
set PROJECT=study23
set COMPOSE_FILE=..\docker-config\compose.yaml

copy /Y ..\keycloak-config\spring-realm.json d:\tmp\spring-realm.json > nul 2>&1
docker container rm --force resource-server-b > nul 2>&1
docker container rm --force resource-server-a > nul 2>&1
docker container rm --force gateway > nul 2>&1
docker container rm --force keycloak > nul 2>&1
docker image rm --force %PROJECT%-resource-server-b:latest > nul 2>&1
docker image rm --force %PROJECT%-resource-server-a:latest > nul 2>&1
docker image rm --force %PROJECT%-gateway:latest > nul 2>&1
docker image rm --force quay.io/keycloak/keycloak:latest > nul 2>&1
set KEY=Y
set /P KEY="Remove Keycloak image from Docker? [Y] N "
if /i "%KEY:~0,1%"=="Y" (
  docker image rm --force quay.io/keycloak/keycloak:latest > nul 2>&1
)
echo ------------------------------------------------------------------------------------------
docker compose down
docker compose -f %COMPOSE_FILE% -p %PROJECT% up --detach
echo ------------------------------------------------------------------------------------------
docker compose -f %COMPOSE_FILE% -p %PROJECT% ps
echo ------------------------------------------------------------------------------------------
docker compose -f %COMPOSE_FILE% -p %PROJECT% images
:: docker network inspect %PROJECT%_net
pause