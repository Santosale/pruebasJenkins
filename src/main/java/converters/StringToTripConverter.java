package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Trip;

import repositories.TripRepository;

@Component
@Transactional
public class StringToTripConverter implements Converter<String, Trip>{

	@Autowired
	private TripRepository tripRepository;
	
	@Override
	public Trip convert(String text) {
		Trip result;
		int id;
		
		try {
			if(StringUtils.isEmpty(text)){
				result = null;
			}else{
				id = Integer.valueOf(text);
				result = this.tripRepository.findOne(id);
			}
		} catch(Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		
		return result;
	}
	
}
