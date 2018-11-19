
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "minPoints,maxPoints")
})
public class Level extends DomainEntity {

	private String	name;

	private String	image;

	private int		minPoints;

	private int		maxPoints;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@URL
	@NotBlank
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getImage() {
		return this.image;
	}

	public void setImage(final String image) {
		this.image = image;
	}

	public int getMinPoints() {
		return this.minPoints;
	}

	public void setMinPoints(final int minPoints) {
		this.minPoints = minPoints;
	}

	public int getMaxPoints() {
		return this.maxPoints;
	}

	public void setMaxPoints(final int maxPoints) {
		this.maxPoints = maxPoints;
	}

}
