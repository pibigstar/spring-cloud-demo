package com.pibigstar.servicefeign.service;

import org.springframework.stereotype.Component;

/**
 * @author pibigstar
 * @create 2018-11-18 20:35
 * @desc HelloService的熔断器
 **/
@Component
public class HelloServiceHystrix implements IHelloService{
    @Override
    public String sayHelloFromFeignClient(String name) {
        return "this is have a error, sorry:" + name;
    }
}
