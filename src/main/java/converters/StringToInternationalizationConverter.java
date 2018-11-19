
package converters;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.InternationalizationRepository;
import domain.Internationalization;

@Component
@Transactional
public class StringToInternationalizationConverter implements Converter<String, Internationalization> {

	@Autowired
	private InternationalizationRepository	internationalizationRepository;


	@Override
	public Internationalization convert(final String text) {
		Internationalization result;
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.internationalizationRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
