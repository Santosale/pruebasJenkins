package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"countryCode", "messageCode"
	})
})
public class Internationalization extends DomainEntity {

	private String	countryCode;

	private String	messageCode;

	private String	value;


	@NotBlank
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	@NotBlank
	public String getMessageCode() {
		return this.messageCode;
	}

	public void setMessageCode(final String messageCode) {
		this.messageCode = messageCode;
	}

	@NotBlank
	@Column(length = 1000000000)
	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
