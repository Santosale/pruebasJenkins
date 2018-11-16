
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Entity
@Access(AccessType.PROPERTY)
public class Configuration extends DomainEntity {

	private String				companyName;

	private String				banner;

	private String				welcomeMessage;

	private double				vat;

	private String				countryCode;

	private Collection<String>	spamWords;

	private double				cachedTime;

	private int					finderNumber;


	@NotBlank
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	@NotBlank
	public String getBanner() {
		return this.banner;
	}

	public void setBanner(final String banner) {
		this.banner = banner;
	}

	@NotBlank
	public String getWelcomeMessage() {
		return this.welcomeMessage;
	}

	public void setWelcomeMessage(final String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	@Min(0)
	public double getVat() {
		return this.vat;
	}

	public void setVat(final double vat) {
		this.vat = vat;
	}

	@Pattern(regexp = "^\\+(([1-9]([0-9]|[1-9])[0-9])|([1-9]([1-9]|[0-9]))|([1-9]))$")
	@NotBlank
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	@ElementCollection
	@NotNull
	public Collection<String> getSpamWords() {
		return this.spamWords;
	}

	public void setSpamWords(final Collection<String> spamWords) {
		this.spamWords = spamWords;
	}

	@Range(min = 1, max = 24)
	public double getCachedTime() {
		return this.cachedTime;
	}

	public void setCachedTime(final double cachedTime) {
		this.cachedTime = cachedTime;
	}

	@Range(min = 1, max = 100)
	public int getFinderNumber() {
		return this.finderNumber;
	}

	public void setFinderNumber(final int finderNumber) {
		this.finderNumber = finderNumber;
	}

}
