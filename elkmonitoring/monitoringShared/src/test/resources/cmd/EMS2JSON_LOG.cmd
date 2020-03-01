:: Name:    Reference Data Validator - Test7
:: Purpose: Validate reference tables in CSV format

@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

SET JAR=ems2json-0.0.1-SNAPSHOT.jar
SET INPUT="F:\eps\99.EMS"
SET OUTPUT="D:\eps\json\EMS\[Y]\[M]\EMS_[D].json"

:: Process
java -Dlogback.configurationFile="F:\eps\config\logback.xml" -jar %JAR% -i %INPUT% -o %OUTPUT% -l -d 2018-12-6

:END
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%