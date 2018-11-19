
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Groupon;

@Component
@Transactional
public class GrouponToStringConverter implements Converter<Groupon, String> {

	@Override
	public String convert(final Groupon groupon) {
		String result;

		if (groupon == null)
			result = null;
		else
			result = String.valueOf(groupon.getId());

		return result;
	}

}
