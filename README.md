[APACHE KAFKA QUICKSTART](https://kafka.apache.org/quickstart) Java 实现版本(基于SpringBoot).

## 打包
```sh
mvn package
```

## 运行
可直接通过 intellij idea 在本地运行.

通过接口发送消息:
```shell
curl --location --request POST 'http://localhost:8080/kafka/send' \
--header 'Content-Type: application/json' \
--data-raw '{
    "title":"kiki",
    "body":"do you love me?"
}'
```

## 代码说明
该代码为[慕课网](https://www.imooc.com/) 课程[Kafka流处理平台](https://www.imooc.com/learn/1043) 的 对应代码(略有改动).
