# SpringCloud 学习例子
## 熔断器
> 由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。

断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。

## Feign中使用熔断器

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