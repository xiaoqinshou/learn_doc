# Trojan 科学上网搭建
* 用了一个月了，挺稳定的。用来偶尔叉查资料还是很不错的。
* 现在把搭建步骤重新记录一下。

## Trojan工作原理浅析
> Trojan是一个比较新的翻墙软件，在设计时采用了更适应国情的思路。在穿透GFW时，人们认为强加密和随机混淆可能会欺骗GFW的过滤机制。然而，Trojan实现了这个思路的反面：它模仿了互联网上最常见的HTTPS协议，以诱骗GFW认为它就是HTTPS，从而不被识别。

![](images/2021-06-24-15-26-05.png)

## Trojan工作原理
如图所示，Trojan工作在443端口，并且处理来自外界的HTTPS请求，如果是合法的Trojan请求，那么为该请求提供服务，否则将该流量转交给web服务器Nginx，由Nginx为其提供服务。基于这个工作过程可以知道，Trojan的一切表现均与Nginx一致，不会引入额外特征，从而达到无法识别的效果。当然，为了防止恶意探测，我们需要将80端口的流量全部重定向到443端口，并且服务器只暴露80和443端口，这样可以使得服务器与常见的Web服务器表现一致。

**系统要求**
* Ubuntu >= 16.04；
* Debian >= 9；
* CentOS >= 7；

## VPS服务器购买
* 国内的太贵了，记录几个国外的，按需所取
* https://www.vultr.com/
* https://justhost.ru/

## 域名申请与解析
* 购买: https://www.namecheap.com/
* 解析: https://cloudflare.com
* trojan需要一个域名用来做伪装，所以需要先申请一个域名。如果你只是用来翻墙没有其他作用，那么建议注册一个免费域名即可。本教程使用域名和cloudflareDNS为例。

1. 将域名转移到cloudflare解析，这个对于已经有域名的来说应该不难。按照域名配置就行


## VPS服务器部署
> 跳过了上面系统选择与购买部分的要注意啦，本教程目前测试通过操作系统版本是Ubuntu 16.04 or Debian 9 or CentOS 7及以上，更低版本系统无法成功搭建，其他系统尚未测试！

### 安装依赖
> 由于Debian系列系统和CentOS系列系统使用不同的包管理软件，所以安装软件的命令不一样，下面两个小节自己对照自己系统选择命令。

#### Ubuntu or Debian
1) 更新源
```shell
sudo apt update
```
```shell
sudo apt upgrade -y
```

2) 安装acme.sh需要的依赖。
```shell
sudo apt install -y socat cron curl
```

3) 启动crontab
```shell
sudo systemctl start cron
```
```shell
sudo systemctl enable cron
```
4) 安装Trojan需要的依赖。
```shell
sudo apt install -y libcap2-bin xz-utils nano
```

5) 安装Nginx。
```shell
sudo apt install -y nginx
```

#### CentOS
1) 安装acme.sh需要的依赖。
```shell
sudo yum install -y socat cronie curl
```

2) 启动crontab
```shell
sudo systemctl start crond
```
```shell
sudo systemctl enable crond
```
3) 安装Trojan需要的依赖。
```shell
sudo yum install -y xz nano
```

4) 安装Nginx。
```shell
sudo yum install -y nginx
```

### 创建证书文件夹
新建一个文件夹/usr/local/etc/certfiles用于存放证书。
```shell
sudo mkdir /usr/local/etc/certfiles
```
将证书文件夹所有者改为acme，使得用户acme有权限写入证书。
```shell
sudo chown -R acme:acme /usr/local/etc/certfiles
```

### 配置证书
1) 安装acme.sh
```shell
curl  https://get.acme.sh | sh
```

2) 添加cloudflare Global CA Key<br/>
  需要让acme.sh自动管理你的证书，所以需要添加cloudflare的API。登录cloudflare之后定位到：头像>>My Profile>>API Tokens。可以看到这里有两个API Keys。我们需要的是Global API Key。Origin CA Key是不可以使用的。点击View即可查看，注意查看之后自己保存下来，每天可查看次数是有限制的。<br/>
  然后配置环境变量，注意：本文中代码需要手动修改的地方都使用<>包裹。
```shell
export CF_Key="<Your Global API Key>"
export CF_Email="<Your cloudflare account Email>"
```

3)  申请证书
执行如下命令（注意域名<tdom.ml>改为你自己的域名），等待一会儿。
```shell
# 默认机构
acme.sh --set-default-ca --server letsencrypt
# 切换机构
acme.sh --set-default-ca --server zerossl

acme.sh --issue --dns dns_cf -d <tdom.ml>
```
看到下图的提示表示证书申请成功。
![](images/2021-06-24-15-52-18.png)

