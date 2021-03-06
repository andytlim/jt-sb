package com.jt.sb.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import com.jt.sb.model.Row;

public class RowProcessor implements ItemProcessor<Row, Row> {

	protected static Logger log = LoggerFactory
			.getLogger(RowProcessor.class);
	
	public RowProcessor() {
	}
	
	@Override
	public Row process(Row row) throws Exception {
	    return row;
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		// Put stuff you want to do before the processor phase starts
	}
	
	@AfterStep
	public void afterStep(StepExecution stepExecution) {
		// Put stuff you want to do after the entire processor phase ends		
	}
}
