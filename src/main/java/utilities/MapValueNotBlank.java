
package utilities;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.internal.constraintvalidators.NotBlankValidator;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationFactory;

public class MapValueNotBlank implements ConstraintValidator<NotBlankMap, Map<Integer, String>> {

	@Override
	public void initialize(final NotBlankMap constraintAnnotation) {

	}

	@Override
	public boolean isValid(final Map<Integer, String> value, final ConstraintValidatorContext context) {
		if (value == null)
			return false;

		final AnnotationDescriptor<NotBlank> descriptor = new AnnotationDescriptor<NotBlank>(NotBlank.class);
		final NotBlank notBlank = AnnotationFactory.create(descriptor);
		final NotBlankValidator notBlankValidator = new NotBlankValidator();
		notBlankValidator.initialize(notBlank);

		for (final String s : value.values())
			if (!notBlankValidator.isValid(s, context))
				return false;
		return true;
	}

}
