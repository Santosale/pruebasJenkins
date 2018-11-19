
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import utilities.URLCollection;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "maxDate")
})
public class Raffle extends DomainEntity {

	private String				title;

	private String				description;

	private String				productName;

	private String				productUrl;

	private Collection<String>	productImages;

	private Date				maxDate;

	private double				price;

	private Company				company;

	private User				winner;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@NotBlank
	public String getProductName() {
		return this.productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	@URL
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getProductUrl() {
		return this.productUrl;
	}

	public void setProductUrl(final String productUrl) {
		this.productUrl = productUrl;
	}

	@ElementCollection
	@URLCollection
	public Collection<String> getProductImages() {
		return this.productImages;
	}

	public void setProductImages(final Collection<String> productImages) {
		this.productImages = productImages;
	}

	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Date getMaxDate() {
		return this.maxDate;
	}

	public void setMaxDate(final Date maxDate) {
		this.maxDate = maxDate;
	}

	@Min(0)
	public double getPrice() {
		return this.price;
	}

	public void setPrice(final double price) {
		this.price = price;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(final Company company) {
		this.company = company;
	}

	@Valid
	@ManyToOne(optional = true)
	public User getWinner() {
		return this.winner;
	}

	public void setWinner(final User winner) {
		this.winner = winner;
	}

}
