package com.company.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.company.system" })
@EnableEurekaClient
@Configuration
@EnableAutoConfiguration
//@EnableRedisHttpSession
//@EnableRedisHttpSession 
@ComponentScan ("com.company.security,com.company.system.*,com.company.system.orderService")
@MapperScan ("com.company.security.mapper")
@ImportResource ({ "classpath:hessian/hessian-client.xml", "classpath:hessian/hessian-server.xml" })
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400*30)
public class SecurityAccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityAccessApplication.class, args);
		
		try {
			
		}
		catch(Throwable e)
		{
		   e.printStackTrace();	
		}
	}
}
