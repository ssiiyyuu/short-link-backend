package com.siyu.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = {"com.siyu.server.mapper"})
@ComponentScan(basePackages = {"com.siyu.common", "com.siyu.redis", "com.siyu.rabbitMQ", "com.siyu.server"})
@SpringBootApplication
public class ShortLinkServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortLinkServerApplication.class, args);
	}

}
