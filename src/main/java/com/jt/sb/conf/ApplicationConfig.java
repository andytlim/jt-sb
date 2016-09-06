package com.jt.sb.conf;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan(basePackages = "org.tiaa.bi.batch")
public class ApplicationConfig {
	
    @Bean(name="someDataSource")
    public DataSource burstingDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("teradata.driver");
        dataSource.setUrl(ApplicationProperties.get("some.host"));
        dataSource.setUsername(ApplicationProperties.get("some.username"));
        dataSource.setPassword(ApplicationProperties.get("some.password"));
        return dataSource;
    }
}
