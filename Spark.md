# 环境搭建

## 本地环境搭建

- #### 准备工作

  - 安装JDK
  - Scala安装包 -- Windows
  - Spark安装包

​	

- #### 操作

  - 将安装包上传到node1
  - 解压spark安装包

  ##### 更改文件权限

  chown -R root 路径

  chgrp -R root 路径

  - 更改长链接为软链

  ln -s 长名 软连

- #### 解压操作

  - tar -zxvf 压缩包 -C 目录

- #### 文件名称更改

  - mv 文件名 更改后



- ### 文件提交与运行

  - --master 谁来提供资源
  - bin/spark-submit --class 类名 --master 运行环境(local本机) jar包路径 



- ### Yarn环境配置

  - /opt/module/spark-yarn/conf
  - 在spark-env.sh最后面添加 YARN_CONF_DIR=/opt/module/hadoop/etc/hadoop

# Spark代码开发

- ## 工程创建 - Manve