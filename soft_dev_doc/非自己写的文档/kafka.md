# kafka安装

## jdk安装

### jdk下载

略

### jdk安装

```
#解包
tar xf jdk-8u74-linux-x64.gz -C /usr/local/
```

### 设置jdk环境变量

```bash
# vi /root/.bash_profile

export JAVA_HOME=/usr/local/jdk1.8.0_74
export PATH=$JAVA_HOME/bin:$PATH
```

### 验证

&emsp;退出ssh客户端，重新连接登录。

```bash
#验证
java -version
```

## kafka安装

>* [kafka官网](http://kafka.apache.org/)

### kafka下载

>* [1.0.0](http://kafka.apache.org/downloads)

```bash
#kafka 2.12-1.0.0下载
wget https://mirrors.tuna.tsinghua.edu.cn/apache/kafka/1.0.0/kafka_2.12-1.0.0.tgz
```

### kafka安装

```
#解包
tar xf kafka_2.12-1.0.0.tgz -C /opt/wacos/server/
```

## kafka配置

>* [kafka configuration](http://kafka.apache.org/documentation/#configuration)

### server.properties

The essential configurations are the following:
* broker.id
* log.dirs
* zookeeper.connect

```properties
# grep -E -v "^#|^$" server.properties
# vi /opt/wacos/server/kafka_2.12-1.0.0/config/server.properties

broker.id=0
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
log.dirs=/opt/wacos/server/kafka_2.12-1.0.0/data/
num.partitions=3
num.recovery.threads.per.data.dir=1
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=10.48.114.10:2181,10.48.114.12:2181,10.48.114.31:2181
zookeeper.connection.timeout.ms=6000
group.initial.rebalance.delay.ms=0
```

## kafka启动

### 启动

```bash
#启动
cd /opt/wacos/server/kafka_2.12-1.0.0/bin/
./kafka-server-start.sh -daemon ../config/server.properties
#查看集群brokers
./zookeeper-shell.sh 10.48.114.12:2181
ls /brokers/ids

#list topic
./kafka-topics.sh --list --zookeeper 10.48.114.10:2181

#查看topic详细信息
./kafka-topics.sh --describe --zookeeper 10.48.114.10:2181  --topic filenotify

#消费者
./kafka-console-consumer.sh --zookeeper 10.48.114.10:2181 --topic sparkStreaming2 --from-beginning

./kafka-topics.sh --delete --zookeeper 10.48.114.12:2181 --topic sparkStreaming1

./kafka-topics.sh --create --zookeeper 10.48.114.12:2181 --replication-factor 2 --partitions 12 --topic sparkStreaming2

./kafka-topics.sh --describe --zookeeper 10.48.114.12:2181 --topic sparkStreaming2

./kafka-console-consumer.sh --bootstrap-server 10.48.114.12:9092,10.48.114.10:9092,10.48.114.31:9092 --topic sparkStreaming1 --from-beginning

# 生产者
./kafka-console-producer.sh --broker-list localhost:9092 --topic sparkStreaming1
```

# 管理

## 主题

```bash
# 帮助
[root@BY-DSI bin]# ./kafka-topics.sh --help
Command must include exactly one action: --list, --describe, --create, --alter or --delete
Option                                   Description                            
------                                   -----------                            
--alter                                  Alter the number of partitions,        
                                           replica assignment, and/or           
                                           configuration for the topic.         
--config <String: name=value>            A topic configuration override for the 
                                           topic being created or altered.The   
                                           following is a list of valid         
                                           configurations:                      
                                                cleanup.policy                        
                                                compression.type                      
                                                delete.retention.ms                   
                                                file.delete.delay.ms                  
                                                flush.messages                        
                                                flush.ms                              
                                                follower.replication.throttled.       
                                           replicas                             
                                                index.interval.bytes                  
                                                leader.replication.throttled.replicas 
                                                max.message.bytes                     
                                                message.format.version                
                                                message.timestamp.difference.max.ms   
                                                message.timestamp.type                
                                                min.cleanable.dirty.ratio             
                                                min.compaction.lag.ms                 
                                                min.insync.replicas                   
                                                preallocate                           
                                                retention.bytes                       
                                                retention.ms                          
                                                segment.bytes                         
                                                segment.index.bytes                   
                                                segment.jitter.ms                     
                                                segment.ms                            
                                                unclean.leader.election.enable        
                                         See the Kafka documentation for full   
                                           details on the topic configs.        
--create                                 Create a new topic.                    
--delete                                 Delete a topic                         
--delete-config <String: name>           A topic configuration override to be   
                                           removed for an existing topic (see   
                                           the list of configurations under the 
                                           --config option).                    
--describe                               List details for the given topics.     
--disable-rack-aware                     Disable rack aware replica assignment  
--force                                  Suppress console prompts               
--help                                   Print usage information.               
--if-exists                              if set when altering or deleting       
                                           topics, the action will only execute 
                                           if the topic exists                  
--if-not-exists                          if set when creating topics, the       
                                           action will only execute if the      
                                           topic does not already exist         
--list                                   List all available topics.             
--partitions <Integer: # of partitions>  The number of partitions for the topic 
                                           being created or altered (WARNING:   
                                           If partitions are increased for a    
                                           topic that has a key, the partition  
                                           logic or ordering of the messages    
                                           will be affected                     
--replica-assignment <String:            A list of manual partition-to-broker   
  broker_id_for_part1_replica1 :           assignments for the topic being      
  broker_id_for_part1_replica2 ,           created or altered.                  
  broker_id_for_part2_replica1 :                                                
  broker_id_for_part2_replica2 , ...>                                           
--replication-factor <Integer:           The replication factor for each        
  replication factor>                      partition in the topic being created.
--topic <String: topic>                  The topic to be create, alter or       
                                           describe. Can also accept a regular  
                                           expression except for --create option
--topics-with-overrides                  if set when describing topics, only    
                                           show topics that have overridden     
                                           configs                              
--unavailable-partitions                 if set when describing topics, only    
                                           show partitions whose leader is not  
                                           available                            
--under-replicated-partitions            if set when describing topics, only    
                                           show under replicated partitions     
--zookeeper <String: urls>               REQUIRED: The connection string for    
                                           the zookeeper connection in the form 
                                           host:port. Multiple URLS can be      
                                           given to allow fail-over. 
```

### 创建主题

```bash
# 创建主题
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --create --topic kafkatest --replication-factor 2 --partitions 6
```

* 指定主题配置

&emsp;可以在创建主题时显示的指定复制系数或者对配置进行覆盖，是通过向kafka-topics.sh传递--config参数来实现的。

* 主题的命名

&emsp;主题名字的开头部分包含两个下划线是合法的，但不建议这么做。具有这种格式的主题一般是集群的内部主题（比如__consumer_offsets主题用于保存消费者群组的偏移量）。也不建议在单个集群里使用英文状态下的句号和下划线来命令，因为主题的名字会被用在度量指标上，句号会被替换成下划线(比如topic.test会被替换成topic_test)

* 禁用基于机架信息的分配策略

&emsp;如果不需要基于机架信息的分配策略,可以指定参数--disable-rack-aware

* 忽略重复创建主题的错误

&emsp;在自动化系统里调用这个脚本时，可以用--if-not-exists 参数，这样即使主题已经存在，也不会抛出重复创建主题的错误

### 增加分区

```bash
# 增加分区
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --alter --topic kafkatest --partitions 8
```

* 调整基于键的主题

&emsp;从消费者的角度来看，为基于键的主题添加分区是很困难的。因为如果改变了分区的数量，键到分区之间的映射也会发生变化。所以，对于基于键的主题来说，建议一开始就设置好分区数量，避免以后对其进行调整。

* 忽略主题不存在的错误

&emsp;在使用--alter命令修改主题时，如果指定了--if-exists参数，主题不存在的错误就会被忽略。如果要修改的主题不存在，该命令不会返回任何错误。在主题不存在的时候本应该创建主题，但它却把错误隐藏起来，不建议使用这个参数。

* 减少分区数量

&emsp;我们无法减少主题的分区数量。因为如果删除了分区，分区里的数据也一并被删除，导致数据不一致。我们无法将这些数据分配给其它分区，因为这样做很难，而且会出现消息乱序。所以，如果一定要减少分区数量，只能删除这个主题，然后重新创建它。

### 删除主题

&emsp;为了能够删除主题，broker的delete.topic.enable参数必须被设置为true。如果该参数被设置为false，删除主题的请求会被忽略。
&emsp;删除主题会丢弃主题里的所有数据。这是一个不可逆的操作，所以在执行时要十分小心。

```bash
# 删除主题
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --delete --topic kafkatest
```

### 列出集群里的所有主题

```bash
# 列出集群里的所有主题
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --list
```

### 列出主题详细信息


* "leader" is the node responsible for all reads and writes for the given partition. Each node will be the leader for a randomly selected portion of the partitions.
* "replicas" is the list of nodes that replicate the log for this partition regardless of whether they are the leader or even if they are currently alive.
* "isr" is the set of "in-sync" replicas. This is the subset of the replicas list that is currently alive and caught-up to the leader.

```bash
# 列出集群里所有主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe

# 列出指定主题的详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming1

# --topics-with-overrides 列出所有包含覆盖配置的主题
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topics-with-overrides

# --under-replicated-partitions 列出包含不同步副本的分区
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --under-replicated-partitions

# --unavailable-partitions 列出所有没有leader的分区。这些分区已经处于离线状态，对于生产者和消费者来说是不可用的。
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --unavailable-partitions
```

## 消费者群组

&emsp;在Kafka里，有两个地方保存着消费者群组消息。新版本kafka用--bootstrap-server，旧版本用--zookeeper

| 版本 | 保存位置 | 参数 |
| --- | --- | --- |
| < 0.9 | zookeeper | --zookeeper |
| >= 0.9 | broker | --bootstrap-server |

```bash
# 帮助
[root@BY-DSI bin]# ./kafka-consumer-groups.sh --bootstrap-server 10.48.114.12:9092
Command must include exactly one action: --list, --describe, --delete, --reset-offset
Option                                  Description                            
------                                  -----------                            
--all-topics                            Consider all topics assigned to a      
                                          group in the `reset-offsets` process.
--bootstrap-server <String: server to   REQUIRED (for consumer groups based on 
  connect to>                             the new consumer): The server to     
                                          connect to.                          
--by-duration <String: duration>        Reset offsets to offset by duration    
                                          from current timestamp. Format:      
                                          'PnDTnHnMnS'                         
--command-config <String: command       Property file containing configs to be 
  config property file>                   passed to Admin Client and Consumer. 
--delete                                Pass in groups to delete topic         
                                          partition offsets and ownership      
                                          information over the entire consumer 
                                          group. For instance --group g1 --    
                                          group g2                             
                                        Pass in groups with a single topic to  
                                          just delete the given topic's        
                                          partition offsets and ownership      
                                          information for the given consumer   
                                          groups. For instance --group g1 --   
                                          group g2 --topic t1                  
                                        Pass in just a topic to delete the     
                                          given topic's partition offsets and  
                                          ownership information for every      
                                          consumer group. For instance --topic 
                                          t1                                   
                                        WARNING: Group deletion only works for 
                                          old ZK-based consumer groups, and    
                                          one has to use it carefully to only  
                                          delete groups that are not active.   
--describe                              Describe consumer group and list       
                                          offset lag (number of messages not   
                                          yet processed) related to given      
                                          group.                               
--execute                               Execute operation. Supported           
                                          operations: reset-offsets.           
--export                                Export operation execution to a CSV    
                                          file. Supported operations: reset-   
                                          offsets.                             
--from-file <String: path to CSV file>  Reset offsets to values defined in CSV 
                                          file.                                
--group <String: consumer group>        The consumer group we wish to act on.  
--list                                  List all consumer groups.              
--new-consumer                          Use the new consumer implementation.   
                                          This is the default, so this option  
                                          is deprecated and will be removed in 
                                          a future release.                    
--reset-offsets                         Reset offsets of consumer group.       
                                          Supports one consumer group at the   
                                          time, and instances should be        
                                          inactive                             
                                        Has 3 execution options: (default) to  
                                          plan which offsets to reset, --      
                                          execute to execute the reset-offsets 
                                          process, and --export to export the  
                                          results to a CSV format.             
                                        Has the following scenarios to choose: 
                                          --to-datetime, --by-period, --to-    
                                          earliest, --to-latest, --shift-by, --
                                          from-file, --to-current. One         
                                          scenario must be choose              
                                        To define the scope use: --all-topics  
                                          or --topic. . One scope must be      
                                          choose, unless you use '--from-file' 
                                          scenario                             
--shift-by <Long: number-of-offsets>    Reset offsets shifting current offset  
                                          by 'n', where 'n' can be positive or 
                                          negative                             
--timeout <Long: timeout (ms)>          The timeout that can be set for some   
                                          use cases. For example, it can be    
                                          used when describing the group to    
                                          specify the maximum amount of time   
                                          in milliseconds to wait before the   
                                          group stabilizes (when the group is  
                                          just created, or is going through    
                                          some changes). (default: 5000)       
--to-current                            Reset offsets to current offset.       
--to-datetime <String: datetime>        Reset offsets to offset from datetime. 
                                          Format: 'YYYY-MM-DDTHH:mm:SS.sss'    
--to-earliest                           Reset offsets to earliest offset.      
--to-latest                             Reset offsets to latest offset.        
--to-offset <Long: offset>              Reset offsets to a specific offset.    
--topic <String: topic>                 The topic whose consumer group         
                                          information should be deleted or     
                                          topic whose should be included in    
                                          the reset offset process. In `reset- 
                                          offsets` case, partitions can be     
                                          specified using this format: `topic1:
                                          0,1,2`, where 0,1,2 are the          
                                          partition to be included in the      
                                          process. Reset-offsets also supports 
                                          multiple topic inputs.               
--zookeeper <String: urls>              REQUIRED (for consumer groups based on 
                                          the old consumer): The connection    
                                          string for the zookeeper connection  
                                          in the form host:port. Multiple URLS 
                                          can be given to allow fail-over.     
```

### 列出并描述消费者群组

* 列出消费者群组

```bash
# 新版本列出消费者群组
./kafka-consumer-groups.sh --bootstrap-server 10.48.113.11:9092 --list 

# 旧版本列出消费者群组
./kafka-consumer-groups.sh --zookeeper SNMBI-CJ3:22181 --list

# 0.8.2.1列出消费者群组
./zookeeper-shell.sh SNMBI-CJ3:22181
ls /consumers
```

* 列出指定消费者群组详细信息

| 字段 | 描述 |
| --- | --- |
| GROUP | 消费者群组的名字 |
| TOPIC | 正在被读取的主题名字 |
| PARTITION | 正在被读取的分区ID |
| CURRENT-OFFSET | 消费者群组最近提交的偏移量，也就是消费者在分区里读取的当前位置 |
| LOG-END-OFFSET | 当前高水位偏移量，也就是最近一个被读取消息的偏移量，同时也是最近一个被提交到集群的偏移量 |
| LAG | 消费者的CURRENT-OFFSET和broker的LOG-END-OFFSET之间的差距 |
| OWNER | 消费者群组里正在读取该分区的消费者。这是一个消费者的ID，不一定包含消费者的主机名 |

```bash
# # 新版本列出所有消费者群组详细信息
./kafka-consumer-groups.sh --bootstrap-server 10.48.113.11:9092 --describe --all-topics

# 新版本列出指定消费者群组详细信息
./kafka-consumer-groups.sh --bootstrap-server 10.48.113.11:9092 --describe --group rtca

# 旧版本列出指定消费者群组详细信息
./kafka-consumer-groups.sh --zookeeper SNMBI-CJ3:22181 --describe --group console-consumer-27

# 0.8.2.1列出指定消费者群组详细信息
./kafka-consumer-offset-checker.sh --zookeeper SNMBI-CJ3:22181 --topic sparkStreaming1 --group rerun_broEvent_2017-11-21_3 --broker-info
```

### 删除群组

&emsp;只有旧版本的消费者客户端才支持删除群组的操作。
&emsp;删除群组操作将从Zookeeper上移除整个群组，包括所有已保存的偏移量。在执行该操作之前，必须关闭所有的消费者。如果不执行这一步，可能会导致消费者出现不可预测的行为，因为群组的元数据已经供Zookeeper上移除了。

```bash
# 删除群组
./kafka-consumer-offset-checker.sh --zookeeper SNMBI-CJ3:22181 --delete --group rerun_broEvent_2017-11-21_3
# 删除群组指定topic
./kafka-consumer-offset-checker.sh --zookeeper SNMBI-CJ3:22181 --delete --group rerun_broEvent_2017-11-21_3 --topic sparkStreaming1
```

### 偏移量管理

* 管理已经提交到Kafka的偏移量

&emsp;还没有工具可以用以管理由消费者客户端提交到kafka的偏移量，管理功能只对提交到Zookeeper的偏移量可用。另外，为了能够管理提交到Kafka的消费者群组偏移量，需要在客户端使用相应的API来提交群组的偏移量。

```bash
# 导出偏移量
./kafka-run-class.sh kafka.tools.ExportZkOffsets --zkconnect SNMBI-CJ3:22181 --group rerun_broEvent_2017-11-21_3 --output-file tommy.test

# 导入偏移量
# 再导入偏移量之前，必须先关闭所有的消费者。如果消费者群组处于活跃状态，它们不会读取新的偏移量，反而可能将导入的偏移量覆盖掉。
./kafka-run-class.sh kafka.tools.ImportZkOffsets --zkconnect SNMBI-CJ3:22181 --input-file tommy.test
```

## 动态配置变更

&emsp;我们可以在集群处于运行状态时覆盖主题配置和客户端的配置参数。这些参数被放进了kafka-configs.sh，这样就可以为特定的主题和客户端指定配置参数。一旦设置完毕，它们就成为集群的永久配置，被保存在zookeeper上，broker在启动时会读取它们。

```bash
# help
# ./kafka-configs.sh 
Add/Remove entity config for a topic, client, user or broker
Option                      Description                                        
------                      -----------                                        
--add-config <String>       Key Value pairs of configs to add. Square brackets 
                              can be used to group values which contain commas:
                              'k1=v1,k2=[v1,v2,v2],k3=v3'. The following is a  
                              list of valid configurations: For entity_type    
                              'topics':                                        
                                cleanup.policy                                    
                                compression.type                                  
                                delete.retention.ms                               
                                file.delete.delay.ms                              
                                flush.messages                                    
                                flush.ms                                          
                                follower.replication.throttled.replicas           
                                index.interval.bytes                              
                                leader.replication.throttled.replicas             
                                max.message.bytes                                 
                                message.format.version                            
                                message.timestamp.difference.max.ms               
                                message.timestamp.type                            
                                min.cleanable.dirty.ratio                         
                                min.compaction.lag.ms                             
                                min.insync.replicas                               
                                preallocate                                       
                                retention.bytes                                   
                                retention.ms                                      
                                segment.bytes                                     
                                segment.index.bytes                               
                                segment.jitter.ms                                 
                                segment.ms                                        
                                unclean.leader.election.enable                    
                            For entity_type 'brokers':                         
                                follower.replication.throttled.rate               
                                leader.replication.throttled.rate                 
                            For entity_type 'users':                           
                                request_percentage                                
                                producer_byte_rate                                
                                SCRAM-SHA-256                                     
                                SCRAM-SHA-512                                     
                                consumer_byte_rate                                
                            For entity_type 'clients':                         
                                request_percentage                                
                                producer_byte_rate                                
                                consumer_byte_rate                                
                            Entity types 'users' and 'clients' may be specified
                              together to update config for clients of a       
                              specific user.                                   
--alter                     Alter the configuration for the entity.            
--delete-config <String>    config keys to remove 'k1,k2'                      
--describe                  List configs for the given entity.                 
--entity-default            Default entity name for clients/users (applies to  
                              corresponding entity type in command line)       
--entity-name <String>      Name of entity (topic name/client id/user principal
                              name/broker id)                                  
--entity-type <String>      Type of entity (topics/clients/users/brokers)      
--force                     Suppress console prompts                           
--help                      Print usage information.                           
--zookeeper <String: urls>  REQUIRED: The connection string for the zookeeper  
                              connection in the form host:port. Multiple URLS  
                              can be given to allow fail-over. 
```


### 覆盖主题的默认配置

&emsp;主题的很多参数都可以进行单独配置，它们大部分都有broker级别的默认值，在没有覆盖的情况下使用默认值。

```bash
### 覆盖主题的默认值
# 动态修改retention.ms
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --alter --entity-type topics --entity-name sparkStreaming1 --add-config retention.ms=86400000
# 查看所有被修改的配置项
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --describe --entity-type topics --entity-name sparkStreaming1
```

### 覆盖客户端的默认配置

&emsp;对于kafka客户端来说，只能修改两个参数：
>* producer_byte_rate
>* consumer_byte_rate

* 客户端ID与消费者群组
&emsp;客户端ID可以与消费者群组的名字不一样。消费者可以有自己的ID，因此不同群组里的消费者可能具有相同的ID。在为消费者客户端设置ID时，最好使用能够表明它们所属群组的标识符，这样便于群组共享配置，从日志里查找负责请求的群组也更容易一些。

```bash
# 覆盖客户端的默认配置
# 动态修改consumer_byte_rate
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --alter --entity-type clients --entity-name test1 --add-config consumer_byte_rate=10485760
# 查看所有被修改的配置项
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --describe --entity-type clients
```

### 列出所有被覆盖的配置

* 只能显示被覆盖的配置
&emsp;这个命令只能显示被覆盖的配置，不包含集群的默认配置。目前还无法通过Zookeeper或Kafka实现动态获取broker本身的配置。

```bash
# 列出所有被覆盖的配置
# 查看所有被修改的topic的配置项
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --describe --entity-type topics
# 查看所有被修改的客户端的配置项
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --describe --entity-type clients
```

### 删除被覆盖的配置

```bash
# 删除被覆盖的配置
# 删除retention.ms
./kafka-configs.sh --zookeeper 10.48.114.12:2181 --alter --entity-type topics --entity-name sparkStreaming1 --delete-config retention.ms
```

## 分区管理

&emsp;Kafka提供了两个脚本用于管理分区,以实现集群流量的负载均衡：
>* kafka-preferred-replica-election.sh  重新选举leader
>* kafka-reassign-partitions.sh 将分区分配给breoker。

### 分区leader选举

```bash
#help
# ./kafka-preferred-replica-election.sh 
This tool causes leadership for each partition to be transferred back to the 'preferred replica', it can be used to balance leadership among the servers.
Option                                 Description                           
------                                 -----------                           
--path-to-json-file <String: list of   The JSON file with the list of        
  partitions for which preferred         partitions for which preferred      
  replica leader election needs to be    replica leader election should be   
  triggered>                             done, in the following format -     
                                       {"partitions":                        
                                        [{"topic": "foo", "partition": 1},   
                                         {"topic": "foobar", "partition": 2}]
                                       }                                     
                                       Defaults to all existing partitions   
--zookeeper <String: urls>             REQUIRED: The connection string for   
                                         the zookeeper connection in the form
                                         host:port. Multiple URLS can be     
                                         given to allow fail-over.     
```

* 自动leader再均衡
&emsp;broker有一个配置可以用于启用自动分区leader再均衡，不过到目前为止，并不建议在生产环境使用该功能。自动均衡会带来严重的性能问题，在大型集群里，它会造成客户端流量长时间停顿。


* 启动集群的分区leader选举

```bash
# 列出集群里所有主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe
# 启动集群的分区leader选举
./kafka-preferred-replica-election.sh --zookeeper 10.48.114.12:2181 
```

* 指定分区json配置文件来启动分区leader选举

>* partitions.json

```json
{
    "version": 1,
    "partitions": [
        {
            "topic": "sparkStreaming2",
            "partition": 0
        },
        {
            "topic": "sparkStreaming2",
            "partition": 1
        },
        {
            "topic": "sparkStreaming2",
            "partition": 2
        }
    ]
}
```

```bash
# 列出集群里指定主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming2
# 启动集群的分区leader选举
./kafka-preferred-replica-election.sh --zookeeper 10.48.114.12:2181 --path-to-json-file /tmp/partitions.json
```

### 修改分区副本

```bash
# help
# ./kafka-reassign-partitions.sh 
This command moves topic partitions between replicas.
Option                                 Description                           
------                                 -----------                           
--bootstrap-server <String: Server(s)  the server(s) to use for              
  to use for bootstrapping>              bootstrapping. REQUIRED if an       
                                         absolution path of the log directory
                                         is specified for any replica in the 
                                         reassignment json file              
--broker-list <String: brokerlist>     The list of brokers to which the      
                                         partitions need to be reassigned in 
                                         the form "0,1,2". This is required  
                                         if --topics-to-move-json-file is    
                                         used to generate reassignment       
                                         configuration                       
--disable-rack-aware                   Disable rack aware replica assignment 
--execute                              Kick off the reassignment as specified
                                         by the --reassignment-json-file     
                                         option.                             
--generate                             Generate a candidate partition        
                                         reassignment configuration. Note    
                                         that this only generates a candidate
                                         assignment, it does not execute it. 
--reassignment-json-file <String:      The JSON file with the partition      
  manual assignment json file path>      reassignment configurationThe format
                                         to use is -                         
                                       {"partitions":                        
                                        [{"topic": "foo",                    
                                          "partition": 1,                    
                                          "replicas": [1,2,3],               
                                          "log_dirs": ["dir1","dir2","dir3"] 
                                         }],                                 
                                       "version":1                           
                                       }                                     
                                       Note that "log_dirs" is optional. When
                                         it is specified, its length must    
                                         equal the length of the replicas    
                                         list. The value in this list can be 
                                         either "any" or the absolution path 
                                         of the log directory on the broker. 
                                         If absolute log directory path is   
                                         specified, it is currently required 
                                         that the replica has not already    
                                         been created on that broker. The    
                                         replica will then be created in the 
                                         specified log directory on the      
                                         broker later.                       
--throttle <Long: throttle>            The movement of partitions will be    
                                         throttled to this value (bytes/sec).
                                         Rerunning with this option, whilst a
                                         rebalance is in progress, will alter
                                         the throttle value. The throttle    
                                         rate should be at least 1 KB/s.     
                                         (default: -1)                       
--timeout <Long: timeout>              The maximum time in ms allowed to wait
                                         for partition reassignment execution
                                         to be successfully initiated        
                                         (default: 10000)                    
--topics-to-move-json-file <String:    Generate a reassignment configuration 
  topics to reassign json file path>     to move the partitions of the       
                                         specified topics to the list of     
                                         brokers specified by the --broker-  
                                         list option. The format to use is - 
                                       {"topics":                            
                                        [{"topic": "foo"},{"topic": "foo1"}],
                                       "version":1                           
                                       }                                     
--verify                               Verify if the reassignment completed  
                                         as specified by the --reassignment- 
                                         json-file option. If there is a     
                                         throttle engaged for the replicas   
                                         specified, and the rebalance has    
                                         completed, the throttle will be     
                                         removed                             
--zookeeper <String: urls>             REQUIRED: The connection string for   
                                         the zookeeper connection in the form
                                         host:port. Multiple URLS can be     
                                         given to allow fail-over.  
```

&emsp;在某些时候，可能需要修改分区副本。以下是一些需要修改分区副本的场景。
>* 主题分区在整个集群里不均衡分布造成了集群的负载不均衡。
>* broker离线造成分区不同步
>* 新加入的broker需要从集群里获得负载

&emsp;可以使用kafka-reassign-partitions.sh工具来修改分区。使用该工具需要经历如下两个步骤：

1. 根据broker列表和topic列表生成一组迁移步骤
2. 执行这些迁移步骤
3. (可选)可以使用生成的迁移步骤验证分区重分配的进度和完成情况

* 生成迁移步骤

>* topics.json

```json
{
    "version": 1,
    "topics": [
        {
            "topic": "sparkStreaming1"
        },
        {
            "topic": "sparkStreaming2"
        }
    ]
}
```

&emsp;kafka-reassign-partitions.sh会在标准控制台上输出两个json对象，分别描述了当前的分区分配情况以及建议的分区分配方案。可以把第一个json对象保存起来，以便在必要的时候进行回滚。第二个json对象应该被保存到另一个文件里，作为kafka-reassign-partitions.sh的输入来执行第二个步骤。

```bash
# 生成迁移步骤
./kafka-reassign-partitions.sh --zookeeper 10.48.114.12:2181 --generate --topics-to-move-json-file /tmp/topics.json --broker-list 0,1,2

Current partition replica assignment
{"version":1,"partitions":[{"topic":"sparkStreaming1","partition":1,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":0,"replicas":[1,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":10,"replicas":[2,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":11,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":4,"replicas":[2,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":8,"replicas":[0,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":2,"replicas":[0,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":2,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":1,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":9,"replicas":[1,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":0,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":5,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":6,"replicas":[1,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":7,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":3,"replicas":[1,0],"log_dirs":["any","any"]}]}

Proposed partition reassignment configuration
{"version":1,"partitions":[{"topic":"sparkStreaming1","partition":0,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":1,"replicas":[0,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":10,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":11,"replicas":[1,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":4,"replicas":[0,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":8,"replicas":[1,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":2,"replicas":[1,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":2,"replicas":[1,2],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":9,"replicas":[2,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":1,"replicas":[0,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming2","partition":0,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":6,"replicas":[2,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":5,"replicas":[1,0],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":3,"replicas":[2,1],"log_dirs":["any","any"]},{"topic":"sparkStreaming1","partition":7,"replicas":[0,1],"log_dirs":["any","any"]}]}
```

* 执行分区迁移

>* reassign-partions.json

```json
{
    "version": 1,
    "partitions": [
        {
            "topic": "sparkStreaming2",
            "partition": 2,
            "replicas": [
                1,
                2
            ],
            "log_dirs": [
                "any",
                "any"
            ]
        },
        {
            "topic": "sparkStreaming2",
            "partition": 1,
            "replicas": [
                0,
                1
            ],
            "log_dirs": [
                "any",
                "any"
            ]
        },
        {
            "topic": "sparkStreaming2",
            "partition": 0,
            "replicas": [
                2,
                0
            ],
            "log_dirs": [
                "any",
                "any"
            ]
        }
    ]
}
```

```bash
# 列出集群里指定主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming2
#执行分区迁移
./kafka-reassign-partitions.sh --zookeeper 10.48.114.12:2181 --execute --reassignment-json-file /tmp/reassign-partions.json
# 列出集群里指定主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming2
```

* 为重新分配副本进行网络优化

&emsp;如果要从单个broker上移除多个分区，比如将broker移出集群，那么在重新分配副本之前最好先关闭或者重启broker。这样，这个broker就不再是任何一个分区的leader，它的分区就可以分配给集群里的其它broker（只要没有启用自动leader选举）。这可以显著提升重分配的性能，并减少对集群的影响，因为复制流将会被分发给多个broker。

* 验证分区重分配的进度和完成情况

```bash
# 验证分区重分配的进度和完成情况
./kafka-reassign-partitions.sh --zookeeper 10.48.114.12:2181 --verify --reassignment-json-file /tmp/reassign-partions.json
```

* 分批重分配

&emsp;分区重分配对集群的性能有很大影响，因为它会引起内存缓存页缓存发送变化，并占用而外的网络和磁盘资源。将重分配过程拆分成多个小步骤可以将这种影响降到最低。

### 修改分区复制份数

&emsp;在reassign-partions.json修改replicas参数，然后执行修改分区的操作，就可以增加或者减少分区的复制份数。下面的例子将sparkStreaming2的分区份数由2份改为3份。

>* reassign-partions.json

```json
{
    "version": 1,
    "partitions": [
        {
            "topic": "sparkStreaming2",
            "partition": 2,
            "replicas": [
                0,
                1,
                2
            ],
            "log_dirs": [
                "any",
                "any",
                "any"
            ]
        },
        {
            "topic": "sparkStreaming2",
            "partition": 1,
            "replicas": [
                0,
                1,
                2
            ],
            "log_dirs": [
                "any",
                "any",
                "any"
            ]
        },
        {
            "topic": "sparkStreaming2",
            "partition": 0,
            "replicas": [
                0,
                1,
                2
            ],
            "log_dirs": [
                "any",
                "any",
                "any"
            ]
        }
    ]
}
```

```bash
# 列出集群里指定主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming2
#执行分区迁移,修改份数
./kafka-reassign-partitions.sh --zookeeper 10.48.114.12:2181 --execute --reassignment-json-file /tmp/reassign-partions.json
# 列出集群里指定主题详细信息
./kafka-topics.sh --zookeeper 10.48.114.12:2181 --describe --topic sparkStreaming2
```