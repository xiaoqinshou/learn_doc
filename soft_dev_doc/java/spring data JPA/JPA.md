## [Spring Data JPA](https://spring.io/projects/spring-data-jpa) 初步使用
#### 概述
>Spring Data JPA, part of the larger Spring Data family, makes it easy to easily implement JPA based repositories. This module deals with enhanced support for JPA based data access layers. It makes it easier to build Spring-powered applications that use data access technologies.

> Implementing a data access layer of an application has been cumbersome for quite a while. Too much boilerplate code has to be written to execute simple queries as well as perform pagination, and auditing. Spring Data JPA aims to significantly improve the implementation of data access layers by reducing the effort to the amount that’s actually needed. As a developer you write your repository interfaces, including custom finder methods, and Spring will provide the implementation automatically.

#### 引入
```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
			<version>your spring boot version</version>
		</dependency>
```
#### 基础配置
```yml
spring:
  jpa:
    show-sql: true  //打印日志
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl //使用@Column 注解上的字段名
    properties:
      hibernate:
        dialect: org.hibernate.dialect.Oracle10gDialect //Oracle 方言化SQL。
```

#### 实体
&emsp;因为 jpa 内置实现使用 Hibernate 实现所以实体类，与Hibernate相同。注解也基本通用，不再赘述。
&emsp;JPA 实体中不允许主键为空，所以实体必须要有主键。
&emsp;JPA 联合主键形式：
```java
@Entity
@IdClass(TestId.class)
@Table(name="test_table")
public class testTable implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * test_id1
     */
    @Id
    @Column(name = "test_id1")
    private Long testId1;

    /**
     * test_id2
     */
    @Id
    @Column(name = "test_id2")
    private String testId2;
}

public class ShelltimeId implements Serializable {

    private Long calcDay;

    private String shell;

}
```
#### dao层
&emsp;dao层只需要继承JpaRepository<T,Interger>就能实现基础的对数据的CRUD操作。

#### 查询排序
&emsp;只需在构造 Pageable 时注入排序 Sort类进去就行。
```java
//排序分页构造
         Sort sort = new Sort(Sort.Direction.DESC, " Your sorter item name");
         PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        }
```

#### 多种查询方式。
##### Example 查询器
&emsp;使用基础的 Example 查询器，可以最快速的完成支持所有参数的筛选功能,像这样的代码：
```java
@Test
public void contextLoads() {
    User user = new User();
    user.setUsername("y");
    user.setAddress("sh");
    user.setPassword("admin");
    ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.startsWith())//模糊查询匹配开头，即{username}%
            .withMatcher("address" ,ExampleMatcher.GenericPropertyMatchers.contains())//全部模糊查询，即%{address}%
            .withIgnorePaths("password")//忽略字段，即不管password是什么值都不加入查询条件
            .withIgnorePaths("id");  //忽略属性：是否关注。因为是基本类型，需要忽略掉
    Example<User> example = Example.of(user ,matcher);
    List<User> list = userRepository.findAll(example);
    System.out.println(list);
}
```
Example会将为null的字段自动过滤掉，不会作为筛选条件，ExampleMatch是为了支持一些稍微复杂一些的查询，比如如果有int类型的id就需要用.withIgnorePaths()忽略掉，因为Int类型默认为0，而不是Null。
如果只是简单的字符串匹配的话，可以直接用：

	Example<User> example = Example.of(user);

来构造Example。
但是使用这种方式会遇到一个问题，它没有办法实现 id > startId && id < endId这样的操作

