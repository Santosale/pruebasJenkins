package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.EducationRecord;

@Component
@Transactional
public class EducationRecordToStringConverter implements Converter<EducationRecord, String>{

	@Override
	public String convert(EducationRecord educationRecord) {
		String result;

		if (educationRecord == null) {
			result = null;
		} else {
			result = String.valueOf(educationRecord.getId());
		}

		return result;
	}
	
}
