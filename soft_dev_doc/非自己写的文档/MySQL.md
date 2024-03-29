# MySQL

## 一条sql的执行过程

![image-20200909193552073](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200909193552073.png)

​																								MySQL的逻辑架构图

## redo log(重做日志)

InnoDB引擎特有的日志

redo log相当于酒店老板的赊账的粉板，如果生意红火的时候每次都写到账本里去，太花费时间了，酒店老板会先写在粉板上，等不忙的时候再写到账本里去

同样，在MySQL里也有这个问题，如果每一次的更新操作都需要写进磁盘，然后磁盘也要找到 对应的那条记录，然后再更新，整个过程IO成本、查找成本都很高。为了解决这个问 题，MySQL的设计者就用了类似酒店掌柜粉板的思路来提升更新效率。 

而粉板和账本配合的整个过程，其实就是MySQL里经常说到的WAL技术，WAL的全称是WriteAhead Logging，它的关键点就是先写日志，再写磁盘，也就是先写粉板，等不忙的时候再写账 本。 

具体来说，当有一条记录需要更新的时候，InnoDB引擎就会先把记录写到redo log（粉板）里 面，并更新内存，这个时候更新就算完成了。同时，InnoDB引擎会在适当的时候，将这个操作 记录更新到磁盘里面，而这个更新往往是在系统比较空闲的时候做，这就像打烊以后掌柜做的事。

如果今天赊账的不多，掌柜可以等打烊后再整理。但如果某天赊账的特别多，粉板写满了，又怎 么办呢？这个时候掌柜只好放下手中的活儿，把粉板中的一部分赊账记录更新到账本中，然后把 这些记录从粉板上擦掉，为记新账腾出空间。 

与此类似，InnoDB的redo log是固定大小的，比如可以配置为一组4个文件，每个文件的大小是 1GB，那么这块“粉板”总共就可以记录4GB的操作。从头开始写，写到末尾就又回到开头循环 写，如下面这个图所示。

![image-20200909200417331](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200909200417331.png)

## binlog（归档日志）

server层日志

这两种日志有以下三点不同。 

1. redo log是InnoDB引擎特有的；binlog是MySQL的Server层实现的，所有引擎都可以使用。 

2. redo log是物理日志，记录的是“在某个数据页上做了什么修改”；binlog是逻辑日志，记录的 是这个语句的原始逻辑，比如“给ID=2这一行的c字段加1 ”。 

3. redo log是循环写的，空间固定会用完；binlog是可以追加写入的。“追加写”是指binlog文件 写到一定大小后会切换到下一个，并不会覆盖以前的日志。

   更新流程如下：

![image-20200909200857196](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200909200857196.png)

## undo log（回滚日志）

## 事务隔离

## 索引

一句话简单来说，索引的出现其实就是为了提高数据查询的效率，就像书的目录一样。一本500 页的书，如果你想快速找到其中的某一个知识点，在不借助目录的情况下，那我估计你可得找一 会儿。同样，对于数据库的表而言，索引其实就是它的“目录”。

哈希索引（参考hashmap）

适用于等值查询的场景，比如Memcached及其他一些NoSQL引 擎。范围查询进行大量的扫描，效果较差。

有序数组

等值查询和范围查询性能都比较优秀，更新数据往中间插入成本太高

InonoDB的索引模型

在InnoDB中，表都是根据主键顺序以索引的形式存放的，这种存储方式的表称为索引组织表。 又因为前面我们提到的，InnoDB使用了B+树索引模型，所以数据都是存储在B+树中的。 每一个索引在InnoDB里面对应一棵B+树

如下建表语句：

```sql
mysql> create table T(
id int primary key,
k int not null,
name varchar(16),
index (k))engine=InnoDB;
```

对应的索引

![image-20200911101116882](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200911101116882.png)

索引分为主键索引和非主键索引，主键索引也叫聚簇索引，非主键索引也叫二级索引。

回表：

如果通过select * from T where k = 3这个查询语句，则先走普通索引，查找到ID，在根据id查找主键索引得到这一行的值，这个过程称为回表，走二级索引会多扫描一棵索引树。

## 优化：

1.索引覆盖

select *fromTwhere k between 3 and 5

回进行回表

select ID fromTwhere k between 3 and 5

k索引树上就已经有ID，不需要回表

覆盖索引可以减少树的搜索次数，显著提升查询性能，所以使用覆盖索引是一个常用 的性能优化手段。

2.最左前缀原则

B+树这种索引结构，可以利用索引的“最左前缀”，来定位记录。

![image-20200911104143394](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200911104143394.png)

可以看到，索引项是按照索引定义里面出现的字段顺序排序的。 当你的逻辑需求是查到所有名字是“张三”的人时，可以快速定位到ID4，然后向后遍历得到所有 需要的结果。 

如果你要查的是所有名字第一个字是“张”的人，你的SQL语句的条件是"where name like ‘张%’"。这时，你也能够用上这个索引，查找到第一个符合条件的记录是ID3，然后向后遍历， 直到不满足条件为止。 

可以看到，不只是索引的全部定义，只要满足最左前缀，就可以利用索引来加速检索。这个最左 前缀可以是联合索引的最左N个字段，也可以是字符串索引的最左M个字符。 

基于上面对最左前缀索引的说明，我们来讨论一个问题：在建立联合索引的时候，如何安排索 引内的字段顺序。 这里我们的评估标准是，索引的复用能力。因为可以支持最左前缀，所以当已经有了(a,b)这个联 合索引后，一般就不需要单独在a上建立索引了。

因此，第一原则是，如果通过调整顺序，可 以少维护一个索引，那么这个顺序往往就是需要优先考虑采用的。 所以现在你知道了，这段开头的问题里，我们要为高频请求创建(身份证号，姓名）这个联合索 引，并用这个索引支持“根据身份证号查询地址”的需求。 那么，如果既有联合查询，又有基于a、b各自的查询呢？查询条件里面只有b的语句，是无法使 用(a,b)这个联合索引的，这时候你不得不维护另外一个索引，也就是说你需要同时维护(a,b)、 (b) 这两个索引。

这时候，我们要考虑的原则就是空间了。比如上面这个市民表的情况，name字段是比age字段 大的 ，那我就建议你创建一个（name,age)的联合索引和一个(age)的单字段索引。

索引下推：

而MySQL 5.6 引入的索引下推优化（index condition pushdown)， 可以在索引遍历过程中，对索 引中包含的字段先做判断，直接过滤掉不满足条件的记录，减少回表次数。

## 全局锁

MySQL提供了一个加全局读锁的方法，命令是 Flush tables with read lock (FTWRL)

