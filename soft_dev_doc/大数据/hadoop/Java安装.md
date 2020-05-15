# JDK 安装
## 安装JDK 1.8
> 官网下载 Linux 的对应安装包，[点击跳转](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
## 上传/解压
```shell
tar xf jdk-8u172-linux-x64.tar.gz -C /opt/hadoop/
```

## 设置JAVA_HOME
```shell
# $ vi .bashrc

export JAVA_HOME=/opt/hadoop/jdk1.8.0_172

export PATH=$JAVA_HOME/bin:$PATH
```
## 验证
```
[hadoop@MASTER ~]$ java -version
java version "1.8.0_172"
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)
```
