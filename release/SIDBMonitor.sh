#!/bin/bash

# USAGE: 
# chmod u+x  SIDBMonitor.sh
# ./SIDBMonitor.sh

# Change SI_PATH to point to your SI directory.
# You can also copy the jdbc driver on the same directory as the SIDBMonitorApplication

SI_PATH=/home/skorn/IBM/SterlingIntegrator/install

JAVA_PATH=${SI_PATH}/jdk/jre/bin

##########################
#   ORACLE
##########################

# If you run the application from SI server
#${JAVA_PATH}/java -cp .:SIDBMonitor.jar:${SI_PATH}/dbjar/jdbc/Oracle/ojdbc6.jar  com.support.SIDBMonitor.SIDBMonitor


# If the driver is on the same folder as the application -- Un-Comment the following line
# Adjust the java path if necessary
#${JAVA_PATH}/java -cp .:SIDBMonitor.jar:ojdbc6.jar  com.support.SIDBMonitor.SIDBMonitor

##########################
#   DB2
##########################

# If you run the application from SI server
#${JAVA_PATH}/java -cp .:SIDBMonitor.jar:${SI_PATH}/dbjar/jdbc/db2/db2jcc.jar: ${SI_PATH}/dbjar/jdbc/db2/db2jcc4.jar com.support.SIDBMonitor.SIDBMonitor


# If the driver is on the same folder as the application -- Un-Comment the following line
# Adjust the java path if necessary
${JAVA_PATH}/java -cp .:SIDBMonitor.jar:db2jcc.jar:db2jcc4.jar  com.support.SIDBMonitor.SIDBMonitor

##########################
#   MS SQL SERVER
##########################

# If you run the application from SI server
#${JAVA_PATH}/java -cp .:SIDBMonitor.jar:${SI_PATH}/dbjar/jdbc/db2/sqljdbc4.jar com.support.SIDBMonitor.SIDBMonitor


# If the driver is on the same folder as the application -- Un-Comment the following line
# Adjust the java path if necessary
#${JAVA_PATH}/java -cp .:SIDBMonitor.jar:sqljdbc4.jar  com.support.SIDBMonitor.SIDBMonitor


