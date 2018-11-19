package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Raffle;

@Component
@Transactional
public class RaffleToStringConverter implements Converter<Raffle, String>{

	@Override
	public String convert(Raffle raffle) {
		String result;

		if (raffle == null) {
			result = null;
		} else {
			result = String.valueOf(raffle.getId());
		}

		return result;
	}

}
