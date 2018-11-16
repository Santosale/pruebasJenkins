package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Explorer;

@Component
@Transactional
public class ApplicantToStringConverter implements Converter<Explorer, String>{

	@Override
	public String convert(Explorer explorer) {
		String result;

		if (explorer == null) {
			result = null;
		} else {
			result = String.valueOf(explorer.getId());
		}

		return result;
	}
	
}
