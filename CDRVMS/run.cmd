#set ORACLE_HOME=E:\Oracle\product\10.1.0\Client_1
#set TNS_ADMIN=E:\Oracle\product\10.1.0\Client_1\network\admin

set CL=lib\classes12.zip;lib\mysql-connector-java-5.1.6-bin.jar;Bilsys.jar;lib\primrose.jar

java -classpath %CL% com.vasc.smpp.cdr.CDRServer

