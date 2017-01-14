package com.jt.sb.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import com.jt.sb.model.Row;

public class RowMapper implements FieldSetMapper<Row> {
	
	@Override
	public Row mapFieldSet(FieldSet fieldSet) {
		Row result = new Row();
		result.setNumber(fieldSet.readInt(0));
		result.setName(fieldSet.readString(1));
		result.setMessage(fieldSet.readString(2));
		
		return result;
	}
}
