
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.GrouponRepository;
import domain.Groupon;

@Component
@Transactional
public class StringToGrouponConverter implements Converter<String, Groupon> {

	@Autowired
	GrouponRepository	grouponRepository;


	@Override
	public Groupon convert(final String text) {
		Groupon result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.grouponRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
