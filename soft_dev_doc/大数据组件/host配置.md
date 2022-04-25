## host配置
* 主机名-IP的映射
```shell
vi /etc/hosts
#add ip hostname
192.168.xxx.xxx ds1
192.168.xxx.xxx ds2
192.168.xxx.xxx ds3
192.168.xxx.xxx ds4
```

### 同时对多台服务器配置
```shell
for ip in ds2 ds3;     #请将此处ds2 ds3替换为自己要部署的机器的hostname
do
    sudo scp -r /etc/hosts  $ip:/etc/          #在运行中需要输入root密码
done
# sshpass -p xxx sudo scp -r /etc/hosts $ip:/etc/ 省去密码替代命令
```
```
centos下sshpass的安装：
先安装epel

yum install -y epel-release

yum repolist

安装完成epel之后，就可以按照sshpass了

yum install -y sshpass
```
