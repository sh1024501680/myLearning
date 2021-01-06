## 02-数仓搭建 ods层

- 保持数据原貌不做任何修改，起到备份数据的作用。
- 数据采用 ORC格式,减少磁盘存储空间,为后续即席查询做数据准备
- 创建分区表，防止后续的全表扫描，在企业开发中大量使用分区表。
- 内部表和外部表相结合的方式创建。

### 1、hive环境准备

#### 1.1 hive引擎介绍

Hive 引擎包括:默认 MR、tez、spark
Hive on Spark:Hive 既作为存储元数据又负责 SQL 的解析优化，语法是 HQL 语法，执行引擎变成了 Spark，Spark 负责采用 RDD 执行。

Spark on Hive : Hive 只作为存储元数据，Spark 负责 SQL 解析优化，语法是 Spark SQL。

语法，Spark 负责采用 RDD 执行。

#### 1.2 hive on spark配置

##### 1）spark版本说明

2.3.4

##### 2）在hive节点部署spark

如果之前已经部署了 Spark，则该步骤可以跳过，但要检查 SPARK_HOME 的环境变量 配置是否正确。

- Spark 官网下载 jar 包地址: http://spark.apache.org/downloads.html 

- 上传并解压spark-2.3.4-bin-hadoop2.7.tgz

```shell
因为hive安装在node02上，所以在node02上安装spark

[hadoop@node02 soft]$ pwd
/kkb/soft
[hadoop@node02 soft]$ tar -zxvf spark-2.3.4-bin-hadoop2.7.tgz -C /kkb/install/
[hadoop@node02 soft]$ cd /kkb/install/
[hadoop@node02 install]$ mv spark-2.3.4-bin-hadoop2.7/ spark
```

##### 3）配置spark环境变量

```shell
[hadoop@node02 install]$ sudo vi /etc/profile
export SPARK_HOME=/kkb/install/spark
export PATH=:$SPARK_HOME/bin:$PATH

[hadoop@node02 install]$ source /etc/profile
```

##### 4) hive中新建spark配置文件 spark-defaults.conf

```shell
[hadoop@node02 hive]$ pwd
/kkb/install/hive
[hadoop@node02 hive]$ cd conf/
[hadoop@node02 conf]$ vi spark-defaults.conf

spark.master yarn
spark.eventLog.enabled true
spark.eventLog.dir hdfs://node01:8020/spark-history 
spark.executor.memory 1g
spark.driver.memory 1g
```

##### 5）在 HDFS 创建如下路径，用于存储历史日志

```shell
[hadoop@node01 bin]$ hadoop fs -mkdir /spark-history
```

##### 6）向 **HDFS** 上传 **Spark**  **jar** 包

解压**spark-2.3.4-bin-without-hadoop.tgz**,将所有的jar包上传到/spark-jars中，供后续yarn使用

- 上传jar包到hdfs

```shell
[hadoop@node01 soft]$ hadoop fs -mkdir /spark-jars

[hadoop@node02 soft]$ pwd
/kkb/soft
[hadoop@node02 soft]$ tar -zxvf spark-2.3.4-bin-without-hadoop.tgz

[hadoop@node02 soft]$ [hadoop@node02 soft]$ hadoop fs -put /kkb/soft/spark-2.3.4-bin-without-hadoop/jars/* hdfs://node01:8020/spark-jars
```

##### 7）修改 **hive-site.xml** 文件

```shell
[hadoop@node02 soft]$ cd /kkb/install/hive/
[hadoop@node02 hive]$ vi conf/hive-site.xml

<!--Spark 依赖位置(注意:端口号 8020 必须和 namenode 的端口号一致)--> 
<property>
   <name>spark.yarn.jars</name>
   <value>hdfs://node01:8020/spark-jars/*</value>
</property>
<!--Hive 执行引擎--> <property>
   <name>hive.execution.engine</name>
   <value>spark</value>
</property>
<!--Hive 和 Spark 连接超时时间-->
<property>
   <name>hive.spark.client.connect.timeout</name>
   <value>10000ms</value>
</property>


<property>
   <name>hive.strict.managed.tables</name>
   <value>false</value>
</property>
<property>
   <name>hive.create.as.insert.only</name>
   <value>false</value>
</property>
<property>
   <name>metastore.create.as.acid</name>
   <value>false</value>
</property>
```

