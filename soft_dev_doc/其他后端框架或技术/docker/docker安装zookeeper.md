### 安装zookeeper

* 测试环境, 懒得自己去写镜像, 直接用现成的即可

#### 拉取
```shell
$ docker pull zookeeper
```

#### 启动
```shell
docker run -d -e TZ="Asia/Shanghai" -p 2181:2181 -v /home/pi/zookeeper/data:/data --name zookeeper --restart always zookeeper
```

```txt
-e TZ=“Asia/Shanghai” # 指定上海时区
-d # 表示在一直在后台运行容器
-p 2181:2181 # 对端口进行映射，将本地2181端口映射到容器内部的2181端口
–name # 设置创建的容器名称
-v # 将本地目录(文件)挂载到容器指定目录；
–restart always #始终重新启动zookeeper
```