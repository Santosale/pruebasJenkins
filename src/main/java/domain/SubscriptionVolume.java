
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"customer_id", "volume_id"
	})
})
public class SubscriptionVolume extends DomainEntity {

	private CreditCard 	creditCard;

	private Customer	customer;

	private Volume		volume;

	@NotNull
	@Valid
	public CreditCard getCreditCard() {
		return this.creditCard;
	}
	
	public void setCreditCard(final CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	
	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Volume getVolume() {
		return this.volume;
	}

	public void setVolume(final Volume volume) {
		this.volume = volume;
	}

}
