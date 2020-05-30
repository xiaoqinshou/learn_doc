[toc]
# 前言
&emsp;&emsp;原文地址 [webpack教程](http://webpack.wuhaolin.cn)
&emsp;&emsp;快速搭建 React 的脚手架有 FaceBook 提供的 [create-react-app](https://github.com/facebook/create-react-app) 和阿里的 [Ant Design of React](https://ant.design/docs/react/introduce-cn)，但是这两个都是现成已经搭好的脚手架，将webpack的相关配置直接封装好了，所以自定制化程度不高。对于小项目脚手架有些偏重。

## 1. 安装 Node.Js
1. 下载对应你系统的 [Node.js版本](https://nodejs.org/en/download/)
2. 默认安装就行，并且配置 [Node.js环境变量](https://baike.baidu.com/item/%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F/1730949?fr=aladdin)
3. 基本能在操作系统下的dos命令行敲出以下测试命令
```
$ npm -version
$ npm -help
```
[Node.js](https://baike.baidu.com/item/node.js) 基本安装成功.

----------
## 2. 初始化一个空项目
```shell
$ d:
$ cd /your_files
$ mkdir react_demo
$ cd react_demo
$ npm init
This utility will walk you through creating a package.json file.
It only covers the most common items, and tries to guess sensible defaults.

See `npm help json` for definitive documentation on these fields
and exactly what they do.

Use `npm install <pkg>` afterwards to install a package and
save it as a dependency in the package.json file.

Press ^C at any time to quit.
package name: (react_demo)
version: (1.0.0)
description: 从零搭建React 脚手架
entry point: (index.js)
test command:
git repository:
keywords:
author: RottenTree
license: (ISC)
About to write to D:\2myproject\react_demo\package.json:

{
  "name": "react_demo",
  "version": "1.0.0",
  "description": "从零搭建React 脚手架",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "RottenTree",
  "license": "ISC"
}


Is this OK? (yes)
$ mkdir src
```
现在就得到了一个最简单的 npm 目录，自带一个 package.json  管理文件。并创建一个src 文件用于存放源码。


## 3. 安装 Webpack
要安装 Webpack 到本项目，可按照你的需要选择以下任意命令运行：
```powershell
# npm i -D 是 npm install --save-dev 的简写，是指安装模块并保存到 package.json 的 devDependencies
# 安装最新稳定版
$npm i -D webpack

# 安装指定版本
$npm i -D webpack@<version>

# 安装最新体验版本
$npm i -D webpack@beta
```

Webpack 在执行构建时默认会从项目根目录下的 webpack.config.js 文件读取配置，所以你还需要新建它，其内容如下(最简单的设置)：
```javascript
const path = require('path');

module.exports = {
  // JavaScript 执行入口文件
  entry: './main.js',
  output: {
    // 把所有依赖的模块合并输出到一个 bundle.js 文件
    filename: 'bundle.js',
    // 输出文件都放到 dist 目录下
    path: path.resolve(__dirname, './dist'),
  }
};
```

## 4. 引入 ECMAScript 6 语言
### 4.1 Babel
Babel可以方便的完成2件事。 Babel 是一个 JavaScript 编译器，能将 ES6 代码转为 ES5 代码，让你使用最新的语言特性而不用担心兼容性问题，并且可以通过插件机制根据需求灵活的扩展。 在 Babel 执行编译的过程中，会从项目根目录下的 .babelrc 文件读取配置。.babelrc 是一个 JSON 格式的文件，内容大致如下：
```json
{
  "plugins": [
    [
      "transform-runtime",
      {
        "polyfill": false
      }
    ]
   ],
  "presets": [
    [
      "es2015",
      {
        "modules": false
      }
    ],
    "stage-2",
    "react"
  ]
}
```
### 4.2 Plugins
plugins 属性告诉 Babel 要使用哪些插件，插件可以控制如何转换代码。
以上配置文件里的 transform-runtime 对应的插件全名叫做 babel-plugin-transform-runtime，即在前面加上了 babel-plugin-，要让 Babel 正常运行我们必须先安装它：
```powershell
npm i -D babel-plugin-transform-runtime
```
babel-plugin-transform-runtime 是 Babel 官方提供的一个插件，作用是减少冗余代码。 Babel 在把 ES6 代码转换成 ES5 代码时通常需要一些 ES5 写的辅助函数来完成新语法的实现.

### 4.3 接入 Babel
在了解 Babel 后，下一步要知道如何在 Webpack 中使用它。 由于 Babel 所做的事情是转换代码，所以应该通过 Loader 去接入 Babel，Webpack 配置如下：
```javascript
module.exports = {
  module: {
    rules: [
      {
        test: /\.js$/,
        use: ['babel-loader'],
      },
    ]
  },
  // 输出 source-map 方便直接调试 ES6 源码
  devtool: 'source-map'
};
```

配置命中了项目目录下所有的 JavaScript 文件，通过 babel-loader 去调用 Babel 完成转换工作。 在重新执行构建前，需要先安装新引入的依赖：
```powershell
# Webpack 接入 Babel 必须依赖的模块
npm i -D babel-core babel-loader
# 根据你的需求选择不同的 Plugins 或 Presets
npm i -D babel-preset-env
```
* 引入了所需要的依赖，就开始配置 webpack 进行解析转换，将 ES6 代码转换为 ES5 代码。

## 5. 引入 TypeScript
TypeScript 官方提供了能把 TypeScript 转换成 JavaScript 的编译器。 你需要在当前项目根目录下新建一个用于配置编译选项的 tsconfig.json 文件，编译器默认会读取和使用这个文件，配置文件内容大致如下：
```json
{
  "compilerOptions": {
    "module": "commonjs", // 编译出的代码采用的模块规范
    "target": "es5", // 编译出的代码采用 ES 的哪个版本
    "sourceMap": true // 输出 Source Map 方便调试
  },
  "exclude": [ // 不编译这些目录里的文件
    "node_modules"
  ]
}
```
### 5.1 减少代码冗余
TypeScript 编译器会有和在 4 使用ES6语言中 Babel 一样的问题：在把 ES6 语法转换成 ES5 语法时需要注入辅助函数， 为了不让同样的辅助函数重复的出现在多个文件中，可以开启 TypeScript 编译器的 importHelpers 选项，修改 tsconfig.json 文件如下：
```json
{
  "compilerOptions": {
    "importHelpers": true
  }
}
```

### 5.2 集成 Webpack
要让 Webpack 支持 TypeScript，需要解决以下2个问题：
1. 通过 Loader 把 TypeScript 转换成 JavaScript。
2. Webpack 在寻找模块对应的文件时需要尝试 ts,tsx 后缀。

对于问题1，社区已经出现了几个可用的 Loader，推荐速度更快的 awesome-typescript-loader。
对于问题2，根据2-4 Resolve 中的 extensions 我们需要修改默认的 resolve.extensions 配置项。

综上，相关 Webpack 配置如下：
```javascript
const path = require('path');

module.exports = {
  // 执行入口文件
  entry: './main',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, './dist'),
  },
  resolve: {
    // 先尝试 ts 后缀的 TypeScript 源码文件
    extensions: ['.tsx', '.ts', '.js']
  },
  module: {
    rules: [
      {
        test: /\.(tsx|ts)$/,
        loader: 'awesome-typescript-loader'
      }
    ]
  },
  devtool: 'source-map',// 输出 Source Map 方便在浏览器里调试 TypeScript 代码
};
```
依赖安装：
```powershell
npm i -D typescript awesome-typescript-loader
```

## 6. 使用 React 框架
### 6.1 React 与 Babel
要在使用 Babel 的项目中接入 React 框架是很简单的，只需要加入 React 所依赖的 Presets babel-preset-react。 接下来通过修改前面讲过的3-1 使用 ES6 语言中的项目，为其接入 React 框架。
通过以下命令：
```powershell
# 安装 React 基础依赖
npm i -D react react-dom
# 安装 babel 完成语法转换所需依赖
npm i -D babel-preset-react
```
安装新的依赖后，再修改 .babelrc 配置文件加入 React Presets
```json
"presets": [
    "react"
],
```
### 6.2 React 与 TypeScript
TypeScript 相比于 Babel 的优点在于它原生支持 JSX 语法，你不需要重新安装新的依赖，只需修改一行配置。 但 TypeScript 的不同在于：
* 使用了 JSX 语法的文件后缀必须是 tsx。
* 由于 React 不是采用 TypeScript 编写的，需要安装 react 和 react-dom 对应的 TypeScript 接口描述模块 @types/react 和 @types/react-dom 后才能通过编译。

接下来通过修改5 引入TypeScript，为其接入 React 框架。 修改 TypeScript 编译器配置文件 tsconfig.json 增加对 JSX 语法的支持，如下：
```json
{
  "compilerOptions": {
    "jsx": "react" // 开启 jsx ，支持 React
  }
}
```
通过以下命令安装所需依赖
```powershell
npm i react react-dom @types/react @types/react-dom
```

## 7. 配置 devService
1. 提供 HTTP 服务而不是使用本地文件预览；
2. 监听文件的变化并自动刷新网页，做到实时预览；
3. 支持 Source Map，以方便调试。

### 7.1 安装 DevServer：
```powershell
npm i -D webpack-dev-server
npm i -D webpack-cli
```

安装成功后执行 webpack-dev-server 命令， DevServer 就启动了，这时你会看到控制台有一串日志输出：
```powershell
Project is running at http://localhost:8080/
webpack output is served from /
```
这意味着 DevServer 启动的 HTTP 服务器监听在 http://localhost:8080/ ，DevServer 启动后会一直驻留在后台保持运行，访问这个网址你就能获取项目根目录下的 index.html。

将命令加进package中：
```json
"scripts": {
  "dev": "webpack-dev-server"
}
```
### 7.2 实时预览
在启动 Webpack 时通过 webpack --watch 来开启监听模式。

### 7.3 模块热替换
在启动 DevServer 时带上 --hot 参数

### 7.4 支持 Source Map
在启动时带上 --devtool source-map 参数。

## 8. 配置HtmlWebpackPlugin
> 就算做完以上配置，然后运行命令，也只能打包，无法做到实时热部署开发的模式，需要配置HtmlWebpackPlugin,才能让该脚手架实时热部署编译，并且在浏览器端实时渲染。

### 8.1 安装HtmlWebpackPlugin
运行以下命令下载最新版HtmlWebpackPlugin
```powershell
$ npm i -D html-webpack-plugin
```

### 8.2 配置Webpack
&emsp;在webpack.config.js中添加以下配置
```javascript
const HtmlWebpackPlugin = require('html-webpack-plugin');
module.exports = {
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        inline: true
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: path.join(__dirname, 'index.html'),
            hash: true,
        })
    ],
};
```
&emsp;表示监听开发服务，配置内容库路径。并添加HtmlWebpackPlugin插件，使其实时编译引入Html页面中。

### Demo
* 同目录下有 Demo.
* 目前这个Demo 只是做了一个搭最基础的脚手架而已，并没有一些代码约定格式化，编译优化，单元测试，mock数据请求等功能在里面。
* 也并未添加React 其他的全家桶
