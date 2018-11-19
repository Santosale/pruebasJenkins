
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

// @Indexed
@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "publicationDate, isPrivate, isPublished, hasTaboo, title, description")
})
public class Newspaper extends DomainEntity {

	private Date						publicationDate;

	private String						title;

	private String						description;

	private String						picture;

	private boolean						isPrivate;

	private boolean						hasTaboo;

	private boolean						isPublished;

	private User						publisher;

	private Collection<Article>			articles;

	private Collection<Advertisement>	advertisements;


	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Date getPublicationDate() {
		return this.publicationDate;
	}

	public void setPublicationDate(final Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	//@Field

	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}
	//	@Field

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@URL
	public String getPicture() {
		return this.picture;
	}

	public void setPicture(final String picture) {
		this.picture = picture;
	}

	public boolean getIsPrivate() {
		return this.isPrivate;
	}

	public void setIsPrivate(final boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public boolean getHasTaboo() {
		return this.hasTaboo;
	}

	public void setHasTaboo(final boolean hasTaboo) {
		this.hasTaboo = hasTaboo;
	}

	public boolean getIsPublished() {
		return this.isPublished;
	}

	public void setIsPublished(final boolean isPublished) {
		this.isPublished = isPublished;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public User getPublisher() {
		return this.publisher;
	}

	public void setPublisher(final User publisher) {
		this.publisher = publisher;
	}

	@NotNull
	@Valid
	@OneToMany(mappedBy = "newspaper", cascade = CascadeType.ALL)
	public Collection<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(final Collection<Article> articles) {
		this.articles = articles;
	}

	@NotNull
	@Valid
	@ManyToMany
	public Collection<Advertisement> getAdvertisements() {
		return this.advertisements;
	}

	public void setAdvertisements(final Collection<Advertisement> advertisements) {
		this.advertisements = advertisements;
	}

}
