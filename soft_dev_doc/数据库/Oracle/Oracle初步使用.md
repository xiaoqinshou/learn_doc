## Oracle 数据库 常用命令

```powershell
# 一定得有"-"号切换用户
$ su - oracle

# 到 oracle 用户下
$ sqlplus /nolog

# 连接Oracle
SQL> conn /as sysdba

# 切换用户
SQL> conn

# 创建用户
SQL> create user 用户名 identified by 口令[即密码]；
     DEFAULT TABLESPACE 默认表空间
     TEMPORARY TABLESPACE 临时表空间;

# 更新用户
SQL> alter user 用户名 identified by 口令[改变的口令];

# 删除用户
SQL> drop user 用户名;

# 若用户拥有对象，则不能直接删除，否则将返回一个错误值。指定关键字cascade,可删除用户所有的对象，然后再删除用户。
SQL> drop user 用户名 cascade;
```
oracle为兼容以前版本，提供三种标准角色（role）:connect/resource和dba.

讲解三种标准角色：

1. connect role(连接角色)

- 临时用户，特指不需要建表的用户，通常只赋予他们connect role. 

- connect是使用oracle简单权限，这种权限只对其他用户的表有访问权限，包括select/insert/update和delete等。

- 拥有connect role 的用户还能够创建表、视图、序列（sequence）、簇（cluster）、同义词(synonym)、回话（session）和其他  数据的链（link） 

2. resource role(资源角色)

- 更可靠和正式的数据库用户可以授予resource role。

- resource提供给用户另外的权限以创建他们自己的表、序列、过程(procedure)、触发器(trigger)、索引(index)和簇(cluster)。

3. dba role(数据库管理员角色)

- dba role拥有所有的系统权限

- 包括无限制的空间限额和给其他用户授予各种权限的能力。system由dba用户拥有

```powershell
# 授权命令
SQL> grant connect|resource|dba to 用户名;

# 撤销权限
SQL> revoke connect|resource|dba from 用户名;

# 创建角色
SQL> create role 角色名;

# 授权角色
SQL> grant select on class to 角色名;

# 删除角色
sql> drop role 角色名;
```
```powershell
# 查询当前数据库实例名
SQL> select name from v$database;

# 创建表空间 大小 M为单位 (注:一定得先创建好对应路径文件，Oracle 不会创建对应路径的文件夹,临时表空间不加LOGGING,数据表空间需要加LOGGING记录日志)
SQL> Create TableSpace 表空间名称
     LOGGING
     DataFile 表空间数据文件路径
     Size 表空间初始大小
     Autoextend on
     next 自增大小 maxsize 最大值
     autoallocate
     extent management local
     segment space management auto;

# 查看当前用户的缺省（默认）表空间：
SQL> select username,default_tablespace from user_users;
```

总结：创建用户一般分四步：

第一步：创建临时表空间

第二步：创建数据表空间

第三步：创建用户并制定表空间

第四步：给用户授予权限

第五步：然后此个用户就类似于，MYSQL用户下单个数据库的概念。