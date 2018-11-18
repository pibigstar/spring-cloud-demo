# SpringCloud 学习例子

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

