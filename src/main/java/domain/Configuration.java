
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Access(AccessType.PROPERTY)
public class Configuration extends DomainEntity {

	private String	name;

	private String	slogan;

	private String	email;

	private String	banner;

	private String	defaultImage;

	private String	defaultAvatar;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@NotBlank
	public String getSlogan() {
		return this.slogan;
	}

	public void setSlogan(final String slogan) {
		this.slogan = slogan;
	}

	@Email
	@NotBlank
	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	@URL
	@NotBlank
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getBanner() {
		return this.banner;
	}

	public void setBanner(final String banner) {
		this.banner = banner;
	}

	@NotBlank
	@URL
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getDefaultImage() {
		return this.defaultImage;
	}

	public void setDefaultImage(final String defaultImage) {
		this.defaultImage = defaultImage;
	}

	@NotBlank
	@URL
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getDefaultAvatar() {
		return this.defaultAvatar;
	}

	public void setDefaultAvatar(final String defaultAvatar) {
		this.defaultAvatar = defaultAvatar;
	}

}
