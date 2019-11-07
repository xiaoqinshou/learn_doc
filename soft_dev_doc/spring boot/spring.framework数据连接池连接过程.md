

### spring boot 内置数据源连接池初始化
首先要弄多数据源，必须要足够了解，`spring boot`中它内置的数据源连接池是如何进行创建，初始化，管理以及回收的。

---
因为我这里看的是 spring boot 2.1.9 的源码，所以源码中用到了一些新注解，所以要先了解一些新注解的用途。
`@Conditional(TestCondition.class)`
这句代码可以标注在类上面，表示该类下面的所有@Bean都会启用配置，也可以标注在方法上面，只是对该方法启用配置。
`@ConditionalOnBean`（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
`@ConditionalOnClass`（该注解的参数对应的类必须存在，否则不解析该注解修饰的配置类）
`@ConditionalOnExpression`（当表达式为true的时候，才会实例化一个Bean）
`@ConditionalOnMissingBean`（该注解表示，如果存在它修饰的类的bean，则不需要再创建这个bean；可以给该注解传入参数例如@ConditionOnMissingBean(name = "example")，这个表示如果name为“example”的bean存在，这该注解修饰的代码块不执行）
`@ConditionalOnMissingClass`（某个class类路径上不存在的时候，才会实例化一个Bean）
`@ConditionalOnNotWebApplication`（不是web应用）
这句代码可以标注在类上面，表示该类下面的所有@Bean都会启用配置，也可以标注在方法上面，只是对该方法启用配置。
</br>
以上引用于[一弦一仙](https://www.cnblogs.com/yixianyixian/p/7346894.html)的博客
***

ConditionalOnProperty注解的一些用途：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {

    String[] value() default {}; //数组，获取对应property名称的值，与name不可同时使用  
  
    String prefix() default "";//property名称的前缀，可有可无  
  
    String[] name() default {};//数组，property完整名称或部分名称（可与prefix组合使用，组成完整的property名称），与value不可同时使用  
  
    String havingValue() default "";//可与name组合使用，比较获取到的属性值与havingValue给定的值是否相同，相同才加载配置  
  
    boolean matchIfMissing() default false;//缺少该property时是否可以加载。如果为true，没有该property也会正常加载；反之报错  
  
    boolean relaxedNames() default true;//是否可以松散匹配，至今不知道怎么使用的  
} 
}
```
</br>

以上引用于[Fredia_Wang](https://www.jianshu.com/p/68a75c093023)的简书。


然后打开`org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration`
中可以看到，内置初始化支持`Tomcat Pool DataSource`,`Hikari DataSource` 
,`DBCP DataSource` 三种数据库连接池，且都互斥。

#### Hikari 连接池默认加载过程
由于表达能力不咋地，这里一边贴出源码一边解释。
这里以 Hikari 连接池为例，来理清，spring boot 初始化数据库连接池的步骤。
首先是加载`Bean`的入口:
```java
abstract class DataSourceConfiguration {

	@SuppressWarnings("unchecked")
	protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
		return (T) properties.initializeDataSourceBuilder().type(type).build();
	}

        @Configuration
        @ConditionalOnClass(HikariDataSource.class)
        @ConditionalOnMissingBean(DataSource.class)
        @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource",
			matchIfMissing = true)
        static class Hikari {
	    @Bean
	    @ConfigurationProperties(prefix = "spring.datasource.hikari")
	    public HikariDataSource dataSource(DataSourcePropertiesproperties{
		    HikariDataSource dataSource = createDataSource(properties,  HikariDataSource.class);
		    if (StringUtils.hasText(properties.getName())) {
			    dataSource.setPoolName(properties.getName());
		    }
		    return dataSource;
	        }
        }
}
```
这里它是定义了一个内部配置类，并且指定当框架内必须有`KikariDataSource`类且`DataSource`这个类型的`Bean`不存在，才会加载当前配置。
然后是获取配置文件中的配置，进行对应的数据库类型实例化创建，并返回一个`DataSourceBuilder`的包装类，该类在`org.springframework.boot.jdbc.DataSourceBuilder`，主要用于维护用于连接数据库连接池的必要属性，用到的具体的连接池，一些必要的连接数据库的Ip,端口,用户名等必要属性（如果没配置数据库驱动的话，其中还会尝试解析配置的URL，去解析出用的是什么数据库，自动配上驱动相关方法`org.springframework.boot.jdbc.DataSourceBuilder.maybeGetDriverClassName`）。
以下便是实例化连接池的具体代码：
```java
    public T build() {
		Class<? extends DataSource> type = getType();
		DataSource result = BeanUtils.instantiateClass(type);
		maybeGetDriverClassName();
		bind(result);
		return (T) result;
	}

    private void bind(DataSource result) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(this.properties);
		ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
		aliases.addAliases("url", "jdbc-url");
		aliases.addAliases("username", "user");
		Binder binder = new Binder(source.withAliases(aliases));
		binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
	}
```
获取当前包装类中所包装的类型，然后通过反射实例化连接池。
尝试根据URL获取对应的数据库连接驱动。
根据当前数据源创建一个新的数据源副本，并且更改名称，让url与jdbc-url都能获取到配置中的url地址，username和user同理。
并将两个副本绑在一起可以互相转换，作用就是无论访问url还是jdbc-url,username还是user都能取到值。
回到最初的入口返回给Bean进行管理。至此HikariData连接池初始化成功。