4) 申请失败怎么办？证书申请失败的可能性一般有：
  1. CF_Key或CF_Email填写错误；
  2. 证书申请次数超限等。此时切忌反复尝试，原因是证书每一个周申请次数是有限制的（20次），如果超限了就需要等一个周或者更换域名了（这个限制是争对每一个子域单独做的限制，所以万一超限了还可以用子域名继续部署）。解决方案是：在上述命令后加–staging参数继续测试。测试通过之后，删除上图所示四个证书文件并取消–staging参数再执行一次。–staging参数申请的证书只作为测试用，客户端是无法认证通过的（提示SSL handshake failed: tlsv1 alert unknown ca），所以使用–staging参数申请到了证书之后要去掉–staging参数重新申请一次。

### 安装证书
1) 使用acme.sh将证书安装到certfiles目录，这样acme.sh更新证书的时候会自动将新的证书安装到这里。
```shell
acme.sh --install-cert -d <tdom.ml> --key-file /usr/local/etc/certfiles/private.key --fullchain-file /usr/local/etc/certfiles/certificate.crt
```

2) 配置acme.sh自动更新和自动更新证书，这样配置完Trojan之后一般不用管服务器。
```shell
acme.sh  --upgrade  --auto-upgrade
# 手动更新证书
acme.sh --cron -f
```

3)  修改权限
```shell
chmod -R 750 /usr/local/etc/certfiles
# trojan-go 要755
```

## 配置Trojan
### 安装Trojan
1) 安装Trojan，安装完成一般会提示版本号注意看是否是最新版本。
```shell
# trojan
sudo bash -c "$(curl -fsSL https://raw.githubusercontent.com/trojan-gfw/trojan-quickstart/master/trojan-quickstart.sh)"

# trojan-go
sudo bash -c "$(curl -fsSL https://raw.githubusercontent.com/DongfeiSay/trojan-go-quickstart/master/trojan-go-quickstart.sh)"
```
2) 备份Trojan配置文件，以防万一。
```shell
sudo cp /usr/local/etc/trojan/config.json /usr/local/etc/trojan/config.json.bak
```

3) nano修改配置文件
```shell
# 修改 trojan 配置
sudo nano /usr/local/etc/trojan/config.json
# 修改trojan-go配置，一摸一样
sudo nano /etc/trojan-go/config.json
# trojan-go
{
    "run_type": "server",
    "local_addr": "0.0.0.0",
    "local_port": 443,
    "remote_addr": "127.0.0.1",
    "remote_port": 80,
    "password": [
      "password"
    ],
    "ssl": {
      "cert": "/usr/local/etc/certfiles/certificate.crt",
      "key": "/usr/local/etc/certfiles/private.key"
    },
    "router": {
        "enabled": true,
        "block": [
            "geoip:private"
        ],
        "geoip": "/usr/share/trojan-go/geoip.dat",
        "geosite": "/usr/share/trojan-go/geosite.dat"
    }
}

```
* Trojan的配置文件，定位到`password`、`cert`和`key`并修改。密码按自己喜好，`cert`和`key`分别改为`/usr/local/etc/certfiles/certificate.crt`和`/usr/local/etc/certfiles/private.key`。编辑完成配置文件之后按屏幕下方快捷键提示（`^O`和`^X`即：`Ctrl+O`和`Ctrl+X`）保存并退出`nano`。修改之后的`config`文件如图所示。另外，如果有`IPv6`地址，将`local_addr`的`0.0.0.0`改为`::`才可以使用。
![](images/2021-06-24-15-58-15.png)

### 启动Trojan
1) 配置Trojan监听443端口
* 执行如下命令，赋予Trojan监听1024以下端口的能力，使得Trojan可以监听到443端口。这是由于我们使用非root用户启动Trojan，但是Linux默认不允许非root用户启动的进程监听1024以下的端口，除非为每一个二进制文件显式声明。
```shell
sudo setcap CAP_NET_BIND_SERVICE=+eip /usr/local/bin/trojan
```

2) 使用systemd启动Trojan
Trojan启动、查看状态命令分别如下，第一条是启动Trojan，第二条是查看Trojan运行状态。启动之后再查看一下状态，Trojan显示active (running)即表示正常启动了。
```shell
sudo systemctl restart trojan
sudo systemctl status trojan

sudo systemctl restart trojan-go
sudo systemctl status trojan-go
```

3) 更新证书
当`acme.sh`重新安装证书之后，需要通知`Trojan`重新加载证书。最简单的方案是每三个月登录服务器重启`Trojan`，但是不够完美，毕竟重启的时候会导致服务中断。其实`Trojan`有实现`reload certificate and private key` 功能，只需要在证书更新后给`Trojan`发送`SIGUSR1`消息即可。`Trojan`收到`SIGUSR1`消息后便会自动加载新的证书和密钥文件，这样就不用重启`Trojan`了。手动给`Trojan`发送`SIGUSR1`消息的命令是`sudo -u trojan killall -s SIGUSR1 trojan`，但是这样也不够完美，也得每三个月登录服务器运行一次该命令。其实我们可以给用户`root`添加定时任务，使其每个月运行一次该命令即可。实现如下。
首先，编辑用户trojan的crontab文件
```shell
sudo -u root crontab -e
```
在文件末尾添加一行如下，该行表示每个月1号的时候运行命令killall -s SIGUSR1 trojan。
```shell
0 0 1 * * killall -s SIGUSR1 trojan
```
最后查看crontab是否生效。
```shell
sudo -u trojan crontab -l
```

