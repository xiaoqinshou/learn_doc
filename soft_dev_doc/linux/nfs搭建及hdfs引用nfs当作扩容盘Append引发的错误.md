# 搭建NFS
## 检查是否安装相关文件
### 检查NFS
```shell
sudo rpm -qa | grep nfs-utils
sudo rpm -qa | grep rpcbind
```
### 安装NFS
```SHELL
sudo yum -y install nfs-utils
sudo yum -y install rpcbind
```

## 修改NFS配置
* 由服务端往客户端映射即可
* 修改NFS配置文件，/etc/exports （默认是空文件）
```shell
sudo vi /etc/exports
/opt/exd 10.48.113.11(rw,sync)
```
* 其它配置属性参考：
> NFS主要有3类选项：
访问权限选项
    设置输出目录只读：ro
    设置输出目录读写：rw

> 用户映射选项
    all_squash：将远程访问的所有普通用户及所属组都映射为匿名用户或用户组（nfsnobody）；
    no_all_squash：与all_squash取反（默认设置）；
    root_squash：将root用户及所属组都映射为匿名用户或用户组（默认设置）；
    no_root_squash：与rootsquash取反；
    anonuid=xxx：将远程访问的所有用户都映射为匿名用户，并指定该用户为本地用户（UID=xxx）；
    anongid=xxx：将远程访问的所有用户组都映射为匿名用户组账户，并指定该匿名用户组账户为本地用户组账户（GID=xxx）；

> 其它选项
    secure：限制客户端只能从小于1024的tcp/ip端口连接nfs服务器（默认设置）；
    insecure：允许客户端从大于1024的tcp/ip端口连接服务器；
    sync：将数据同步写入内存缓冲区与磁盘中，效率低，但可以保证数据的一致性；
    async：将数据先保存在内存缓冲区中，必要时才写入磁盘；
    wdelay：检查是否有相关的写操作，如果有则将这些写操作一起执行，这样可以提高效率（默认设置）；
    no_wdelay：若有写操作则立即执行，应与sync配合使用；
    subtree：若输出目录是一个子目录，则nfs服务器将检查其父目录的权限(默认设置)；
    no_subtree：即使输出目录是一个子目录，nfs服务器也不检查其父目录的权限，这样可以提高效率；

## NFS挂载
* 手动挂载
```shell
# 创建文件夹
mkdir /opt/hadoop/exd
# 挂载NFS，默认情况下只有 root 可以去挂载
mount -t nfs 172.19.64.99:/opt/exd /opt/hadoop/exd
# 查看挂载情况
df -h
# 卸载
umount /opt/hadoop/exd
# 卸载
umount 172.19.64.99:/opt/exd
```
* 自动挂载
```shell
sudo vi /etc/fstab
172.19.64.99:/opt/exd /opt/hadoop/exd               nfs  defaults  0    0
```

## 其它常用命令
```shell
# 新增节点以后可以直接同步下发不用重启NFS服务
exportfs -arv
#卸载所有共享目录
exportfs -au
#重新共享所有目录并输出详细信息
exportfs -rv

Usage: showmount [-adehv]
        [--all] [--directories] [--exports]
        [--no-headers] [--help] [--version] [host]
-a或--all
    以 host:dir 这样的格式来显示客户主机名和挂载点目录。
 -d或--directories
    仅显示被客户挂载的目录名。
 -e或--exports
    显示NFS服务器的输出清单。
 -h或--help
    显示帮助信息。
 -v或--version
    显示版本信。
 --no-headers
    禁止输出描述头部信息。

# 显示NFS客户端信息
showmount

# 显示指定NFS服务器连接NFS客户端的信息
showmount 192.168.1.1  #此ip为nfs服务器的

# 显示输出目录列表
showmount -e

# 显示指定NFS服务器输出目录列表（也称为共享目录列表）
showmount -e 172.19.64.99

# 显示被挂载的共享目录
showmount -d

# 显示客户端信息和共享目录
showmount -a

# 显示指定NFS服务器的客户端信息和共享目录
showmount -a 172.19.64.99
```

# hadoop 引入挂载盘
* hdfs dfs.datanode.data.dir 加一个节点就行
```
<property>
        <name>dfs.datanode.data.dir</name>
        <value>/opt/hadoop/exd</value>
</property>
```

* hdfs能正常初始化，能写入文件，但是append文件的时候会出错

## hdfs增加nfs配置
* hdfs.xml 增加nfs配置
```shell
<property>
    <name>nfs.exports.allowed.hosts</name>
    <value>172.19.64.99 rw</value>
</property>
<property>
    <name>nfs.dump.dir</name>
    <value>/opt/hadoop/hadoop-2.7.2/nfstmp</value>
</property>
```

* core.xml增加 nfs 协议的适配
```shell
<property>
        <name>hadoop.proxyuser.nfs.groups</name>
        <value>hadoopgroup</value>
    </property>
    <property>
        <name>hadoop.proxyuser.nfs.hosts</name>
        <value>172.19.64.99</value>
    </property>
```

* log4j.properties
```shell
log4j.logger.org.apache.hadoop.hdfs.nfs=DEBUG
log4j.logger.org.apache.hadoop.oncrpc=DEBUG
```

* hdfs一样能正常初始化，能写入文件，但是append文件的时候会出错
