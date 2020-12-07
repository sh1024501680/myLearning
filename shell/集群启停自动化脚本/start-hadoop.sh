#!/bin/bash

zk_status=`source /etc/profile;$ZOOKEEPER_HOME/bin/zkServer.sh status |grep Error`
if [ $zk_stauts!="" ]
  then
    sh /home/hadoop/start-zkCluster.sh
fi

ssh centos7-101 'source /etc/profile;$HADOOP_HOME/sbin/start-dfs.sh'

ssh centos7-102 'source /etc/profile;$HADOOP_HOME/sbin/start-yarn.sh'

ssh centos7-103 'source /etc/profile;$HADOOP_HOME/sbin/yarn-daemon.sh start resourcemanager'

ssh centos7-101 'source /etc/profile;$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver'