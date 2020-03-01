:: Name: NMON2JSON
:: Purpose: Generate JSON files from NMON file with infrastructure stats information

@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

SET me=%~n0
SET parent=%~dp0

SET JAR=nmon2json-0.0.4-SNAPSHOT.jar
SET DC=BBP
SET HOSTNAMES="F:\eps\config\hostnames.txt"
SET DATES="F:\eps\config\dates.txt"
SET INPUT="F:\eps\99.NMON"
SET OUTPUT="D:\eps\json\NMON\[Y]\[M]\%DC%_[D].json"
SET REFERENCE="F:\eps\config\eps_infra_complete.csv"

:: Process
::del %OUTPUT%
java -Dlogback.configurationFile="F:\eps\config\logback.xml" -jar %parent%\%JAR% -i %INPUT% -o %OUTPUT% -d 2018-12-01 -l -r %REFERENCE% -dc %DC% 

:END
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%