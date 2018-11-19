
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Company extends Actor implements Surveyer {

	private String	companyName;

	private String	type;


	@NotBlank
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	@Pattern(regexp = "^(SL||SA||AUTONOMO||COOPERATIVA)$")
	@NotBlank
	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

}
