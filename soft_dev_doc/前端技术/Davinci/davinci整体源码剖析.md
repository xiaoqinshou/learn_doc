# Davinci 源码剖析
[toc]
## 项目文件结构
```file
+ assembly
    -src
    -pom.xml
+ bin
    -migration
    -patch
    -start.sh
+ config
+ davinci-ui
+ docs
+ lib
+ logs
+ server
    - src
+ userfile
+ webapp
+ pom
```
* assembly：项目打包配置文件，所有的项目打包时所需要操作的步骤都在该文件的src下。
* bin: 主要放项目的启动安装等脚本，以及项目升级插件，和sql的安装升级脚本
* config: 项目的主要配置文件架
* davinci-ui: webapp代码编译后，所发布的JS
* docs：项目相关文档
* lib：所需要导入的本地JAR包
* logs: 项目运行日志
* server: 提供后端服务的JAVA代码
* userfile: 用户上传或下载生成的文件所放目录
* webapp: davinci 前端UI源码

### 后端文件结构
略

### 前端文件结构
略

## 前端项目
### 前言
React，只是一个UI框架，它并不是一个类似于 spring boot 的东西，完成了所有东西的整合。它可能更像的是Spring，Spring的核心就是 IOC 和 AOP，所以要用到 React 开发它需要一套完整的生态，就像以前的 Spring 一样，如果需要用Spring 做Web 端的服务器开发，要去整合 Mybatis, SpringMvc，Tomcat等一系列的东西。所以 Davinci的源码也是类似于以前使用Spring时是经过项目架构师所架构出来的项目，所用到的技术，以及前端性能优化等。

### 技术栈
以下仅介绍主要技术（代码中主要用到技术，不含项目优化技术）：
* React: 用于构建用户界面的 JavaScript 库
* react-intl：项目国际化
* react-router：项目路由控制
* react-redux/redux：使React组件从Redux store中读取数据，并且向store分发actions以更新数据。
1. redux-saga：用于管理redux应用异步操作的中间件
2. redux-immutable：静态数据集，相当于 java 中的，static 对象，一旦创建永不改变。
* axios：后端请求发起组件，可理解成AJAX

### 项目运行机制
```plantuml
actor user
storage store
component containers
control 改变URL
control containers中的action事件
package HOC封装{
    usecase 新增store 
    usecase 订阅store 
    usecase 分发action 
    usecase 新增saga  
}

file page 
user -> 改变URL: 进入页面
改变URL-> containers: 选择多个容器
containers -> HOC封装: 新增功能
HOC封装 -> page: 返回新增好的容器,并组合
user<--page
user-->containers中的action事件: 触发
containers中的action事件-->store: 改变
store-->containers: 更新订阅该store的containers
containers-->page: 拿到新的store重新渲染
```

### containers 规范
在davinci 中一个合格的容器一般都含有 6 个文件：
1. index.tsx: 当前容器的 UI 文件，主要用于渲染可视化界面
2. constants.ts: 当前容器的 所需要触发的 Action Type 定义名称，可理解为 Action 的配置文件
3. action.ts: 当前容器所能触发的 action 分类
4. reducer.ts: 当前容器所修改store 的文件
5. sagas.ts: 当前容器所需要触发的异步操作
6. selectors.ts: 筛选store中实际该容器所需要用到的数据
