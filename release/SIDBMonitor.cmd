@echo off

REM USAGE: 
REM SIDBMonitor.cmd 


REM Change SI_PATH to point to your SI directory.
REM You can also copy the jdbc driver on the same directory as the application

set SI_PATH=%/Sterling
set JAVA_PATH=%SI_PATH%/jdk/jre/bin

REM Use this command if you run the application from SI server with Oracle
REM %JAVA_PATH%/java -cp ".;SIDBMonitor.jar;%SI_PATH%/dbjar/jdbc/Oracle/ojdbc6.jar"  com.support.SIDBMonitor.SIDBMonitor

REM Use this command if you run the application from SI server with DB2
REM %JAVA_PATH%/java -cp ".;SIDBMonitor.jar;%SI_PATH%/dbjar/jdbc/DB2/db2jcc.jar;%SI_PATH%/dbjar/jdbc/DB2/db2jcc4.jar"  com.support.SIDBMonitor.SIDBMonitor

REM Use this command if you run the application from SI server with DB2
REM %JAVA_PATH%/java -cp ".;SIDBMonitor.jar;%SI_PATH%/dbjar/jdbc/MSSQL/sqljdbc4.jar"  com.support.SIDBMonitor.SIDBMonitor


REM Use this command if the driver is on the same folder as the application 
REM Adjust the java path if necessary

REM Oracle
java -cp ".;./SIDBMonitor.jar;./ojdbc6.jar"  com.support.SIDBMonitor.SIDBMonitor


REM DB2
REM java -cp ".;./SIDBMonitor.jar;./db2jcc.jar;./db2jcc4.jar"  com.support.SIDBMonitor.SIDBMonitor

REM MS SQL SERVER

REM java -cp ".;./SIDBMonitor.jar;./sqljdbc4.jar"  com.support.SIDBMonitor.SIDBMonitor




