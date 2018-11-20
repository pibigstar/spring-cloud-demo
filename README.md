# SpringCloud 学习例子

## 配置文件服务器(config-server)
> 使用配置服务来保存各个服务的配置文件

1. 添加依赖
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
2. 打开配置服务功能
```java
@SpringBootApplication
@EnableConfigServer // 开启配置服务器功能
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```
3. 设置配置文件
```yaml
server:
  port: 8081
spring:
  application:
    name: config-server

  # 配置配置文件地址
  cloud:
    config:
      default-label: feat-5  # 设置分支
      server:
        git:
          # git仓库地址
          uri: ://github.com/pibigstar/spring-cloud-demo
          # 配置仓库下的路径
          search-paths: spring-cloud-config
          # 配置仓库用户名
          username:
          # 配置仓库密码
          password:
```

## 从配置服务器中读取配置(config-client)

1. 添加依赖

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```
2. 配置配置文件

必须是这个名字，不然不是报错，就是拿不到信息

**bootstrap.properties** 
```properties
server.port=8901
spring.application.name=config-client

# 配置服务器服务中心地址
spring.cloud.config.uri=http://localhost:8081/
spring.cloud.config.label=feat-5
spring.cloud.config.profile=dev

#dev开发环境配置文件
#test测试环境
#pro正式环境
```
3. 使用

```java
@Value("${name}")
private String name;
@Value("${message}")
private String message;
```


