# maven-compiler-plugin
> maven的打包辅助插件
* 具体详细参数查询[官方文档](http://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#forceJavacCompilerUse)
```xml
<plugin>
    <!-- 指定maven编译的jdk版本,如果不指定,maven3默认用jdk 1.5 maven2默认用jdk1.3 -->
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.6.1</version>
    <configuration>
        <!-- 一般而言，target与source是保持一致的，但是，有时候为了让程序能在其他版本的jdk中运行(对于低版本目标jdk，源代码中不能使用低版本jdk中不支持的语法)，会存在target不同于source的情况 -->
        <!-- 源代码使用的JDK版本 -->
        <source>1.8</source>
        <!-- 需要生成的目标class文件的编译版本 -->
        <target>1.8</target>
        <!-- 字符集编码 -->
        <encoding>UTF-8</encoding>
        <!-- 跳过测试 -->
        <skipTests>true</skipTests>
        <!-- 是否展示详细信息 -->
        <verbose>true</verbose>
        <!-- 是否显示编译告警信息 -->
        <showWarnings>true</showWarnings>
        <fork>true</fork><!-- 要使compilerVersion标签生效，还需要将fork设为true，用于明确表示编译版本配置的可用 -->
        <executable><!-- path-to-javac --></executable><!-- 使用指定的javac命令，例如：<executable>${JAVA_1_4_HOME}/bin/javac</executable> -->
        <compilerVersion>1.3</compilerVersion><!-- 指定插件将使用的编译器的版本 -->
        <meminitial>128m</meminitial><!-- 编译器使用的初始内存 -->
        <maxmem>512m</maxmem><!-- 编译器使用的最大内存 -->
        <compilerArgument>-verbose -bootclasspath ${java.home}\lib\rt.jar</compilerArgument><!-- 这个选项用来传递编译器自身不包含但是却支持的参数选项 -->
    </configuration>
</plugin>
```
