# Gradle使用
## 前言 
* [官方文档](https://docs.gradle.org/current/samples/sample_building_java_applications_multi_project.html)

* [依赖说明](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_plugin_and_dependency_management)

* [抽离依赖](https://docs.gradle.org/current/userguide/platforms.html)

* 了解了一段时间后, 发现这玩意儿真的复杂, 灵活且确实强大, 用于构建及打包过程,依赖优化等, 功能预计比前端的nodeJs打包还要复杂一些, 毕竟有各种继承依赖等, 而且还有各种灵活的
* 本来用Spring boot自动构建的工程, 嫌太多太杂东西了. 换
* 之后尝试用 idea 创建gradle工程, 也均以失败告终, 刚玩gradle 看不太懂, 感觉和 nodejs 差不多
但是 nodejs 我还能知道入口起点在哪里,还会配置编译器之类的东西, 这玩意儿在idea上找了几年了, 愣是没找到入口, Spring boot 创建的项目还能有启动按钮列表呢, idea生成的,是连build啥的都没有, 由于没有全局安装, 一些命令也用不了,  还是老老实实装一个不投机从命令开始熟悉吧. 

## 安装gradle
* 折腾了小半天了, 还是自己安装吧, 不用idea的自带的了. 
* 偷个懒用 `brew` 安装, 环境变量也懒得配了
```shell
brew update && brew install gradle
```

## 安装jdk
* 这里安装 gradle 的时候自动帮我安装了openjdk 18
* 配置环境变量, 和Linux 系统差不多, 在用户根目录下的 .bash_profile 文件新增path
```shell
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home

PATH=$JAVA_HOME/bin:$PATH:.

# 刷新 
source .bash_profile

# 查看
echo $JAVA_HOME
```

## 创建项目
```shell
% mkdir hrd
% cd hrd
$ gradle init

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 2

Split functionality across multiple subprojects?:
   1: no - only one application project
   2: yes - application and library projects
Enter selection (default: no - only one application project) [1..2] 2

Select implementation language:
  1: C++
  2: Groovy
  3: Java
  4: Kotlin
  5: Scala
  6: Swift
Enter selection (default: Java) [1..6] 3

Select build script DSL:
  1: Groovy
  2: Kotlin
Enter selection (default: Groovy) [1..2] 1

Select test framework:
  1: JUnit 4
  2: TestNG
  3: Spock
  4: JUnit Jupiter
Enter selection (default: JUnit 4) [1..4]

Project name (default: demo):
Source package (default: demo):


BUILD SUCCESSFUL
2 actionable tasks: 2 executed
```

## 文件结构
```makefile
├── gradle  1
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew 2
├── gradlew.bat 2
├── settings.gradle 3
├── buildSrc
│   ├── build.gradle 4
│   └── src
│       └── main
│           └── groovy 5
│               ├── demo.java-application-conventions.gradle
│               ├── demo.java-common-conventions.gradle
│               └── demo.java-library-conventions.gradle
├── app
│   ├── build.gradle 6
│   └── src
│       ├── main 7
│       │   └── java
│       │       └── demo
│       │           └── app
│       │               ├── App.java
│       │               └── MessageUtils.java
│       └── test 8
│           └── java
│               └── demo
│                   └── app
│                       └── MessageUtilsTest.java
├── list 
│   ├── build.gradle 6
│   └── src
│       ├── main 7
│       │   └── java
│       │       └── demo
│       │           └── list
│       │               └── LinkedList.java
│       └── test 8
│           └── java
│               └── demo
│                   └── list
│                       └── LinkedListTest.java
└── utilities
    ├── build.gradle 6
    └── src
        └── main 7
            └── java
                └── demo
                    └── utilities
                        ├── JoinUtils.java
                        ├── SplitUtils.java
                        └── StringUtils.java
```
* 默认生成的项目 
1. Generated folder for wrapper files
2. Gradle wrapper start scripts 对应不同系统下的命令
3. Settings file to define build name and subprojects
4. Build script of buildSrc to configure dependencies of the build logic
5. Source folder for convention plugins written in Groovy or Kotlin DSL
6. Build script of the three subprojects - app, list and utilities
7. Java source folders in each of the subprojects
8. Java test source folders in the subprojects

## 引入依赖
* 前言中有文档, 跳到官网去看基础引入方式
* 不说踩坑历程了, 直接上结果, 为了满足类似maven的功能引用springboot的bom清单.
```
// 引入spring的依赖注入插件
plugins {
    id 'io.spring.dependency-management' version '1.0.11.RELEASE' 
}

// 配置spring-boot的bom
dependencyManagement {
    imports {
        mavenBom "org.springframework.boot:spring-boot-dependencies:${YourBomVersion}"
    }
}

// 然后就可以正常不带版本的进行依赖Springboot bom中的包了
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 依赖方式
```text
api
引入需要的依赖包，使用此关键字引用的依赖可以逐级传递到各个依赖的模块中。作用范围大。

implementation
仅实现依赖项。

compileOnly
仅编译时依赖项，不在运行时使用。

compileClasspath延伸compileOnly, implementation
编译类路径，编译源码时使用。由任务使用compileJava。

annotationProcessor
编译期间使用的注释处理器。

runtimeOnly
仅运行时依赖项。

runtimeClasspath延伸runtimeOnly, implementation
运行时类路径包含实现的元素，以及仅运行时的元素。

testImplementation延伸implementation
仅实现测试的依赖项。

testCompileOnly
仅用于编译测试的附加依赖项，不在运行时使用。

testCompileClasspath延伸testCompileOnly, testImplementation
测试编译类路径，在编译测试源时使用。由任务使用compileTestJava。

testRuntimeOnly延伸runtimeOnly
运行时仅依赖于运行测试。

testRuntimeClasspath延伸testRuntimeOnly, testImplementation
用于运行测试的运行时类路径。由任务使用test。

archives
该项目产生的工件（例如罐子）。Gradle 使用它来确定构建时要执行的“默认”任务。

default延伸runtimeElements
此项目的项目依赖项使用的默认配置。包含此项目在运行时所需的工件和依赖项。
```

### 引入lombok
* 仅编译依赖, 以及注解处理
* [原文地址](https://projectlombok.org/setup/gradle)
```groovy
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
}
```