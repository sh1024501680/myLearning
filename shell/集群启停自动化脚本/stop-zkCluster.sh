#!binbash

for host in centos7-101 centos7-102 centos7-103
  do
    echo -------停止$host的ZooKeeper------
    ssh $host 'source etcprofile;optinstallzookeeper-3.5.8-binbinzkServer.sh stop'
    if [ $!= 0 ];
      then
      echo Can not stop zookeeper server on host $host
      exit 1
    fi
  done