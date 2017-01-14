package com.jt.sb.conf;

import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.jt.sb.batch.CountItemStepExecutionListener;
import com.jt.sb.batch.RowMapper;
import com.jt.sb.batch.RowProcessor;
import com.jt.sb.model.Row;

@Configuration
@ComponentScan(basePackages = "com.jt.sb.conf")
@EnableBatchProcessing
public class JobConfig {
	
	private static final String OVERRIDDEN_BY_EXPRESSION = null;
	
	@Autowired
	ApplicationConfig appConfig;
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;
	
	@Bean
	@StepScope
    public FlatFileItemReader<Row> reader(@Value("#{jobParameters[pathToFile]}") String pathToFile) throws MalformedURLException {		
        FlatFileItemReader<Row> reader = new FlatFileItemReader<Row>();
        reader.setResource(new FileSystemResource(pathToFile));
        //reader.setLinesToSkip(1);
        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        reader.setLineMapper(new DefaultLineMapper<Row>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(new RowMapper());
        }});
        
        return reader;
    }
 
	@Bean
	@StepScope
	public RowProcessor processor(@Value("#{jobParameters[something]}") String reportId) {
		return new RowProcessor();
	}
	
//	@Bean
//	public ItemWriter<Row> writer(){
//		JdbcBatchItemWriter<Row> itemWriter = new JdbcBatchItemWriter<Row>();
//		itemWriter.setSql("INSERT INTO SOME_TABLE VALUES (:someField)");
//		itemWriter.setDataSource(stagingDataSource());
//		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Row>());
//		return itemWriter;
//	}
	
	@Bean
	public FlatFileItemWriter<Row> writer(){
		FlatFileItemWriter<Row> itemWriter = new FlatFileItemWriter<Row>();
		DelimitedLineAggregator<Row> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter("|");
		BeanWrapperFieldExtractor<Row> fieldExtractor = new BeanWrapperFieldExtractor<Row>();
		fieldExtractor.setNames(new String[]{"name", "message", "number"});
		lineAggregator.setFieldExtractor(fieldExtractor);
		itemWriter.setLineAggregator(lineAggregator);
		itemWriter.setResource(new FileSystemResource(appConfig.get("target.dir")+"/output.txt"));
		return itemWriter;
	}
	
    @Bean
    public Job CustomJob() throws MalformedURLException {
        return jobs.get("SpringJob")
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
