
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.SubscriptionVolume;

@Component
@Transactional
public class SubscriptionVolumeToStringConverter implements Converter<SubscriptionVolume, String> {

	@Override
	public String convert(final SubscriptionVolume subscriptionVolume) {
		String result;

		if (subscriptionVolume == null)
			result = null;
		else
			result = String.valueOf(subscriptionVolume.getId());

		return result;
	}

}
