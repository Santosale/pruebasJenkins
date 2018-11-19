
package converters;

import javax.transaction.Transactional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Evaluation;

@Component
@Transactional
public class EvaluationToStringConverter implements Converter<Evaluation, String> {

	@Override
	public String convert(final Evaluation evaluation) {
		String result;

		if (evaluation == null)
			result = null;
		else
			result = String.valueOf(evaluation.getId());

		return result;
	}

}
