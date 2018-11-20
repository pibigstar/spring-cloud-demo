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


## 服务过滤

1. filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
- pre：路由之前
- routing：路由之时
- post： 路由之后
- error：发送错误调用
2. filterOrder：过滤的顺序
3. shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
4. run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问
