
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Internationalization;

@Component
@Transactional
public class InternationalizationToStringConverter implements Converter<Internationalization, String> {

	@Override
	public String convert(final Internationalization internationalization) {
		String result;

		if (internationalization == null)
			result = null;
		else
			result = String.valueOf(internationalization.getId());

		return result;
	}

}
