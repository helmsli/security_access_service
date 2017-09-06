package com.company.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
//@EnableRedisHttpSession

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400*30)
@ComponentScan ("com.company.security")
@MapperScan ("com.company.security.mapper")
@ImportResource ({ "classpath:hessian/hessian-client.xml", "classpath:hessian/hessian-server.xml" })
public class SecurityAccessApplication {

	public static void main(String[] args) {
		try {
		SpringApplication.run(SecurityAccessApplication.class, args);
		}
		catch(Throwable e)
		{
		   e.printStackTrace();	
		}
	}
}
