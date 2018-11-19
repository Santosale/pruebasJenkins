package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import utilities.URLCollection;

import java.util.Collection;
import java.util.Date;

@Entity
@Access(AccessType.PROPERTY)
public class Comment extends DomainEntity {

	private Date moment;

	private String text;

	private Collection<String> images;

	private Bargain bargain;

	private Comment repliedComment;

	private User user;

	@Past
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getMoment() {
		return moment;
	}

	public void setMoment(Date moment) {
		this.moment = moment;
	}

	@NotBlank
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@ElementCollection
	@URLCollection
	public Collection<String> getImages() {
		return images;
	}

	public void setImages(Collection<String> images) {
		this.images = images;
	}
	
	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Bargain getBargain() {
		return bargain;
	}

	public void setBargain(Bargain bargain) {
		this.bargain = bargain;
	}

	@Valid
	@ManyToOne(optional = true)
	public Comment getRepliedComment() {
		return repliedComment;
	}

	public void setRepliedComment(Comment repliedComment) {
		this.repliedComment = repliedComment;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
