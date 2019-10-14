## VPS搭建

自用机房 [justhost](http://justhost.ru) 购买服务器使用 Debian 9 系统

使用 root 用户输入下面命令安装或卸载

    bash <(curl -s -L https://git.io/v2ray.sh)
如果提示 ```curl: command not found``` ，那是因为你的 VPS 没装 Curl
ubuntu/debian 系统安装 Curl 方法: ```apt-get update -y && apt-get install curl -y```
centos 系统安装 Curl 方法: ```yum update -y && yum install curl -y```

按照步骤安装即可基本都选默认
安装好 curl 之后就能安装脚本了

备注：安装完成后，输入 v2ray 即可管理 V2Ray
如果提示你的系统不支持此脚本，那么请尝试更换系统



#### Debian9 快速开启 TCP BBR 实现高效单边加速
**修改系统变量：**
```shellpower
echo "net.core.default_qdisc=fq" >> /etc/sysctl.conf
echo "net.ipv4.tcp_congestion_control=bbr" >> /etc/sysctl.conf
```
保存生效

    sysctl -p
执行

    sysctl net.ipv4.tcp_available_congestion_control
如果结果是这样

    "rootMF8-BIZ sysctl net.ipv4.tcp_available_congestion_controlnet.ipv4.tcp_available_congestion_control = bbr cubic reno
就开启了。

执行  ```lsmod | grep bbr``` ，以检测 BBR 是否开启。