让整个数据库处于只读状态，之后其他线程的以下语句会被阻塞：数据更新语句（数据的增删改）、数据定义语句（包括 建表、修改表结构等）和更新类事务的提交语句。

## 表级锁

Mysql里面的表级锁有两种，一种是表锁，一种是元数据锁

表锁的语法是 lock tables …read/write。

lock tables语法除了会限制别的线程的读写 外，也限定了本线程接下来的操作对象。 

举个例子, 如果在某个线程A中执行lock tables t1 read, t2 write; 这个语句，则其他线程写t1、读 写t2的语句都会被阻塞。同时，线程A在执行unlock tables之前，也只能执行读t1、读写t2的操 作。连写t1都不允许，自然也不能访问其他表。

元数据锁MDL

MDL不需要显示的使用，在访问任何一个表的时候都会自动加上，MDL的作用是保证读写的正确性。

你可以想象一下，如果一个查询正在遍历一个 表中的数据，而执行期间另一个线程对这个表结构做变更，删了一列，那么查询线程拿到的结果 跟表结构对不上，肯定是不行的。

 **因此，在MySQL 5.5版本中引入了MDL，当对一个表做增删改查操作的时候，加MDL读锁；当 要对表做结构变更操作的时候，加MDL写锁。** 

读锁之间不互斥，因此你可以有多个线程同时对一张表增删改查。 

读写锁之间、写锁之间是互斥的，用来保证变更表结构操作的安全性。

因此，如果有两个线 程要同时给一个表加字段，其中一个要等另一个执行完才能开始执行。

如果你要做DDL变更的表刚好有长事务 在执行，要考虑先暂停DDL，或者kill掉这个长事务。 

但考虑一下这个场景。如果你要变更的表是一个热点表，虽然数据量不大，但是上面的请求很频 繁，而你不得不加个字段，你该怎么做呢？ 这时候kill可能未必管用，因为新的请求马上就来了。比较理想的机制是，

**在alter table语句里面 设定等待时间**，如果在这个指定的等待时间里面能够拿到MDL写锁最好，拿不到也不要阻塞后 面的业务语句，先放弃。之后开发人员或者DBA再通过重试命令重复这个过程。

```sql
begin;
set lock_wait_timeout=5;
ALTER TABLE T add test char(1);
commit;
```

**在MySQL的informationschema 库的 innodbtrx表中，你可以查到当前执行中的事务。**

## 索引失效

 **1．隐式转换导致索引失效.这一点应当引起重视.也是开发中经常会犯的错误.**

 由于表的字段tu_mdn定义为varchar2(20),但在查询时把该字段作为number类型以where条件传给Oracle,这样会导致索引失效.

 错误的例子：select * from test where tu_mdn=13333333333;

 正确的例子：select * from test where tu_mdn='13333333333';

 **2. 对索引列进行运算导致索引失效,我所指的对索引列进行运算包括(+，-，\*，/，! 等)**

 错误的例子：select * from test where id-1=9;

 正确的例子：select * from test where id=10;

 **3. 使用Oracle内部函数导致索引失效.对于这样情况应当创建基于函数的索引.**

 错误的例子：select * from test where round(id)=10; 说明，此时id的索引已经不起作用了

 正确的例子：首先建立函数索引，create index test_id_fbi_idx on test(round(id));然后 select * from test where round(id)=10; 这时函数索引起作用了

 **4. 以下使用会使索引失效，应避免使用；**

 a. 使用 <> 、not in 、not exist、!=

 b. like "%_" 百分号在前（可采用在建立索引时用reverse(columnName)这种方法处理）

 c. 单独引用复合索引里非第一位置的索引列.应总是使用索引的第一个列，如果索引是建立在多个列上, 只有在它的第一个列被where子句引用时，优化器才会选择使用该索引。

 d. 字符型字段为数字时在where条件里不添加引号.

 e. 当变量采用的是times变量，而表的字段采用的是date变量时.或相反情况。

 **5. 不要将空的变量值直接与比较运算符（符号）比较。**

 如果变量可能为空，应使用 IS NULL 或 IS NOT NULL 进行比较，或者使用 ISNULL 函数。

 **6. 不要在 SQL 代码中使用双引号。**

 因为字符常量使用单引号。如果没有必要限定对象名称，可以使用（非 ANSI SQL 标准）括号将名称括起来。

 **7. 将索引所在表空间和数据所在表空间分别设于不同的磁盘chunk上，有助于提高索引查询的效率。**

 **8. Oracle默认使用的基于代价的SQL优化器（CBO）非常依赖于统计信息，一旦统计信息不正常，会导致数****据库查询时不使用索引或使用错误的索引。**

 一般来说，Oracle的自动任务里面会包含更新统计信息的语句，但如果表数据发生了比较大的变化（超过20%）,可以考虑立即手动更新统计信息，例如：analyze table abc compute statistics，但注意，更新  统计信息比较耗费系统资源，建议在系统空闲时执行。

 **9. Oracle在进行一次查询时，一般对一个表只会使用一个索引.**

 因此，有时候过多的索引可能导致Oracle使用错误的索引，降低查询效率。例如某表有索引1（Policyno）和索引2（classcode），如果查询条件为policyno = ‘xx’ and classcode = ‘xx’，则系统有可能会使用索  引2，相较于使用索引1，查询效率明显降低。

 **10. 优先且尽可能使用分区索引。**

## 行锁

在InnoDB事务中，行锁是在需要的时候才加上的，但并不是不需要了就立刻释 放，而是要等到事务结束时才释放。这个就是**两阶段锁协议**。

## 死锁和死锁检测

当并发系统中不同线程出现循环资源依赖，涉及的线程都在等待别的线程释放资源时，就会导致 这几个线程都进入无限等待的状态，称为死锁。这里我用数据库中的行锁举个例子。

![image-20200912112051216](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200912112051216.png)

这时候，事务A在等待事务B释放id=2的行锁，而事务B在等待事务A释放id=1的行锁。 事务A和 事务B在互相等待对方的资源释放，就是进入了死锁状态。

当出现死锁以后，有两种策略： 

一种策略是，直接进入等待，直到超时。这个超时时间可以通过参数 innodb_lock_wait_timeout来设置。

 另一种策略是，发起死锁检测，发现死锁后，主动回滚死锁链条中的某一个事务，让其他事 务得以继续执行。将参数innodb_deadlock_detect设置为on，表示开启这个逻辑。 

在InnoDB中，innodb_lock_wait_timeout的默认值是50s，意味着如果采用第一个策略，当出现 死锁以后，第一个被锁住的线程要过50s才会超时退出，然后其他线程才有可能继续执行。

