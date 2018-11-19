
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "name")
})
public class Tag extends DomainEntity {

	private String				name;

	private Collection<Bargain>	bargains;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@NotNull
	@Valid
	@ManyToMany
	public Collection<Bargain> getBargains() {
		return this.bargains;
	}

	public void setBargains(final Collection<Bargain> bargains) {
		this.bargains = bargains;
	}

}
