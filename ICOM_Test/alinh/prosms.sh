#!/bin/bash
thumucchay=/home/oracle/Process
thamsogrep="/home/oracle/Process"
thumuclog=/home/oracle/Process/log
today=`date '+%Y-%m-%d'`
# cac gam mau cho text
r=`tput setaf 1`
r1=`tput setaf 2`
N=`tput sgr0`

start(){
    t1=`ps -ef |grep $thamsogrep |grep -v grep |grep -v run | wc -l`
    if [ $t1 -eq 0  ];
    then
    $thumucchay/run.sh &
    echo "Start${r1}[ok]${N}"
    fi
    if [ $t1 -eq 1  ]; then
    echo "${r1}Started${N}"
    fi
    if (( $t1 > 1  ))
    then
    echo "${r1}Started${N} hon mot tien trinh"
    fi
}

stop(){
    /bin/ps -ef |grep $thamsogrep |grep -v grep |awk '{print$2}' |xargs kill >/dev/null 2>&1 &
    t2=`ps -ef |grep $thamsogrep |grep -v grep |grep -v run| wc -l`
    if [ $t2 -eq 0 ]; then
    echo "Stop${r1}[ok]${N}"
    fi
    if (( $t2 > 0  ))
    then
    STOPTIMEOUT=7
    while [ $STOPTIMEOUT -gt 0 ]; do
         sleep 1
         let STOPTIMEOUT=${STOPTIMEOUT}-1
         done
         if [ $STOPTIMEOUT -eq 0 ]; then
         t3=`ps -ef |grep $thamsogrep |grep -v grep |grep -v run| wc -l`
         if (( $t3 > 0  ))
         then
         echo "Stop${r}[faild]${N}"
         echo "Timeout error khi stop" $thumucchay "va Process chua stop"
         echo "Su dung prosms.sh kill de kill -9 ung dung" $thumucchay
        fi
        if [ $t3 -eq 0  ];
        then
         echo "Stop${r1}[ok]${N}"
        fi
      fi
   fi
}


kill(){
/bin/ps -ef |grep $thamsogrep |grep -v grep |awk '{print$2}' |xargs kill -9 >/dev/null 2>&1 &
echo "Ung dung" $thumucchay "da duoc kill -9"
echo "kill -9 ${r1}[ok]${N}"
}

restart(){
    stop
    start
}

taillog(){
  /usr/bin/tail -f $thumuclog/$today.log 
}

case "$1" in
  start)
   start 
   ;;
  stop)
   stop 
   ;;
  status)
    t3=`ps -ef |grep $thamsogrep |grep -v grep |grep -v run |wc -l`
    if [ $t3 -eq 1 ];
    then
    echo $thumucchay "${r1}started${N}"
    fi
    if [ $t3 -eq 0 ];
    then
    echo $thumucchay stopped
    fi
    if (( $t3 > 1  ))
    then
    echo $thumucchay "dang chay nhieu hon 1 tien trinh, phai dung prosms.sh kill de kill -9 ung dung nay"
    fi
    ;;
  taillog)
  taillog
  ;;
  restart)
   restart  
   ;;
  kill)
    kill
   ;;
  *)
    echo $"Usage: $0 {start|stop|status|restart|kill|taillog}"
    exit 1
esac
exit $?
