
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Plan;

@Component
@Transactional
public class PlanToStringConverter implements Converter<Plan, String> {

	@Override
	public String convert(final Plan plan) {
		String result;

		if (plan == null)
			result = null;
		else
			result = String.valueOf(plan.getId());

		return result;
	}

}
