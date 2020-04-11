# springboot 整合 flyway
[toc]
## maven 依赖
&emsp; springboot 内置了 [flyway](https://flywaydb.org) 的依赖，如果用的是springboot的话可以不用指定版本号，直接依赖于springboot中对 [flyway](https://flywaydb.org) 的依赖即可。
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <!-- <version>5.0.3</version> -->
</dependency>
```

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <!-- <version>5.0.3</version> -->
</plugin>
```

## 增加SQL脚本
名称格式：V + 版本号 + 双下划线 + 描述 + 单下划线 + 结束符。
例如：V1__INIT_DATABASE.sql

按照SQL脚本名称格式一步一步加即可。flyway会根据需要迁移的版本自动从第一个版本的脚步陆续迁移。