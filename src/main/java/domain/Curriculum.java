
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Curriculum extends DomainEntity {

	private String	ticker;

	private String	fullNamePR;

	private String	photoPR;

	private String	emailPR;

	private String	phonePR;

	private String	linkedinPR;

	private Ranger	ranger;


	//@Column(unique = true)
	@NotBlank
	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(final String ticker) {
		this.ticker = ticker;
	}

	@NotBlank
	public String getFullNamePR() {
		return this.fullNamePR;
	}

	public void setFullNamePR(final String fullNamePR) {
		this.fullNamePR = fullNamePR;
	}

	@URL
	@NotBlank
	public String getPhotoPR() {
		return this.photoPR;
	}

	public void setPhotoPR(final String photoPR) {
		this.photoPR = photoPR;
	}

	@NotBlank
	@Email
	public String getEmailPR() {
		return this.emailPR;
	}

	public void setEmailPR(final String emailPR) {
		this.emailPR = emailPR;
	}

	// "+CC (AC) PN", "+CC PN", or "PN":"+CC" denotes a country code in range
	// "+1" up to "+999", "(AC)" denotes an area code in range "(1)" up to
	// "(999)", and "PN" denotes a number that must have at least four digits.
	// Phone numbers with pattern "PN" must be added automatically a default
	// country, which is a parameter that can be changed by administrators. If a
	// phone number that does not match this pattern is entered, the system must
	// ask for confirmation
	@NotBlank
	public String getPhonePR() {
		return this.phonePR;
	}

	public void setPhonePR(final String phonePR) {
		this.phonePR = phonePR;
	}

	@URL
	@NotBlank
	public String getLinkedinPR() {
		return this.linkedinPR;
	}

	public void setLinkedinPR(final String linkedinPR) {
		this.linkedinPR = linkedinPR;
	}

	@Valid
	@NotNull
	@OneToOne(optional = false)
	public Ranger getRanger() {
		return this.ranger;
	}

	public void setRanger(final Ranger ranger) {
		this.ranger = ranger;
	}

}
