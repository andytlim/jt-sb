package org.tiaa.bi.batch.batch;

import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.tiaa.bi.batch.config.ApplicationProperties;
import org.tiaa.bi.batch.model.Row;

@Configuration
@EnableBatchProcessing
public class CustomJobConfig {
	
	private static final String OVERRIDDEN_BY_EXPRESSION = null;
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;
	
    public DataSource stagingDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl(ApplicationProperties.get("db.nigoStaging.jndi"));
        dataSource.setUsername(ApplicationProperties.get("db.nigoStaging.username"));
        dataSource.setPassword(ApplicationProperties.get("db.nigoStaging.password"));
         
        return dataSource;
    }
	
	@Bean
	@StepScope
    public FlatFileItemReader<Row> reader(@Value("#{jobParameters[pathToFile]}") String pathToFile) throws MalformedURLException {		
        FlatFileItemReader<Row> reader = new FlatFileItemReader<Row>();
        reader.setResource(new FileSystemResource(pathToFile));
        reader.setLinesToSkip(1);
        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        reader.setLineMapper(new DefaultLineMapper<Row>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(new CustomRowMapper());
        }});
        
        return reader;
    }
 
	@Bean
	@StepScope
	public CustomRowProcessor processor(@Value("#{jobParameters[something]}") String reportId) {
		return new CustomRowProcessor();
	}
	
	@Bean
	public ItemWriter<Row> writer(){
		JdbcBatchItemWriter<Row> itemWriter = new JdbcBatchItemWriter<Row>();
		itemWriter.setSql("INSERT INTO SOME_TABLE VALUES (:someField)");
		itemWriter.setDataSource(stagingDataSource());
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Row>());
		return itemWriter;
	}
	
    @Bean
    public Job CustomJob() throws MalformedURLException {
        return jobs.get("CustomJob")
        		.start(processFile())
        		.build();
    }
 
    @Bean
    public Step processFile() throws MalformedURLException {
        return steps.get("processFile")
                .<Row, Row> chunk(5)
                .reader(reader(OVERRIDDEN_BY_EXPRESSION))
                .processor(processor(OVERRIDDEN_BY_EXPRESSION))
                .writer(writer())
                .listener(new CountItemStepExecutionListener())
                .build();
    }
}
