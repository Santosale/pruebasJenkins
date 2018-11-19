
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.PlanRepository;
import domain.Plan;

@Component
@Transactional
public class StringToPlanConverter implements Converter<String, Plan> {

	@Autowired
	PlanRepository	planRepository;


	@Override
	public Plan convert(final String text) {
		Plan result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.planRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
