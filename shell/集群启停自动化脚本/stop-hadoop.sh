#!/bin/bash

ssh centos7-101 'source /etc/profile;$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh stop historyserver'

ssh centos7-102 'source /etc/profile;$HADOOP_HOME/sbin/stop-yarn.sh'

ssh centos7-103 'source /etc/profile;$HADOOP_HOME/sbin/yarn-daemon.sh stop resourcemanager'

ssh centos7-101 'source /etc/profile;$HADOOP_HOME/sbin/stop-dfs.sh'