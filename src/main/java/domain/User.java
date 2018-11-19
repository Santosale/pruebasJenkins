
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
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "points")
})
public class User extends Actor {

	private boolean				isPublicWishList;

	private int					points;

	private String				avatar;

	private Collection<Bargain>	wishList;


	public boolean getIsPublicWishList() {
		return this.isPublicWishList;
	}

	public void setIsPublicWishList(final boolean isPublicWishList) {
		this.isPublicWishList = isPublicWishList;
	}

	public int getPoints() {
		return this.points;
	}

	public void setPoints(final int points) {
		this.points = points;
	}

	@URL
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	@NotBlank
	public String getAvatar() {
		return this.avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

	@Valid
	@NotNull
	@ManyToMany
	public Collection<Bargain> getWishList() {
		return this.wishList;
	}

	public void setWishList(final Collection<Bargain> wishList) {
		this.wishList = wishList;
	}

}
