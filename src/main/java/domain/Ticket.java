package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Ticket extends DomainEntity {

	private String code;
	
	private User user;
	
	private Raffle raffle;
	
	private CreditCard creditCard;
	
	private boolean isGift;
	
	@NotBlank
	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
	
	@NotNull
	@Valid
	@ManyToOne(optional=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@NotNull
	@Valid
	@ManyToOne(optional=false)
	public Raffle getRaffle() {
		return raffle;
	}

	public void setRaffle(Raffle raffle) {
		this.raffle = raffle;
	}
	
	@Valid
	@ManyToOne(optional=true)
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	
	public boolean getIsGift() {
		return this.isGift;
	}
	
	public void setIsGift(final boolean isGift) {
		this.isGift = isGift;
	}
	
}
