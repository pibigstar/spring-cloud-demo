package com.pibigstar.servicefeign.controller;

import com.pibigstar.servicefeign.service.IHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pibigstar
 * @create 2018-11-18 19:37
 * @desc
 **/
@RestController
public class HelloController {

    @Autowired
    private IHelloService helloService;// 因为这个Bean是在程序启动的时候注入的，编译器感知不到，所以报错。

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name") String name){
        return helloService.sayHelloFromFeignClient(name);
    }

}
