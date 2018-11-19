
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

@Entity
@Access(AccessType.PROPERTY)
public class Identifier extends DomainEntity {

	private int	firstCounter;

	private int	secondCounter;

	private int	thirdCounter;

	private int	fourthCounter;

	private int	fivethCounter;

	@Min(0)
	public int getFirstCounter() {
		return this.firstCounter;
	}

	public void setFirstCounter(final int firstCounter) {
		this.firstCounter = firstCounter;
	}

	@Min(0)
	public int getSecondCounter() {
		return this.secondCounter;
	}

	public void setSecondCounter(final int secondCounter) {
		this.secondCounter = secondCounter;
	}

	@Min(0)
	public int getThirdCounter() {
		return this.thirdCounter;
	}

	public void setThirdCounter(final int thirdCounter) {
		this.thirdCounter = thirdCounter;
	}

	@Min(0)
	public int getFourthCounter() {
		return this.fourthCounter;
	}

	public void setFourthCounter(final int fourthCounter) {
		this.fourthCounter = fourthCounter;
	}

	@Min(0)
	public int getFivethCounter() {
		return this.fivethCounter;
	}

	public void setFivethCounter(final int fivethCounter) {
		this.fivethCounter = fivethCounter;
	}
	
}