注意:hive.spark.client.connect.timeout 的默认值是 1000ms，如果执行 hive 的 insert 语句 时，抛如下异常，可以调大该参数到 10000ms

```
FAILED: SemanticException Failed to get a spark session: org.apache.hadoop.hive.ql.metadata.HiveException: Failed to create Spark client for Spark session d9e0224c-3d14-4bf4-95bc-ee3ec56df48e
 
```

##### 8) 上传spark的包到hive中

将/kkb/install/spark/jars/ 中所有的spark的包和scala的包上传到/kkb/install/hive/lib中。

```
[hadoop@node02 ~]$ cp /kkb/install/spark/jars/spark*.jar /kkb/install/hive/lib/
[hadoop@node02 ~]$ cp /kkb/install/spark/jars/scala*.jar /kkb/install/hive/lib/
```

将spark包中的spark*，拷贝到hive lib中。

将spark包中的scala*，拷贝到hive lib中。

##### 9) hive on spark 测试

```
hive>  create table student(id int, name string);
OK
Time taken: 0.554 seconds

hive> insert into table student values(998,'abc');
Query ID = hadoop_22250807232313_1e30e047-e99a-4cef-a6bb-bd87b4494b8f
Total jobs = 1
Launching Job 1 out of 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Running with YARN Application = application_1601276404402_0012
Kill Command = /kkb/install/hadoop-3.1.4/bin/yarn application -kill application_1601276404402_0012
Hive on Spark Session Web UI URL: http://node02.kaikeba.com:33396

Query Hive on Spark job[0] stages: [0, 1]
Spark job[0] status = RUNNING
--------------------------------------------------------------------------------------
          STAGES   ATTEMPT        STATUS  TOTAL  COMPLETED  RUNNING  PENDING  FAILED
--------------------------------------------------------------------------------------
Stage-0 ........         0      FINISHED      1          1        0        0       0
Stage-1 ........         0      FINISHED      1          1        0        0       0
--------------------------------------------------------------------------------------
STAGES: 02/02    [==========================>>] 100%  ELAPSED TIME: 8.71 s
--------------------------------------------------------------------------------------
Spark job[0] finished successfully in 8.71 second(s)
WARNING: Spark Job[0] Spent 23% (1163 ms / 5097 ms) of task time in GC
Loading data to table default.student
OK
Time taken: 50.131 seconds

hive> select * from student;
OK
1	abc
Time taken: 0.468 seconds, Fetched: 1 row(s)
```

### 2、**Yarn** 容量调度器并发度问题演示

Yarn 默认调度器为 Capacity Scheduler(容量调度器)，且默认只有一个队列——default。 如果队列中执行第一个任务资源不够，就不会再执行第二个任务，一直等到第一个任务执行 完毕。

#### 1)启动 1 个 hive 客户端，执行以下插入数据的 sql 语句。

```
insert into table student values(1,'abc');
```



执行该语句，hive 会初始化一个 Spark Session，用以执行 hive on spark 任务。由于未指

定队列，故该 Spark Session 默认占用使用的就是 default 队列，且会一直占用该队列，直到 退出 hive 客户端。

#### 2)在 hive 客户端开启的状态下，提交一个 MR

```shell
hadoop jar  /kkb/install/hadoop-3.1.4/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.4.jar pi 1 1
```

MR 任务同样未指定队列，所以其默认也提交到了 default 队列，由于容量调度器单个队列的并行度为 1。故后提交的 MR 任务会一直等待，不能开始执行。

#### 3)容量调度器 default 队列中，同一时间只有一个任务执行，并发度低，如何解决呢? 

方案一:增加 ApplicationMaster 资源比例，进而提高运行 app 数量。 

方案二:创建多队列，比如增加一个 hive 队列。

##### 2.3.1 增加 **ApplicationMaster** 资源比例

针对容量调度器并发度低的问题，考虑调整 yarn.scheduler.capacity.maximum-am-resource-percent 该参数。默认值是 0.1，表示集群上 AM 最多可使用的资源比例，目的为限制过多的 app 数量。

