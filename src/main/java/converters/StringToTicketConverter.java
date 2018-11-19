package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.TicketRepository;

import domain.Ticket;

@Component
@Transactional
public class StringToTicketConverter implements Converter<String, Ticket> {

	@Autowired
	TicketRepository	ticketRepository;


	@Override
	public Ticket convert(final String text) {
		Ticket result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.ticketRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
