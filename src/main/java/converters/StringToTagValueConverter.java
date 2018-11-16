
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.TagValueRepository;
import domain.TagValue;

@Component
@Transactional
public class StringToTagValueConverter implements Converter<String, TagValue> {

	@Autowired
	private TagValueRepository	tagValueRepository;


	@Override
	public TagValue convert(final String text) {
		TagValue result;
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.tagValueRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
