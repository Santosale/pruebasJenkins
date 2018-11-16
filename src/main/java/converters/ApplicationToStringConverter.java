package converters;

import org.springframework.core.convert.converter.Converter;

import domain.Application;

public class ApplicationToStringConverter implements Converter<Application, String> {

	@Override
	public String convert(Application application) {
		String result;

		if (application == null) {
			result = null;
		} else {
			result = String.valueOf(application.getId());
		}

		return result;
	}
	
}
