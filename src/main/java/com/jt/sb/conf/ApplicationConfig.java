package com.jt.sb.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "com.jt.sb")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
	
	protected static Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
	
	@Autowired
	private Environment env;
	
	@Bean
	public ApplicationConfig appConfig() {
		return new ApplicationConfig();
	}

	public String get(String key) {
		return env.getProperty(key);
	}
}

