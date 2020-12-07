#!/bin/bash
#1 获取输入参数个数,如果没有参数,直接退出
pcount=$#
if((pcount==0)); then
echo no args;
exit;
fi

#2 获取文件名称
p1=$1
fname=`basename $p1`
echo fname=$fname

#3 获取上级目录到绝对路径
pdir=`cd -P $(dirname $p1);pwd`
echo pdir=$pdir

#4 获取当前用户名称
user=`whoami`

#5 循环
# 需安装rsync命令
for((host=101; host<104; host++ ));
  do
        #echo $pdir/$fname $user@hadoop$host:$pdir
        echo ------------centos7-$host------------
        rsync -rvl $pdir/$fname $user@centos7-$host:$pdir
  done