
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Advertisement extends DomainEntity {

	private String		title;

	private String		urlBanner;

	private String		urlTarget;

	private CreditCard	creditCard;

	private Agent		agent;

	private boolean		hasTaboo;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@NotBlank
	@URL
	public String getUrlBanner() {
		return this.urlBanner;
	}

	public void setUrlBanner(final String urlBanner) {
		this.urlBanner = urlBanner;
	}

	@NotBlank
	@URL
	public String getUrlTarget() {
		return this.urlTarget;
	}

	public void setUrlTarget(final String urlTarget) {
		this.urlTarget = urlTarget;
	}

	@NotNull
	@Valid
	public CreditCard getCreditCard() {
		return this.creditCard;
	}

	public void setCreditCard(final CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Agent getAgent() {
		return this.agent;
	}

	public void setAgent(final Agent agent) {
		this.agent = agent;
	}

	public boolean getHasTaboo() {
		return this.hasTaboo;
	}

	public void setHasTaboo(final boolean hasTaboo) {
		this.hasTaboo = hasTaboo;
	}

}
