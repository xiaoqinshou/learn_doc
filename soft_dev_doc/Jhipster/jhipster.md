# 1. 概述

>* [JHipster](https://www.jhipster.tech/)

&emsp;JHipster is a development platform to generate, develop and deploy Spring Boot + Angular Web applications and Spring microservices.

__Goal__

&emsp;Our goal is to generate for you a complete and modern Web app or microservice architecture, unifying:

* A high-performance and robust Java stack on the server side with Spring Boot
* A sleek, modern, mobile-first front-end with Angular and Bootstrap
* A robust microservice architecture with [JHipster Registry](https://www.jhipster.tech/jhipster-registry/), Netflix OSS, ELK stack and Docker
* A powerful workflow to build your application with Yeoman, Webpack/Gulp and Maven/Gradle

# 2. Local installation with Yarn using Angular<span id = "YarnUsingAngular"></span>

>* [Installing JHipster](https://www.jhipster.tech/installation/)

__Quick setup when using Angular__

1. Install Java 8 from [the Oracle website](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Install Node.js from the [Node.js website](https://nodejs.org/) (prefer an LTS version)
3. Install Yarn from [the Yarn website](https://yarnpkg.com/en/docs/install)
4. If you want to use the JHipster Marketplace, install Yeoman: ```yarn global add yo```
5. Install JHipster: ```yarn global add generator-jhipster```

Now that JHipster is installed, your next step is to create an application

## 2.1. 安装jdk1.8

>* [jdk1.8下载](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

&emsp;在oracle官网下载jdk1.8，然后点击安装。安装完成之后，在windows的环境变量->系统变量中添加JAVA_HOME环境变量，然后将java的bin目录添加到path系统变量中。

```properties
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
path=%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;%JAVA_HOME%\bin;%M2_HOME%\bin
```

然后打开windwos cmd窗口验证

```powershell
PS C:\Users\utsc0167> java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```

## 2.2. 安装maven

&esmp;参考[eclipse&Git.doc](http://172.19.64.100:9999/doc/%E6%96%B0%E5%91%98%E5%B7%A5%E5%85%A5%E8%81%8C/eclipse%26Git.doc)

## 2.3. 安装git

&emsp;参考[eclipse&Git.doc](http://172.19.64.100:9999/doc/%E6%96%B0%E5%91%98%E5%B7%A5%E5%85%A5%E8%81%8C/eclipse%26Git.doc)

## 2.4. 安装node.js

>* [node.js官网](https://nodejs.org/en/)

### 2.4.1. 安装

&emsp;从[node.js官网](https://nodejs.org/en/)下载node.js的安装包之后，点击安装。安装完成之后，在windows的环境变量->系统变量的中添加NODE_HOME和NODE_PATH环境变量，然后将NODE_HOME和NODE_PATH目录添加到path系统变量中。

```properties
NODE_HOME=D:\Program Files\node-v8.9.3-win-x64
NODE_PATH=D:\workspace\nodejs\global
path=%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;%JAVA_HOME%\bin;%M2_HOME%\bin
```

然后打开windwos cmd窗口验证

```powershell
PS C:\Users\utsc0167> node -v
v8.9.3
PS C:\Users\utsc0167> npm -v
5.5.1
```

### 2.4.2. 配置

&emsp;设置taobao registry

```bash
#设置taobao registry
npm config set metrics-registry "https://registry.npm.taobao.org/"
npm config get metrics-registry
```

&emsp;主要是设置npm的缓存目录。

```bash
#查看当前配置
npm config list

#查看napm的缓存目录
npm config get cache

#设置缓存目录
npm config set prefix "d:\workspace\nodejs\cache"
npm config set cache "d:\workspace\nodejs\global"
```

## 2.5. 安装yarn

>* [yarn官网](https://yarnpkg.com/en/)

### 2.5.1. 安装

&emsp;从[yarn官网](https://yarnpkg.com/en/)下载yarn的安装包之后，点击安装。安装完成之后，在windows的环境变量->系统变量的的path中添加yarn的安装目录。同时，需要将```yarn global dir```的node_modules\.bin目录加入到windows环境变量中，```yarn global list```看到的bin文件才能正常全局执行。似乎```yarn config set prefix```设置之后的```yarn global bin```没有bug，而```yarn global dir```又不能通过命令修改。

```properties
path=;C:\Program Files (x86)\Yarn\bin\;C:\Users\utsc0167\AppData\Local\Yarn\Data\global\node_modules\.bin\
```

然后打开windwos cmd窗口验证

```powershell
PS C:\Users\utsc0167> yarn -v
1.5.1
```

### 2.5.2. 配置

&emsp;主要是配置yarn的cache。

```bash
#查看cache dir
yarn cache dir
#更改yarn cache的默认位置
yarn config set cache-folder "d:\workspace\nodejs\yarn\cache"
```

## 2.6. 安装yo

>* [Yeoman官网](http://yeoman.io/)

```bash
#查看yarn global dir
yarn global dir

#全局安装yo
yarn global add yo
#查看yo
yarn global list

#验证
yo --version
```

## 2.7. 安装generator-jhipster

```bash
#安装generator-jhipster
yarn global add generator-jhipster
#查看generator-jhipster
yarn global list

#验证
jhipster --version
```

## 2.8. Creating an application

>* [Creating an application](https://www.jhipster.tech/creating-an-app/)

Please check our video tutorial on creating a new JHipster application!

1. Quick start
2. Questions asked when generating an application
3. Command-line options
4. Tips


### 2.8.1. Quick start

First of all, create an empty directory in which you will create your application:

```bash
mkdir myapplication
```

Go to that directory:

```bash
cd myapplication/
```

To generate your application, type:

```bash
jhipster
```

Answer the questions asked by the generator to create an application tailored to your needs. Those options are described in the [next section]().

Once the application is generated, you can launch it using Maven (./mvnw on Linux/MacOS/Windows PowerShell, mvnw on Windows Cmd).

The application will be available on http://localhost:8080

>* __Important__ if you want to have “live reload” of your JavaScript/TypeScript code, you will need run ```gulp``` (for JavaScript/AngularJS 1) or ```yarn start``` (for TypeScript/Angular 2+). You can go to the [Using JHipster in development](https://www.jhipster.tech/development/) page for more information.


### 2.8.2. Questions asked when generating an application

Some questions change depending on the previous choices you have made. For example, you won’t need to configure an Hibernate cache if you didn’t select an SQL database.

__Which type of application would you like to create?__

Your type of application depends on whether you wish to use a microservices architecture or not. A full explanation on microservices is [available here](https://www.jhipster.tech/microservices-architecture/), if unsure use the default “Monolithic application”.

You can either use:

* Monolithic application: this a classical, one-size-fits-all application. It’s easier * to use and develop, and is our recommended default.
* Microservice application: in a microservices architecture, this is one of the  services.
* Microservice gateway: in a microservices architecture, this is an edge server that  routes and secures requests.
* JHipster UAA server: in a microservices architecture, this is an OAuth2  authentication server that secures microservices. Refer to the [JHipster UAA  documentation](https://www.jhipster.tech/using-uaa/) for more information.

__What is the base name of your application?__

This is the name of your application.

__What is your default Java package name?__

Your Java application will use this as its root package. This value is stored by Yeoman so that the next time you run the generator the last value will become default. Of course you can override it by providing a new value.

__Do you want to use the JHipster Registry to configure, monitor and scale your application?__

The [JHipster Registry](https://www.jhipster.tech/jhipster-registry/) is an Open Source tool used to manage your application at runtime.
It is required when using a microservices architecture (this is why this question is only asked when generating a monolith).

__Which type of authentication would you like to use?__
Answers to this question depend on previous answers. For example, if you selected the [JHipster Registry](https://www.jhipster.tech/jhipster-registry/) above, you can only use JWT authentication.

Here are all the possible options:

* JWT authentication: use a [JSON Web Token (JWT)](https://jwt.io/), which is the default choice
* HTTP Session Authentication: the classical session-based authentication mechanism, like we are used to do in Java (this is how most people use [Spring Security](https://docs.spring.io/spring-security/site/index.html). You can use this option with Spring Social, which will enable you to use “social login” (such as Google, Facebook, Twitter): this is configured by Spring Boot’s support of Spring Social.
* OAuth 2.0 / OIDC Authentication: this uses an OpenID Connect server, like [Keycloak](http://www.keycloak.org/) or [Okta](https://www.okta.com/), which handles authentication outside of the application.
* Authentication with [JHipster UAA server](https://www.jhipster.tech/using-uaa/): this uses a JHipster UAA server that must be generated separately, and which is an OAuth2 server that handles authentication outside of the application.

You can find more information on our [securing your application](https://www.jhipster.tech/security/) page.

__Which type of database would you like to use?__

You can choose between:
* No database (only available when using a [microservice application](https://www.jhipster.tech/microservices-architecture/))
* An SQL database (H2, MySQL, MariaDB, PostgreSQL, MSSQL, Oracle), which you will access with Spring Data JPA
* [MongoDB](https://www.jhipster.tech/using-mongodb/)
* [Cassandra](https://www.jhipster.tech/using-cassandra/)
* [Couchbase](https://www.jhipster.tech/using-cassandra/)

__Which production database would you like to use?__

This is the database you will use with your “production” profile. To configure it, please modify your ```src/main/resources/config/application-prod.yml``` file.
If you want to use Oracle, you will need to [install the Oracle JDBC driver manually](https://www.jhipster.tech/using-oracle/).

__Which development database would you like to use?__

This is the database you will use with your “development” profile. You can either use:
* H2, running in-memory. This is the easiest way to use JHipster, but your data will be lost when you restart your server.
* H2, with its data stored on disk. This is currently in BETA test (and not working on Windows), but this would eventually be a better option than running in-memory, as you won’t lose your data upon application restart.
* The same database as the one you chose for production: it’s a bit more complex to set up, but it should be better in the end to work on the same database as the one you will use in production. This is also the best way to use liquibase-hibernate as described in [the development guide](https://www.jhipster.tech/development/).

To configure it, please modify your ```src/main/resources/config/application-dev.yml``` file.

__Do you want to use the Spring cache abstraction?__

The Spring cache abstraction allows to use different cache implementations: you can use [ehcache](http://ehcache.org/) (local cache), [Hazelcast](http://www.hazelcast.com/) (distributed cache), or [Infinispan](http://infinispan.org/) (another distributed cache). This can have a very positive impact on your application’s performance, and hence it is a recommended option.

__Do you want to use Hibernate 2nd level cache?__

This option will only be available if you selected to use an SQL database (as JHipster will use Spring Data JPA to access it) and selected a cache provider in the previous question.
[Hibernate](http://hibernate.org/) is the JPA provider used by JHipster, and it can use a cache provider to greatly improve its performance. As a result, we highly recommend you to use this option, and to tune your cache implementation according to your application’s needs.

__Would you like to use Maven or Gradle?__

You can build your generated Java application either with [Maven](https://maven.apache.org/) or [Gradle](http://www.gradle.org/). Maven is more stable and more mature. Gradle is more flexible, easier to extend, and more hype.

__Which other technologies would you like to use?__

This is a multi-select answer, to add one or several other technologies to the application. Available technologies are:

* Social login (Google, Facebook, Twitter)
This option is only available if you selected an SQL, MongoDB, or Couchbase database. It adds [Spring Social](https://projects.spring.io/spring-social/) support to JHipster, so end-users can log-in using their Google, Facebook or Twitter account.

* API first development using swagger-codegen
This option lets you do [API-first development](https://www.jhipster.tech/doing-api-first-development) for your application by integrating the [Swagger-Codegen](https://github.com/swagger-api/swagger-codegen) into the build.

* Search engine using ElasticSearch
[Elasticsearch](https://github.com/elastic/elasticsearch) will be configured using Spring Data Elasticsearch. You can find more information on our [Elasticsearch guide](https://www.jhipster.tech/using-elasticsearch/).

* Clustered HTTP sessions using Hazelcast
By default, JHipster uses a HTTP session only for storing [Spring Security](https://docs.spring.io/spring-security/site/index.html)’s authentication and authorisation information. Of course, you can choose to put more data in your HTTP sessions. Using HTTP sessions will cause issues if you are running in a cluster, especially if you don’t use a load balancer with “sticky sessions”. If you want to replicate your sessions inside your cluster, choose this option to have [Hazelcast](http://www.hazelcast.com/) configured.

* WebSockets using Spring Websocket
Websockets can be enabled using Spring Websocket. We also provide a complete sample to show you how to use the framework efficiently.

* Asynchronous messages using Apache Kafka
Use [Apache Kafka](https://www.jhipster.tech/using-kafka/) as a publish/subscribe message broker.

__Which Framework would you like to use for the client?__

The client-side framework to use.

You can either use:
* Angular version 4+
* AngularJS version 1.x (which will be deprecated in the future)

__Would you like to use the LibSass stylesheet preprocessor for your CSS?__

[Node-sass](https://www.npmjs.com/package/node-sass) a great solution to simplify designing CSS. To be used efficiently, you will need to run a [Gulp](http://www.gulpjs.com/) server, which will be configured automatically.

__Would you like to enable internationalization support?__

By default JHipster provides excellent internationalization support, both on the client side and on the server side. However, internationalization adds a little overhead, and is a little bit more complex to manage, so you can choose not to install this feature.

__Which testing frameworks would you like to use?__

By default JHipster provide Java unit/integration testing (using Spring’s JUnit support) and JavaScript unit testing (using Karma.js). As an option, you can also add support for:
* Performance tests using Gatling
* Behaviour tests using Cucumber
* Angular integration tests with Protractor

You can find more information on our [“Running tests” guide](https://www.jhipster.tech/running-tests/).

__Would you like to install other generators from the JHipster Marketplace?__

The [JHipster Marketplace](https://www.jhipster.tech/modules/marketplace/) is where you can install additional modules, written by third-party developers, to add non-official features to your project.

### 2.8.3. example

```bash
#创建工作目录
mkdir jhipsterAngular5

#进入目录
cd jhipsterAngular5

#生成前端Angular5项目架构
PS D:\workspace-study\workspace-spring-boot\jhipsterAngular5> jhipster
Using JHipster version installed globally
Running default command
Executing jhipster:app
Options:


        ██╗ ██╗   ██╗ ████████╗ ███████╗   ██████╗ ████████╗ ████████╗ ███████╗
        ██║ ██║   ██║ ╚══██╔══╝ ██╔═══██╗ ██╔════╝ ╚══██╔══╝ ██╔═════╝ ██╔═══██╗
        ██║ ████████║    ██║    ███████╔╝ ╚█████╗     ██║    ██████╗   ███████╔╝
  ██╗   ██║ ██╔═══██║    ██║    ██╔════╝   ╚═══██╗    ██║    ██╔═══╝   ██╔══██║
  ╚██████╔╝ ██║   ██║ ████████╗ ██║       ██████╔╝    ██║    ████████╗ ██║  ╚██╗
   ╚═════╝  ╚═╝   ╚═╝ ╚═══════╝ ╚═╝       ╚═════╝     ╚═╝    ╚═══════╝ ╚═╝   ╚═╝

                            http://www.jhipster.tech

Welcome to the JHipster Generator v4.14.1
 _______________________________________________________________________________________________________________

  If you find JHipster useful consider supporting our collective https://opencollective.com/generator-jhipster
  Documentation for creating an application: http://www.jhipster.tech/creating-an-app/
 _______________________________________________________________________________________________________________

Application files will be generated in folder: D:\workspace-study\workspace-spring-boot\jhipsterAngular5
? Which *type* of application would you like to create? (Use arrow keys)
> Monolithic application (recommended for simple projects)
  Microservice application
  Microservice gateway
  JHipster UAA server (for microservice OAuth2 authentication)
# 选择要生成的框架应用模型，默认为传统的一站式服务应用，选择Monolithic application

? Which *type* of application would you like to create? Monolithic application (recommended for simple projects)
? What is the base name of your application? (jhipsterAngular5)
# 填写应用名称，默认为jhipsterAngular5

? What is the base name of your application? jhipsterAngular5
? What is your default Java package name? (com.zaf.jhipster)
# 填写java默认的包名称：com.example.jhipsterAngular5

? What is your default Java package name? com.example.jhipsterangular5
? Do you want to use the JHipster Registry to configure, monitor and scale your application? (Use arrow keys)
> No
  Yes
# 是否使用JHipster Registry，默认为No，选择No

? Do you want to use the JHipster Registry to configure, monitor and scale your application? No
? Which *type* of authentication would you like to use? (Use arrow keys)
> JWT authentication (stateless, with a token)
  OAuth 2.0 / OIDC Authentication (stateful, works with Keycloak and Okta)
  HTTP Session Authentication (stateful, default Spring Security mechanism)
# 选择认证方式，默认为JWT authentication，这里选择HTTP Session Authentication

? Which *type* of authentication would you like to use? HTTP Session Authentication (stateful, default Spring Security mechanism)
? Which *type* of database would you like to use? (Use arrow keys)
> SQL (H2, MySQL, MariaDB, PostgreSQL, Oracle, MSSQL)
  MongoDB
  Cassandra
  [BETA] Couchbase
# 选择数据库类型，默认为SQL类型数据库，这里选择SQL数据库

? Which *type* of database would you like to use? SQL (H2, MySQL, MariaDB, PostgreSQL, Oracle, MSSQL)
? Which *production* database would you like to use? (Use arrow keys)
> MySQL
  MariaDB
  PostgreSQL
  Oracle (Please follow our documentation to use the Oracle proprietary driver)
  Microsoft SQL Server
# 生产环境选择使用那种类型的数据库，默认为MySQL，这里选择MySQL。

? Which *production* database would you like to use? MySQL
? Which *development* database would you like to use? (Use arrow keys)
> H2 with disk-based persistence
  H2 with in-memory persistence
  MySQL
# 开发环境选择使用哪种类型的数据库，默认为H2存硬盘，这里选择H2存硬盘

? Which *development* database would you like to use? H2 with disk-based persistence
? Do you want to use the Spring cache abstraction? (Use arrow keys)
> Yes, with the Ehcache implementation (local cache, for a single node)
  Yes, with the Hazelcast implementation (distributed cache, for multiple nodes)
  [BETA] Yes, with the Infinispan (hybrid cache, for multiple nodes)
  No (when using an SQL database, this will also disable the Hibernate L2 cache)
# 是否使用spring cache，默认使用Ehcache，这里选择Ehcache

? Do you want to use the Spring cache abstraction? Yes, with the Ehcache implementation (local cache, for a single node)
? Do you want to use Hibernate 2nd level cache? (Y/n)
# 是否使用Hibernate二级缓存，这里选择是一个，输入y

? Do you want to use Hibernate 2nd level cache? Yes
? Would you like to use Maven or Gradle for building the backend? (Use arrow keys)
> Maven
  Gradle
# 选择项目构建工具，默认Maven，这里选择Maven

? Would you like to use Maven or Gradle for building the backend? Maven
? Which other technologies would you like to use? (Press <space> to select, <a> to toggle all, <i> to inverse selection)
>( ) Social login (Google, Facebook, Twitter)
 ( ) Search engine using Elasticsearch
 ( ) WebSockets using Spring Websocket
 ( ) API first development using swagger-codegen
 ( ) Asynchronous messages using Apache Kafka
# 选择一些要使用的其它技术，这里一个也不选，直接回车跳过

? Which other technologies would you like to use?
? Which *Framework* would you like to use for the client? (Use arrow keys)
> Angular 5
  AngularJS 1.x
# 使用那个前端框架，默认为Angular 5,这里选择Angular 5。

? Which *Framework* would you like to use for the client? Angular 5
? Would you like to enable *SASS* support using the LibSass stylesheet preprocessor? (y/N)
# 是否使用css的SASS，这里不用，输入n

? Would you like to enable *SASS* support using the LibSass stylesheet preprocessor? No
? Would you like to enable internationalization support? (Y/n)
# 是否需要国际化支持，这里不用，输入n

? Would you like to enable internationalization support? No
? Besides JUnit and Karma, which testing frameworks would you like to use? (Press <space> to select, <a> to toggle all, <i> to inverse selection)
>( ) Gatling
 ( ) Cucumber
 ( ) Protractor
# 除了Junit和Karma，还需要那些测试框架，这里不需要，回车一个也不选。

? Besides JUnit and Karma, which testing frameworks would you like to use?
? Would you like to install other generators from the JHipster Marketplace? (y/N)
# 是否需要从JHipster Marketplace安装其他generators，这里不需要，输入n

# 至此，框架创建选择结束，开始创建jhipster框架。

# 等数分钟之后，出现下面的片段，表示创建jhipster框架完成。

Done in 38.27s.
If you find JHipster useful consider supporting our collective https://opencollective.com/generator-jhipster

Server application generated successfully.

Run your Spring Boot application:
 ./mvnw (mvnw if using Windows Command Prompt)

Client application generated successfully.

Start your Webpack development server with:
 yarn start

Congratulations, JHipster execution is complete!
Application successfully committed to Git.
```

### 2.8.4. Command-line options

You can also run JHipster with some optional command-line options. Reference for those options can be found by typing `jhipster app --help`.

Here are the options you can pass:

* `--help` - Print the generator’s options and usage
* `--skip-cache` - Do not remember prompt answers (Default: false)
* `--skip-git` - Do not add the generated project to Git automatically (Default: false)
* `--skip-install` - Do not automatically install dependencies (Default: false)
* `--skip-client` - Skip the client-side application generation, so you only have the * Spring Boot back-end code generated (Default: false). This is same as running server * sub-generator with `jhipster server`.
* `--skip-server` - Skip the server-side application generation, so you only have the * front-end code generated (Default: false). This is same as running client * sub-generator with `jhipster client`.
* `--skip-user-management` - Skip the user management generation, both on the back-end * and on the front-end (Default: false)
* `--i18n` - Disable or enable i18n when skipping client side generation, has no effect * otherwise (Default: true)
* `--auth` - Specify the authentication type when skipping server side generation, has no * effect otherwise but mandatory when using skip-server
* `--db` - Specify the database when skipping server side generation, has no effect * otherwise but mandatory when using skip-server
* `--with-entities` - Regenerate the existing entities if they were already generated * (using their configuration in the .jhipster folder) (Default: false)
* `--skip-checks` - Skip the check of the required tools (Default: false)
* `--jhi-prefix` - Add prefix before services, components and state/route names (Default: * jhi)
* `--npm` - Use NPM instead of Yarn (Default: false)
* `--experimental` - Enable experimental features. Please note that these features may be * unstable and may undergo breaking changes at any time

### 2.8.5. Tips

If you are an advanced user you can use our client and server sub-generators by running `jhipster client --[options]` and `jhipster server --[options]`. Run the above sub-generators with `--help` flag to view all the options that can be passed.

You can also use the Yeoman command-line options, like `--force` to automatically overwrite existing files. So if you want to regenerate your whole application, including its entities, you can run jhipster `--force --with-entities`.

## 2.9. 开发

### 2.9.1. 在eclipse中导入

&emsp;在eclipse中，选择创建的jhipster框架作为maven项目导入。

* 启动入口： com.example.jhipsterangular5.JhipsterAngular5App


### 2.9.2. 配置文件

* 基本配置
    * application.yml
* dev环境
    * application-dev.yml
* 生产环境
    * application-prod.yml

### 2.9.3. 编译

&emsp;进入项目目录，执行下面命令。

```bash
#编译
mvn -Pprod -DskipTests clean package
#查看编译包
ls target/*.war
#运行
java -jar .\target\jhipster-angular-5-0.0.1-SNAPSHOT.war
#传入参数
java -jar .\target\jhipster-angular-5-0.0.1-SNAPSHOT.war --spring.datasource.url="jdbc:mysql://172.19.64.100:3306/jhipstersampleapplication?useUnicode=true&characterEncoding=utf8&useSSL=false" --spring.datasource.password="rss123"
```

__编译中可能遇到的问题？__

* node下载慢或失败

在编译的过程中，要求node.exe 8.94版本，在编译的过程中到node的官网去下载，感觉mvn插件在这个过程中下载比较慢，可以看编译过程中的日志，手工到官网下载之后，然后改名覆盖。

```bash
#编译到这里会有较长停顿
[INFO] --- frontend-maven-plugin:1.6:install-node-and-yarn (install node and yarn) @ jhipster-angular-5 ---
[INFO] Installing node version v8.9.4
[INFO] Downloading https://nodejs.org/dist/v8.9.4/win-x64/node.exe to D:\workspace\maven\repository\com\github\eirslett\node\8.9.4\node-8.9.4-win-x64
.exe
[INFO] No proxies configured
[INFO] No proxy was configured, downloading directly
```

* yarn下载慢或失败

在编译的过程中，要求yarn v1.3.2版本，在编译的过程中到github下载，感觉mvn插件在这个过程中下载比较慢，可以看编译过程中的日志，手工到官网下载之后，然后改名覆盖。

```bash
#编译到这里会有较长停顿
[INFO] Installing Yarn version v1.3.2
[INFO] Downloading https://github.com/yarnpkg/yarn/releases/download/v1.3.2/yarn-v1.3.2.tar.gz to D:\workspace\maven\repository\com\github\eirslett\y
arn\1.3.2\yarn-1.3.2.tar.gz
[INFO] No proxies configured
[INFO] No proxy was configured, downloading directly
```

# 3. Local installation with Yarn using AngularJS 1.X

>* [Installing JHipster](https://www.jhipster.tech/installation/)

__Quick setup when using AngularJS 1.x__

1. Install Java 8 from [the Oracle website](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Install Node.js from the [Node.js website](https://nodejs.org/) (prefer an LTS version)
3. Install Yarn from [the Yarn website]
(https://yarnpkg.com/en/docs/install)
4. Install Bower: yarn `global add bower`
5. Install Gulp: yarn `global add gulp-cli`
6. If you want to use the JHipster Marketplace, install Yeoman: ```yarn global add yo```
7. Install JHipster: ```yarn global add generator-jhipster```

* 说明：使用AngularJS 1.X和Angular5区别不大，请参考[Local installation with Yarn using Angular](#YarnUsingAngular)。主要区别如下：
    * 前端需要多安装`bower`和`gulp-cli`
    * jhipster创建项目框架时，前端框架选择`AngularJS 1.x`

## 3.1. 安装bower

>* [bower官网](https://bower.io/)

```bash
#查看yarn global dir
yarn global dir

#全局安装bower
yarn global add bower
#查看bower
yarn global list

#验证
bower --version
```

## 3.2. 安装gulp-cli

>* [gulp官网](https://gulpjs.com/)

```bash
#查看yarn global dir
yarn global dir

#全局安装gulp-cli
yarn global add gulp-cli
#查看bower
yarn global list

#验证
gulp --version
```

## 3.3. Creating an application

创建jhipster框架时选择`AngularJS 1.x`

```bash
...

? Which other technologies would you like to use?
? Which *Framework* would you like to use for the client?
  Angular 5
> AngularJS 1.x
#创建jhipster框架时选择AngularJS 1.x

? Which *Framework* would you like to use for the client? AngularJS 1.x
? Would you like to enable *SASS* support using the LibSass stylesheet preprocessor? (y/N)

...

# 等数分钟之后，出现下面的片段，表示创建jhipster框架完成。

Server application generated successfully.

Run your Spring Boot application:
 ./mvnw (mvnw if using Windows Command Prompt)

Client application generated successfully.

Inject your front end dependencies into your source code:
 gulp inject

Generate the AngularJS constants:
 gulp ngconstant:dev

Or do all of the above:
 gulp install

Congratulations, JHipster execution is complete!
Application successfully committed to Git.
```

# 4. Configuring your IDE

>* [Configuring your IDE](https://www.jhipster.tech/configuring-ide/)

## 4.1. Configuring Eclipse with Maven

>* [Configuring Eclipse with Maven](https://www.jhipster.tech/configuring-ide-eclipse/)

### 4.1.1. Import your project as a Maven project

* Select File -> Import
* Choose “Existing Maven Projects”
* Select your project
* Click on “Finish”

### 4.1.2. Excluding generated static folders

__Exclude the ‘node_modules’ folder__

* Right-click on Project -> Properties -> Resource -> Resource Filters
* Select: Exclude all, Applies to folders, Name matches node_modules
* Press “Ok”

__Exclude ‘app’ from src/main/webapp__

* Right click on Project -> Properties -> Javascript -> Include path
* Click on the “source” tab and select your_project/src/main/webapp
* Select “Excluded: (None) -> Edit -> Add multiple
* Select `app` and click “Ok”
* The following folders should have been automatically excluded (if not, exclude them  manually):
    * `bower_components`
    * `node_modules/`

## 4.2. Configuring MapStruct plugins

>* 主要是安装Eclipse插件[m2e-apt](https://marketplace.eclipse.org/content/m2e-apt)和[MapStruct Eclipse Plugin](https://marketplace.eclipse.org/content/mapstruct-eclipse-plugin)

In case for the IDE correctly recognize the mapstruct code generator some more things needs to be done.

You should use the plugin [m2e-apt](https://marketplace.eclipse.org/content/m2e-apt). Installing the m2e-apt plugin, enable Eclipse to work along with mapstruct.

Also you can install the plugin [MapStruct Eclipse Plugin](https://marketplace.eclipse.org/content/mapstruct-eclipse-plugin) for help and tips from the IDE.

# 5. Tools

## 5.1. JDL

>* [JDL](https://www.jhipster.tech/jdl/)

### 5.1.1. JDL Sample

The Oracle “Human Resources” sample application has been translated into JDL, and is available [here](https://github.com/jhipster/jhipster-core/blob/master/lib/dsl/example.jh). The same application is loaded by default in [JDL-Studio](https://start.jhipster.tech/jdl-studio/) as well.

### 5.1.2. How to use it

If you want to use JHipster UML instead of the `import-jdl` sub-generator you need to install it by running `npm install -g jhipster-uml`.

You can then use JDL files to generate entities:

simply create a file with the extension ‘.jh’ or ‘.jdl’,
declare your entities and relationships or create and download the file with [JDL-Studio](https://start.jhipster.tech/jdl-studio/),
in your JHipster application’s root folder, run `jhipster import-jdl my_file.jdl` or `jhipster-uml my_file.jdl`.

## 5.2. JHipster IDE

>* [JHipster IDE](https://www.jhipster.tech/jhipster-ide/)

### 5.2.1. Eclipse installation

JHipster IDE is available in the [Eclipse Marketplace](https://marketplace.eclipse.org/content/jhipster-ide).
on the start page into your Eclipse or just open the marketplace dialog in Eclipse (Help > Eclipse Marketplace…) and search for JHipster.

## 5.3. JHipster-UML

>* [JHipster-UML](https://www.jhipster.tech/jhipster-uml/)

# 6. development

## 6.1. Using JHipster in development

>* [Using JHipster in development](https://www.jhipster.tech/development/)

## 6.2. Database updates

&emsp;开发环境，如果我们更改了jdl文件，需要执行`jhipster import-jdl .\src\main\model\uaollo.jdl`更新相关的文件。

```bash
# jhipster import-jdl .\src\main\model\uaollo.jdl

#注意不要覆盖变动的entity的liquibase的changelog的相关xml，其它可以直接覆盖。
The entity Server is being updated.

 conflict src\main\resources\config\liquibase\changelog\20180328082703_added_entity_Server.xml
? Overwrite src\main\resources\config\liquibase\changelog\20180328082703_added_entity_Server.xml? do not overwrite
     skip src\main\resources\config\liquibase\changelog\20180328082703_added_entity_Server.xml
identical src\main\resources\config\liquibase\changelog\20180328082703_added_entity_constraints_Server.xml
 conflict src\main\java\com\utstar\uapollo\domain\Server.java
? Overwrite src\main\java\com\utstar\uapollo\domain\Server.java? overwrite this and all others
    force src\main\java\com\utstar\uapollo\domain\Server.java

...

Entity generation completed
Congratulations, JHipster execution is complete!
```

这时，我们需要在项目的根目录执行先编译`mvn -DskipTests clean compile`，然后执行`mvn liquibase:diff`

```bash
# 编译dev
# mvn -DskipTests clean compile
#执行diff
# mvn liquibase:diff

...

#成功后，会生成一个diff change xml文件。
[INFO] Differences written to Change Log File, src/main/resources/config/liquibase/changelog/20180329092143_changelog.xml
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

将`src/main/resources/config/liquibase/changelog/20180329092143_changelog.xml`配置文件加到`liquibase`的`src/main/resources/config/liquibase/master.xml`配置文件。

```xml

...

<include file="config/liquibase/changelog/20180328082708_added_entity_constraints_NodeConfig.xml" relativeToChangelogFile="false"/>
<include file="config/liquibase/changelog/20180328082709_added_entity_constraints_GlobalConfig.xml" relativeToChangelogFile="false"/>
<include file="config/liquibase/changelog/20180329092143_changelog.xml" relativeToChangelogFile="false"/>
```

然后重启项目。
