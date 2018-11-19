package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Chirp;

@Component
@Transactional
public class ChirpToStringConverter implements Converter<Chirp, String> {

	@Override
	public String convert(Chirp chirp) {
		String result;

		if (chirp == null) {
			result = null;
		} else {
			result = String.valueOf(chirp.getId());
		}

		return result;
	}

		

}
