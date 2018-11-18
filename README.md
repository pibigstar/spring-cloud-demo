# SpringCloud 学习例子

## Zuul路由转发
> Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，
比如／api/user转发到到user服务，/api/shop转发到到shop服务。
zuul默认和Ribbon结合实现了负载均衡的功能。


## feign使用Zuul

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

## 浏览器中调用
通过不同的地址调用不同的服务

http://localhost:8803/api-a/hello?name=pibigstar

http://localhost:8803/api-b/hello?name=pibigstar
