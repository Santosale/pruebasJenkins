
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Bargain;

@Component
@Transactional
public class BargainToStringConverter implements Converter<Bargain, String> {

	@Override
	public String convert(final Bargain bargain) {
		String result;

		if (bargain == null)
			result = null;
		else
			result = String.valueOf(bargain.getId());

		return result;
	}

}
