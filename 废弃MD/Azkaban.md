# Azkaban

## job定义

- job1

```json
type=command
command=echo 'start'
```

- job2

```json
type=command
dependencies=job1
command=echo 'job2Start'
```

- job3

```yaml
type=command
dependencies=job1
command=echo 'job3Start'
```

- job4

```yaml
type=command
dependencies=job2,job3
command=echo 'Stop'
```





## 脚本执行

- sh1

```sh
spark-submit --class js01 ./js01.jar
```

- sh2

```sh
spark-submit --class js02 ./js02.jar
```



- job1

```yaml
type=command
command=echo 'start'
```

- job2

```yaml
type=command
dependencies=job1
command=sh /sh1.sh
```

- job3

```yaml
type=command
dependencies=job1
command=sh /sh2.sh
```

- job4

```yaml
type=command
dependencies=job2,job3
command=echo 'stop'
```

4个job需要压缩在一起为`zip`格式后上传