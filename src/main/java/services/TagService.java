
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.TagRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Bargain;
import domain.Tag;

@Service
@Transactional
public class TagService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private TagRepository	tagRepository;

	//Supporting services -----------------------------------------------------------

	@Autowired
	private Validator		validator;


	// Constructors -----------------------------------------------------------

	public TagService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------

	public Tag create(final Bargain bargain) {
		Tag result;
		Collection<Bargain> bargains;

		bargains = new HashSet<Bargain>();
		bargains.add(bargain);
		result = new Tag();
		result.setBargains(bargains);

		return result;
	}

	public Tag findOne(final int tagId) {
		Tag result;

		Assert.isTrue(tagId != 0);
		result = this.tagRepository.findOne(tagId);

		return result;
	}

	public Collection<Tag> findAll() {
		Collection<Tag> result;

		result = this.tagRepository.findAll();

		return result;
	}

	public Tag save(final Tag tag) {
		Tag result;
		Authority authority;
		Authority authority2;

		Assert.notNull(tag);

		// Las compañías pueden crearlas y editarlas
		authority = new Authority();
		authority.setAuthority("COMPANY");

		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority) || LoginService.getPrincipal().getAuthorities().contains(authority2));

		result = this.tagRepository.save(tag);

		return result;
	}

	public void delete(final Tag tag) {
		Authority authority;
		Authority authority2;
		UserAccount userAccount;

		Assert.notNull(tag);
		userAccount = LoginService.getPrincipal();

		// Las compañías pueden borrarlas
		authority = new Authority();
		authority.setAuthority("COMPANY");

		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");
		Assert.isTrue(userAccount.getAuthorities().contains(authority) || userAccount.getAuthorities().contains(authority2));

		this.tagRepository.delete(tag);
	}

	//Other business methods -------------------------------------------------

	//Actualizar tags al guardar bargain
	public void saveByBargain(final List<String> tagsName, final Bargain bargain) {
		Tag tag;
		Collection<Bargain> bargains;
		List<Tag> oldTags;

		oldTags = new ArrayList<Tag>();
		oldTags.addAll(this.findByBargainId(bargain.getId()));

		for (final String tagName : tagsName) {

			tag = this.findByName(tagName.toLowerCase().trim());
			//Si la tag no existe la creamos
			if (tag == null) {
				Assert.isTrue(!tagName.trim().equals(""));
				tag = this.create(bargain);
				tag.setName(tagName.toLowerCase().trim());
				this.save(tag);

				//Si la tag existe pero no es del bargain, añadimos el bargain
			} else if (tag != null && !tag.getBargains().contains(bargain)) {
				bargains = tag.getBargains();
				bargains.add(bargain);
				tag.setBargains(bargains);
				this.save(tag);

				//Si ya la tenia no la borro
			} else if (tag != null && tag.getBargains().contains(bargain))
				oldTags.remove(tag);
		}

		//Borramos las que ya no quiere y no se usan
		for (Tag oldTag : oldTags) {
			oldTag = this.findOne(oldTag.getId());

			//Si no se esta usando más, se borra
			if (oldTag.getBargains().size() == 1)
				this.delete(oldTag);
			else {
				oldTag.getBargains().remove(bargain);
				this.save(oldTag);
			}
		}

	}

	//Actualizar tags al borrar bargain
	public void deleteByBargain(final Bargain bargain) {
		Collection<Tag> tags;
		Collection<Bargain> bargains;

		tags = this.findByBargainId(bargain.getId());
		Assert.notNull(tags);

		for (final Tag tag : tags)
			//La tag es usada solo por el bargain a borrar, borramos la tag 
			if (tag.getBargains().size() == 1)
				this.delete(tag);

			//La tag es usada por más bargains, le quitamos el bargain a borrar
			else {
				bargains = tag.getBargains();
				bargains.remove(bargain);
				tag.setBargains(bargains);
				this.save(tag);
			}

	}

	public Collection<Tag> findByBargainId(final int bargainId) {
		Collection<Tag> result;

		Assert.isTrue(bargainId != 0);

		result = this.tagRepository.findByBargainId(bargainId);

		return result;
	}

	public Tag findByName(final String name) {
		Tag result;

		Assert.notNull(name);

		result = this.tagRepository.findByName(name);

		return result;
	}

	public Page<Tag> findAllPaginated(final int page, final int size) {
		Page<Tag> result;

		result = this.tagRepository.findAllPaginated(this.getPageable(page, size));

		return result;
	}

	public Collection<String> findNames(final int bargainId) {
		Collection<String> result;

		Assert.isTrue(bargainId != 0);

		result = this.tagRepository.findNames(bargainId);

		return result;
	}

	public Tag reconstruct(final Tag tag, final BindingResult binding) {
		Tag result, aux;

		if (tag.getId() == 0)
			result = tag;
		else {
			result = tag;
			aux = this.tagRepository.findOne(tag.getId());
			result.setVersion(aux.getVersion());
			result.setName(aux.getName());
			result.setBargains(aux.getBargains());
		}

		this.validator.validate(result, binding);

		return result;
	}

	public Double avgRatioTagsPerBargain() {
		Double result;

		result = this.tagRepository.avgRatioTagsPerBargain();

		return result;
	}

	// Auxiliary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;
	}

}