对于 在线服务来说，这个等待时间往往是无法接受的。 但是，我们又不可能直接把这个时间设置成一个很小的值，比如1s。这样当出现死锁的时候，确 实很快就可以解开，但如果不是死锁，而是简单的锁等待呢？

所以，超时时间设置太短的话，会 出现很多误伤。 所以，正常情况下我们还是要采用第二种策略，即：主动死锁检测，而且 innodb_deadlock_detect的默认值本身就是on。主动死锁检测在发生死锁的时候，是能够快速发 现并进行处理的，但是它也是有额外负担的。

## “快照”在MVCC里是怎么工作的？

在可重复读隔离级别下，事务在启动的时候就“拍了个快照”。注意，这个快照是基于整库的。

InnoDB里面每个事务有一个唯一的事务ID，叫作transaction id。它是在事务开始的时候向 InnoDB的事务系统申请的，是按申请顺序严格递增的。

而每行数据也都是有多个版本的。每次事务更新数据的时候，都会生成一个新的数据版本，并且 把transaction id赋值给这个数据版本的事务ID，记为rowtrx_id。同时，旧的数据版本要保留， 并且在新的数据版本中，能够有信息可以直接拿到它。

## 普通索引和唯一索引

执行查询的语句是 select id fromTwhere k=5

对于普通索引来说，查找到满足条件的第一个记录(5,500)后，需要查找下一个记录，直到碰 到第一个不满足k=5条件的记录。 

对于唯一索引来说，由于索引定义了唯一性，查找到第一个满足条件的记录后，就会停止继 续检索。

那么，这个不同带来的性能差距会有多少呢？答案是，微乎其微。

 你知道的，InnoDB的数据是按数据页为单位来读写的。也就是说，当需要读一条记录的时候， 并不是将这个记录本身从磁盘读出来，而是以页为单位，将其整体读入内存。在InnoDB中，每 个数据页的大小默认是16KB。

因为引擎是按页读写的，所以说，当找到k=5的记录的时候，它所在的数据页就都在内存里了。 那么，对于普通索引来说，要多做的那一次“查找和判断下一条记录”的操作，就只需要一次指针寻找和一次计算。

## change buffe

当需要更新一个数据页时，如果数据页在内存中就直接更新，而如果这个数据页还没有在内存中 的话，在不影响数据一致性的前提下，InooDB会将这些更新操作缓存在change buffer中，这样 就不需要从磁盘中读入这个数据页了。在下次查询需要访问这个数据页的时候，将数据页读入内 存，然后执行change buffer中与这个页有关的操作。通过这种方式就能保证这个数据逻辑的正 确性。

将change buffer中的操作应用到原数据页，得到最新结果的过程称为merge。除了访问这个数据 页会触发merge外，系统有后台线程会定期merge。在数据库正常关闭（shutdown）的过程中， 也会执行merge操作。

唯一索引出入操作要判断索引是否存在。唯一索引的更新就不能使用change buffer，实际上也只有普通索引可以使用。

## change buffer的使用场景

对于写多读少的业务来说，页面在写完以后马上被访问到的概率比较小，此时change buffer的使用效果最好。这种业务模型常见的就是账单类、日志类的系统。

反过来，假设一个业务的更新模式是写入之后马上会做查询，那么即使满足了条件，将更新先记 录在change buffer，但之后由于马上要访问这个数据页，会立即触发merge过程。这样随机访问 IO的次数不会减少，反而增加了change buffer的维护代价。所以，对于这种业务模式来 说，change buffer反而起到了副作用。

## 优化器的逻辑

而优化器选择索引的目的，是找到一个最优的执行方案，并用最小的代价去执行语句。

在数据库 里面，扫描行数是影响执行代价的因素之一。扫描的行数越少，意味着访问磁盘数据的次数越 少，消耗的CPU资源越少。 当然，扫描行数并不是唯一的判断标准，优化器还会结合是否使用临时表、是否排序等因素进行 综合判断。

**MySQL是怎样得到索引的基数的呢？**

这里，我给你简单介绍一下MySQL采样统计的方 法。 

为什么要采样统计呢？因为把整张表取出来一行行统计，虽然可以得到精确的结果，但是代价太 高了，所以只能选择“采样统计”。 

采样统计的时候，InnoDB默认会选择N个数据页，统计这些页面上的不同值，得到一个平均 值，然后乘以这个索引的页面数，就得到了这个索引的基数。 

**而数据表是会持续更新的，索引统计信息也不会固定不变。所以，当变更的数据行数超过1/M的 时候，会自动触发重新做一次索引统计。**

**索引统计不准确可以使用analyze命令重新统计**

## 怎么给字符串添加索引

MySQL是支持前缀索引的，也就是说，你可以定义字符串的一部分作为索引

```sql
select count(distinct left(email,4)）as L4, count(distinct left(email,5)）as L5, count(distinct left(email,6)）as L6, count(distinct left(email,7)）as L7, from SUser;
```

查看区分度

使用前缀索引就用不上覆盖索引对查询性能的优化了，这也是你在选择是否使用前缀 索引时需要考虑的一个因素。

## SQL变慢了

当内存数据页跟磁盘数据页内容不一致的时候，我们称这个内存页为“脏页”。

内存数据写 入到磁盘后，内存和磁盘上的数据页的内容就一致了，称为“干净页”。

InnoDB刷脏页的控制策略

首先，你要正确地告诉InnoDB所在主机的IO能力，这样InnoDB才能知道需要全力刷脏页的时 候，可以刷多快。 这就要用到**innodb_io_capacity**这个参数了，它会告诉InnoDB你的磁盘能力。这个值我建议你设 置成磁盘的IOPS。磁盘的IOPS可以通过fio这个工具来测试，下面的语句是我用来测试磁盘随机 读写的命令：

```shell
fio -filename=./redis-4.0.1.tar.gz -direct=1 -iodepth 1 -thread -rw=randrw -ioengine=psync -bs=16k -size=500M  -group_reporting -name=mytest
```

查看脏页比例，尽量控制脏页比例不要超过75%

```sql
select VARIABLE_VALUE into @a from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_dirty';
select VARIABLE_VALUE into @b from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_total';
select @a/@b;
```

连坐机制

刷脏页时如果发现这个数据页的邻居页也是脏页，就会把这个邻居也顺带刷掉，并且这个机制还可以蔓延。

在InnoDB中，innodb_flush_neighbors 参数就是用来控制这个行为的，值为1的时候会有上述 的“连坐”机制，值为0时表示不找邻居，自己刷自己的。SSD硬盘建议设为0，MySql8.0的时候默认值已经是0了。

