# Swagger-3.0
&emsp;[官方版本跳转地址](https://github.com/springfox/springfox)

## 前言
&emsp;总的来说变化还是比较大的，之前的swagger2的配置都无法使用。

## 简介
官方在3.0版本上主要做了以下大改动(以下主要是针对`spring boot`程序，其他程序还访问官网阅读文档)：
* 移除了2.x版本的冲突版本，移除了 `guava` 等第三方库
* 移除了 `@EnableSwagger2`
* 新增了 `springfox-boot-starter`
* 新增了 `@EnableOpenApi`
* 2.X 与 3.0 不兼容
## 引用
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 使用
&emsp;应用主类增加注解@EnableOpenApi，启动项目，访问地址：http://localhost:8088/swagger-ui/index.html，注意2.x版本中访问的地址的为http://localhost:8088/swagger-ui.html
