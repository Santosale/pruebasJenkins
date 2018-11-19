package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.RaffleRepository;

import domain.Raffle;

@Component
@Transactional
public class StringToRaffleConverter implements Converter<String, Raffle> {

	@Autowired
	RaffleRepository	raffleRepository;


	@Override
	public Raffle convert(final String text) {
		Raffle result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.raffleRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
