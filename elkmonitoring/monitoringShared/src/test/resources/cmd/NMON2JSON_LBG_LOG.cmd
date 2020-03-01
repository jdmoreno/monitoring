:: Name:    Reference Data Validator - Test7
:: Purpose: Validate reference tables in CSV format

@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

SET JAR=nmon2json-0.0.3-SNAPSHOT.jar
SET DC=LBG
SET HOSTNAMES="F:\eps\config\hostnames.txt"
SET DATES="F:\eps\config\dates.txt"
SET INPUT="F:\eps\99.NMON"
SET OUTPUT="D:\eps\json\[Y]\[M]\%DC%_[D].json"
SET REFERENCE="F:\eps\config\eps_infra_complete.csv"

:: Process
::del %OUTPUT%
java -Dlogback.configurationFile="F:\eps\config\logback.xml" -jar %JAR% -i %INPUT% -o %OUTPUT% -d %DATES% -l -r %REFERENCE% -c %DC% 

:END
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%