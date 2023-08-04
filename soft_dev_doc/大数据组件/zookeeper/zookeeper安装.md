## zookeeper 安装
### 下载安装包
```shell
# 直接下载
wget http://mirror.bit.edu.cn/apache/zookeeper/stable/zookeeper-3.4.13.tar.gz

# 或者本地下载上传到服务器上
```

### 准备JAVA环境
[java安装](../hadoop/Java%E5%AE%89%E8%A3%85.md)

### 安装
1. 创建并解压，例如：
```shell
mkdir ~/zookeeper
cd zookeeper/
tar -xf zookeeper-3.4.13.tar.gz
```

2. 配置myid
  每台zookeeper服务器的编号，不能重复，且必须跟zookeeper-3.4.13/conf/zoo.cfg中的server.1=machine1:2888:3888中ip和server后id对应上。

3. 配置zoo.cfg
  ```shell
  $ su - hadoop
  $ vi zookeeper-3.4.13/conf/zoo.cfg
  tickTime=5000
  minSessionTimeout=10000
  maxSessionTimeout=60000
  dataDir=./../data
  #for security considerations, only listen to the local host intranet IP address
  #clientPortAddress=0.0.0.0
  clientPort=2181
  initLimit=10
  syncLimit=5
  autopurge.purgeInterval=3
  autopurge.snapRetainCount=3
  server.1=machine1:2888:3888
  server.2=machine2:2888:3888
  server.3=machine3:2888:3888
  ```

4. 启动
  ```shell
  $ cd zookeeper-3.4.13/bin/
  $ ./zkServer.sh start

  ##查看状态
  $ ./zkServer.sh status

  #停止
  $ ./zkServer.sh stop
  ```

### 其它常用命令
```shell
# bin 目录下
$ ./zkCli.sh -h
# 进入控制台
# 可以看到有哪些节点分布，以及具体信息
$ ls，get
```