4) 更新Trojan
如果Trojan版本有更新（可以去 [这里](https://github.com/trojan-gfw/trojan/releases) 查看是否有更新）。
```shell
sudo bash -c "$(curl -fsSL https://raw.githubusercontent.com/trojan-gfw/trojan-quickstart/master/trojan-quickstart.sh)"
sudo setcap CAP_NET_BIND_SERVICE=+eip /usr/local/bin/trojan
sudo systemctl restart trojan
```
第一条命令会提示是否覆盖配置文件，如果没有必要请回答n，否则配置文件将会被覆盖（如果不小心覆盖了就得自己重新编辑了）。第二条命令重新赋予Trojan监听443端口的能力。第三条命令重启Trojan。

## 配置Nginx
### 写入虚拟主机到Nginx配置文件
由于Nginx配置在Debian系列系统和CentOS系列系统组织方式不同，所以配置文件位置和使用方式有细微区别，为了统一，我将CentOS系列系统的组织结构做细微调整。
<br/>
在Debian系列系统中，Nginx的虚拟主机配置文件在/etc/nginx/sites-available/文件夹中，如果要开启某一个虚拟主机，则建立一个软连接到/etc/nginx/sites-enabled/文件夹并重启Nginx即可。默认虚拟主机在/etc/nginx/sites-enabled/文件夹，需要关闭掉，否则会冲突。
<br/>
在CentOS系列系统中，Nginx的虚拟主机配置文件在/etc/nginx/conf.d/文件夹中以.conf后缀保存，写入之后就可以使用。默认虚拟主机集成在Nginx配置文件/etc/nginx/nginx.conf中，需要打开将其中的server块删除，否则会冲突。Debian系列系统中的/etc/nginx/sites-enabled/和/etc/nginx/sites-available/文件夹结构在CentOS系列系统中是没有的，不过这个策略很不错，可以很方便的开启和关闭虚拟主机，我这里手动调整一下。

#### CentOS/Debian
按上述分析，我们使用下面两条命令在/etc/nginx/中添加两个文件夹。
```shell
sudo mkdir /etc/nginx/sites-available
sudo mkdir /etc/nginx/sites-enabled
```
执行如下命令使用nano打开Nginx配置文件，删除其中server块，并添加对/etc/nginx/sites-enabled/文件夹的索引。
```shell
sudo nano /etc/nginx/nginx.conf
```
配置文件修改结果如下图所示。
默认写入了个伪装网站，大家可以自行去github上找。
```shell
rm -rf /usr/share/nginx/html/*   #删除目录原有文件
cd /usr/share/nginx/html/    #进入站点更目录
wget https://github.com/V2RaySSR/Trojan/raw/master/web.zip
unzip web.zip    #也可以上传自己的网站
```
![](images/2021-06-24-16-08-20.png)

* 或者是配置反向代理服务，使用 next 网站进行伪装
```nginx
server {
  listen 127.0.0.1:80;
  server_name <example.com>;

  location / {
    proxy_pass http://127.0.0.1:3000;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_cache_bypass $http_upgrade;
  }
}
```

CentOS反向代理需要配置SELinux允许httpd模块可以联网，否则服务器会返回502错误。
```shell
sudo setsebool -P httpd_can_network_connect true
```

#### Ubuntu or Debian
使用如下命令关闭Nginx默认虚拟主机。
```shell
sudo rm /etc/nginx/sites-enabled/default
```

### 写入配置
1) 执行如下命令，使用nano添加虚拟主机。(注意域名<tdom.ml>改为你自己的域名，这是虚拟主机的文件名，只是用来自己识别的。如果你已经有配置虚拟主机在这个文件中，可以自己使用cp命令备份一下或者换个名字也行，等介绍完基本配置再讲如何与现有服务集成。)
```shell
sudo nano /etc/nginx/sites-available/<tdom.ml>
```
基于综述部分讲解的Trojan工作原理，现给定Nginx虚拟主机如下所示。这些虚拟主机可以直接拷贝到上面虚拟主机配置文件中再修改为你自己的，其中要修改的地方包括：

第4行的server_name的值<10.10.10.10>改为你自己的IP；
第6行<tdom.ml>改为自己的域名，注意别填错了。

![](images/2021-06-24-16-10-58.png)

```nginx
server {
  listen 127.0.0.1:80;
  server_name <10.10.10.10>;
  return 301 https://<tdom.ml>$request_uri;
}

server {
  listen 0.0.0.0:80;
  listen [::]:80;

  server_name _;
  return 301 https://$host$request_uri;
}
```

