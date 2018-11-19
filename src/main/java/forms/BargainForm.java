
package forms;

import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import domain.Bargain;

public class BargainForm {

	private Collection<String>	tagsName;

	private Bargain				bargain;

	private Integer				categoryId;


	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	@NotNull
	@ElementCollection
	public Collection<String> getTagsName() {
		return this.tagsName;
	}

	public void setTagsName(final Collection<String> tagsName) {
		this.tagsName = tagsName;
	}

	@Valid
	public Bargain getBargain() {
		return this.bargain;
	}

	public void setBargain(final Bargain bargain) {
		this.bargain = bargain;
	}

}
