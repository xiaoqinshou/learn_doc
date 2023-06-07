### 搭建 GPT
> 首先感谢各位孜孜不倦奉献的各位大佬, 各种开源我来捡漏

#### 白嫖服务器
* 注册亚马逊账号即可白嫖一年 1H1G30G 的美国VPS, 同时还解决了翻墙的问题
* [白嫖链接](https://aws.amazon.com/cn/free)


#### 具体步骤
```sh
# 更新源
$ sudo apt update
# 安装 git
$ sudo apt-get install git

# 下载源项目 869413421/chatgpt-web
$ git clone https://github.com/869413421/chatgpt-web.git

# 填写配置
$ cp config.dev.json  config.json

# 下载 go
$ wget https://go.dev/dl/go1.20.3.linux-amd64.tar.gz

# 解压
$ sudo tar -zxvf go1.20.3.linux-amd64.tar.gz

# 修改 增加环境变量
$ sudo vi /etc/profile
export PATH=$PATH:/usr/local/go/bin

# 验证
$ go version
go version go1.20.3 linux/amd64

# 跑起来, 跑不起来
$ go run main.go

# 发布版
$ wget https://github.com/869413421/chatgpt-web/releases/download/v0.2.4/chatgpt-web-v0.2.4-linux-amd64.tar.gz

# 跑起来
$ sudo nohup ./chatgpt-web &> run.log &
```