2) 使用配置文件注意域名<tdom.ml>改为你自己的域名
```shell
sudo ln -s /etc/nginx/sites-available/<tdom.ml> /etc/nginx/sites-enabled/
```

### 启动Nginx
* Nginx启动命令和Trojan一样，就不过多解释了。
```shell
sudo systemctl restart nginx
sudo systemctl status nginx
```

## 启动bbr加速
安装BBR家族安装集成脚本
```shell
wget --no-check-certificate https://github.com/teddysun/across/raw/master/bbr.sh && chmod +x bbr.sh && ./bbr.sh
```

## 配置Trojan和Nginx开机自启
```shell
sudo systemctl enable trojan
sudo systemctl enable nginx
```

## 检查服务器是否配置成功
到这里服务器就配置完成了。此时你可以在浏览器里面访问你的网站看是否能够访问，如果你的网站可以访问了，那么就一切正常啦。

另外，基于以上考虑到的可能的恶意探测，可以验证一下以下情况是否正常。

* 浏览器中使用ip访问：重定向到https://tdom.ml;
* 浏览器中使用https://ip访问：重定向到https://tdom.ml(跳转的时候浏览器可能提示不安全是正常的);
* 浏览器中使用tdom.ml访问：重定向到https://tdom.ml。

## 额外配置
* 由于需要合理的使用流量，需要对流量进行简单的统计
### 安装环境
* 分析脚本需要用到python 环境
```sh
$ python3 --version
# 没有就安装，一般是默认有的
$ sudo apt install python3
$ pip3 --version
# 没有就安装
$ sudo apt install python3-pip
# 查看一下是否有相关依赖
$ pip3 list
# 创建静态资源文件夹
$ mkdir /var/www/html/reports
# 创建基础csv
$ vi /var/www/html/reports/trojan_traffic.csv
# 内容
user,date_time,website,recv,sent
```
### 保留日志
* 设置日志
```sh
# trojan
$ vi /etc/systemd/system/trojan.service

# trojan-go
$ vi /etc/systemd/system/trojan-go.service

# 新增以下数据指定日志
[Service]
ExecStart=/usr/local/bin/trojan /usr/local/etc/trojan/config.json
StandardOutput=append:/var/log/trojan.log
StandardError=inherit

$ sudo systemctl daemon-reload
$ sudo systemctl restart trojan
```

### 创建轮转日志
```sh
$ vi /etc/logrotate.d/trojan
/var/log/trojan.log {
    daily
    missingok
    rotate 7
    compress
    delaycompress
    notifempty
    create 0640 root adm
    postrotate
        systemctl restart trojan.service > /dev/null 2>&1 || true
    endscript
}
```

### 日志分析脚本
#### trojan
* 根目录下，新增分析流量脚本
* 简单的统计一下，因为涉及到日志的轮转，肯定会有部分数据统计不到的，无妨

