:: Name: NMON2JSON
:: Purpose: Generate JSON files from NMON file with infrastructure stats information

@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

SET me=%~n0

:: To run the script from Windows Explorer
SET interactive=1
ECHO %CMDCMDLINE% | FIND /I "/c" >NUL 2>&1
IF %ERRORLEVEL% == 0 SET interactive=0

PUSHD
CD ..
PUSHD "%~dp0" >NUL && SET parent=%CD% && POPD >NUL
ECHO %parent%

SET DC=BBP
SET JAR=nmon2json-0.0.8-SNAPSHOT.jar
SET INPUT="C:\99.NMON\ATOS"
SET OUTPUT="C:\99.JSON\[DC]\[Y]\[M]\%DC%_[HN]_[D].json"
SET REFERENCE=".\config\eps_infra_complete.csv"
SET HOSTNAMES=".\config\hostnames.txt"
SET DATES=".\config\dates.txt"

:: Process
::del %OUTPUT%
java -Dlogback.configurationFile=".\config\logback.xml" -jar %JAR% -i %INPUT% -o %OUTPUT% -d 2018-12-01 -r %REFERENCE% -dc %DC% 

:END
IF "%interactive%"=="0" PAUSE

POPD >NUL
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%