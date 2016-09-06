package org.tiaa.bi.batch.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.tiaa.bi.batch.model.Row;

public class CustomRowMapper implements FieldSetMapper<Row> {
	
	@Override
	public Row mapFieldSet(FieldSet fieldSet) {
		Row result = new Row();
		result.setSomeField(fieldSet.readString(0));
		
		return result;
	}
}
