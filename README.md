# SpringCloud 学习例子


通过分支来对项目归类，详情可通过Issue查看每个分支具体内容

- feat-1: 使用ribbon+restTemplate方式消费服务
- feat-2: 使用feign方式消费服务
- feat-3: 在feign方式中使用熔断器（hystrix）
- feat-4: 使用Zuul实现路由转发功能
- feat-5: 配置文件服务器(config-server)
- feat-6: 配置高可用（带注册中心）文件服务器(config-server)

## 服务的注册和发现

#### 使用

##### 1. 创建服务注册中心
1. 添加依赖
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```
2. 新增配置
```yaml
server:
  port: 8080
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```
3. 添加注解
```java
@EnableEurekaServer // 启动一个注册中心
@SpringBootApplication
public class EurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverApplication.class, args);
	}
}
```

##### 2. 创建一个服务提供者
1. 添加依赖
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```
2. 添加配置
```yaml
server:
  port: 8701

spring:
  application:
    name: service-hello  # 服务名

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8080/eureka/  # 注册中心地址
```
3. 添加注解
```java
@SpringBootApplication
// 设置为Eureka客户端
@EnableEurekaClient
@RestController
public class ServiceHelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceHelloApplication.class, args);
	}
	@Value("${server.port}")
	String port;

	@RequestMapping("/hello")
	public String home(@RequestParam(value = "name", defaultValue = "forezp") String name) {
		return "hi " + name + " ,i am from port:" + port;
	}
}
```

## feat-1 使用ribbon+restTemplate方式消费服务
>在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。
Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign。首先讲解下基于ribbon+rest。

ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。

- eureka-server: 注册中心
- service-hello: 服务提供者
- service-ribbon: 服务消费者

在浏览器上多次访问http://localhost:8801/hello?name=pibigstar，浏览器交替显示：
```
hi pibigstar,i am from port:8701
hi pibigstar,i am from port:8702
```
**核心:**
当我们通过调用restTemplate.getForObject(“http://service-hello/hello?name=”+name,String.class)方法时，已经做了负载均衡，访问了不同的端口的服务实例。

[点击查看详情](https://blog.csdn.net/forezp/article/details/81040946)

##### 1. 创建一个服务消费者
1. 添加依赖
```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>

```
2. 添加配置
```yaml
server:
  port: 8801

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8080/eureka/  # 注册中心地址

spring:
  application:
    name: service-ribbon
```
3. 添加注解
```java
@SpringBootApplication
@EnableEurekaClient     // 让注册中心找到此客户端
@EnableDiscoveryClient  // 向服务中心注册
public class ServiceRibbonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRibbonApplication.class, args);
	}

	@Bean
	@LoadBalanced //表明这个restRemplate开启负载均衡的功能。
	RestTemplate restTemplat(){
		return new RestTemplate();
	}
}
```

## feat-2 使用feign方式消费服务
> Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。 使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性， 可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。 Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。

简而言之：

- Feign 采用的是基于接口的注解
- Feign 整合了ribbon，具有负载均衡的能力
- 整合了Hystrix，具有熔断的能力

#### 使用

1. 添加依赖
```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
2. 添加配置
```yaml
server:
  port: 8802
spring:
  application:
    name: service-feign
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8080/eureka/
```
3. 添加注解
```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients  //开启Feign的功能
public class ServiceFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceFeignApplication.class, args);
	}
}
```
4. 调用服务
```java
@FeignClient(value = "service-hello", fallback = HelloServiceHystrix.class)
public interface IHelloService {
    @GetMapping(value = "/hello")
    String sayHelloFromFeignClient(@RequestParam(value = "name") String name);
}
```

## feat-3 feign方式使用熔断器（hystrix）
> 由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。

断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。

#### Feign中使用熔断器
1. 打开配置文件
```yaml
feign:
  hystrix:
    enabled: true
```
2. 创建服务无法调用成功时的处理器
需要实现IHelloService 接口，并注入到Ioc容器中
```java
@Component
public class HelloServiceHystrix implements IHelloService{
    @Override
    public String sayHelloFromFeignClient(String name) {
        return "this is have a error, sorry:" + name;
    }
}
```
3. 调用服务时设置熔断器
```java
@FeignClient(value = "service-hello", fallback = HelloServiceHystrix.class)
```


## feat-4 使用Zuul实现路由转发功能
> Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，
比如／api/user转发到到user服务，/api/shop转发到到shop服务。
zuul默认和Ribbon结合实现了负载均衡的功能。


#### feign使用Zuul

1. 添加依赖
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
```
2. 配置路由
```yaml
zuul:
  routes:
    api-a:
      path: /api-a/*
      serviceId: service-feign
    api-b:
      path: /api-b/*
      serviceId: service-feign
```
3. 启动类中开启路由转发
```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableZuulProxy // 开启路由代理
public class ServiceZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceZuulApplication.class, args);
	}
}
```

#### 浏览器中调用
通过不同的地址调用不同的服务

http://localhost:8803/api-a/hello?name=pibigstar

http://localhost:8803/api-b/hello?name=pibigstar


#### 服务过滤

1. filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
- pre：路由之前
- routing：路由之时
- post： 路由之后
- error：发送错误调用
2. filterOrder：过滤的顺序
3. shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
4. run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问


## feat-6 配置高可用文件服务器(config-server)
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