## Innodb表

一个Innodb表包含两部分，表结构定义和数据，在MySQL 8.0版本以前，表结构是存在以.frm为后缀的文件里。而 MySQL 8.0版本，则已经允许把表结构定义放在系统数据表中了

## 参数innodb_file_per_table

1. 这个参数设置为OFF表示的是，表的数据放在系统共享表空间，也就是跟数据字典放在一 起； 

2. 这个参数设置为ON表示的是，每个InnoDB表数据存储在一个以 .ibd为后缀的文件中。 从MySQL 5.6.6版本开始，它的默认值就是ON了。

   不管哪个版本这个值都建议设为ON，因为，一个表单独存储为一个文 件更容易管理，而且在你不需要这个表的时候，通过drop table命令，系统就会直接删除这个文 件。而如果是放在共享表空间中，即使表删掉了，空间也是不会回收的。

## 数据删除流程

![image-20200915161130626](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200915161130626.png)

假设，我们要删掉R4这个记录，InnoDB引擎只会把R4这个记录标记为删除。如果之后要再插入 一个ID在300和600之间的记录时，可能会复用这个位置。但是，磁盘文件的大小并不会缩小。 现在，你已经知道了InnoDB的数据是按页存储的，那么如果我们删掉了一个数据页上的所有记 录，会怎么样？ 答案是，整个数据页就可以被复用了。

但是，数据页的复用跟记录的复用是不同的。 记录的复用，只限于符合范围条件的数据。比如上面的这个例子，R4这条记录被删除后，如果 插入一个ID是400的行，可以直接复用这个空间。但如果插入的是一个ID是800的行，就不能复 用这个位置了。 而当整个页从B+树里面摘掉以后，可以复用到任何位置。以图1为例，如果将数据页page A上的 所有记录删除以后，page A会被标记为可复用。这时候如果要插入一条ID=50的记录需要使用新 页的时候，page A是可以被复用的。 如果相邻的两个数据页利用率都很小，系统就会把这两个页上的数据合到其中一个页上，另外一 个数据页就被标记为可复用。

进一步地，如果我们用delete命令把整个表的数据删除呢？结果就是，所有的数据页都会被标记 为可复用。但是磁盘上，文件不会变小。

你现在知道了，delete命令其实只是把记录的位置，或者数据页标记为了“可复用”，但磁盘文件 的大小是不会变的。也就是说，通过delete命令是不能回收表空间的。这些可以复用，而没有被 使用的空间，看起来就像是“空洞”。

不止是删除数据会造成空洞，插入数据也会。（页分裂可能在末尾留下空洞）

另外，更新索引上的值，可以理解为删除一个旧的值，再插入一个新值。不难理解，这也是会造 成空洞的。 也就是说，经过大量增删改的表，都是可能是存在空洞的。所以，如果能够把这些空洞去掉，就 能达到收缩表空间的目的。 而重建表，就可以达到这样的目的。

## 重建表

```sql
alter table A engine=InnoDB
```

在MySQL 5.6版本开始引入的Online DDL，对这个操作流程做了优化（以前采用临时表，DDL过程中不能更新表A）。 我给你简单描述一下引入了Online DDL之后，重建表的流程：

1. 建立一个临时文件，扫描表A主键的所有数据页； 
2. 用数据页中表A的记录生成B+树，存储到临时文件中； 
3. 生成临时文件的过程中，将所有对A的操作记录在一个日志文件（rowlog）中，对应的是图 中state2的状态； 
4.  临时文件生成后，将日志文件中的操作应用到临时文件，得到一个逻辑数据上与表A相同的 数据文件，对应的就是图中state3的状态；
5. 用临时文件替换表A的数据文件。

alter语句在启动的时候需要获取MDL写锁，但是这个写锁在真正拷贝数据 之前就退化成读锁了。 

为什么要退化呢？为了实现Online，MDL读锁不会阻塞增删改操作。 那为什么不干脆直接解锁呢？为了保护自己，禁止其他线程对这个表同时做DDL。 而对于一个大表来说，Online DDL最耗时的过程就是拷贝数据到临时表的过程，这个步骤的执 行期间可以接受增删改操作。所以，相对于整个DDL过程来说，锁的时间非常短。对业务来说， 就可以认为是Online的。

## count(*)的实现方式

你首先要明确的是，在不同的MySQL引擎中，count(*)有不同的实现方式。 

*MyISAM引擎把一个表的总行数存在了磁盘上，因此执行count(*)的时候会直接返回这个数， 效率很高； 

而InnoDB引擎就麻烦了，它执行count(*)的时候，需要把数据一行一行地从引擎里面读出 来，然后累积计数。 

这里需要注意的是，我们在这篇文章里讨论的是没有过滤条件的count(*****)，如果加了where 条件 的话，MyISAM表也是不能返回得这么快的。

在数据库保存计数：

缓存保存计数存在逻辑性不精确的问题

可以利用事务的特性，用一张表保存计数

## 不同的count用法

在select count(?) fromt这样的查询语句里 面，count(*)、count(主键id)、count(字段)和count(1)等不同用法的性能，有哪些差别。

count()是一个聚合函数，对于返回的结果集，一行行地 判断，如果count函数的参数不是NULL，累计值就加1，否则不加。最后返回累计值。

