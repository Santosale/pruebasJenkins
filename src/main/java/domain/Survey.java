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
public class Survey extends DomainEntity {

	private String title;
	
	private Surveyer surveyer;

	@NotBlank
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false, targetEntity = Actor.class)
	public Surveyer getSurveyer() {
		return surveyer;
	}

	public void setSurveyer(Surveyer surveyer) {
		this.surveyer = surveyer;
	}
	
}
