package com.pibigstar.servicefeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author pibigstar
 * @create 2018-11-18 19:35
 * @desc 此接口用来消费服务，调用服务
 **/
// 此注解用来表明具体调用那个服务，并设置熔断器
@FeignClient(value = "service-hello", fallback = HelloServiceHystrix.class)
public interface IHelloService {
    @GetMapping(value = "/hello")
    String sayHelloFromFeignClient(@RequestParam(value = "name") String name);
}
