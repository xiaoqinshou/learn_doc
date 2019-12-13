[toc]
## 引言
> 因为业务上产生了一个需求，要定义一套动态SQL 的语法来进行组装动态SQL语句。

### 解决办法
> 想到了直接引用 `Mybatis` XML的动态XML格式的 SQL语法，这样不仅兼容动态数据SQL，也兼容于其他多个 `MYSQL` 支持连接的数据库。

### 思路
* 将 `Mybatis` 中的，解析 XML SQL 的模块找出来。
* 调用 `Mybatis` 的解析模块将 XML SQL 语法进行解析，转换成实际SQL。

### 源码解析
&emsp; Mybatis 具体运作流程我不知道，虽然百度了，但是看得各种图，各种解释，只能说大体上的流程懂个大概，具体稍微细节点的流程还是懵懵懂懂，似懂非懂；在细一点基本上不懂了。
&emsp; 虽然天才有天才的方法，但是蠢材，有蠢材的方式。
```source
+ ibatis
    - annotations
    - binding
    - builder
    - cache
    - cursor
    - datasource
    - exceptions
    - executor
    - io
    - javassist
    - jdbc
    - lang
    - logging
    - mapping
    - ognl
    - parsing
    - plugin
    - reflection
    - scripting
    - session
    - transaction
    - type
```
从目录中一眼就看中了 `mapping` 文件夹，然后看中了里面的 `SqlSource` 接口文件，因为我只需要知道 XML 解析的那一块具体细节就行，其他的地方不是我这种小菜鸡能窥探的，所以理所当然的就决定将它当做是入口进行代码扩散追溯了。

首先来理一理这个 `SqlSource` 是啥玩意儿,它有以下四个实现类。

```plantuml
interface SqlSource
class DynamicSqlSource implements SqlSource
class ProviderSqlSource implements SqlSource
class RawSqlSource implements SqlSource
class StaticSqlSource implements SqlSource
```
其实这4个实现类我都看了，每个里面都有 `String sql` 作为参数，作为一个小菜鸡立马就判断这就是生成后的实际 SQL 语句。所以理所当然的把追溯重点放在了 `DynamicSqlSource` 类中。