```python
#!/usr/bin/env python3
import re
import csv
import os
import tempfile
import shutil
from datetime import datetime, timedelta
from collections import defaultdict

# 配置路径
LOG_FILE = "/var/log/trojan.log.1"  # 昨天的日志文件（logrotate 后）
CSV_FILE = "/var/www/html/reports/trojan_traffic.csv"

# 用户名称映射
USER_NAME = {
    "password1": "username1",
    "password2": "username2",
    "password3": "username3"
}

# 数据默认日期
DEFAULT_DATE_STR = (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d")

# 超出日期删除
CUTOFF_STR = (datetime.now() - timedelta(days=365)).strftime("%Y-%m-%d")

# 如果CSV文件所在目录不存在，则创建
csv_dir = os.path.dirname(CSV_FILE)
os.makedirs(csv_dir, exist_ok=True)

# 定义正则表达式，捕获日志行中的时间戳以及其他信息
auth_re = re.compile(r'^\[(.*?)\]\s+\[INFO\]\s+(\S+):(\d+)\s+authenticated as (\S+)')
req_re  = re.compile(r'^\[(.*?)\]\s+\[INFO\]\s+(\S+):(\d+)\s+requested connection to ([^:]+):\d+')
disconn_re = re.compile(r'^\[(.*?)\]\s+\[INFO\]\s+(\S+):(\d+)\s+disconnected, (\d+) bytes received, (\d+) bytes sent,.*')

# 用于存储会话信息，按连接端口号分组
sessions = {}

def filter_csv_by_date(file_path, cutoff_date, date_format="%Y-%m-%d"):
    """
    过滤 CSV 文件，将 date_time 字段值小于 cutoff_date 的记录删除，
    并将剩余数据写回文件。整个过程采用流式处理，适合大文件。

    :param file_path: CSV 文件路径
    :param cutoff_date: 截止日期字符串，例如 "2022-01-01"
    :param date_format: date_time 字段的日期格式，默认为 "%Y-%m-%d"
    """
    cutoff = datetime.strptime(cutoff_date, date_format).date()
    # 创建临时文件，用于保存过滤后的数据
    with tempfile.NamedTemporaryFile(mode="w", delete=False, newline='', encoding="utf-8") as tmpfile:
        with open(file_path, "r", newline='', encoding="utf-8") as infile:
            reader = csv.DictReader(infile)
            writer = csv.DictWriter(tmpfile, fieldnames=reader.fieldnames)
            writer.writeheader()
            for row in reader:
                try:
                    # 假设 CSV 中的 date_time 字段格式与 date_format 一致
                    row_date = datetime.strptime(row['date_time'], date_format).date()
                except Exception:
                    # 如果格式有误，可以选择跳过该记录
                    continue
                if row_date >= cutoff:
                    writer.writerow(row)
    # 替换原文件
    shutil.move(tmpfile.name, file_path)
    print(f"Filtered CSV file saved: only records with date_time >= {cutoff_date} are kept.")

def needs_filter(file_path, cutoff_date, date_format="%Y-%m-%d"):
    """
    检查 CSV 文件中是否存在 date_time 字段值小于 cutoff_date 的记录，
    如果存在则返回 True，否则返回 False。整个过程采用流式处理。

    :param file_path: CSV 文件路径
    :param cutoff_date: 截止日期字符串，例如 "2022-01-01"
    :param date_format: date_time 字段的日期格式，默认为 "%Y-%m-%d"
    :return: True 如果需要过滤（存在过期数据），否则 False
    """
    cutoff = datetime.strptime(cutoff_date, date_format).date()
    with open(file_path, "r", newline='', encoding="utf-8") as infile:
        reader = csv.DictReader(infile)
        for row in reader:
            try:
                row_date = datetime.strptime(row['date_time'], date_format).date()
            except Exception:
                continue
            if row_date < cutoff:
                return True
    return False

with open(LOG_FILE, 'r', encoding='utf-8') as f:
    for line in f:
        line = line.strip()
        # 认证事件：记录用户名
        m = auth_re.search(line)
        if m:
            timestamp, ip, port, user = m.groups()
            if port not in sessions:
                sessions[port] = {}
            sessions[port]['user'] = user
            continue

        # 请求事件：记录目标网站
        m = req_re.search(line)
        if m:
            timestamp, ip, port, website = m.groups()
            if port not in sessions:
                sessions[port] = {}
            sessions[port]['website'] = website
            continue

        # 断开事件：记录断开时间和流量
        m = disconn_re.search(line)
        if m:
            timestamp, ip, port, recv_str, sent_str = m.groups()
            if port not in sessions:
                sessions[port] = {}
            sessions[port]['date_time'] = timestamp[0:10]  # 使用断开时的日期作为记录时间
            sessions[port]['recv_bytes'] = int(recv_str)
            sessions[port]['sent_bytes'] = int(sent_str)
            continue

# 按用户和网站进行汇总：report[user][website] = {'bytes_received': total, 'bytes_sent': total}
report = defaultdict(lambda: defaultdict(lambda: {'date_time': DEFAULT_DATE_STR,'recv_bytes': 0, 'sent_bytes': 0}))
for port, data in sessions.items():
    user = data['user'] if data.get('user', None) else 'Unknown'
    website = data.get('website', None) if data['website'] else 'Unknown'
    report[user][website]['date_time'] = data.get('date_time', DEFAULT_DATE_STR)
    report[user][website]['recv_bytes'] += data.get('recv_bytes', 0)
    report[user][website]['sent_bytes'] += data.get('sent_bytes', 0)

new_records = []
for user, data in report.items():
    user = USER_NAME.get(user, 'Unknown')
    for website, data in data.items():
        date_time = data['date_time']  # 原始字符串格式，例如 "2025-03-07"
        recv_bit = f"{data.get('recv_bytes', 0)}"
        sent_bit = f"{data.get('sent_bytes', 0)}"
        new_records.append({
            'user': user,
            'date_time': date_time,
            'website': website,
            'recv': recv_bit,
            'sent': sent_bit
        })

# 过滤数据：只保留最近12个月的记录
if needs_filter(CSV_FILE, CUTOFF_STR):
    print("数据存量超过一年,需要清除多余数据...")
    filter_csv_by_date(csv_path, CUTOFF_STR)
else:
    print("数据未超过一年,无需过滤")

# 写回 CSV 文件（包含表头）
with open(CSV_FILE, 'a', newline='') as csvfile:
    fieldnames = ['user', 'date_time', 'website', 'recv', 'sent']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    for row in new_records:
        writer.writerow(row)

print(f"CSV report updated: {len(new_records)} new records appended")
```

* `sudo chmod +x trojan_traffic.py`记得加上执行权限

