## 1. 概述 [JHipster](https://www.jhipster.tech/)
> &emsp;[JHipster](https://www.jhipster.tech/) 是一个开发平台，用于生成，开发和部署[Spring Boot](https://spring.io/projects/spring-boot) + [Angular](https://angular.io/) / [React](https://reactjs.org/) / [Vue](https://vuejs.org/) Web应用程序和Spring微服务。

--------------

## 2. 所需环境
> [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
> [Maven](http://maven.apache.org/)
> [Node.js](https://nodejs.org/en/)
> [Yarn](https://yarn.bootcss.com/)
> [Yeoman](https://yeoman.io/learning/)
> [JHipster](https://www.jhipster.tech/)

#### 2.1 安装 [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
&emsp;在oracle官网下载jdk1.8，然后点击安装。安装完成之后，在windows的环境变量->系统变量中添加JAVA_HOME环境变量，然后将java的bin目录添加到path系统变量中。

```properties
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
path=%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;%JAVA_HOME%\bin;%M2_HOME%\bin
```
&emsp;然后打开windwos cmd窗口验证
```powershell
PS C:\Users\admin> java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```
#### 2.2 安装 [Maven](http://maven.apache.org/)
&emsp;在 [Maven 官网](http://maven.apache.org/) 下载安装包，并且在本地任意目录下解压，并且配置同之前一样配置环境变量即可。例如：
```properties
D:\Program Files\apache-maven-3.3.9
M2_HOME=D:\Program Files\apache-maven-3.3.9
path=%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;%JAVA_HOME%\bin;%M2_HOME%\bin
```
&emsp;同样打开windows cmd命令窗口进行验证:
```powershell
PS C:\Users\admin>mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-11T00:41:47+08:00)
```

#### 2.3 安装 [Node.js](https://nodejs.org/en/)
&emsp;在 [Node.js](https://nodejs.org/en/) 下载安装包，执行安装程序，安装完成后配置同之前一样配置环境变量即可。例如：
```properties
NODE_HOME=D:\Program\nodejs
path=%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;%NODE_HOME%;
```
&emsp;同样打开windows cmd命令窗口进行验证:
```powershell
PS C:\Users\admin>npm -v
6.4.1
```

#### 2.4 安装 [Yarn](https://yarn.bootcss.com/)
&emsp;只需在安装好 [Node.js](https://nodejs.org/en/) 环境下执行Node命令即可完成安装。
```properties
PS npm install -g yarn --registry=https://registry.npm.taobao.org
```
&emsp;其中命令指定Node全局安装 [Yarn](https://yarn.bootcss.com/) 不需要再次配置环境变量，后面跟着的是阿里家的数据源。继续验证是否安装成功
```powershell
PS C:\Users\admin>yarn -version
1.15.2
```
&emsp;当然还有其他安装方式，就不一一赘述。

#### 2.5 安装 [Yeoman](https://yeoman.io/learning/)
&emsp;同样在 [Node.js](https://nodejs.org/en/) 环境下执行Node命令即可完成安装。
```powershell
PS npm install -g yo
PS C:\Users\admin>yo --version
2.0.6
```
&emsp;同样出现版本号即为安装成功。

#### 2.6 安装 [JHipster](https://www.jhipster.tech/)
&emsp;同样在 [Node.js](https://nodejs.org/en/) 环境下执行Node命令即可完成安装。
```powershell
PS npm install -g generator-jhipster
PS C:\Users\admin>jhipster --version
INFO! Using JHipster version installed globally 
6.0.0
```
&emsp;同样出现版本号即为安装成功。

## 3. 概述 [JHipster](https://www.jhipster.tech/) 微服务架构
> [JHipster](https://www.jhipster.tech/) 微服务应用架构图
![jhipster微服务架构](https://www.jhipster.tech/images/microservices_architecture_detail.001.png "jhipster微服务架构")

* 由架构描述可知一个完整的微服务应用至少要有3个服务同时运行。
* 微服务注册配置中心，管理所有微服务的配置及注册中心
* 微服务提供业务逻辑处理能力的微型服务
* 微服务网关，用于暴露前端展示页面，以及API接口，直接与用户交互的服务



## 4. 创建第一个 [JHipster](https://www.jhipster.tech/) 微服务

>* [Creating an application](https://www.jhipster.tech/creating-an-app/)
>* [快速创建](#4.1 快速创建)
>* [创建应用程序时询问的问题](#4.2 创建应用程序时询问的问题)
>* [命令行选项](#4.3 命令行选项)
>* [提示](#4.4 提示)

#### 4.1 快速创建

&emsp;首先，创建一个空目录，在其中创建应用程序：
```powershell
PS mkdir TestJhipster
```
&emsp;进入刚创建的空文件夹：
```powershell
PS cd TestJhipster/
```
&emsp;输入创建应用程序命令：
```powershell
PS jhipster
```
&emsp;回答生成器提出的问题，以创建适合您需求的应用程序。

#### 4.2 创建应用程序时询问的问题

&emsp;因为我这边使用的是微服务架构，所以只生成微服务项目。
&emsp;首先第一个问题
```powershell
Which type of application would you like to create?
  Monolithic application<recommended for simple projects>
> Microservice application
  Microservice gateway
  JHipster UAA server<for microservice OAuth2 authentication>
```
> 大致解释一下就是问选择哪种服务类型，[单片服务](https://www.jhipster.tech/microservices-architecture/)应用，[微服务](https://www.jhipster.tech/microservices-architecture/)应用，[微服务网关](https://baike.baidu.com/item/API%E7%BD%91%E5%85%B3/22636779?fr=aladdin)，[JHipster UAA](https://www.jhipster.tech/using-uaa/)服务器（用于保护微服务的OAuth2身份验证服务器）


```powershell
What is the base name of your application?<TestJhipster>
```
> 输入应用名称，默认即可。

```powershell
as you are running in a microservice architecture , on which port would like you server to run? It should be unique to avoid port conflicts .(8081)
```
> 询问端口号，并且指定端口号唯一并且不冲突。这里给默认8081吧。

```powershell
What is your default Java package name?
```
> 包名称，一般是由公司域名反过来写。这里就随便给了一个cn.com.xxx

```
which service discovery server do you want to use?
 > Jhipster Registry(uses Eureka, provides Spring config support and monitoring dashboards)
   consul
   no server discovery
```
> 就问微服务器如何被发现，这里选第一个

```
? Which *type* of authentication would you like to use? (Use arrow keys)
> JWT authentication (stateless, with a token)
  OAuth 2.0 / OIDC Authentication (stateful, works with Keycloak and Okta)
  Authentication with JHipster UAA server (the server must be generated separately)
```
> 选择认证方式，默认为JWT authentication，这里选择默认

```
? Which *type* of database would you like to use? (Use arrow keys)
> SQL (H2, MySQL, MariaDB, PostgreSQL, Oracle, MSSQL)
  MongoDB
  Couchbase
  Cassandra
  No database
```
> 选择数据库类型，默认为SQL类型数据库，这里选择SQL数据库

```
? Which *production* database would you like to use? (Use arrow keys)
> MySQL
  MariaDB
  PostgreSQL
  Oracle (Please follow our documentation to use the Oracle proprietary driver)
  Microsoft SQL Server
```
> 生产环境选择使用那种类型的数据库，默认为MySQL，这里选择MySQL。

```
? Which *development* database would you like to use? (Use arrow keys)
> H2 with disk-based persistence
  H2 with in-memory persistence
  MySQL
```
> 开发环境选择使用哪种类型的数据库，默认为H2存硬盘，这里选择H2存硬盘

```
? Do you want to use the Spring cache abstraction? (Use arrow keys)
> Yes, with the Ehcache implementation (local cache, for a single node)
  Yes, with the Hazelcast implementation (distributed cache, for multiple nodes, supports rate-limiting for gateway applications)
  [BETA] Yes, with the Infinispan (hybrid cache, for multiple nodes)
  Yes, with Memcached<distributed cache> - Warning, when using an SQL database, this will disable the Hibernate 2nd level cahe!
  No - Warning, when using an SQL database, this will also disable the Hibernate 2nd level cache
```
>是否使用spring cache，默认使用Ehcache，这里选择Ehcache 

```
? Do you want to use Hibernate 2nd level cache? (Y/n)
```
> 是否使用Hibernate二级缓存，这里选择是一个，输入y

```
? Would you like to use Maven or Gradle for building the backend? (Use arrow keys)
> Maven
  Gradle
```
> 选择项目构建工具，默认Maven，这里选择Maven

```
? Which other technologies would you like to use? (Press <space> to select, <a> to toggle all, <i> to inverse selection)
 ( ) Search engine using Elasticsearch
 ( ) Asynchronous messages using Apache Kafka
 ( ) API first development using OpenAPI-generator
```
> 选择一些要使用的其它技术，这里一个也不选，直接回车跳过

```
? Would you like to enable internationalization support? (Y/n)
```
> 是否需要国际化支持，这里不用，输入n

```
? Besides JUnit and Jest, which testing frameworks would you like to use? (Press <space> to select, <a> to toggle all, <i> to inverse selection)
>( ) Gatling
 ( ) Cucumber
```
> 除了Junit和Karma，还需要那些测试框架，这里不需要，回车一个也不选

```
? Would you like to install other generators from the JHipster Marketplace? (y/N)
```
> 是否需要从JHipster Marketplace安装其他generators，这里不需要，输入n

> 至此，框架创建选择结束，开始创建jhipster框架。

> 等数分钟之后，出现下面的片段，表示创建jhipster框架完成。

```
Application successfully committed to Git 
If you find JHipster useful consider sponsoring the project  https://www.jehopster.tech/sponsors

Server application generated successfully.

Run your Spring Boot application:
 ./mvnw (mvnw if using Windows Command Prompt)

INFO! Congratulations, JHipster execution is complete!
```

#### 4.3 命令行选项

在这里就直接给出中文的命令行选项了
>--help - 打印发电机的选项和用法
--blueprint - 指定要使用的蓝图。例如jhipster --blueprint kotlin
--skip-cache - 不记得提示答案（默认值：false）
--skip-git - 不要自动将生成的项目添加到Git（默认值：false）
--skip-install - 不要自动安装依赖项（默认值：false）
--skip-client - 跳过客户端应用程序生成，因此您只生成Spring Boot后端代码（默认值：false）。
--skip-server - 跳过服务器端应用程序生成，因此您只生成了前端代码（默认值：false）。
--skip-user-management - 在后端和前端都跳过用户管理生成（默认值：false）
--i18n - 跳过客户端生成时禁用或启用i18n，否则无效（默认值：true）
--auth - 在跳过服务器端生成时指定身份验证类型，否则无效但在使用时必须使用 skip-server
--db - 在跳过服务器端生成时指定数据库，否则无效但在使用时是必需的 skip-server
--with-entities- 如果现有实体已生成（使用.jhipster文件夹中的配置），则重新生成现有实体（默认值：false）
--skip-checks - 略过所需工具的检查（默认值：false）
--jhi-prefix - 在服务，组件和状态/路由名称之前添加前缀（默认值：jhi）
--entity-suffix - 在实体类名后添加后缀（默认值：空字符串）
--dto-suffix - 在DTO类名后添加后缀（默认值：DTO）
--yarn - 使用Yarn而不是NPM（默认值：false）
--experimental - 启用实验功能。请注意，这些功能可能不稳定，并且可能随时发生重大变化
提示
您还可以使用Yeoman命令行选项，例如--force自动覆盖现有文件。因此，如果要重新生成整个应用程序（包括其实体），则可以运行jhipster --force --with-entities。

#### 4.4 提示
> &emsp;您还可以使用Yeoman命令行选项，例如--force自动覆盖现有文件。因此，如果要重新生成整个应用程序（包括其实体），则可以运行jhipster --force --with-entities。

## 5. 导入开发编辑器

#### 5.1. 在编辑器中导入

&emsp;在[eclipse](https://baike.baidu.com/item/Eclipse/61703)/[idea](https://baike.baidu.com/item/IntelliJ%20IDEA/9548353?fromtitle=idea&fromid=1671803&fr=aladdin)中，选择创建的jhipster框架作为maven项目导入。
* 启动入口： cn.com.xxx.TestJhipsterApp

#### 5.2. 配置文件

* 基本配置
    * application.yml
* dev环境
    * application-dev.yml
* 生产环境
    * application-prod.yml
* 证书配置文件
    * application-tls.yml
* Spring Cloud配置“dev”配置文件的bootstrap配置,会被bootstrap-prod所覆盖
    * bootstrap.yml
* “prod”配置文件的Spring Cloud Config引导程序配置，会覆盖bootstrap.yml文件中的配置
    * bootstrap-prod.yml

#### 5.3 更改配置文件 
* &emsp;因为这里创建的是分布式下的微服务项目，所以需要修改配置文件，向微服务注册中心发送注册通知，才能正常启动。否则一直报微服务注册失败。

#### 5.4 pom 文件以及配置文件的删改
* &emsp;因为 [JHipster](https://www.jhipster.tech/) 创建出来的微服务应用，内部集成了大多数框架例如: [Hibernate](https://baike.baidu.com/item/Hibernate/206989?fr=aladdin) ,[Spring Data JPA](https://www.ibm.com/developerworks/cn/opensource/os-cn-spring-jpa/)这对使用其他工具的小伙伴就不是很友好了。
* &emsp;所以可以根据自己的技术栈需求进行相应的整改pom文件。

## 6. 创建一个简单的 [Eureka](https://baike.baidu.com/item/Eureka/22402835?fr=aladdin) 注册配置中心
+ 在这里就简单的用 [IDEA](https://baike.baidu.com/item/IntelliJ%20IDEA/9548353?fromtitle=idea&fromid=1671803&fr=aladdin) 编辑器介绍一下
* 主要步骤就是首先创建一个简单以 [Maven](http://maven.apache.org/) 的 [Spring boot](https://spring.io/projects/spring-boot) 项目
* 其次在 Pom 文件中引入配置

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
            <version>2.1.1.RELEASE</version>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

* 然后在主配置文件application.yml中添加 [Eureka](https://baike.baidu.com/item/Eureka/22402835?fr=aladdin) 的相关配置即可。

```yaml
server:
  port: 1111

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone:  http://${eureka.instance.hostname}:${server.port}/eureka/

spring:
  application:
```
* 至此最简单的一个 [Eureka](https://baike.baidu.com/item/Eureka/22402835?fr=aladdin) 注册中心就搭建完成，最后启动服务即可。
* 然后在浏览器中输入 http://localhost:1111/ 即可看到搭建好的 [Eureka](https://baike.baidu.com/item/Eureka/22402835?fr=aladdin) 配置中心页面

## 7. 工具
> [工具介绍](#6.1 工具介绍)
> [代码生成](#6.2 代码生成)

#### 7.1 工具介绍
>* [JHipster UML](https://www.jhipster.tech/jhipster-uml/)，允许您使用UML编辑器。

>* [JDL Studio](https://start.jhipster.tech/jdl-studio/)，我们使用特定领域语言JDL创建实体和关系的在线工具。

#### 7.2 代码生成

如果您使用的是JDL Studio：

> 您可以使用import-jdl子生成器通过运行从JDL文件生成实体
```
jhipster import-jdl your-jdl-file.jh。
```

>如果您不想在导入JDL时重新生成实体，则可以使用该--json-only标志跳过实体创建部分，并仅在文件.jhipster夹中创建json文件。
```
jhipster import-jdl ./my-jdl-file.jdl --json-only
```
默认情况下，import-jdl仅重新生成已更改的实体，如果要重新生成所有实体，则传入--force 标志。请注意，这将覆盖对实体文件的所有本地更改
```
jhipster import-jdl ./my-jdl-file.jdl --force
```
如果要使用JHipster UML而不是import-jdl子生成器，则需要通过运行安装它
```
npm install -g jhipster-uml
```
然后运行jhipster-uml yourFileName.jh。