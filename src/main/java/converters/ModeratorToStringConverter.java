package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Moderator;

@Component
@Transactional
public class ModeratorToStringConverter implements Converter<Moderator, String> {

	@Override
	public String convert(Moderator moderator) {
		String result;

		if (moderator == null) {
			result = null;
		} else {
			result = String.valueOf(moderator.getId());
		}

		return result;
	}

		

}
