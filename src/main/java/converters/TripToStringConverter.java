package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Trip;

@Component
@Transactional
public class TripToStringConverter implements Converter<Trip, String>{
	
	@Override
	public String convert(Trip trip) {
		String result;

		if (trip == null) {
			result = null;
		} else {
			result = String.valueOf(trip.getId());
		}

		return result;
	}

}
