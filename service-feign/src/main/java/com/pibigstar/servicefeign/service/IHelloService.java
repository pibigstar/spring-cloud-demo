package com.pibigstar.servicefeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author pibigstar
 * @create 2018-11-18 19:35
 * @desc
 **/

@FeignClient(value = "service-hello")
public interface IHelloService {
    @GetMapping(value = "/hello")
    String sayHelloFromFeignClient(@RequestParam(value = "name") String name);
}
