#!/bin/sh
/usr/java/jdk1.6.0_14/bin/java  -cp /home/oracle/VIETTEL/cdr/Bilsys.jar:lib/classes12.zip:lib/primrose.jar:lib/mysql-connector-java-5.1.6-bin.jar com.vasc.smpp.cdr.CDRServer  2>&1
