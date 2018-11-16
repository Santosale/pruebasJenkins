
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Folder extends DomainEntity {

	private String				name;

	private boolean				system;

	private Collection<Folder>	childrenFolders;

	private Folder				fatherFolder;

	private Actor				actor;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean getSystem() {
		return this.system;
	}

	public void setSystem(final boolean system) {
		this.system = system;
	}

	@Valid
	@NotNull
	@OneToMany(mappedBy = "fatherFolder")
	public Collection<Folder> getChildrenFolders() {
		return this.childrenFolders;
	}

	public void setChildrenFolders(final Collection<Folder> childrenFolders) {
		this.childrenFolders = childrenFolders;
	}

	@Valid
	@ManyToOne(optional = true)
	public Folder getFatherFolder() {
		return this.fatherFolder;
	}

	public void setFatherFolder(final Folder fatherFolder) {
		this.fatherFolder = fatherFolder;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Actor getActor() {
		return this.actor;
	}

	public void setActor(final Actor actor) {
		this.actor = actor;
	}

}
