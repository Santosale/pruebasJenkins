
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "maxDate")
})
public class Groupon extends DomainEntity {

	private String	title;

	private String	description;

	private String	productName;

	private String	productUrl;

	private int		minAmountProduct;

	private Date	maxDate;

	private double	originalPrice;

	private double	price;

	private String	discountCode;

	private User	creator;


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

	@NotBlank
	@URL
	@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
	public String getProductUrl() {
		return this.productUrl;
	}

	public void setProductUrl(final String productUrl) {
		this.productUrl = productUrl;
	}

	@Min(0)
	public int getMinAmountProduct() {
		return this.minAmountProduct;
	}

	public void setMinAmountProduct(final int minAmountProduct) {
		this.minAmountProduct = minAmountProduct;
	}

	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getMaxDate() {
		return this.maxDate;
	}

	public void setMaxDate(final Date maxDate) {
		this.maxDate = maxDate;
	}

	@Min(0)
	public double getOriginalPrice() {
		return this.originalPrice;
	}

	public void setOriginalPrice(final double originalPrice) {
		this.originalPrice = originalPrice;
	}

	@Min(0)
	public double getPrice() {
		return this.price;
	}

	public void setPrice(final double price) {
		this.price = price;
	}

	public String getDiscountCode() {
		return this.discountCode;
	}

	public void setDiscountCode(final String discountCode) {
		this.discountCode = discountCode;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public User getCreator() {
		return this.creator;
	}

	public void setCreator(final User creator) {
		this.creator = creator;
	}

}
