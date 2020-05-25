# EMS2JSON

## Description
Utility to log Tibo EMS monitoring statistics to JSON records.

### JSON output server record
Identified by "RecordType": "EMS_STATS_SERVER"
<code>
{
  "RecordType": "EMS_STATS_SERVER",
  "timestamp": "2020-05-25T19:26:34.9620379",
  "hostname": "ems.test",
  "dataCentre": "JM",
  "environment": "TEST",
  "serverFunction": "ALL",
  "serverName": "EMS-SERVER",
  "url": "tcp://ems.test:7222",
  "stats": {
    "asyncDBSize": 4235264,
    "inboundBytesRate": 0,
    "queueCount": 164,
    "connectionCount": 2,
    "pendingMessageSize": 98606,
    "syncDBSize": 2048,
    "outboundBytesRate": 0,
    "msgMem": 304540,
    "inboundMessageCount": 5270989,
    "sessionCount": 2,
    "pendingMessageCount": 844,
    "topicCount": 13,
    "diskWriteRate": 0,
    "diskReadOperationsRate": 0,
    "msgMemPooled": 338944,
    "outboundMessageCount": 5270172,
    "outboundMessageRate": 0,
    "clientConnectionCount": 0,
    "diskWriteOperationsRate": 0,
    "consumerCount": 8,
    "durableCount": 0,
    "inboundMessageRate": 0,
    "producerCount": 2,
    "diskReadRate": 0,
    "adminConnectionCount": 2
  }
}
</code>

### JSON output destination (queue/topic) record
Identified by "RecordType": "EMS_STATS_DESTINATION"
<code>
{
  "RecordType": "EMS_STATS_DESTINATION",
  "timestamp": "2020-05-25T20:47:09.566343",
  "hostname": "ems.test",
  "dataCentre": "JM",
  "environment": "TEST",
  "serverFunction": "ALL",
  "serverName": "EMS-SERVER",
  "destinationName": "sample.out",
  "destinationType": "QUEUE",
  "store": "$sys.nonfailsafe",
  "static": true,
  "temporary": false,
  "stats": {
    "pendingPersistentMessageSize": 0,
    "consumerCount": 0,
    "pendingMessageCount": 0,
    "pendingPersistentMessageCount": 0,
    "pendingMessageSize": 0,
    "receiverCount": 0,
    "inTransitMessageCount": 0,
    "deliveredMessageCount": 0
  },
  "statsInbound": {
    "inboundByteRate": 0,
    "inboundTotalMessages": 43680,
    "inboundMessageRate": 0,
    "inboundTotalBytes": 3562020
  },
  "statsOutbound": {
    "outboundTotalMessages": 43680,
    "outboundByteRate": 0,
    "outboundMessageRate": 0,
    "outboundTotalBytes": 3562020
  }
}
</code>

## Usage
<code>
usage: EMS2JSON
EMS2JSON Help
  -ch,--check           Only check the arguments. Does not process the command.
                        Optional
  -f,--interval <arg>   Polling interval. Mandatory
  -help                 Help.
  -r,--reference <arg>  CSV file with infrastructure reference data. Optional
  -s,--servers <arg>    Servers yaml configuration file. Mandatory
  -v,--version          Prints EMS2JSON version. Does not process the command.
                        Optional
Read the documentation for further details.
</code>

### Sample CMD to call EMS2JSON
<code>
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
</code>

## Configuration

