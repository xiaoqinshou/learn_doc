# 编译安装Redis(涵盖树莓派centos)

## 准备工作
### 编译
> 众所周知 Redis 是 C 写的，所以编译 Redis 需要 C 的编译器，没毛病。

### 安装GCC环境
```shell
# 安装 gcc 环境
$ yum install gcc-c++
# 等漫长的安装过程完成就行了
```

### 安装make命令
* 可能一些 centos 系统不带 make 命令，装它。
```shell
# 安装 make 命令
$ yum -y install gcc automake autoconf libtool make
# 等漫长的安装过程完成就行了
```

### <span id="jump">注意</span>
* centos 7.6 以下貌似安装 gcc 环境默认是4.8.5
* 本人树莓派，默认安装的是 gcc 默认安装 4.8.5，redis版本太新编译失败。
```shell
[fons@localhost redis-6.0.9]$ cat /proc/version
Linux version 5.4.28-v7l.1.el7 (mockbuild@armhfp-06.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-39) (GCC)) #1 SMP Mon Mar 30 21:19:23 UTC 2020
```

#### <span id="newGcc">升级 gcc 环境</span>
```shell
# 升级到gcc 9.3：
yum -y install centos-release-scl
yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils
scl enable devtoolset-9 bash
# 需要注意的是scl命令启用只是临时的，退出shell或重启就会恢复原系统gcc版本。
# 如果要长期使用gcc 9.3的话：
echo "source /opt/rh/devtoolset-9/enable" >>/etc/profile
# 这样退出shell重新打开就是新版的gcc了
# 以下其他版本同理，修改devtoolset版本号即可。
```
* 顺带提一下：scl软件集(Software Collections),是为了给 RHEL/CentOS 用户提供一种以方便、安全地安装和使用应用程序和运行时环境的多个（而且可能是更新的）版本的方式，同时避免把系统搞乱。
* centos 一般都是 4.8.5 兼容性比较好，这里我们暂时拿来编译一下就完事了，主要使用还是使用稳定版。
---
* 提一下特殊情况安装时遇到 no package 的情况，就跳转到[再次注意](#agin)
```shell
$ Loading mirror speeds from cached hostfile
 * base: ftp.yz.yamagata-u.ac.jp
 * centos-kernel: ftp.yz.yamagata-u.ac.jp
 * extras: ftp.yz.yamagata-u.ac.jp
 * updates: ftp.yz.yamagata-u.ac.jp
No package devtoolset-9-gcc available.
No package devtoolset-9-gcc-c++ available.
No package devtoolset-9-binutils available.
Error: Nothing to do
```

#### <span id="agin">再次注意</span>
* 没少的跳过
* 本人服务器是 树莓派,很多东西都没有需要自己安装
* 具体少什么，自行查找
```shell
# 很多 epel 库没有,装!
# 先装命令
$ yum install wget

# 不知道的可以查一下系统版本
[root@localhost ~]# cat /etc/redhat-release
CentOS Linux release 7.8.2003 (AltArch)

# 以下是 正常 centos 系统的操作方式
# 其次安装企业包 centos几 就安装epel几 这里我是7 所以安装7
$ wget http://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm

# 加载一下
$ rpm -ivh epel-release-latest-7.noarch.rpm

# 还找不到的话 就更新一下
$ yum update
```

* 树莓派的如果按照上面会报这个错 不存在
```shell
 One of the configured repositories failed (Unknown),
 and yum doesn't have enough cached data to continue. At this point the only
 safe thing yum can do is fail. There are a few ways to work "fix" this:

     1. Contact the upstream for the repository and get them to fix the problem.

     2. Reconfigure the baseurl/etc. for the repository, to point to a working
        upstream. This is most often useful if you are using a newer
        distribution release than is supported by the repository (and the
        packages for the previous distribution release still work).

     3. Run the command with the repository temporarily disabled
            yum --disablerepo=<repoid> ...

     4. Disable the repository permanently, so yum won't use it by default. Yum
        will then just ignore the repository until you permanently enable it
        again or use --enablerepo for temporary usage:

            yum-config-manager --disable <repoid>
        or
            subscription-manager repos --disable=<repoid>

     5. Configure the failing repository to be skipped, if it is unavailable.
        Note that yum will try to contact the repo. when it runs most commands,
        so will have to try and fail each time (and thus. yum will be be much
        slower). If it is a very temporary problem though, this is often a nice
        compromise:

            yum-config-manager --save --setopt=<repoid>.skip_if_unavailable=true
Cannot retrieve metalink for repository: epel/armhfp. Please verify its path and try again
```

* 树莓派安装EPEL的正确姿势
```shell
$ cat > /etc/yum.repos.d/epel.repo << EOF
[epel]
name=Epel rebuild for armhfp
baseurl=https://armv7.dev.centos.org/repodir/epel-pass-1/
enabled=1
gpgcheck=0

EOF

# 在更新一下 yum
$ yum update
```

* 如果是树莓派，且是centos 系统放弃吧  没有4.8.5 以上版本的gcc g++ 可以下载，整了一晚上，找了国外、阿里、华为的epel源，都只有4.8.5的GCC环境，放弃了。而且经过测试还是国外官方的 epel 好用啊，直接下面保平安
```shell
# 直接下载保平安吧
$ yum install redis
```
* 如果不是树莓派，搞完了再回到[上一节](#newGcc)

## 安装
### 编译
```shell
# 首先进入源码根目录下
$ cd /opt/fons/soft/redis-6.0.9
$ make
$ ...
$ ...
$ ...
server.c: In function ‘allPersistenceDisabled’:
server.c:1484:1: warning: control reaches end of non-void function [-Wreturn-type]
 }
 ^
server.c: In function ‘writeCommandsDeniedByDiskError’:
server.c:3934:1: warning: control reaches end of non-void function [-Wreturn-type]
 }
 ^
server.c: In function ‘iAmMaster’:
server.c:5134:1: warning: control reaches end of non-void function [-Wreturn-type]
 }
```
* 如果遇到这种一大堆报错，说明 GCC 环境太老，需要用更新一点的环境回到[注意](#jump)
* 环境都搞完了
* 正常编译结果
```shell
Hint: It' S a good idea to run‘ make test’ ; )
make[1]: Laving directory + /usr/1ocal/redis/redis-6.0.9/src'
[root@localhost ~]#
```

### 安装
```shell
# 指定 /usr/local/redis 为安装目录
make PREFIX=/usr/local/redis install
# 安装完后会在/usr/local/redis下出现一个bin目录
# bin目录中就是我们要使用的内容
# 里面是我们需要用到的脚本
```

### 常用操作命令
```shell
# 前端模式启动服务端：
$ ./redis-server
# 客户端命令行连接工具
$ ./redis-cli
# 配置redis.conf 文件，并放到bin目录下
# 且将 daemonize 改为 yes 后端启动
# 执行后端启动
$ ./redis-server redis.conf
# 关闭服务
# 方法一 PS找到进程(不推荐，会丢失数据)
$ kill -9 进程号
# 方法二 使用客户端工具关闭
$ ./redis-cli shutdown

```

## 参考
[小q轩](https://blog.csdn.net/weidu01/article/details/105946606)
[蓝天飞翔的白云](https://www.cnblogs.com/dj0325/p/8481092.html)
