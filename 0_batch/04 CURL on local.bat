@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-24
set SITE=http://localhost:9091/api/document
set HR_YELLOW=@powershell -Command Write-Host "----------------------------------------------------------------------" -foreground "Yellow"
set HR_RED=@powershell -Command Write-Host "----------------------------------------------------------------------" -foreground "Red"
set SOURCE_FILE=..\docker-config\tests\Extractor.java
set TEMP_FILE=%TEMP%\curl_output.txt
set USERS=alice bob charlie david
set ENDPOINTS=official restricted confidential secret
setlocal EnableDelayedExpansion
chcp 65001 > nul 2>&1

for %%U in (%USERS%) do (
    %HR_YELLOW%
    powershell -Command Write-Host "User %%U accessing the endpoints" -foreground "Green"
    %HR_YELLOW%

    set SUCCESS_COUNT=0
    set FAILURE_COUNT=0
    :: Running the java launcher in source-file mode
    for /f "tokens=1,2 delims==" %%i in ('call %JAVA_HOME%\bin\java %SOURCE_FILE% %%U localhost') do (
        if "%%i"=="ACCESS_TOKEN" set "ACCESS_TOKEN=%%j"
    )
    for %%E in (%ENDPOINTS%) do (
        powershell -Command Write-Host "Accessing %%E document for user %%U" -foreground "Cyan"
        curl -s -H "Authorization: Bearer !ACCESS_TOKEN!" %SITE%/%%E > "%TEMP_FILE%"
        set "RESPONSE="
        if exist "%TEMP_FILE%" (
            for /f "usebackq delims=" %%R in ("%TEMP_FILE%") do set "RESPONSE=%%R"
        )
        if defined RESPONSE (
            echo !RESPONSE!
            set /a SUCCESS_COUNT+=1
        ) else (
            powershell -Command Write-Host ^
                "████▌HTTP ERROR 403▐████▌No authorization to access the document.▐████" -foreground "Red"
            set /a FAILURE_COUNT+=1
        )
        %HR_YELLOW%
    )
    powershell -Command Write-Host ^
        "User %%U finished accessing endpoints: !SUCCESS_COUNT! successful and !FAILURE_COUNT! failed attempts" ^
        -foreground "Green"
    %HR_YELLOW%
    echo.
)
if exist "%TEMP_FILE%" del "%TEMP_FILE%"
%HR_RED%
powershell -Command Write-Host "FINISH" -foreground "Red"
pause