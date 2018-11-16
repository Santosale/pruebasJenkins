
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Auditor;

@Component
@Transactional
public class AuditorToStringConverter implements Converter<Auditor, String> {

	@Override
	public String convert(final Auditor auditor) {
		String result;

		if (auditor == null)
			result = null;
		else
			result = String.valueOf(auditor.getId());

		return result;
	}

}
