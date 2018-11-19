
package converters;

import java.net.URLDecoder;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.CreditCard;

@Component
@Transactional
public class StringToCreditCardConverter implements Converter<String, CreditCard> {

	@Override
	public CreditCard convert(final String text) {
		CreditCard result;
		String parts[];

		if (StringUtils.isEmpty(text))
			result = null;
		else
			try {
				parts = text.split("\\|");
				result = new CreditCard();

				result.setBrandName(URLDecoder.decode(parts[0], "UTF-8"));
				result.setHolderName(URLDecoder.decode(parts[1], "UTF-8"));
				result.setNumber(URLDecoder.decode(parts[2], "UTF-8"));
				result.setCvvcode(Integer.valueOf(URLDecoder.decode(parts[3], "UTF-8")));
				result.setExpirationMonth(Integer.valueOf(URLDecoder.decode(parts[4], "UTF-8")));
				result.setExpirationYear(Integer.valueOf(URLDecoder.decode(parts[5], "UTF-8")));

			} catch (final Throwable oops) {
				throw new IllegalArgumentException(oops);
			}

		return result;
	}

}
