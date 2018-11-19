
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"user_id", "plan_id"
	})
})
public class Subscription extends DomainEntity {

	private String		payFrecuency;

	private CreditCard	creditCard;

	private User		user;

	private Plan		plan;


	@NotBlank
	@Pattern(regexp = "^(Monthly|Quarterly|Anually)$")
	public String getPayFrecuency() {
		return this.payFrecuency;
	}

	public void setPayFrecuency(final String payFrecuency) {
		this.payFrecuency = payFrecuency;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public CreditCard getCreditCard() {
		return this.creditCard;
	}

	public void setCreditCard(final CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@NotNull
	@Valid
	@OneToOne(optional = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Plan getPlan() {
		return this.plan;
	}

	public void setPlan(final Plan plan) {
		this.plan = plan;
	}

}
