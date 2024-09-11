# sharding-jdbc-demo
study notes about sharding-jdbc

## MySQL主服务器

### 1.**在docker中创建并启动MySQL主服务器：**`端口3306`

```docker
docker run -d \
-p 3306:3306 \
-v /kelvin/mysql/master/conf:/etc/mysql/conf.d \
-v /kelvin/mysql/master/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
--name kelvin-mysql-master \
mysql:8.0.29
```

### 2.**创建MySQL主服务器配置文件**

```docker
vim /kelvin/mysql/master/conf/my.cnf

[mysqld]
# 服务器唯一id，默认值1
server-id=1
# 设置日志格式，默认值ROW
binlog_format=STATEMENT
# 二进制日志名，默认binlog
# log-bin=binlog
# 设置需要复制的数据库，默认复制全部数据库
#binlog-do-db=mytestdb
# 设置不需要复制的数据库
#binlog-ignore-db=mysql
#binlog-ignore-db=infomation_schema
```
### 3.重启MySQL容器

```docker
docker restart kelvin-mysql-master
```

### 4.**使用命令行登录MySQL主服务器 修改密码校验方式**

```docker
#进入容器：env LANG=C.UTF-8 避免容器中显示中文乱码
docker exec -it kelvin-mysql-master env LANG=C.UTF-8 /bin/bash
#进入容器内的mysql命令行
mysql -uroot -p
#修改默认密码校验方式
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```

### 5.**主机中创建slave用户**

```sql
-- 创建slave用户 
-- @ 后面跟的是指定用户连接 MySQL 的来源主机
-- % 是主机名通配符，表示用户可以从任何主机（任意IP地址）连接到 MySQL 服务器。
CREATE USER 'kelvin_slave'@'%';
-- 设置密码
ALTER USER 'kelvin_slave'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
-- 授予复制权限
GRANT REPLICATION SLAVE ON *.* TO 'kelvin_slave'@'%';
-- 刷新权限
FLUSH PRIVILEGES;
```


### 6.**主机中查询master状态**

执行完此步骤后`不要再操作主服务器MYSQL`，防止主服务器状态值变化

```sql
SHOW MASTER STATUS;
```

记下`File`和`Position`的值。执行完此步骤后不要再操作主服务器MYSQL，防止主服务器状态值变化。


## MySQL从服务器

### 1.**在docker中创建并启动MySQL从服务器：**`端口3307`

```docker
docker run -d \
-p 3307:3306 \
-v /kelvin/mysql/slave1/conf:/etc/mysql/conf.d \
-v /kelvin/mysql/slave1/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
--name kelvin-mysql-slave1 \
mysql:8.0.29
```

### **2.创建MySQL从服务器配置文件**

```docker
vim /kelvin/mysql/slave1/conf/my.cnf

[mysqld]
# 服务器唯一id，每台服务器的id必须不同，如果配置其他从机，注意修改id
server-id=2
# 中继日志名，默认xxxxxxxxxxxx-relay-bin
#relay-log=relay-bin
```

### 3.重启MySQL容器

```docker
docker restart kelvin-mysql-slave1
```

### 4.**使用命令行登录MySQL从服务器**

```docker
#进入容器：
docker exec -it kelvin-mysql-slave1 env LANG=C.UTF-8 /bin/bash
#进入容器内的mysql命令行
mysql -uroot -p
#修改默认密码校验方式
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```

### 5.**在从机上配置主从关系**

在**从机**上执行以下SQL操作：

```docker
## 跟上面主服务器的binlog一样！
CHANGE MASTER TO MASTER_HOST='114.132.182.93', 
MASTER_USER='kelvin_slave',MASTER_PASSWORD='123456', MASTER_PORT=3306,
MASTER_LOG_FILE='binlog.000003',MASTER_LOG_POS=1354; 
```


## 再创建一台从服务器

```docker
docker run -d \
-p 3308:3306 \
-v /kelvin/mysql/slave2/conf:/etc/mysql/conf.d \
-v /kelvin/mysql/slave2/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
--name kelvin-mysql-slave2 \
mysql:8.0.29

vim /kelvin/mysql/slave2/conf/my.cnf

[mysqld]
# 服务器唯一id，每台服务器的id必须不同，如果配置其他从机，注意修改id
server-id=3
# 中继日志名，默认xxxxxxxxxxxx-relay-bin
#relay-log=relay-bin

docker restart kelvin-mysql-slave2

#进入容器：
docker exec -it kelvin-mysql-slave2 env LANG=C.UTF-8 /bin/bash
#进入容器内的mysql命令行
mysql -uroot -p
#修改默认密码校验方式
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';

## 跟上面主服务器的binlog一样！
CHANGE MASTER TO MASTER_HOST='114.132.182.93', 
MASTER_USER='kelvin_slave',MASTER_PASSWORD='123456', MASTER_PORT=3306,
MASTER_LOG_FILE='binlog.000003',MASTER_LOG_POS=1354; 
```

## 启动主从复制

### 1.在第一台从服务器执行

```sql
START SLAVE;
-- 查看状态（不需要分号）
SHOW SLAVE STATUS\G
```


### 2.在第二台从服务器执行

```sql
START SLAVE;
-- 查看状态（不需要分号）
SHOW SLAVE STATUS\G
```

## 测试主从同步

在主机中执行以下SQL，在从机中查看数据库、表和数据是否已经被同步

```sql
CREATE DATABASE db_user;
USE db_user;
CREATE TABLE t_user (
 id BIGINT AUTO_INCREMENT,
 uname VARCHAR(30),
 PRIMARY KEY (id)
);
INSERT INTO t_user(uname) VALUES('zhang3');
INSERT INTO t_user(uname) VALUES(@@hostname);
```

确实有了！

