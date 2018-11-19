
package forms;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class CompanyForm extends ActorForm {

	private String companyName;
	private String type;

	public CompanyForm() {
		super();
	}

	@NotBlank
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Pattern(regexp="^(SL||SA||AUTONOMO||COOPERATIVA)$")
	@NotBlank
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
