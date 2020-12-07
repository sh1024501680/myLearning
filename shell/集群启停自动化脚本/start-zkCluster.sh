#!/bin/bash

for host in centos7-101 centos7-102 centos7-103
  do
    ssh $host 'source /etc/profile;$ZOOKEEPER_HOME/bin/zkServer.sh start'
    if [ $? != 0 ];
      then
      echo "Can not starting zookeeper server on host $host"
      exit 1
    fi
  done

sleep 1s
echo "========================================================"
sleep 1s

for host in centos7-101 centos7-102 centos7-103
  do
    ssh $host 'source /etc/profile;$ZOOKEEPER_HOME/bin/zkServer.sh status'
  done