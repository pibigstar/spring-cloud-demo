package com.pibigstar.servicehello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public String home(@RequestParam(value = "name", defaultValue = "pibigstar") String name) {
		return "hi " + name + " ,i am from port:" + port;
	}
}
