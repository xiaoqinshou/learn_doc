## linux SSH免密登陆
&emsp;各种分布式集群系统中常需要用到的。

* 给予用户 ssh 权限输入 `ssh-keygen -t rsa` 然后一路回车即可
```shell
#查看生成的证书情况
[hadoop@BY-DSI ~]$ ls -l /opt/hadoop/.ssh/id_rsa*
-rw------- 1 root root 1675 Jun 14 15:52 /root/.ssh/id_rsa
-rw-r--r-- 1 root root  393 Jun 14 15:52 /root/.ssh/id_rsa.pub
```
* 修改 hosts 文件将需要配置的集群服务器所有的 ip 地址和主机名写入其中。
```shell
[root@root ~]# vi /etc/hosts
0.0.0.0 hostname
```
* 将公钥发送给 已配置好的 hosts 集群(Master)名下。
```shell
# 远程发送配置
ssh-copy-id -i ~/.ssh/id_rsa.pub hostname
# 远程复制
scp /opt/hadoop/.ssh/id_rsa.pub 0.0.0.0:/opt/hadoop/.ssh/authorized_keys
```

### 常见问题一
&emsp;因为公司有台测试机集群，用于ssh开放的用户，对应下文件夹权限乱糟糟的，整个一公交车，任意用户拥有任意权限，连用户最顶层文件都是 777。导致免密无法成功配置。找了很久终于找到根源所在了。
* 所开放的 ssh 免密登陆的非root用户 权限必须是 700 不能是 777。只能是公交车，不能是私家车。
* 当开放 ssh 用户的文件夹目录下，乱作一团时，ssh 会因为权限争执问题，无法正常免密访问。
