# 4b安装centos
## 前言
平时工作时已经习惯了使用 centos 为了不增加学习成本，最终选择了安装centos。 
## 镜像地址
因为树莓派是 arm 架构。所以系统镜像得去下 arm 类型的镜像(CentOS-AltArch)。
[华为镜像官网](https://mirrors.huaweicloud.com/)
本来想踩 8 的坑，但是没有树莓派版本的 centos 8，那没办法了只能上最新的7了。
[下载链接](https://mirrors.huaweicloud.com/centos-altarch/7.8.2003/isos/armhfp/CentOS-Userland-7-armv7hl-RaspberryPI-Minimal-4-2003-sda.raw.xz)

## 补充
armv7指的是 32位的操作系统，目前很多东西对32位操作系统支持不友好，放弃他。
armv8是64位操作系统

## 64位操作系统
找了好久都没找到centos 64位操作系统，只能踩坑 debian 了
64位debian 地址：
1. https://github.com/openfans-community-offical/Debian-Pi-Aarch64
2. https://gitee.com/openfans-community/Debian-Pi-Aarch64/blob/master/README_zh.md

这个版本还是很全的，有各种桌面或者web监控界面的版本做选择。
我这里选择带docker容器的版本。

## 版本安装
最终选择了2.0 版本的 debian 无桌面增强版本。

* 账户及密码
系统默认账户：pi ，默认密码：raspberry

默认账户pi账户支持ssh登录，root账户密码请登陆后使用命令 “sudo passwd root” 执行设置，

或使用命令 “sudo -i” 来切换到root用户。

* Web登录接口说明
    1. Web可视化管理界面
    登录地址 https://你树莓派的IP地址:9090
    说明：请使用系统默认账户pi登录

    2. WEB SSH 客户端 入口界面
    登录地址 https://你树莓派的IP地址:4200
    说明：使用具有控制台登录权限的帐户登录，例如：pi

    3. CecOS CaaS 容器云管理平台 登录界面
    登录地址 https://你树莓派的IP地址:8443
    说明：默认管理账户 admin , 默认密码：password 。请登录后立即修改默认密码！！

* 定制服务命令： 
    * Docker容器
    ```
    手动开启：systemctl start docker.service
    手动停止：systemctl stop docker.service
    启用开机启动：systemctl enable docker.service
    禁用开机启动：systemctl disable docker.service
    ```
    * CecOS CaaS容器云（服务端口：8443）
    ```
    手动开启：systemctl start cecos-caas.service
    手动停止：systemctl stop cecos-caas.service
    启用开机启动：systemctl enable cecos-caas.service
    禁用开机启动：systemctl disable cecos-caas.service
    ```
    * WebGUI管理平台（服务端口：9090）
    ```
    手动开启：systemctl start cockpit.socket
    手动停止：systemctl stop cockpit.socket
    启用开机启动：systemctl enable cockpit.socket
    禁用开机启动：systemctl disable cockpit.socket
    ```
    * Web SSH客户端（服务端口：4200）
    ```
    手动开启：systemctl start shellinabox.service
    手动停止：systemctl stop shellinabox.service
    启用开机启动：systemctl enable shellinabox.service
    禁用开机启动：systemctl disable shellinabox.service
    ```
    
### Chromium浏览器界面显示错误
* 问题根源：
> 您可能会在 版本20200615 U3 上看到此问题，因为我们更改了mesalib，应该使用另一种方式设置Chromium。

* 解决办法：
运行命令：
```
sudo sed -i  \
's/"hardware_acceleration_mode":{"enabled":true},/"hardware_acceleration_mode":{"enabled":false},/' \
/home/pi/.config/chromium/Local\ State
```
然后重新打开您的Chromium浏览器。

## 格式化工具
SD卡专业的格式化工具
[SDFformatter](https://www.sdcard.org/downloads/formatter/eula_windows/)

## 系统烧录工具
SD卡镜像烧录工具,安装完后将镜像写入
[win32diskimager](https://sourceforge.net/projects/win32diskimager/files/latest/download)