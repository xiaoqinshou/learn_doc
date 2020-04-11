### 1. 安装 clone
[toc]
> https://github.com/metabase/metabase.git
> git clone 下来即可
* 本人使用 webstorm 工具开发，所以重点从webstorm角度撰写。

### 2. 安装 Node.js
> https://npm.taobao.org/mirrors/node/v10.16.3/node-v10.16.3-x64.msi
点击下载即可

### 3. 安装 yarn.js
```shell
$ npm install -g yarn --registry=https://registry.npm.taobao.org
# 配置 yarn 下载源
$ yarn config set registry https://registry.npm.taobao.org -g
$ yarn config set sass_binary_site http://cdn.npm.taobao.org/dist/node-sass -g
```
### 4. windows 下个性化配置
打开 package.json 文件 里面命令大都是给予 linux 下的命令 会产生不兼容的情况，所以需要更改一些命令
```json
# 添加一个插件，用于实现无法跨平台的命令
"dependencies": {
"cross-env": "^5.2.0",
}

# 此命令记得加上 cross-env
# dev 命令，将单引号修改为双引号并转义，单引号它会当做普通字符串处理
# 删除preinstall 检查命令，因为这就是检查linux 环境是否齐全，在Windows下无法使用
"scripts":{
    "build-hot": "yarn && cross-env NODE_ENV=hot webpack-dev-server --progress",
    "dev": "concurrently --kill-others -p name -n \"backend,frontend,docs\" -c \"blue,green,yellow\" \"lein run\" \"yarn build-hot\" \"yarn docs\"",
    "preinstall": "echo $npm_execpath | grep -q yarn || echo '\\033[0;33mSorry, npm is not supported. Please use Yarn (https://yarnpkg.com/).\\033[0m'",
}
```