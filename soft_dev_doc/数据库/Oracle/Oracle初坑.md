## Oracle 初学踩坑之旅

#### 1. 表字段名列名双引号问题
 重新用 Power Designer 建模，用 Navicat 重新创建表之后，原本纯大写的表都变成了对应的大小写，还挺开心的和 Mysql 一样了，结果后端程序报错，找了一会儿原因发现这是 Oracle 数据库的特性吧。
>  解决方法：
> 转自于 [光于前裕于后](https://blog.csdn.net/dr_guo/article/details/50723643)
1、oracle表和字段是有大小写的区别。oracle默认是大写，如果我们用双引号括起来的就区分大小写，如果没有，系统会自动转成大写。
2、我们在使用navicat使用可视化创建数据库时候，navicat自动给我们加上了“”。
3、①不加双引号创建变：
  ②加双引号，跟我们使用navicat可视化操作的结果一样：

#### 2. Power Designer 建模主键自增问题
 本来用 Power Designer 建模之后将生成的 Oracle Sql 语句放入 Navicat 中执行都没问题全能执行成功。无论是创建序列，添加触发器，还是创建表格语句等。
 但是在后续的插入操作，报错未通过什么检测的，具体详情忘了。仔细的从生成的语句中查看 sql 代码，都没问题，将代码复制出来手动创建也没问题，从头试了好几次。都是一样的结果。
 最后找到原因，也是因为这个鬼双引号，在 Power Designer 中生成的创建序列语句，自动的在序列名称上面加了双引号，然后在引用序列，查询序列，触发器中引用序列的语句又没加双引号，导致这个序列不存在。