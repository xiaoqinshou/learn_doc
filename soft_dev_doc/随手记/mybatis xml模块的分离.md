[toc]
## 引言
> 因为业务上产生了一个需求，要定义一套动态SQL 的语法来进行组装动态SQL语句。

### 解决办法
> 想到了直接引用 `Mybatis` XML的动态XML格式的 SQL语法，这样不仅兼容动态数据SQL，也兼容于其他多个 `MYSQL` 支持连接的数据库。

### 思路
* 将 `Mybatis` 中的，解析 XML SQL 的模块找出来。
* 调用 `Mybatis` 的解析模块将 XML SQL 语法进行解析，转换成实际SQL。

### 源码梳理
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

然后大概的就看了一下 每一个的 `getBoundSql` 方法，主要是生成一个 `BoundSql` 类。
```java
public class BoundSql {

  private final String sql;
  private final List<ParameterMapping> parameterMappings;
  private final Object parameterObject;
  private final Map<String, Object> additionalParameters;
  private final MetaObject metaParameters;

  public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.parameterObject = parameterObject;
    this.additionalParameters = new HashMap<>();
    this.metaParameters = configuration.newMetaObject(additionalParameters);
  }

  public String getSql() {
    return sql;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

  public Object getParameterObject() {
    return parameterObject;
  }

  public boolean hasAdditionalParameter(String name) {
    String paramName = new PropertyTokenizer(name).getName();
    return additionalParameters.containsKey(paramName);
  }

  public void setAdditionalParameter(String name, Object value) {
    metaParameters.setValue(name, value);
  }

  public Object getAdditionalParameter(String name) {
    return metaParameters.getValue(name);
  }
}
```
&emsp;其中可以看出这里面存储了一条解析过后的 `sql`。一个参数实际值的容器 `parameterMappings`, 一个参数对象 `parameterObject`, 一个 `configuration` 中未包含的映射参数 `additionalParameters`, 并将这个附加参数映射进 `configuration` 中进行管理。
&emsp; 断点看了一下附加参数 `additionalParameters` ,里面就是一个 `_databaseId` 记录该条数据的唯一值吧，然后 `_parameter` 参数放对应的实际参数键值对。
测试代码：
```java
public static void main(String[] args) {
        Configuration configuration = new Configuration();
        String xmlSql = "<script>\n" +
                "select * from test" +
                "\n" +
                "<where>\n" +
                "\n" +
                "<if test=\"code != null and code != '' \"> AND code = #{code} </if>\n" +
                "\n" +
                "<if test=\"showStatus != null and showStatus != '' \"> AND showstatus = #{showStatus} </if>\n" +
                "\n" +
                "</where>\n" +
                "limit #{offset},#{pageSize} \n" +
                "</script>";
        Map map = new HashMap<>();
        map.put("offset",1);
        map.put("showStatus","asd");
        map.put("pageSize",10);

        XMLLanguageDriver xmlLanguageDriver = new XMLLanguageDriver();
        SqlSource sqlSource = xmlLanguageDriver.createSqlSource(configuration, xmlSql, Map.class);
        BoundSql boundSql = sqlSource.getBoundSql(map);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();
        System.out.println("-----------"+sql);
        for(ParameterMapping p : parameterMappings ) {
            sql = sql.replaceFirst("\\?", map.get(p.getProperty()) + "");
        }
        System.out.println("-----------" +sql);
    }
```
直接获取SQL，再将问好顺序替换既能得到解析过后的SQL;

由于业务需求目前能简单的实现，性能方面没时间去调整只能暂且搁置。
Mybatis 的源码就暂时看到这里吧。