1) 在 node01 的kkb/install/hadoop-3.1.4/etc/hadoop/capacity-scheduler.xml 文件中 修改如下参数值

```
[hadoop@node01 bin]$ vi /kkb/install/hadoop-3.1.4/etc/hadoop/capacity-scheduler.xml
<property>
    <name>yarn.scheduler.capacity.maximum-am-resource-percent</name>
    <value>0.5</value>
    <description>
      Maximum percent of resources in the cluster which can be used to run
      application masters i.e. controls number of concurrent running
      applications.
    </description>
  </property>
```

```
集群中用于运行应用程序 ApplicationMaster 的资源比例上限， 该参数通常用于限制处于活动状态的应用程序数目。该参数类型为浮点型，默认是 0.1，表示 10%。所有队列的 ApplicationMaster 资源比例上限可通过参数 yarn.scheduler.capacity.maximum-am-resource-percent 设置，而单个队列可通过参数yarn.scheduler.capacity.<queue-path>.maximum-am-resource-perce nt 设置适合自己的值。
```

2) 分发文件

```
[hadoop@node01 bin]$ syncfile.sh /kkb/install/hadoop-3.1.4/etc/hadoop/capacity-scheduler.xml
```

3）重启hadoop

##### 2.3.2 增加容量调度器队列

**1)修改容量调度器配置文件**
 默认 Yarn 的配置下，容量调度器只有一条 default 队列。在 capacity-scheduler.xml 中可以配置多条队列，修改以下属性，增加 hive 队列。

```shell
[hadoop@node01 bin]$ vi /kkb/install/hadoop-3.1.4/etc/hadoop/capacity-scheduler.xml

 <property>
    <name>yarn.scheduler.capacity.root.queues</name>
    <value>default,hive</value>
    <description>
      The queues at the this level (root is the root queue).
    </description>
  </property>
  
  <property>
    <name>yarn.scheduler.capacity.root.default.capacity</name>
    <value>50</value>
    <description>Default queue target capacity.</description>
  </property>

```

同时为hive队列增加必要的属性：

```xml
<property>
   <name>yarn.scheduler.capacity.root.hive.capacity</name>
   <value>50</value>
   <description>
hive 队列的容量为 50% </description>
</property>
<property>
   <name>yarn.scheduler.capacity.root.hive.user-limit-factor</name>
   <value>1</value>
   <description>
一个用户最多能够获取该队列资源容量的比例，取值 0-1
</description>
</property>
<property>
   <name>yarn.scheduler.capacity.root.hive.maximum-capacity</name>
   <value>80</value>
   <description>
hive 队列的最大容量(自己队列资源不够，可以使用其他队列资源上限) </description>
</property>
<property>
   <name>yarn.scheduler.capacity.root.hive.state</name>
   <value>RUNNING</value>
   <description>
开启 hive 队列运行，不设置队列不能使用
</description>
</property>
<property>
<name>yarn.scheduler.capacity.root.hive.acl_submit_applications</name>
   <value>*</value>
   <description>
访问控制，控制谁可以将任务提交到该队列,*表示任何人 </description>
</property>
<property>
   <name>yarn.scheduler.capacity.root.hive.acl_administer_queue</name>
   <value>*</value>
   <description>
访问控制，控制谁可以管理(包括提交和取消)该队列的任务，*表示任何人
</description>
</property>
<property> <name>yarn.scheduler.capacity.root.hive.acl_application_max_priority</name>
    <value>*</value>
   <description>
   指定哪个用户可以提交配置任务优先级
   </description>
</property>

<property>
<name>yarn.scheduler.capacity.root.hive.maximum-application-lifetime</name> 
<value>-1</value> 
<description>
hive 队列中任务的最大生命时长，以秒为单位。任何小于或等于零的值将被视为禁用。 </description>
</property>
<property>
<name>yarn.scheduler.capacity.root.hive.default-application-lifetime</name> 
<value>-1</value> 
<description>
hive 队列中任务的默认生命时长，以秒为单位。任何小于或等于零的值将被视为禁用。
</description>
</property>
```























































