#### trojan-go
```py
#!/usr/bin/env python
# coding: utf-8
import re
import csv
import os
import tempfile
import shutil
from datetime import datetime, timedelta
from collections import defaultdict

# 配置路径
LOG_FILE = "./trojan.log.1"  # 昨天的日志文件（logrotate 后）
CSV_FILE = "./trojan_traffic_data.csv"

# 用户名称映射
USER_NAME = {
    "password1": "username1",
    "password2": "username2",
    "password3": "username3"
}

# 数据默认日期
DEFAULT_DATE_STR = (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d")

# 超出日期删除
CUTOFF_STR = (datetime.now() - timedelta(days=365)).strftime("%Y-%m-%d")

# 如果CSV文件所在目录不存在，则创建
csv_dir = os.path.dirname(CSV_FILE)
os.makedirs(csv_dir, exist_ok=True)

# 新日志格式正则表达式：
# 组1：时间戳 "2025/03/17 00:00:42"
# 组2：用户ID
# 组3：来源 IP
# 组4：来源端口
# 组5：目标地址（website）
# 组6：目标端口
# 组7：发送流量，如 "42.86 KiB"
# 组8：接收流量，如 "13.02 KiB"
disconn_re = re.compile(
    r'^\[INFO\]\s+(\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s+user\s+(\S+)\s+from\s+(\S+):(\d+)\s+tunneling to\s+([^:]+):(\d+)\s+closed\s+sent:\s+([\d\.]+\s*\S+)\s+recv:\s+([\d\.]+\s*\S+)'
)
# 用于存储会话信息，按连接端口号分组
sessions = {}

def parse_size(s):
    """
    将类似 "42.86 KiB"、"678 B"、"4.08 MiB" 的流量字符串转换为整数字节数。
    """
    s = s.strip()
    parts = s.split()
    try:
        value = float(parts[0])
    except Exception:
        return 0
    unit = parts[1].lower() if len(parts) > 1 else "b"
    if unit.startswith("k"):
        multiplier = 1024
    elif unit.startswith("m"):
        multiplier = 1024 * 1024
    elif unit.startswith("g"):
        multiplier = 1024 * 1024 * 1024
    else:
        multiplier = 1
    return int(value * multiplier)

def filter_csv_by_date(file_path, cutoff_date, date_format="%Y-%m-%d"):
    """
    过滤 CSV 文件，将 date_time 字段值小于 cutoff_date 的记录删除，
    并将剩余数据写回文件。整个过程采用流式处理，适合大文件。

    :param file_path: CSV 文件路径
    :param cutoff_date: 截止日期字符串，例如 "2022-01-01"
    :param date_format: date_time 字段的日期格式，默认为 "%Y-%m-%d"
    """
    cutoff = datetime.strptime(cutoff_date, date_format).date()
    # 创建临时文件，用于保存过滤后的数据
    with tempfile.NamedTemporaryFile(mode="w", delete=False, newline='', encoding="utf-8") as tmpfile:
        with open(file_path, "r", newline='', encoding="utf-8") as infile:
            reader = csv.DictReader(infile)
            writer = csv.DictWriter(tmpfile, fieldnames=reader.fieldnames)
            writer.writeheader()
            for row in reader:
                try:
                    # 假设 CSV 中的 date_time 字段格式与 date_format 一致
                    row_date = datetime.strptime(row['date_time'], date_format).date()
                except Exception:
                    # 如果格式有误，可以选择跳过该记录
                    continue
                if row_date >= cutoff:
                    writer.writerow(row)
    # 替换原文件
    shutil.move(tmpfile.name, file_path)
    print(f"Filtered CSV file saved: only records with date_time >= {cutoff_date} are kept.")

def needs_filter(file_path, cutoff_date, date_format="%Y-%m-%d"):
    """
    检查 CSV 文件中是否存在 date_time 字段值小于 cutoff_date 的记录，
    如果存在则返回 True，否则返回 False。整个过程采用流式处理。

    :param file_path: CSV 文件路径
    :param cutoff_date: 截止日期字符串，例如 "2022-01-01"
    :param date_format: date_time 字段的日期格式，默认为 "%Y-%m-%d"
    :return: True 如果需要过滤（存在过期数据），否则 False
    """
    cutoff = datetime.strptime(cutoff_date, date_format).date()
    with open(file_path, "r", newline='', encoding="utf-8") as infile:
        reader = csv.DictReader(infile)
        for row in reader:
            try:
                row_date = datetime.strptime(row['date_time'], date_format).date()
            except Exception:
                continue
            if row_date < cutoff:
                return True
    return False

# 存储汇总数据，结构为：report[user][website] = {'date_time': <日期>, 'recv_bytes': total, 'sent_bytes': total}
report = defaultdict(lambda: defaultdict(lambda: {'date_time': DEFAULT_DATE_STR, 'recv_bytes': 0, 'sent_bytes': 0}))

with open(LOG_FILE, 'r', encoding='utf-8') as f:
    for line in f:
        line = line.strip()
        # 认证事件：记录用户名
        m = disconn_re.search(line)
        if m:
            timestamp_str, user, src_ip, src_port, website, target_port, sent_str, recv_str = m.groups()
            # 解析时间戳并转换为 "YYYY-MM-DD" 格式
            try:
                dt = datetime.strptime(timestamp_str, "%Y/%m/%d %H:%M:%S")
                date_str = dt.strftime("%Y-%m-%d")
            except Exception:
                date_str = DEFAULT_DATE_STR
            # 将流量字符串转换为整数字节数
            sent_bytes = parse_size(sent_str)
            recv_bytes = parse_size(recv_str)
            # 根据用户ID进行友好名称映射（如果不存在则设为 Unknown）
            friendly_user = USER_NAME.get(user, "Unknown")
            # 汇总：以用户和目标网站为键进行统计
            report[friendly_user][website]['date_time'] = date_str
            report[friendly_user][website]['sent_bytes'] += sent_bytes
            report[friendly_user][website]['recv_bytes'] += recv_bytes

# 转换汇总结果为新记录列表
new_records = []
for user, website_dict in report.items():
    for website, data in website_dict.items():
        new_records.append({
            'user': user,
            'date_time': data['date_time'],
            'website': website,
            'recv': str(data.get('recv_bytes', 0)),
            'sent': str(data.get('sent_bytes', 0))
        })

# 如果 CSV 文件已存在，先检查并过滤掉超过一年的记录
if os.path.exists(CSV_FILE):
    if needs_filter(CSV_FILE, CUTOFF_STR):
        print("数据存量超过一年,需要清除多余数据...")
        filter_csv_by_date(CSV_FILE, CUTOFF_STR)
    else:
        print("数据未超过一年,无需过滤")
else:
    # 如果CSV文件不存在，则写入表头
    with open(CSV_FILE, 'w', newline='') as csvfile:
        fieldnames = ['user', 'date_time', 'website', 'recv', 'sent']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

# 追加新记录到 CSV 文件
with open(CSV_FILE, 'a', newline='') as csvfile:
    fieldnames = ['user', 'date_time', 'website', 'recv', 'sent']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    for row in new_records:
        writer.writerow(row)

print(f"CSV report updated: {len(new_records)} new records appended")
```

