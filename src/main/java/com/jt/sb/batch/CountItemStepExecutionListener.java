package com.jt.sb.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class CountItemStepExecutionListener extends
		StepExecutionListenerSupport {

	protected static Logger log = LoggerFactory
			.getLogger(CountItemStepExecutionListener.class);

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {		
		stepExecution.getJobExecution().getExecutionContext().putInt(
				"readCount",	stepExecution.getReadCount());
		
		stepExecution.getJobExecution().getExecutionContext().putInt(
				"writeCount", stepExecution.getWriteCount());

		return null;
	}
}