package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Bargain;

import repositories.BargainRepository;

@Component
@Transactional
public class StringToBargainConverter implements Converter<String, Bargain> {

	@Autowired
	private BargainRepository bargainRepository;

	@Override
	public Bargain convert(String text) {
		Bargain result;
		int id;

		try {
			if (StringUtils.isEmpty(text)) {
				result = null;
			} else {
				id = Integer.valueOf(text);
				result = this.bargainRepository.findOne(id);
			}
		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
