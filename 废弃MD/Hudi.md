# [Hudi](https://github.com/apache/hudi)

1. 下载CentOS7 ，配置环境 => maven ，java8，scala2.12
2. 配置HADOOP Spark



# HADOOP

配置HADOOPLiunx环境变量

```sh
export HADOOP_HOME=/xxx/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_YARN_HOME=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/bin
```

配置HADOOP自身环境变量

```sh
vim /hadoop/etc/hadoop/hadoop-env.sh
export JAVA_HOME=/jdk
export HADOOP_HOME=/hadoop #HADOOP根
```

配置HDFS核心

```sh
vim /hadoop/etc/hadoop/core-site.xml
```

```xml
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://namehost:8020</value>
</property>

#临时文件
<property>
    <name>hadoop.tmp.dir</name>
    <value>/hadoop/datas/tmp</value>
</property>

#网页登陆用户名
<property>
    <name>hadoop.http.staticuser.user</name>
    <value>root</value>
</property>
```

```sh
mkdir -p /hadoop/datas/tmp
```



配置HDFS分布式

```sh
vim /hadoop/etc/hadoop/hdfs-site.xml
```

```xml
<property>
    <name>dfs.namenode.name.dir</name>
    <value>/hadoop/datas/dfs/nn</value>
</property>

<property>
    <name>dfs.replication</name>
    <value>1</value>
</property>

<property>
    <name>dfs.permissions.endabled</name>
    <value>false</value>
</property>

<property>
    <name>dfs.datanode.data.dir.perm</name>
    <value>750</value>
</property>
```



配置节点信息

```sh
vim /hadoop/etc/hadoop/slaves
本机hostname
```



首次启动

```sh
#只格式化一次
hdfs namenode -format

#启动集群
hdfs-daemon.sh start namenode
hdfs-daemon.sh start datanode
```





# Spark

1. 上传软件包，解压，设置软链接

```sh
ln -s /spakr-xxxxhadoopxxxx /spark
```

2. 安装scala

```sh
tar -zxvf /xxxx/scalaxxx.tar -C /xxxx/scala

#创建软链
ln -s /xxx/scalaxxx /xxx/scala

#环境变量
vim /etc/profile
export SHOME=/xxx/scala
export PATH=$PATH:$SHOME/bin
```

3. 修改Spark配置名称

```sh
cd /spark/conf
mv spark-env.sh.template spark-env.sh

#增加
JAVA_HOME=jdk/bin
SCALA_HOME=/xxx/scala
HADOOP_CONF_DIR=/XXXX/hadoop/etc/hadoop
```

4. 启动spark测试

```sh
cd /xxxx/spark
bin/spark-shell --master local[*]
```

5. 上传文件到HDFS进行读取测试

```sh
hdfs dfs -put file HFSFilePath
```

6. 读取

