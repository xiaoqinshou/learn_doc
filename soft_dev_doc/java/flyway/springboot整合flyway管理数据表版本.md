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
路径: src/main/resources/db/migration
名称格式：V + 版本号 + 双下划线 + 描述 + 单下划线 + 结束符。
例如：V1__INIT_DATABASE.sql

按照SQL脚本名称格式一步一步加即可。flyway会根据需要迁移的版本自动从第一个版本的脚步陆续迁移。

## 概念
* Schema History Table
    * Flyway 对数据库进行版本控制的方式，是在指定数据库中创建一张表，即 Schema History Table（默认为 flyway_schema_history），记录由 Flyway 所执行的 sql 脚本状态。
* Migration
    * 在 Flyway 中，所有对数据库的变动，均称为 migration(译：迁移)，migration 可以是 SQL 文件，也可以是 Java 类。
    * 默认的查找 migration 的路径是 classpath:db/migration，也就是说对应 SQL 文件可以放置在 src/main/resources/db/migration 下，Java 类可以放置在 src/main/java/db/migration 下。
    * Migration 可以是仅执行一次的（versioned），也可是重复执行的（repeatable）。

## 配置配置项
```yml
spring.flyway.enabled：true #是否开启 flyway，默认就是开启的
spring.flyway.encoding：utf-8 #flyway 字符编码
spring.flyway.locations：your_path #sql 脚本的目录，默认是classpath:db/migration，如果有多个用 , 隔开
spring.flyway.clean-disabled：true #表示是否要清除已有库下的表，如果执行的脚本是 V1__xxx.sql，那么会先清除已有库下的表，然后再执行脚本，这在开发环境下还挺方便，但是在生产环境下就要命了，而且它默认就是要清除，生产环境一定要自己配置设置为 true。
spring.flyway.table：new_table_name #配置数据库信息表的名称，默认是flyway_schema_history。
```

## 其他问题
* SQL 报错
    * 通过 Spring Boot 自动执行 migration 时要注意，一旦 migration 执行失败，应用启动会终止。出现 migration 执行失败时，需要将 Schema History Table 表中的失败记录处理掉，才能再次执行 migration，否则应用会一直无法启动。

* out-of-order
    * 多人开发时，可能会出现 A 写了 V1 脚本，B 写了 V2 脚本，B 的代码先合并进去了，V2 脚本先执行了，此时 A 的 V1 脚本受版本号只能增加的要求不能再执行。
    * 这种情况可以通过将 spring.flyway.out-of-order 设置为 true 来暂时取消这个限制，不过还是强烈建议 A 将 V1 脚本版本号改为 V3。