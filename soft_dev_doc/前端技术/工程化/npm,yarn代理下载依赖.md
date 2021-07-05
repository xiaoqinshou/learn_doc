# 前言
&emsp;&emsp;众所周知，国内使用`npm`、`yarn`对项目的依赖进行管理，体验极差，因为国内镜像不完全，导致常用的依赖，或者是国外其它优秀的依赖，下载不下来。
&emsp;&emsp;所以记录一下，利用代理进行下载依赖。一劳永逸，脱离国内镜像的苦海。

## 代理
* 大部分市面上的代理都是socket5协议
* `npm`、`yarn` 虽然都支持代理，但是只支持http、https转发。
* 所以思路有了，安装一个http、https 转socket代理的中间件就行

## 设置代理
http代理(`npm`为例)：
`yarn` 也一样，只要把`npm`替换成`yarn`就行
```shell
# 假设本地代理端口为8002
npm config set proxy http://127.0.0.1:8002
npm config set https-proxy http://127.0.0.1:8002

# 有用户密码的代理
npm config set proxy http://username:password@127.0.0.1:8002
npm confit set https-proxy http://username:password@127.0.0.1:8002
```

## 查看
`npm`替换成`yarn`
```shell
npm config get item_name
npm config list
```

## 清除npm代理
`npm`替换成`yarn`就是`yarn`的命令
```shell
npm config delete proxy
npm config delete https-proxy
```

## socks5 代理
`npm` 不支持 `socks` 代理，但是我们可以用一个工具将 `http` 代理转成 `socks` 代理，然后将 `npm` 代理地址设置到这个工具的地址。
```shell
# 假设本地socks5代理端口为1081
# 首先安装转换工具
npm install -g http-proxy-to-socks
# 然后使用这个工具监听8002端口,支持http代理，然后所有8002的http代理数据都将转换成socks的代理数据发送到1081上
hpts -s 127.0.0.1:1081 -p 8002
# 最后设置npm代理为8080
npm config set proxy http://127.0.0.1:8002
npm config set https-proxy http://127.0.0.1:8002
```
* 相当于又加了一个中间层，将 http 转成 socks。 hpts -s localhost:1081 -p 8002需要一直开着比较麻烦，但是加速效果还是很不错的。

其他软件如：`yarn`、`gradle`...同理

