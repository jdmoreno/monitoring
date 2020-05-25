:: Name:    EMS2JSON
:: Purpose: Query EMS servers for stats, transform to json and write them in a file
:: Output file in the logback configuration

@ECHO OFF
SETLOCAL ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

set JAR-FOLDER=..\..\..\target
SET JAR-FILE=ems2json-0.0.1-SNAPSHOT.jar
SET MAIN-CLASS=eps.platform.infraestructure.EMS2JSON
SET INPUT="F:\eps\99.EMS"
SET OUTPUT="D:\eps\json\EMS\[Y]\[M]\EMS_[D].json"

:: Process
java -Dlogback.configurationFile=".\config\logback.xml" -cp %JAR-FOLDER%\%JAR-FILE%;tibjms.jar %MAIN-CLASS% -f 5 -s ./config/servers3.yaml -r ./config/ems2jsonRef.csv

:END
ENDLOCAL
ECHO ON
@EXIT /B %ERRORLEVEL%