所以，count(*)、count(主键id)和count(1) 都表示返回满足条件的结果集的总行数；而count(字 段），则表示返回满足条件的数据行里面，参数“字段”不为NULL的总个数。

对于count(主键id)来说，InnoDB引擎会遍历整张表，把每一行的id值都取出来，返回给server 层。server层拿到id后，判断是不可能为空的，就按行累加。

对于count(1)来说，InnoDB引擎遍历整张表，但不取值。server层对于返回的每一行，放一个 数字“1”进去，判断是不可能为空的，按行累加。

对于count(字段)来说： 1. 如果这个“字段”是定义为not null的话，一行行地从记录里面读出这个字段，判断不能为 null，按行累加； 2. 如果这个“字段”定义允许为null，那么执行的时候，判断到有可能是null，还要把值取出来再 判断一下，不是null才累加。

但是count(*****)是例外，并不会把全部字段取出来，而是专门做了优化，不取值。count(*****)肯定不 是null，按行累加。

按照效率排序的话，count(字段)<count(主键id)<count(1)≈count(*****)，所以我建议你，尽量使用count(*****)。

## order by是怎么工作的

MySQL会给每个线程分配一块内存用于 排序，称为sort_buffer

sort_buffer_size，就是MySQL为排序开辟的内存（sort_buffer）的大小。如果要排序的数据量 小于sort_buffer_size，排序就在内存中完成。但如果排序数据量太大，内存放不下，则不得不 利用磁盘临时文件辅助排序

```sql
-- 查看排序过程中是否使用了外部文件辅助。

/* 打开optimizer_trace，只对本线程有效 */
SET optimizer_trace='enabled=on';
/* @a保存Innodb_rows_read的初始值 */
select VARIABLE_VALUE into @a from performance_schema.session_status where variable_name = 'Innodb_rows_read';
/* 执行语句 */
select city, name,age from t where city='杭州' order by name limit 1000;
/* 查看 OPTIMIZER_TRACE 输出 */
SELECT * FROM `information_schema`.`OPTIMIZER_TRACE`\G
/* @b保存Innodb_rows_read的当前值 */
select VARIABLE_VALUE into @b from performance_schema.session_status where variable_name = 'Innodb_rows_read';
/* 计算Innodb_rows_read差值 */
select @b-@a;
```

全字段排序

```sql
select city,name,age from t where city='杭州' order by name limit 1000 ;
```

通常情况下，这个语句执行流程如下所示 ： 

1. 初始化sort_buffer，确定放入name、city、age这三个字段； 
2.  从索引city找到第一个满足city='杭州’条件的主键id，也就是图中的ID_X；
3. 到主键id索引取出整行，取name、city、age三个字段的值，存入sort_buffer中； 
4.  从索引city取下一个记录的主键id； 
5. 重复步骤3、4直到city的值不满足查询条件为止，对应的主键id也就是图中的ID_Y；
6. 对sort_buffer中的数据按照字段name做快速排序； 
7. 按照排序结果取前1000行返回给客户端。 我们暂且把这个排序过程，称为全字段排序，执行流程的示意图如下所示，下一篇文章中我们还 会用到这个排序。

![image-20200917155946261](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200917155946261.png)

rowid排序

在上面这个算法过程里面，只对原表的数据读了一遍，剩下的操作都是在sort_buffer和临时文件 中执行的。

但这个算法有一个问题，就是如果查询要返回的字段很多的话，那么sort_buffer里面 要放的字段数太多，这样内存里能够同时放下的行数很少，要分成很多个临时文件，排序的性能 会很差。 

所以如果单行很大，这个方法效率不够好。 那么，如果MySQL认为排序的单行长度太大会怎么做呢？ 接下来，我来修改一个参数，让MySQL采用另外一种算法。

```sql
SET max_length_for_sort_data = 16;
```

max_length_for_sort_data，是MySQL中专门控制用于排序的行数据的长度的一个参数。它的意 思是，如果单行的长度超过这个值，MySQL就认为单行太大，要换一个算法。

 city、name、age 这三个字段的定义总长度是36，我把max_length_for_sort_data设置为16，我 们再来看看计算过程有什么改变。 新的算法放入sort_buffer的字段，只有要排序的列（即name字段）和主键id。

但这时，排序的结果就因为少了city和age字段的值，不能直接返回了，整个执行流程就变成如 下所示的样子： 

1. 初始化sort_buffer，确定放入两个字段，即name和id； 
2. 从索引city找到第一个满足city='杭州’条件的主键id，也就是图中的ID_X； 
3. 到主键id索引取出整行，取name、id这两个字段，存入sort_buffer中； 
4. 从索引city取下一个记录的主键id； 
5.  重复步骤3、4直到不满足city='杭州’条件为止，也就是图中的ID_Y；
6.  对sort_buffer中的数据按照字段name进行排序； 
7.  遍历排序结果，取前1000行，并按照id的值回到原表中取出city、name和age三个字段返回 给客户端。

![image-20200917160347283](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200917160347283.png)

## 为什么只查询一行也要这么久

开启慢查询日志：

```sql
-- 全局变量设置(该方式数据库重启全部失效，得重新配置)
set global slow_query_log='ON'; 
-- 日志地址
set global slow_query_log_file='/usr/local/mysql/data/slow.log'; 
-- 查询时间超过一秒就记录
set global long_query_time=1;
```

**查询长时间不返回：**

```sql
select * from t where id=1;
```

一般碰到这种情况，大概率是表被锁住了，一般是执行show processlist命令去查看当前语句处于什么状态。

等MDL锁：

遇到这类问题就是，找到谁持有MDL写锁，把它kill掉

等flush：

等行锁:

```sql
-- 这个查询语句会去拿读锁
select * from t where id=1 lock in share mode;
```

**查询慢**

一致性读中如果别的事务将数据更改了，那么需要回滚，有时间开销

## 间隙锁

产生幻读的原因是，行锁只能锁住行，但是新插入记录这个动作，要更新的是记 录之间的“间隙”。因此，为了解决幻读问题，InnoDB只好引入新的锁，也就是间隙锁(Gap Lock)。

顾名思义，间隙锁，锁的就是两个值之间的空隙。当你执行 select *fromt where d=5 for update的时候，就不止是给数据库中已有的6个记 录加上了行锁，还同时加了7个间隙锁。这样就确保了无法再插入新的记录。

也就是说这时候，在一行行扫描的过程中，不仅将给行加上了行锁，还给行两边的空隙，也加上 了间隙锁。

跟行锁有冲突的往往是另一个行锁，但是跟间隙锁存在冲突关系的，是"往这个间隙中插入一个记录"这个操作。

间隙锁于行锁合称next-key lock，每个next-key lock是前开后闭区间。也就是说，我们的表t初始 化以后，如果用select *fromt for update要把整个表所有记录锁起来，就形成了7个next-key lock，分别是 (-∞,0]、(0,5]、(5,10]、(10,15]、(15,20]、(20, 25]、(25, +supremum]。

间隙锁的引入，可能会导致同样的语句锁住更大的范围，这其实是影响了并 发度的

如果把隔离级别设置为读提交的话， 就没有间隙锁了。但同时，你要解决可能出现的数据和日志不一致问题，需要把binlog格式设置 为row。

## 加锁原则

加锁规则里面，包含了两个“原则”、两个“优化”和一个“bug”。

1. 原则1：加锁的基本单位是next-key lock。希望你还记得，next-key lock是前开后闭区间。 
2. 原则2：查找过程中访问到的对象才会加锁。 
3. 优化1：索引上的等值查询，给唯一索引加锁的时候，next-key lock退化为行锁。 
4. 优化2：索引上的等值查询，向右遍历时且最后一个值不满足等值条件的时候，next-key lock退化为间隙锁。 
5. 一个bug：唯一索引上的范围查询会访问到不满足条件的第一个值为止。

## 短链接风暴

正常的短连接模式就是连接到数据库后，执行很少的SQL的语句就断开，下次需要的时候再重连。如果使用的是短链接，再业务高峰期的时候，就可能出现连接数暴涨的情况。

MySQL建立连接的过程，成本是很高的

数据库压力小的时候，这些额外的成本并不明显。

但是，短链接模型存在一个风险，一旦数据库处理得慢一些，连接数就会暴涨。

max_connections参数，用来控制一个MySQL实例同时存在的连接数的上限，超过这个值，系统就会拒绝接下来的连接请求，并报错提示“Too many connections”。对于被拒绝连接的请求来 说，从业务角度看就是数据库不可用。

调高max_connections会有风险。

第一种方法：处理掉那些占着连接却不工作的线程

max_connections的计算，不是看谁在running，是只要连着就占用一个计数位置。对于那些不需 

要保持的连接，我们可以通过kill connection主动踢掉。这个行为跟事先设置wait_timeout的效果 

是一样的。设置wait_timeout参数表示的是，一个线程空闲wait_timeout这么多秒之后，就会被 

MySQL直接断开连接

第二种方法：减少连接过程的消耗

有的业务代码会在短时间内先大量申请数据库连接做备用，如果现在数据库确认是被连接行为打 

挂了，那么一种可能的做法，是让数据库跳过权限验证阶段。 

跳过权限验证的方法是：重启数据库，并使用–skip-grant-tables参数启动。这样，整个MySQL会 

跳过所有的权限验证阶段，包括连接过程和语句执行过程在内。 

但是，这种方法特别符合我们标题里说的“饮鸩止渴”，风险极高，是我特别不建议使用的方案。 

尤其你的库外网可访问的话，就更不能这么做了。 

## 慢查询性能问题

在MySQL中，会引发性能问题的慢查询，大体有以下三种可能：

1.索引没有设计好

2.SQL语句没有写好

3.MySQL选错了索引

**索引没有设计好：**

这种场景一般就是通过紧急创建索引来解决。MySQL 5.6版本以后，创建索引都支持Online DDL 

了，对于那种高峰期数据库已经被这个语句打挂了的情况，最高效的做法就是直接执行alter 

table 语句。比较理想的是能够在备库先执行。假设你现在的服务是一主一备，主库A、备库B，这个方案的 

大致流程是这样的： 

1. 在备库B上执行 set sql_log_bin=off，也就是不写binlog，然后执行alter table 语句加上索 

引； 

2. 执行主备切换； 

3. 这时候主库是B，备库是A。在A上执行 set sql_log_bin=off，然后执行alter table 语句加上 

索引。 

这是一个“古老”的DDL方案。平时在做变更的时候，你应该考虑类似gh-ost这样的方案，更加稳 

妥。但是在需要紧急处理时，上面这个方案的效率是最高的。 

**SQL语句没有写好**

SQL语句没写好，可能会导致索引失效。

**MySQL选错索引**

应急方案就是给这个语句加上force index。

这些情况都是可以避免的

上线前，在测试环境中，把慢查询日志（slow log）打开，并且把long_query_time设置成0，

确保每个语句都会被记录入慢查询日志；

在测试表里插入模拟线上的数据，做一遍回归测试； 

 观察慢查询日志里每类语句的输出，特别留意Rows_examined字段是否与预期一致。（我 们在前面文章中已经多次用到过Rows_examined方法了，相信你已经动手尝试过了。如果 还有不明白的，欢迎给我留言，我们一起讨论）。

## QPS突增问题

有时候由于业务突然出现高峰，或者应用程序bug，导致某个语句的QPS突然暴涨，也可能导致MySQL压力过大，影响服务。

## binlog的写入机制

事务执行的过程中，先把日志写到binlog cache 事务提交的时候，再把binlog cache写到binlog文件中

一个事务的binlog是不能拆分的，因此不论这个事务多大，也要确保一次性写入。这就涉及到了binlog cache的保存问题。

系统给binlog cache分配了一片内存，每个线程一个，参数binlog_cache_size用于控制单个线程内binlog cache所占内存的大小。如果超过了这个参数规定的大小，就要暂存到磁盘。

事务提交的时候，执行器把binlog cache里的完整事务写入到binlog中，并清空binlog cache。

write 和fsync的时机，是由参数sync_binlog控制的： 

1. sync_binlog=0的时候，表示每次提交事务都只write，不fsync； 

2. sync_binlog=1的时候，表示每次提交事务都会执行fsync； 

3. sync_binlog=N(N>1)的时候，表示每次提交事务都write，但累积N个事务后才fsync。 

因此，在出现IO瓶颈的场景里，将sync_binlog设置成一个比较大的值，可以提升性能。在实际 

的业务场景中，考虑到丢失日志量的可控性，一般不建议将这个参数设成0，比较常见的是将其 

设置为100~1000中的某个数值。 

但是，将sync_binlog设置为N，对应的风险是：如果主机发生异常重启，会丢失最近N个事务的 

binlog日志。

## redo log的写入机制

![image-20200929101519445](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200929101519445.png)

redo log 三种状态：

1.存在redo log buffer中，物理上是在MySQL进程内存中，就是图中红色部分；

2.写到磁盘（write），但是没有持久化（fsync），物理上是在文件系统的page cache里面，也就是途中黄色部分；

3.持久化磁盘，对应的是hard disk，也就是图中绿色部分。

日志写到redo log buffer是很快的，wirte到page cache也差不多，但是持久化到磁盘的速度就慢多了。

为了控制redo log的写入策略，InnoDB提供了innodb_flush_log_at_trx_commit参数，它有三种 可能取值： 

1. 设置为0的时候，表示每次事务提交时都只是把redo log留在redo log buffer中; 

2. 设置为1的时候，表示每次事务提交时都将redo log直接持久化到磁盘； 

3. 设置为2的时候，表示每次事务提交时都只是把redo log写到page cache。 

InnoDB有一个后台线程，每隔1秒，就会把redo log buffer中的日志，调用write写到文件系统的page cache，然后调用fsync持久化到磁盘。

注意，事务执行中间过程的redo log也是直接写在redo log buffer中的，这些redo log也会被后台线程一起持久化到磁盘。也就是说，一个没有提交的事务的redo log，也是可能已经持久化到磁盘的。

实际上，除了后台线程每秒一次的轮询操作外，还有两种场景会让一个没有提交的事务的redo log写入到磁盘中。

一种是，redo log buffer 占用的空间即将达到innodb_log_buffer_size一半的时候，后台线程回主动写盘。由于这个事务并没有提交，这个写盘动作只是write，而且没有调用fsync，也就是只留在了文件系统的page cache。

另一种是，并行事务提交的时候，顺带将这个事务的redo log buffer持久化到磁盘。假设一个事务A执行到一半，已经写了一些redo log到buffer中，这时候有另外一个线程的事务B提交，如果innodb_flush_log_at_trx_commit设置是1，那么按照这个参数的逻 辑，事务B要把redo log buffer里的日志全部持久化到磁盘。这时候，就会带上事务A在redo log buffer里的日志一起持久化到磁盘。

如果把innodb_flush_log_at_trx_commit设置成1，那么redo log在prepare阶段就要持久化一次，因为有一个崩溃恢复逻辑是要依赖于prepare 的redo log，再加上binlog来恢复的。

通常我们说MySQL的“双1”配置，指的就是sync_binlog和innodb_flush_log_at_trx_commit都设 置成 1。也就是说，一个事务完整提交前，需要等待两次刷盘，一次是redo log（prepare 阶段），一次是binlog。

## 组提交

一次组提交里面，组员越多，节约磁盘IOPS的效果越好。

在并发更新场景下，第一个事务写完redo log buffer以后，接下来这个fsync越晚调用，组员可能越多，节约IOPS的效果就越好。

MySQL出现了性能瓶颈，而且瓶颈在IO上：

1. 设置 binlog_group_commit_sync_delay和 binlog_group_commit_sync_no_delay_count参数，减少binlog的写盘次数。这个方法是基于“额外的故意等待”来实现的，因此可能会增加语句的响应时间，但没有丢失数据的风险。 

2. 将sync_binlog 设置为大于1的值（比较常见是100~1000）。这样做的风险是，主机掉电时会丢binlog日志。 

3. 将innodb_flush_log_at_trx_commit设置为2。这样做的风险是，主机掉电的时候会丢数据。

我不建议你把innodb_flush_log_at_trx_commit 设置成0。因为把这个参数设置成0，表示redo log只保存在内存中，这样的话MySQL本身异常重启也会丢数据，风险太大。而redo log写到文件系统的page cache的速度也是很快的，所以将这个参数设置成2跟设置成0其实性能差不多， 但这样做MySQL异常重启时就不会丢数据了，相比之下风险会更小。

## MySQL主备基本原理

![image-20200930100544519](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200930100544519.png)

备库B跟主库A之间维持了一个长连接。主库A内部有一个线程，专门用于服务备库B的这个长连接。一个事务日志同步的完整过程是这样的：

1. 在备库B上通过change master命令，设置主库A的IP、端口、用户名、密码，以及要从哪个位置开始请求binlog，这个位置包含文件名和日志偏移量。 

2. 在备库B上执行start slave命令，这时候备库会启动两个线程，就是图中的io_thread和sql_thread。其中io_thread负责与主库建立连接。 后来由于多线程复制方案的引入，sql_thread演化成为了多个线程。

3. 主库A校验完用户名、密码后，开始按照备库B传过来的位置，从本地读取binlog，发给B。 

4. 备库B拿到binlog后，写到本地文件，称为中转日志（relay log）。 

5. sql_thread读取中转日志，解析出日志里的命令，并执行。

## binlog的三种格式

查看binlog语句  show binlog events in 'mysql-bin.000001';

**statement**

当binlog_format=statement时，binlog里面记录的就是SQL语句的原文。在备库上执行时可能有些Sql存在风险。

**row**

binlog_format='row'时。

binlog_row_image的默认配置是FULL，因此Delete_event里面，包含了删掉的行的所有字段 的值。如果把binlog_row_image设置为MINIMAL，则只会记录必要的信息，在这个例子里，就是只会记录id=4这个信息

这种格式下binlog会记录所操作的行。

**mixed**

MySQL自己会判断这条SQL语句是否可能引起主备不一致，如果有可能，就用row格式， 否则就用statement格式。

## 循环复制问题

![image-20200930102313354](https://mkdown-images.oss-cn-shenzhen.aliyuncs.com/images/image-20200930102313354.png)

生产过程中大都是采用双M结构

节点A和B之间总是互为主备关系。这样在切换的时候就不用再修改主备关系

业务逻辑在节点A上更新了一条语句，然后再把生成的binlog 发给节点B，节点B执行完这条更新 语句后也会生成binlog。（我建议你把参数log_slave_updates设置为on，表示备库执行relay log 后生成binlog）。 

那么，如果节点A同时是节点B的备库，相当于又把节点B新生成的binlog拿过来执行了一次，然 后节点A和B间，会不断地循环执行这个更新语句，也就是循环复制了

从上面的图6中可以看到，MySQL在binlog中记录了这个命令第一次执行时所在实例的server 

id。因此，我们可以用下面的逻辑，来解决两个节点间的循环复制的问题： 

1. 规定两个库的server id必须不同，如果相同，则它们之间不能设定为主备关系； 

2. 一个备库接到binlog并在重放的过程中，生成与原binlog的server id相同的新的binlog； 

3. 每个库在收到从自己的主库发过来的日志后，先判断server id，如果跟自己的相同，表示这个日志是自己生成的，就直接丢弃这个日志。 

按照这个逻辑，如果我们设置了双M结构，日志的执行流就会变成这样： 

1. 从节点A更新的事务，binlog里面记的都是A的server id； 

2. 传到节点B执行一次以后，节点B生成的binlog 的server id也是A的server id； 

3. 再传回给节点A，A判断到这个server id与自己的相同，就不会再处理这个日志。所以，死循环在这里就断掉了。 

## 主备延迟

主库A执行挖宝成一个事务，写入binlog的时刻，记为T1；

传给备库，备库B接收完这个binlog的时刻，记为T2；

备库B执行完这个事务的时刻，记为T3；

所谓主备延迟，就是同一个事务，在备库执行完成的时间和主库执行完成的时间之间的差值，也就是T3-T1。

备库上执行show slave status\G；返回结果里面seconds_behind_master用于表示当前备库延迟了多少秒。

在网络正常的时候，日志从主库传给备库所需的时间是很短的，即T2-T1的值是非常小的。也就是说，网络正常情况下，主备延迟的主要来源是备库接收完binlog和执行完这个事务之间的时间差。 

所以说，主备延迟最直接的表现是，备库消费中转日志（relay log）的速度，比主库生产binlog的速度要慢。接下来，我就和你一起分析下，这可能是由哪些原因导致的。

**来源**

有些部署条件下，**备库所在机器的性能要比主库所在的机器性能差**。采用对称部署。

第二种情况就是备库压力大。一般的想法是，主库既然提供了写能力，那么备库可以提供一些读能力。或者一些运营后台需要的分析语句，不能影响正常业务，所以只能在备库上跑。 处理：一主多从。除了备库外，可以多接几个从库，让这些从库来分担读的压力。 或通过binlog输出到外部系统，比如Hadoop这类系统，让外部系统提供统计类查询的能力

第三种情况，大事务。大事务这种情况很好理解。因为主库上必须等事务执行完成才会写入binlog，再传给备库。所以，如果一个主库上的语句执行10分钟，那这个事务很可能就会导致从库延迟10分钟。 

## 可靠性优先策略

在双M结构下，从状态1到状态2切换的详细过程是这样的： 

1. 判断备库B现在的seconds_behind_master，如果小于某个值（比如5秒）继续下一步，否则 

持续重试这一步； 

2. 把主库A改成只读状态，即把readonly设置为true； 

3. 判断备库B的seconds_behind_master的值，直到这个值变成0为止； 

4. 把备库B改成可读写状态，也就是把readonly设置为false； 

5. 把业务请求切到备库B。 

这个切换流程，一般是由专门的HA系统来完成的，我们暂时称之为可靠性优先流程。

## 可用性优先策略

如果我强行把步骤4、5调整到最开始执行，也就是说不等主备数据同步，直接把连接切到备库 

B，并且让备库B可以读写，那么系统几乎就没有不可用时间了。 

我们把这个切换流程，暂时称作可用性优先流程。这个切换流程的代价，就是可能出现数据不一 

致的情况。

## 未解决问题

读写分离实现(mybatis二次开发)

双主模式宕机自动切换

## GTID

GTID的全称是Global Transaction Identifier，也就是全局事务ID，是一个事务在提交的时候生成的，是这个事务的唯一标识。它由两部分组成，格式是GTID=server_uuid:gno 

其中：

server_uuid是一个实例第一次启动时自动生成的，是一个全局唯一的值； 

gno是一个整数，初始值是1，每次提交事务的时候分配给这个事务，并加1。

GTID模式的启动也很简单，我们只需要在启动一个MySQL实例的时候，加上参数gtid_mode=on和enforce_gtid_consistency=on就可以了。

**在GTID模式下，每个事务都会跟一个GTID一一对应。这个GTID有两种生成方式，而使用哪种方式取决于session变量gtid_next的值。**

1. 如果gtid_next=automatic，代表使用默认值。这时，MySQL就会把server_uuid:gno分配给 

   这个事务。 

   a. 记录binlog的时候，先记录一行 SET@@SESSION.GTID_NEXT=‘server_uuid:gno’; 

   b. 把这个GTID加入本实例的GTID集合。

2. 如果gtid_next是一个指定的GTID的值，比如通过set gtid_next='current_gtid’指定为 current_gtid，那么就有两种可能： 

   a. 如果current_gtid已经存在于实例的GTID集合中，接下来执行的这个事务会直接被系统忽略；

   b. 如果current_gtid没有存在于实例的GTID集合中，就将这个current_gtid分配给接下来要执 行的事务，也就是说系统不需要给这个事务生成新的GTID，因此gno也不用加1。 

注意，一个current_gtid只能给一个事务使用。这个事务提交后，如果要执行下一个事务，就要执行set 命令，把gtid_next设置成另外一个gtid或者automatic。 

这样，每个MySQL实例都维护了一个GTID集合，用来对应“这个实例执行过的所有事务”。

**基于GTID的主备切换：**

在GTID模式下，备库B要设置为新主库A’的从库的语法如下：

```sql
CHANGE MASTER TO 

MASTER_HOST=$host_name 

MASTER_PORT=$port 

MASTER_USER=$user_name 

MASTER_PASSWORD=$password 

master_auto_position=1
```

## 读写分离有哪些坑

由于主从可能存在延迟，客户端执行完一个更新事务后马上发起查询，如果查询选择的是从库的话，就有可能读到刚刚的事务更新之前的状态。这种"在从库上会读到系统的一个过期状态"的现象，暂且称之为过期读。

**强制走主库方案：**

主要是对查询请求做分类，对于需要拿最新结果的请求，强制将其发到主库上。比如交易平台，商家发布商品。

对于可以读到旧数据的请求，才将其发到从库上。

**Sleep方案**

主库更新后，读从库之前先sleep一下。具体的方案就是，类似于执行一条select sleep(1)命令。这个方案的假设是，大多数情况下主备延迟在1秒之内，做一个sleep可以有很大概率拿到最新的数据。 

**判断主备无延迟方案**

showslave status结果里的seconds_behind_master参数的值，可以用来衡量主备延迟时间的长短

第一种确保主备无延迟的方法是每次从库执行查询请求前，先判断seconds_behind_master是否已经等于0。如果还不等于0 ，那就必须等到这个参数变为0才能执行查询请求。

第二种方法，对比位点确保主备无延迟： 

Master_Log_File和Read_Master_Log_Pos，表示的是读到的主库的最新位点； 

Relay_Master_Log_File和Exec_Master_Log_Pos，表示的是备库执行的最新位点。 

如果Master_Log_File和Relay_Master_Log_File、Read_Master_Log_Pos和 

Exec_Master_Log_Pos这两组值完全相同，就表示接收到的日志已经同步完成。 

第三种方法，对比GTID集合确保主备无延迟： 

Auto_Position=1 ，表示这对主备关系使用了GTID协议。 

Retrieved_Gtid_Set，是备库收到的所有日志的GTID集合； 

Executed_Gtid_Set，是备库所有已经执行完成的GTID集合。 

如果这两个集合相同，也表示备库接收到的日志都已经同步完成。 

## 半同步复制**semi**-sync replication

semi-sync做了这样的设计： 

1. 事务提交的时候，主库把binlog发给从库； 

2. 从库收到binlog以后，发回给主库一个ack，表示收到了； 

3. 主库收到这个ack以后，才能给客户端返回“事务完成”的确认。 

也就是说，如果启用了semi-sync，就表示所有给客户端发送过确认的事务，都确保了备库已经收到了这个日志。

semi-sync配合前面关于位点的判断，就能够确定在从库上执行的查询请求，可以避免过 

期读。 

但是，semi-sync+位点判断的方案，只对一主一备的场景是成立的。在一主多从场景中，主库只要等到一个从库的ack，就开始给客户端返回确认。这时，在从库上执行查询请求，就有两种情 况：

1. 如果查询是落在这个响应了ack的从库上，是能够确保读到最新数据； 

2. 但如果是查询落到其他从库上，它们可能还没有收到最新的日志，就会产生过期读的问题。 

## 预防误删数据

把sql_safe_updates参数设置为on。这样一来，如果我们忘记在delete或者update语句中写 

where条件，或者where条件里面没有包含索引字段的话，这条语句的执行就会报错。