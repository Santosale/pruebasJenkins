package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.StageRepository;

import domain.Stage;

@Component
@Transactional
public class StringToStageConverter implements Converter<String, Stage> {

	@Autowired
	StageRepository	stageRepository;


	@Override
	public Stage convert(final String text) {
		Stage result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.stageRepository.findOne(id);
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
