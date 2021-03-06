# [Ant Design]("https://ant.design/index-cn") 项目搭建
#### &ensp;&ensp;首先安装 [Node.Js](https://baike.baidu.com/item/node.js)

------------
1. 下载对应你系统的 [Node.js版本](https://nodejs.org/en/download/) 
2. 默认安装就行，并且配置 [Node.js环境变量](https://baike.baidu.com/item/%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F/1730949?fr=aladdin)
3. 基本能在操作系统下的dos命令行敲出以下测试命令
```
$ npm -version
$ npm -help
```
[Node.js](https://baike.baidu.com/item/node.js) 基本安装成功.

----------

#### &ensp;&ensp;搭建 [Ant Design]("https://ant.design/index-cn") 项目 

----------
1. 先创建一个空项目，空应用
```
$ mkdir newProject
```
+ 或者直接创建一个新文件夹，然后使用dos命令进入文件夹
```
$ cd newProject
```
2. 官方推荐使用 yarn 命令搭建安装 Ant Design项目
```
$ yarn create umi
```
+ 也可使用 npm 命令
```
$ npm install -g create-umi && create-umi
```
3. 运行完npm命令后选择app选项进行下一步
```
 create-umi@0.12.1
updated 1 package in 11.155s
? Select the boilerplate type
  ant-design-pro  - Create project with an layout-only ant-design-pro boilerplate, use together with umi block.
> app             - Create project with a simple boilerplate, support typescript.
  block           - Create a umi block.
  library         - Create a library with umi.
  plugin          - Create a umi plugin.
```
4. 然后选择antd和dva两项，其中antd是 [Ant Design]("https://ant.design/index-cn") 的UI组件，dva是[Dva.js](https://dvajs.com/)
```
? Select the boilerplate type app
? Do you want to use typescript? Yes
? What functionality do you want to enable?
 (*) antd
>(*) dva
 ( ) code splitting
 ( ) dll
 ( ) internationalization
```
5. 最后看到命令行中出现
```
✨ File Generate Done
```
就初始化成功了一个 [Ant Design]("https://ant.design/index-cn") 项目，然后根据初始化的项目中所拥有的配置文件，利用 [Node.Js](https://baike.baidu.com/item/node.js) 下载项目依赖

6. 我这里使用的是 [WebStorm](https://baike.baidu.com/item/WebStorm/9392448?fr=aladdin) 编辑器中初始化好项目以后根据配置文件自动根据 npm 自动下载依赖，官方推荐使用
```
$ yarn
```
安装依赖
7. 启动项目，官方推荐使用以下命令启动
```
$ yarn start
```
本人使用 [WebStorm](https://baike.baidu.com/item/WebStorm/9392448?fr=aladdin) 初始化后的package.json中配置好的启动命令按钮直接启动，效果相同。以下是启动成功后所出现的命令行
```
 build [==                  ] 10%Starting the development server...
 Build completed in 9.412s
 DONE  Compiled successfully in 9421ms
 App running at:
  - Local:   http://localhost:8000/ (copied to clipboard)
  - Network: http://xxx.xxx.xxx.xxx:8000/
```
8. 打开命令行中的地址，即可看到搭建好并启动成功后的欢迎界面