* 因为trojan和trojan-go的日志格式不一样，所以各取所需


### 创建任务
```sh
# 增加任务
$ sudo crontab -e

10 3 * * * /root/trojan_traffic.py > /root/trojan_traffic.log
```

### 创建分析页面
* 因为服务器太垃圾，把所有的数据放到客户端进行计算查看

```html
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <title>Trojan 流量数据分析</title>
  <!-- DataTables CSS -->
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/jquery.dataTables.min.css">
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
    }

    h1 {
      margin-bottom: 20px;
    }

    table {
      margin-bottom: 40px;
    }
  </style>
</head>

<body>
  <h1>Trojan 流量数据分析 - 明细数据</h1>
  <table id="trafficTable" class="display" style="width:100%">
    <thead>
      <tr>
        <th>用户</th>
        <th>日期时间</th>
        <th>网站</th>
        <th>收到 (KB)</th>
        <th>发送 (KB)</th>
      </tr>
    </thead>
    <tbody></tbody>
  </table>

  <h1>Trojan 流量日数据汇总</h1>
  <table id="summaryTable" class="display" style="width:100%">
    <thead>
      <tr>
        <th>用户</th>
        <th>日期</th>
        <th>收到合计 (MB)</th>
        <th>发送合计 (MB)</th>
        <th>总流量 (MB)</th>
      </tr>
    </thead>
    <tbody></tbody>
  </table>

  <h1>Trojan 流量月数据汇总</h1>
  <table id="summaryMonthTable" class="display" style="width:100%">
    <thead>
      <tr>
        <th>用户</th>
        <th>日期</th>
        <th>收到合计 (GB)</th>
        <th>发送合计 (GB)</th>
        <th>总流量 (GB)</th>
      </tr>
    </thead>
    <tbody></tbody>
  </table>

  <!-- jQuery -->
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <!-- PapaParse -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/PapaParse/5.4.1/papaparse.min.js"></script>
  <!-- DataTables JS -->
  <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
  <script>
    $(document).ready(function () {
      Papa.parse("reports/trojan_traffic.csv", {
        download: true,
        header: true,
        complete: function (results) {
          var data = results.data;
          var tbodyDetail = $("#trafficTable tbody");

          // 数组用于存放汇总数据，按 "用户|日期" 分组
          var summaryMap = {};

          // 数组用于存放汇总数据，按 "用户|月份" 分组
          var summaryMonMap = {};

          data.forEach(function (row) {
            // 填充明细表格
            if (!row.user) return;
            var tr = $("<tr>");
            tr.append($("<td>").text(row.user));
            tr.append($("<td>").text(row.date_time));
            tr.append($("<td>").text(row.website));
            tr.append($("<td>").text(parseFloat((row.recv / 1024).toFixed(2))));
            tr.append($("<td>").text(parseFloat((row.sent / 1024).toFixed(2))));
            tbodyDetail.append(tr);

            // 对汇总数据进行处理：假设 date_time 格式为 "yyyy-mm-dd HH:MM:SS"，取前10位作为日期
            var date = row.date_time ? row.date_time.substring(0, 10) : "Unknown";
            var key = row.user + "|" + date;

            // 将收到和发送转换成数字（如果为空或非数字，转为 0）
            var recv = parseFloat(row.recv) || 0;
            var sent = parseFloat(row.sent) || 0;
            if (summaryMap[key]) {
              summaryMap[key].recv += recv;
              summaryMap[key].sent += sent;
            } else {
              summaryMap[key] = { user: row.user, date: date, recv: recv, sent: sent };
            }

            var date_mon = row.date_time ? row.date_time.substring(0, 7) : "Unknown";
            var key_mon = row.user + "|" + date_mon;
            if (summaryMonMap[key_mon]) {
              summaryMonMap[key_mon].recv += recv;
              summaryMonMap[key_mon].sent += sent;
            } else {
              summaryMonMap[key_mon] = { user: row.user, date: date.substring(0, 7), recv: recv, sent: sent };
            }
          });

          // 构造汇总数据数组
          var summaryData = [];
          for (var key in summaryMap) {
            var entry = summaryMap[key];
            entry.total = parseFloat(((entry.recv + entry.sent) / 1024 / 1024).toFixed(2));
            // 保留两位小数
            entry.recv = parseFloat((entry.recv / 1024 / 1024).toFixed(2));
            entry.sent = parseFloat((entry.sent / 1024 / 1024).toFixed(2));
            summaryData.push(entry);
          }

          // 构造汇总数据数组
          var summaryMonData = [];
          for (var key in summaryMonMap) {
            var entry = summaryMonMap[key];
            entry.total = parseFloat(((entry.recv + entry.sent) / 1024 / 1024).toFixed(2));
            // 保留两位小数
            entry.recv = parseFloat((entry.recv / 1024 / 1024).toFixed(2));
            entry.sent = parseFloat((entry.sent / 1024 / 1024).toFixed(2));
            summaryMonData.push(entry);
          }

          // 填充汇总表格
          var tbodySummary = $("#summaryTable tbody");
          summaryData.forEach(function (entry) {
            var tr = $("<tr>");
            tr.append($("<td>").text(entry.user));
            tr.append($("<td>").text(entry.date));
            tr.append($("<td>").text(entry.recv.toFixed(2)));
            tr.append($("<td>").text(entry.sent.toFixed(2)));
            tr.append($("<td>").text(entry.total.toFixed(2)));
            tbodySummary.append(tr);
          });

          var tbodySummaryMon = $("#summaryMonthTable")
          summaryMonData.forEach(function (entry) {
            var tr = $("<tr>");
            tr.append($("<td>").text(entry.user));
            tr.append($("<td>").text(entry.date));
            tr.append($("<td>").text(entry.recv.toFixed(2)));
            tr.append($("<td>").text(entry.sent.toFixed(2)));
            tr.append($("<td>").text(entry.total.toFixed(2)));
            tbodySummaryMon.append(tr);
          })

          // 初始化 DataTables
          $("#trafficTable").DataTable();
          $("#summaryTable").DataTable();
          $("#summaryMonthTable").DataTable();
        }
      });
    });
  </script>
</body>
</html>
```

