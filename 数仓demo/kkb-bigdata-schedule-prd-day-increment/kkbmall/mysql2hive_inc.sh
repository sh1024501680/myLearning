#!/bin/bash

APP_NAME="/kkb/code/spark/kkb-spark-framework-1.0-SNAPSHOT.jar"
APP_MAINCLASS="com.kkb.spark.batch.TestMysql2Hive"
FILENAME=$1
OPERATE_TIME_START="$2"
OPERATE_TIME_END="$3"
ETL_DATE=$4
FILEPATH="/kkb/code/spark/conf/$FILENAME"
JAVA_HOME="/kkb/install/jdk1.8.0_141"
DRIVER_MEM=1G
EXECUTOR_MEM=1G
EXECUTOR_CORE=1
EXECUTOR_NUM=1


CMD="spark-submit --master yarn  \
--deploy-mode cluster \
--class $APP_MAINCLASS  \
--jars $APP_NAME \
--files $FILEPATH \
--driver-class-path /kkb/code/spark/mysql-connector-java-5.1.27.jar \
--driver-memory $DRIVER_MEM \
--num-executors $EXECUTOR_NUM \
--executor-memory $EXECUTOR_MEM \
--executor-cores $EXECUTOR_CORE \
$APP_NAME $FILENAME '$OPERATE_TIME_START' '$OPERATE_TIME_END' $ETL_DATE"

eval $CMD