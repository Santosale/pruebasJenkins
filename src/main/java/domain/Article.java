
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import utilities.URLCollection;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "isFinalMode, hasTaboo, title, summary, body")
})
public class Article extends DomainEntity {

	private String				title;

	private Date				moment;

	private String				summary;

	private String				body;

	private Collection<String>	pictures;

	private boolean				isFinalMode;

	private boolean				hasTaboo;

	private Newspaper			newspaper;

	private User				writer;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Date getMoment() {
		return this.moment;
	}

	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	@NotBlank
	public String getSummary() {
		return this.summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	@NotBlank
	public String getBody() {
		return this.body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	@NotNull
	@URLCollection
	@ElementCollection
	public Collection<String> getPictures() {
		return this.pictures;
	}

	public void setPictures(final Collection<String> pictures) {
		this.pictures = pictures;
	}

	public boolean getIsFinalMode() {
		return this.isFinalMode;
	}

	public void setIsFinalMode(final boolean isFinalMode) {
		this.isFinalMode = isFinalMode;
	}

	public boolean getHasTaboo() {
		return this.hasTaboo;
	}

	public void setHasTaboo(final boolean hasTaboo) {
		this.hasTaboo = hasTaboo;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Newspaper getNewspaper() {
		return this.newspaper;
	}

	public void setNewspaper(final Newspaper newspaper) {
		this.newspaper = newspaper;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public User getWriter() {
		return this.writer;
	}

	public void setWriter(final User writer) {
		this.writer = writer;
	}

}
