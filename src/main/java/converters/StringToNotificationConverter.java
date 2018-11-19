
package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.NotificationRepository;
import domain.Notification;

@Component
@Transactional
public class StringToNotificationConverter implements Converter<String, Notification> {

	@Autowired
	private NotificationRepository	notificationRepository;


	@Override
	public Notification convert(final String text) {
		Notification result;
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.notificationRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
