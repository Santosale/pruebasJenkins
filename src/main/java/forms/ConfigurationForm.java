
package forms;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

import domain.Configuration;

public class ConfigurationForm {

	private Configuration	configuration;
	private String			welcomeMessage;


	@Valid
	public Configuration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(final Configuration configuration) {
		this.configuration = configuration;
	}

	@NotBlank
	public String getWelcomeMessage() {
		return this.welcomeMessage;
	}

	public void setWelcomeMessage(final String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

}
