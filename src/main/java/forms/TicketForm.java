package forms;

import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import domain.CreditCard;
import domain.Raffle;

public class TicketForm {
	
	private Raffle raffle;

	private CreditCard creditCard;

	private int amount;
	
	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Raffle getRaffle() {
		return raffle;
	}

	public void setRaffle(Raffle raffle) {
		this.raffle = raffle;
	}

	@Valid
	@ManyToOne(optional = true)
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@Min(1)
	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

}