##### 自定义SQL语句
&emsp;使用@Query注解，这种方式可以直接在Repository里面写sql，但是这种方式的问题就是太麻烦了，而且非常容易出错，扩展性也很差，还不如直接用mybatis。
```java
@Repository
public interface ShelltimeRepository extends JpaRepository<testTable,Integer>, JpaSpecificationExecutor {

	/**
	* nativeQuery = true 指明是原生SQL语法
	*/
    @Query(value = "SELECT COUNT(0) FROM testTable WHERE CALC_DAY > ?1 AND CALC_DAY < ?2", nativeQuery = true)
    int findByTimeCount(Long begin, Long end);

	/**
	* 加上 Pageable 说明需要分页  不需要删除即可
	*/
	@Query(value = "select * from testTable where username like concat('%',?1,'%') and password = ?2")
    Page<User> findByUsernameLikeAndPassword(String username, Integer password, Pageable pageable);

	@Query(nativeQuery = true,
           value = "select * from z_cashier_data" +
                   "where abstract_code = ?1 " +
                   "and time BETWEEN ?2 and ?3")
    Page<CashierData> findAllabcd(String code, Date start, Date end, Pageable pageable);//函数名随意

//等同于
	Page<CashierData> findAllByAbstractCodeAndTimeBetween(String code, Date start, Date end, Pageable pageable);

 @Modifying
    @Query(nativeQuery = true, value = "DELETE from z_cashier_data where id in (:ids)")
    void deleteIn(@Param("ids") List<Long> ids);//函数名随意

//等同于
	void deleteByIdIn(List<Long> ids);

}
```

##### Criteria 查询
&emsp;使用 Criteria 查询，这是 springdata 中最强的使用方式了，所有的场景应该都能完成。
&emsp;首先 Repository 要继承 JpaSpecificationExecutor；
```java
@Repository
public interface testRepository extends JpaRepository<testEntity,Integer>, JpaSpecificationExecutor {

}
```
然后就是构造动态查询:
```java
public Page<UpgradeScheduleView> getPageUpgradeScheduleView(UpgradeViewSelector upgradeViewSelector, int pageNumber, int pageSize) {
        Specification querySpeci = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = Lists.newArrayList();
                if(!StringUtils.isEmpty(upgradeViewSelector.getTaskName())) {
                    predicates.add(criteriaBuilder
                            .like(root.get("taskName"), "%" + upgradeViewSelector.getTaskName() + "%"));
                }
                if(!StringUtils.isEmpty(upgradeViewSelector.getTboxId())){
                    predicates.add(criteriaBuilder
                            .like(root.get("tboxId"), "%" + upgradeViewSelector.getTboxId() + "%"));
                }
                if(null != upgradeViewSelector.getOver()){
                    predicates.add(criteriaBuilder.equal(root.get("over"), upgradeViewSelector.getOver()));
                }
                if(null != upgradeViewSelector.getFlag()){
                    predicates.add(criteriaBuilder.equal(root.get("flag"), upgradeViewSelector.getFlag()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize);
        return upgradeScheduleViewRepository.findAll(querySpeci,pageRequest);
    }
```
改进一下,直接添加接口,懒得多添加一层。
```java
public Page<Item> findByConditions(String name, Integer price, Integer stock, Pageable page) {
     Page<Item> page = itemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            //name模糊查询 ,like语句
            if (name != null) {
                predicatesList.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(
                                        root.get("itemName"), "%" + name + "%")));
            }
            // itemPrice 小于等于 <= 语句
            if (price != null) {
                predicatesList.add(
                        criteriaBuilder.and(
                                criteriaBuilder.le(
                                        root.get("itemName"), price)));
            }
            //itemStock 大于等于 >= 语句
            if (stock != null) {
                predicatesList.add(
                        criteriaBuilder.and(
                                criteriaBuilder.ge(
                                        root.get("itemName"), stock)));
            }
            return criteriaBuilder.and(
                    predicatesList.toArray(new Predicate[predicatesList.size()]));
        }, page);
    return page;
}
```

> 引用于CSDN [billluffy](https://blog.csdn.net/billluffy/article/details/83147538) , [张念磊](https://blog.csdn.net/Mr_Zhang____/article/details/83963573)， 简书 [龙历旗](https://www.jianshu.com/p/0939cec7e207) 的博客。