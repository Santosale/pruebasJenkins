package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.SubscriptionNewspaperRepository;

import domain.SubscriptionNewspaper;

@Component
@Transactional
public class StringToSubscriptionNewspaperConverter implements Converter<String, SubscriptionNewspaper> {

	@Autowired
	private SubscriptionNewspaperRepository subscriptionRepository;
	
	@Override
	public SubscriptionNewspaper convert(String text) {
		SubscriptionNewspaper result;
		int id;

		try {
			if(StringUtils.isEmpty(text)){
				result = null;
			}else{
				id = Integer.valueOf(text);
				result = this.subscriptionRepository.findOne(id);
			}
		} catch(Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		
		return result;
	}
	
	
}
