package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Ticket;

@Component
@Transactional
public class TicketToStringConverter implements Converter<Ticket, String>{

	@Override
	public String convert(Ticket ticket) {
		String result;

		if (ticket == null) {
			result = null;
		} else {
			result = String.valueOf(ticket.getId());
		}

		return result;
	}

}