### 修改nginx配置
* 每次上服务器看不方便，直接暴露到nginx上，然后用nginx认证，简单阻拦一下就好了，省事儿。
`nano /etc/nginx/nginx.conf`
```nginx
server {
  listen 127.0.0.1:80;
  server_name <example.com>;

  location / {
    proxy_pass http://127.0.0.1:3000;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_cache_bypass $http_upgrade;
  }

  # 静态文件 report.html 的访问，并要求 HTTP 基础认证
  location = /report.html {
    auth_basic "Restricted Access";
    auth_basic_user_file /etc/nginx/.htpasswd;
    root /var/www/html;
  }

  # 数据文件也需要加密访问
  location = /reports/trojan_traffic.csv {
    auth_basic "Restricted Access";
    auth_basic_user_file /etc/nginx/.htpasswd;
    root /var/www/html;
  }
}
```
* 设置密码
```sh
# 安装工具包
$ sudo apt install apache2-utils
# 生成密码
$ htpasswd -c /etc/nginx/.htpasswd admin
# 输入两次相同密码即可
$ nginx -t
# 重新加载配置
$ sudo systemctl reload nginx
```

### 访问地址
* http://<tdom.com>/report.html
* 账密: admin:password

## PC端链接
配置SwitchyOmega插件(代理浏览器流量) + Trojan客户端在(本地流量统一转发)即可，配置文件配置为客户端链接。
![](images/2021-06-24-16-16-59.png)

## 其他端链接
### IOS
* 美区ID + [ Pharos Pro | Shadowrocket ];

### 安卓
* [igniter](https://github.com/trojan-gfw/igniter/releases)

