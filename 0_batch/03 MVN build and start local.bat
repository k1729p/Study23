@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-24
set M2_HOME=c:\\tools\\apache-maven-3.9.9
set MAVEN_OPTS="--enable-native-access=ALL-UNNAMED"
::set SKIP_TESTS=-DskipTests
set SPRING_PROFILES_ACTIVE=local

call %M2_HOME%\bin\mvn -f ..\pom.xml %SKIP_TESTS% clean install

set TITLE=Gateway
echo -------------------------------------------------------------------------- %TITLE%
start "%TITLE%" /MAX %M2_HOME%\bin\mvn -f ..\pom.xml --quiet ^
  --projects gateway spring-boot:run
timeout /t 1
set TITLE=A Resource Server
echo -------------------------------------------------------------------------- %TITLE%
start "%TITLE%" /MAX %M2_HOME%\bin\mvn -f ..\pom.xml --quiet ^
  --projects resource-server-a spring-boot:run
timeout /t 1
set TITLE=B Resource Server
echo -------------------------------------------------------------------------- %TITLE%
start "%TITLE%" /MAX %M2_HOME%\bin\mvn -f ..\pom.xml --quiet ^
  --projects resource-server-b spring-boot:run
pause