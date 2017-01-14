package com.jt.sb.run;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.jt.sb.conf.ApplicationConfig;
import com.jt.sb.conf.JobConfig;

@Component
public class Application {

	protected static Logger log = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		new Application().run();				
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
		log.info("Completed in " + elapsedTime + " seconds.");
	}
	
	public void run() {
		try {
			log.info("Starting spring batch process...");
			ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
			ApplicationContext jobContext = new AnnotationConfigApplicationContext(JobConfig.class);
	        JobLauncher jobLauncher = (JobLauncher) jobContext.getBean("jobLauncher");
	        ApplicationConfig appConfig = (ApplicationConfig) context.getBean("appConfig");
	       	        
	        String uri = appConfig.get("source.dir");
	        System.out.println(uri);
	        File dir = new File(uri);
	        File[] directoryListing = dir.listFiles();
	        
	        // Iterate through nigo datafiles in designated path
	        if (directoryListing != null) {	        	
	        	for (File datafile : directoryListing) {	        			        		
        			log.info("Processing " + datafile + "...");
        			
        			// Get job bean
	    	        Job job = (Job) jobContext.getBean("CustomJob");
	    	        
	    	        // Set job parameters
	        		JobParametersBuilder jpBuilder = new JobParametersBuilder();
	        		jpBuilder.addString("pathToFile", datafile.getPath());
	        		
	        		// Launch nigo reader job
	                JobExecution execution = jobLauncher.run(job, jpBuilder.toJobParameters());
	                
	                Integer readCount = (execution.getExecutionContext().get("readCount") != null) ? (Integer) execution.getExecutionContext().get("readCount") : 0;
	                Integer writeCount = (execution.getExecutionContext().get("writeCount") != null) ? (Integer) execution.getExecutionContext().get("writeCount") : 0;

                	log.info("Items read from datafile: " + readCount);
                	log.info("Items written to staging table: " + writeCount);
        	    }
	        } else {
	          log.error("Not a directory!");
	        }
		} catch (Exception e) {
			log.info("A fatal error has a occured. Please check error logs for more details. Process is exiting...");
			log.error("Fatal Error", e);
			System.exit(-1);
		}
	}
}
