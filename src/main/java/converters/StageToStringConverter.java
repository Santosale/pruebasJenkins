package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Stage;

@Component
@Transactional
public class StageToStringConverter implements Converter<Stage, String> {

	@Override
	public String convert(final Stage stage) {
		String result;

		if (stage == null)
			result = null;
		else
			result = String.valueOf(stage.getId());

		return result;
	}

}
