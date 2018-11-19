package converters;

import java.net.URLDecoder;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import security.Authority;

@Component
@Transactional
public class StringToAuthorityConverter implements Converter<String, Authority> {

	@Override
	public Authority convert(final String text) {
		Authority result;
		String parts[];

		if (StringUtils.isEmpty(text)) {
			result = null;
		} else {
			try {
				parts = text.split("\\|");
				result = new Authority();
				
				result.setAuthority(URLDecoder.decode(parts[0], "UTF-8"));

			} catch (Throwable oops) {
				throw new IllegalArgumentException(oops);
			}

		}

		return result;
	}

}
