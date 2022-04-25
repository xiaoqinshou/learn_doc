## Flink安装
### 概述
> Flink是一个对有界和无界数据流进行状态计算的分布式处理引擎和框架。通俗地讲，Flink就是一个流计算框架，主要用来处理流式数据。其起源于2010年德国研究基金会资助的科研项目“Stratosphere”，2014年3月成为Apache孵化项目，12月即成为Apache顶级项目。Flinken在德语里是敏捷的意思，意指快速精巧。其代码主要是由 Java 实现，部分代码由 Scala实现。Flink既可以处理有界的批量数据集，也可以处理无界的实时流数据，为批处理和流处理提供了统一编程模型。

### 本地安装部署
#### 本地模式
* 本地模式即在linux服务器直接解压flink二进制包就可以使用，不用修改任何参数，用于一些简单测试场景。

#### 下载安装包
* 直接在[Flink官网](https://www.apache.org)下载安装包，目前最新版为[flink-1.14.3-bin-scala_2.11.tgz](https://www.apache.org/dyn/closer.lua/flink/flink-1.14.3/flink-1.14.3-bin-scala_2.11.tgz)

#### 解压到指定目录
```shell
$ tar -xf flink-1.14.3-bin-scala_2.11.tgz
```

#### 启动Flink
* 注意运行之前确保机器上已经安装了JDK1.8或以上版本，并配置了JAVA_HOME环境变量。
* [java安装](../hadoop/Java安装.md)
```shell
$ java -version
java version "1.8.0_172"
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)
```

* 进入bin目录执行启动命令
```shell
$ cd bin/
$ ./start-cluster.sh
Starting cluster.
Starting standalonesession daemon on host 2U.
Starting taskexecutor daemon on host 2U.
```

* 查看是否运行成功
```shell
$ jps
19008 StandaloneSessionClusterEntrypoint
20130 Jps
19526 TaskManagerRunner
```

* 访问Web界面 [http://your_flink_host:8081]()

### 集群模式
* 集群环境适合在生产环境下面使用，且需要修改对应的配置参数。Flink提供了多种集群模式，我们这里主要介绍standalone和Flink on Yarn两种模式。

#### Linux机器规划
节点类型|主机名|IP
:-|:-|:-
Master|vm1|192.168.1.136
Slave|vm2|192.168.1.137
Slave|vm3|192.168.1.138

* 在Flink集群中，Master节点上会运行JobManager(StandaloneSessionClusterEntrypoint)进程，Slave节点上会运行TaskManager(TaskManagerRunner)进程。

* 集群中Linux节点都要配置JAVA_HOME，并且节点之间需要设置ssh免密码登录，至少保证Master节点可以免密码登录到其他两个Slave节点，linux防火墙也需关闭。[免密配置](../hbase/ssh免密.md)

* 集群各个节点主机时间也得保持一致

#### 调整时间
```shell
# 安装ntpdate
$ yum install -y ntpdate

# 同步时间
$ ntpdate -u ntp.sjtu.edu.cn
```

#### Standalone模式
* Standalone是Flink的独立集群部署模式，不依赖任何其它第三方软件或库。如果想搭建一套独立的Flink集群，不依赖其它组件可以使用这种模式。搭建一个标准的Flink集群，需要准备3台Linux机器。

##### Flink安装步骤
* 下列步骤都是先在Master机器上操作，再拷贝到其它机器(确保每台机器都安装了jdk)
1. 解压
    ```shell
    $ tar -xf flink-1.14.3-bin-scala_2.11.tgz
    ```
2. 修改Flink的配置文件./conf/flink-conf.yaml
    ```shell
    $ vi ./conf/flink-conf.yaml

    # 将参数改为对应主机名
    jobmanager.rpc.address: vm1
    ```
3. 修改Flink的配置文件./conf/workers
    ```shell
    vi ./conf/workers
    # 把slaver 机器主机名添加上去
    vm2
    vm3
    ```
4. 将vm1这台机器上修改后的flink-1.14.3目录复制到其他两个Slave节点
    ```shell
    scp -rq /usr/local/myapp/flink vm2:/usr/local/myapp/
    scp -rq /usr/local/myapp/flink vm3:/usr/local/myapp/
    ```
5. 在vm1这台机器上启动Flink集群服务
    ```shell
    $ bin/start-cluster.sh
    Starting cluster.
    Starting standalonesession daemon on host vm1.
    Starting taskexecutor daemon on host vm2.
    Starting taskexecutor daemon on host vm3.
    ```
6. 查看vm1、vm2和vm3这3个节点上的进程信息
    ```shell
    $ jps
    4983 StandaloneSessionClusterEntrypoint
    5048 Jps

    $ jps
    4122 TaskManagerRunner
    4175 Jps

    $ jps
    4101 Jps
    4059 TaskManagerRunner
    ```
7. 查看Flink Web UI界面，访问http://vm1:8081
8. 提交任务执行
    ```shell
    $ ./bin/flink run ./examples/batch/WordCount.jar
    ```
    提交任务可以在任意一台flink客户端服务器提交，本例中在vm1、vm2、vm3都可以
9. 停止flink集群
    ```shell
    $ ./bin/stop-cluster.sh
    ```
10. 单独启动、停止进程
    ```shell
    # 手工启动、停止主进程StandaloneSessionClusterEntrypoint
    $ ./bin/jobmanager.sh start
    $ ./bin/jobmanager.sh stop

    # 手工启动、停止TaskManagerRunner(常用于向集群中添加新的slave节点)
    $ ./bin/taskmanager.sh start
    $ ./bin/taskmanager.sh stop
    ```

#### Flink on YARN 模式
* Flink on Yarn模式使用YARN 作为任务调度系统，即在YARN上启动运行flink。好处是能够充分利用集群资源，提高服务器的利用率。这种模式的前提是要有一个Hadoop集群，并且只需公用一套hadoop集群就可以执行MapReduce和Spark以及Flink任务，非常方便。因此需要先搭建一个hadoop集群。

##### hadoop集群
* [hadoop集群搭建](../hadoop/安装文档.md)

##### Flink on Yarn的两种方式
1. 在YARN中预先初始化一个Flink集群，占用YARN中固定的资源。该Flink集群常驻YARN 中，所有的Flink任务都提交到这里。这种方式的缺点在于不管有没有Flink任务执行，Flink集群都会独占系统资源，除非手动停止。如果YARN中给Flink集群分配的资源耗尽，只能等待YARN中的一个作业执行完成释放资源才能正常提交下一个Flink作业。
2. 每次提交Flink任务时单独向YARN申请资源，即每次都在YARN上创建一个新的Flink集群，任务执行完成后Flink集群终止，不再占用机器资源。这样不同的Flink任务之间相互独立互不影响。这种方式能够使得资源利用最大化，适合长时间、大规模计算任务。

###### 第1种方式
1. 启动Hadoop集群
    ```shell
    $ ./sbin/start-all.sh
    ```
2. 将flink依赖的hadoop相关jar包拷贝到flink目录
    ```shell
    $ cp /opt/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar /opt/hadoop/flink-1.14.3/flink-1.14.3/lib/
    $ cp /opt/hadoop/hadoop-2.7.2/share/hadoop/yarn/sources/hadoop-yarn-api-2.7.2-sources.jar /opt/hadoop/flink-1.14.3/flink-1.14.3/lib/
    ```
    引入 flink 与 hadoop 的兼容包 [flink-shaded-hadoop-2-uber-2.8.3-10.0.jar ](https://search.maven.org/artifact/org.apache.flink/flink-shaded-hadoop-2-uber/2.8.3-10.0/jar),也放到flink的lib目录下

3. 创建并启动flink集群
    ```shell
    $ ./bin/yarn-session.sh -n 2 -jm 512 -tm 512 -d
    # 此处省略一大堆启动日志
    # 最后会打印出flink的web页面，直接访问即可，以及集群ID
    2022-02-15 10:11:31,176 INFO  org.apache.hadoop.yarn.client.api.impl.YarnClientImpl        [] - Submitted application application_1644831390513_0001
    2022-02-15 10:11:31,177 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Waiting for the cluster to be allocated
    2022-02-15 10:11:31,178 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Deploying cluster, current state ACCEPTED
    2022-02-15 10:11:44,523 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - YARN application has been deployed successfully.
    2022-02-15 10:11:44,524 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Found Web Interface sit-strm26:11619 of application 'application_1644831390513_0001'.
    JobManager Web Interface: http://sit-strm26:11619
    ```

4. 附着到flink集群
* 创建flink集群后会有对应的applicationId，因此执行flink任务时也可以附着到已存在的、正在运行的flink集群
    ```shell
    # 附着到指定flink集群, 上一步骤打印出来的集群运行ID
    $ bin/yarn-session.sh -id application_1644831390513_0001
    ```

5.  提交flink任务
* 可以运行flink自带的wordcount样例：
    ```
    $ bin/flink run ./examples/batch/WordCount.jar
    ```
* 在flink web页面 http://sit-strm26:11619 可以看到运行记录：

* 可以通过-input和-output来手动指定输入数据目录和输出数据目录:
    ```shell
    -input hdfs://vm1:9000/words
    -output hdfs://vm1:9000/wordcount-result.txt
    ```

###### 第2种方式
* 这种方式很简单，就是在提交flink任务时同时创建flink集群
    ```shell
    $ bin/flink run -m yarn-cluster -yjm 1024 ./examples/batch/WordCount.jar
    ```
* 需要在执行上述命令的机器(即flink客户端)上配置环境变量YARN_CONF_DIR、HADOOP_CONF_DIR或者HADOOP_HOME环境变量，Flink会通过这个环境变量来读取YARN和HDFS的配置信息。

* 如果报下列错，则需要禁用hadoop虚拟内存检查：
    ```shell
    Diagnostics from YARN: Application application_1602852161124_0004 failed 1 times (global limit =2; local limit is =1) due to AM Container for appattempt_1602852161124_0004_000001 exited with  exitCode: -103
    Failing this attempt.Diagnostics: [2020-10-16 23:35:56.735]Container [pid=6890,containerID=container_1602852161124_0004_01_000001] is running beyond virtual memory limits. Current usage: 105.8 MB of 1 GB physical memory used; 2.2 GB of 2.1 GB virtual memory used. Killing container.
    ```

* 修改所有hadoop机器(所有 nodemanager)的文件$HADOOP_HOME/etc/hadoop/yarn-site.xml
    ```shell
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    ```

* 重启hadoop集群再次运行
    ```shell
    $ sbin/stop-all.sh
    $ sbin/start-all.sh
    $ bin/flink run  -m yarn-cluster  -yjm 1024 ./examples/batch/WordCount.jar
    ```

* 任务成功执行，控制台输出如下
    ```shell
    022-02-16 09:56:06,918 INFO  org.apache.hadoop.yarn.client.api.impl.YarnClientImpl        [] - Submitted application application_1644831390513_0002
    2022-02-16 09:56:06,918 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Waiting for the cluster to be allocated
    2022-02-16 09:56:06,920 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Deploying cluster, current state ACCEPTED
    2022-02-16 09:56:16,968 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - YARN application has been deployed successfully.
    2022-02-16 09:56:16,969 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Found Web Interface sit-strm26:6121 of application 'application_1644831390513_0002'.
    Job has been submitted with JobID 157597c7fd0e6e0498984658dd101615
    Program execution finished
    Job with JobID 157597c7fd0e6e0498984658dd101615 has finished.
    Job Runtime: 13658 ms
    Accumulator Results:
    - 262bae41deba4ff33cc88a467ac4fbf9 (java.util.ArrayList) [170 elements]


    (a,5)
    (action,1)
    (after,1)
    (against,1)
    (all,2)
    (and,12)
    (arms,1)
    (arrows,1)
    (awry,1)
    (ay,1)
    (bare,1)
    (be,4)
    (bear,3)
    (bodkin,1)
    (bourn,1)
    (but,1)
    (by,2)
    (calamity,1)
    (cast,1)
    (coil,1)
    (come,1)
    (conscience,1)
    (consummation,1)
    (contumely,1)
    (country,1)
    (cowards,1)
    (currents,1)
    (d,4)
    (death,2)
    (delay,1)
    (despis,1)
    (devoutly,1)
    (die,2)
    (does,1)
    (dread,1)
    (dream,1)
    (dreams,1)
    (end,2)
    (enterprises,1)
    (er,1)
    (fair,1)
    (fardels,1)
    (flesh,1)
    (fly,1)
    (for,2)
    (fortune,1)
    (from,1)
    (give,1)
    (great,1)
    (grunt,1)
    (have,2)
    (he,1)
    (heartache,1)
    (heir,1)
    (himself,1)
    (his,1)
    (hue,1)
    (ills,1)
    (in,3)
    (insolence,1)
    (is,3)
    (know,1)
    (law,1)
    (life,2)
    (long,1)
    (lose,1)
    (love,1)
    (make,2)
    (makes,2)
    (man,1)
    (may,1)
    (merit,1)
    (might,1)
    (mind,1)
    (moment,1)
    (more,1)
    (mortal,1)
    (must,1)
    (my,1)
    (name,1)
    (native,1)
    (natural,1)
    (no,2)
    (nobler,1)
    (not,2)
    (now,1)
    (nymph,1)
    (o,1)
    (of,15)
    (off,1)
    (office,1)
    (ophelia,1)
    (opposing,1)
    (oppressor,1)
    (or,2)
    (orisons,1)
    (others,1)
    (outrageous,1)
    (pale,1)
    (pangs,1)
    (patient,1)
    (pause,1)
    (perchance,1)
    (pith,1)
    (proud,1)
    (puzzles,1)
    (question,1)
    (quietus,1)
    (rather,1)
    (regard,1)
    (remember,1)
    (resolution,1)
    (respect,1)
    (returns,1)
    (rub,1)
    (s,5)
    (say,1)
    (scorns,1)
    (sea,1)
    (shocks,1)
    (shuffled,1)
    (sicklied,1)
    (sins,1)
    (sleep,5)
    (slings,1)
    (so,1)
    (soft,1)
    (something,1)
    (spurns,1)
    (suffer,1)
    (sweat,1)
    (take,1)
    (takes,1)
    (than,1)
    (that,7)
    (the,22)
    (their,1)
    (them,1)
    (there,2)
    (these,1)
    (this,2)
    (those,1)
    (thought,1)
    (thousand,1)
    (thus,2)
    (thy,1)
    (time,1)
    (tis,2)
    (to,15)
    (traveller,1)
    (troubles,1)
    (turn,1)
    (under,1)
    (undiscover,1)
    (unworthy,1)
    (us,3)
    (we,4)
    (weary,1)
    (what,1)
    (when,2)
    (whether,1)
    (whips,1)
    (who,2)
    (whose,1)
    (will,1)
    (wish,1)
    (with,3)
    (would,2)
    (wrong,1)
    (you,1)
    ```
    * 其中 ```sit-strm26:6121``` 为flink web页面的地址
    * 不过这种模式下任务执行完成后Flink集群即终止
    * 所以不一定随时打开这个页面都有效

#### 关键参数
* 上述Flink On Yarn的2种方式案例中分别使用了两个命令：```yarn-session.sh``` 和 ```flink run```
* ```yarn-session.sh``` 可以用来在Yarn上创建并启动一个flink集群，可以通过如下命令查看常用参数：
  ```shell
  $ yarn-session.sh -h
  -n :表示分配的容器数量，即TaskManager的数量

  -jm:设置jobManagerMemory，即JobManager的内存，单位MB

  -tm:设置taskManagerMemory ，即TaskManager的内存，单位MB

  -d: 设置运行模式为detached，即后台独立运行

  -nm：设置在YARN上运行的应用的name（名字）

  -id: 指定任务在YARN集群上的applicationId ,附着到后台独立运行的yarn session中
  ```
* flink run命令既可以提交任务到Flink集群中执行，也可以在提交任务时创建一个新的flink集群，可以通过如下命令查看常用参数：
  ```shell
  $ flink run -h
  -m: 指定主节点(JobManger)的地址，在此命令中指定的JobManger地址优先于配置文件中的

  -c: 指定jar包的入口类，此参数在jar 包名称之前

  -p: 指定任务并行度，同样覆盖配置文件中的值
  ```

* 例子
  ```shell
  # 提交并执行flink任务，默认查找当前YARN集群中已有的yarn-session的JobManager
  # 读取hdfs 集群路径下的某个文件，将值输出到hdfs的某个路径下
  $ bin/flink run ./examples/batch/WordCount.jar -input hdfs://vm1:9000/hello.txt -output hdfs://vm1:9000/result_hello

  # 提交flink任务时显式指定JobManager的的host的port，该域名和端口是创建flink集群时控制台输出的
  $ bin/flink run -m vm3:39921 ./examples/batch/WordCount.jar  -input hdfs://vm1:9000/hello.txt -output hdfs://vm1:9000/result_hello

  # 在YARN中启动一个新的Flink集群，并提交任务
  $ bin/flink run  -m yarn-cluster  -yjm 1024 ./examples/batch/WordCount.jar -input hdfs://vm1:9000/hello.txt -output hdfs://vm1:9000/result_hello
  ```
#### Flink on Yarn集群HA
> Flink on Yarn模式的HA利用的是YARN的任务恢复机制。Flink on Yarn模式依赖hadoop集群，这里可以使用前文中的hadoop集群。这种模式下的HA虽然依赖YARN的任务恢复机制，但是Flink任务在恢复时，需要依赖检查点产生的快照。快照虽然存储在HDFS上，但是其元数据保存在zk中，所以也需要一个zk集群，使用前文配置好的zk集群即可。

