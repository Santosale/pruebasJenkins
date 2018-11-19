
package utilities;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.internal.constraintvalidators.URLValidator;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationFactory;

public class URLCollectionValidator implements ConstraintValidator<URLCollection, Collection<String>> {

	@Override
	public void initialize(final URLCollection constraintAnnotation) {

	}

	@Override
	public boolean isValid(final Collection<String> value, final ConstraintValidatorContext context) {
		if (value == null)
			return false;

		final AnnotationDescriptor<URL> descriptor = new AnnotationDescriptor<URL>(URL.class);
		final URL url = AnnotationFactory.create(descriptor);
		final URLValidator urlValidator = new URLValidator();
		urlValidator.initialize(url);

		for (final String s : value)
			if (!urlValidator.isValid(s, context) || s.trim().equals(""))
				return false;
		return true;
	}

}
