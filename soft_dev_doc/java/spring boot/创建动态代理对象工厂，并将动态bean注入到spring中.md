# 前言
* 有个需求，需要在代码中，将ES数据中的数据过期删除。
* 后台连接ES，用的是Spring集成的JPA，多个doc都配置保留时间不等。

1. 因为需求这样，spring Elasticsearch JPA 又非常好用，并不想破坏它的封装.

```plantuml
database ISOP
rectangle Server
database Solr

Server --> Solr: 同步，并遍历删除过期数据
Server -up-> ISOP: 每3-4小时拉取一次数据同步
```
