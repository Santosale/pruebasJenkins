package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.SubscriptionNewspaper;

@Component
@Transactional
public class SubscriptionNewspaperToStringConverter implements Converter<SubscriptionNewspaper, String>{

	@Override
	public String convert(SubscriptionNewspaper subscription) {
		String result;

		if (subscription == null) {
			result = null;
		} else {
			result = String.valueOf(subscription.getId());
		}

		return result;
	}

}
