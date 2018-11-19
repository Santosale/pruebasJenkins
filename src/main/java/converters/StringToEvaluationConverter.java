package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.EvaluationRepository;

import domain.Evaluation;

@Component
@Transactional
public class StringToEvaluationConverter implements Converter<String, Evaluation> {

	@Autowired
	EvaluationRepository	evaluationRepository;


	@Override
	public Evaluation convert(final String text) {
		Evaluation result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.evaluationRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
