### 安装docker
* [官方文档](https://docs.docker.com/engine/install/centos/)

#### 安装步骤
##### 设置存储库
```sh
# 安装 yum-utils 工具包
$ sudo yum install -y yum-utils

# 设置docker存储库
$ sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

##### 安装Docker引擎
```sh
# 安装最新版本的 Docker Engine、containerd 和 Docker Compose 或转到下一步安装特定版本：
$ sudo yum install docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 安装特定版本
# 列出版本号
$ yum list docker-ce --showduplicates | sort -r

# 替换安装即可
$ sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io docker-compose-plugin

# 启动
$ sudo systemctl start docker

# 验证Docker是否正常运行
sudo docker run hello-world

# 设置cgroupdriver 为 systemd
[root@VM-0-8-centos ~]# cat > /etc/docker/daemon.json <<EOF
> {
>     "exec-opts": ["native.cgroupdriver=systemd"]
> }
> EOF
[root@VM-0-8-centos ~]# systemctl restart docker

```

##### 启动停止
```sh
# 启动
$ sudo systemctl start docker

# 查看状态
$ sudo systemctl status docker

# 开机自启
$ systemctl enable docker

# 停止
$ sudo systemctl stop docker
```

#### 可能问题
* 语言环境不对
```sh
# 报错如下
Failed to set locale, defaulting to C.UTF-8
Last metadata expiration check: 2:42:25 ago on Tue Jan 10 13:25:01 2023.
No match for argument: docker-ce
No match for argument: docker-ce-cli
No match for argument: containerd.io
No match for argument: docker-compose-plugin
Error: Unable to find a match: docker-ce docker-ce-cli containerd.io docker-compose-plugin
```
* 这里会有两种原因
  1. 系统没有安装对应的语言包
  2. 没有配置正确的语言环境

* 一般情况下都是第二种情况居多
```sh
# 查看已安装的语言包
[root@VM-0-16-centos ~]# locale -a
locale: Cannot set LC_CTYPE to default locale: No such file or directory
locale: Cannot set LC_MESSAGES to default locale: No such file or directory
locale: Cannot set LC_COLLATE to default locale: No such file or directory
C
C.utf8
POSIX
en_AG
en_AU
en_AU.utf8
en_BW
en_BW.utf8
en_CA
en_CA.utf8
en_DK
en_DK.utf8
en_GB
en_GB.iso885915
en_GB.utf8
en_HK
en_HK.utf8
en_IE
en_IE.utf8
en_IE@euro
en_IL
en_IN
en_NG
en_NZ
en_NZ.utf8
en_PH
en_PH.utf8
en_SC.utf8
en_SG
en_SG.utf8
en_US
en_US.iso885915
en_US.utf8
en_ZA
en_ZA.utf8
en_ZM
en_ZW
en_ZW.utf8
# 这里有en_US.utf8，所以是第二种情况
# 指定语言环境
echo "export LC_ALL=en_US.UTF-8"  >>  /etc/profile
echo "export LC_CTYPE=en_US.UTF-8"  >>  /etc/profile
source /etc/profile
# 再次执行命令
[root@hecs-131104 data]# yum install -y yum-utils
Last metadata expiration check: 0:18:53 ago on Sat 02 Oct 2021 08:52:23 PM CST.
Package yum-utils-4.0.18-4.el8.noarch is already installed.
Dependencies resolved.
Nothing to do.
Complete!

```

### pull代理设置
* 主要是内网集群需要代理才能连接外网的服务器, 如果都能连接, 无视

```sh
$ mkdir /etc/systemd/system/docker.service.d
$ vim /etc/systemd/system/docker.service.d/http-proxy.conf

[Service]
Environment="HTTP_PROXY=http://172.17.0.8:3128"
Environment="HTTPS_PROXY=http://172.17.0.8:3128"

# 重启docker
$ sudo systemctl daemon-reload
$ sudo systemctl restart docker

# 检查代理配置
$ docker info | grep Proxy
HTTP Proxy: http://172.17.0.8:3128
HTTPS Proxy: http://172.17.0.8:3128

```
