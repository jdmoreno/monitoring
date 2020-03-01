:: Name: NMON2JSON
:: Purpose: Generate JSON files from NMON file with infrastructure stats information

::@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

SET me=%~n0

:: To run the script from Windows Explorer
SET interactive=1
ECHO %CMDCMDLINE% | FIND /I "/c" >NUL 2>&1
IF %ERRORLEVEL% == 0 SET interactive=0

PUSHD >NUL
CD ..\..\..\..\target
SET JAR_PATH=%CD%
POPD >NUL

ECHO %JAR_PATH%
DIR %JAR_PATH%


SET DC=BBP
::SET JAR_PATH=..\..\..\..\target
SET JAR=nmon2json-0.0.9-SNAPSHOT.jar
SET INPUT="C:\99.NMON\ATOS"
SET OUTPUT="C:\99.JSON\[DC]\[Y]\[M]\%DC%_[HN]_[D].json"
SET REFERENCE=".\config\eps_infra_complete.csv"
SET HOSTNAMES=".\config\hostnames.txt"
SET DATES=".\config\dates.txt"

:: Process
::del %OUTPUT%
java -Dlogback.configurationFile=".\config\logback.xml" -jar %JAR_PATH%\%JAR% -help

:END
IF "%interactive%"=="0" PAUSE

POPD >NUL
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%