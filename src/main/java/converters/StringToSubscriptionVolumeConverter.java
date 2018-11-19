
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.SubscriptionVolumeRepository;
import domain.SubscriptionVolume;

@Component
@Transactional
public class StringToSubscriptionVolumeConverter implements Converter<String, SubscriptionVolume> {

	@Autowired
	private SubscriptionVolumeRepository	subscriptionVolumeRepository;


	@Override
	public SubscriptionVolume convert(final String text) {
		SubscriptionVolume result;
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.subscriptionVolumeRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
