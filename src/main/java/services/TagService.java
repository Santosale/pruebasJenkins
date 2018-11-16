
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.TagRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Administrator;
import domain.Tag;
import domain.TagValue;

@Service
@Transactional
public class TagService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private TagRepository			tagRepository;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private TagValueService			tagValueService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private AdministratorService	administratorService;


	// Constructors -----------------------------------------------------------
	public TagService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Tag create() {
		Tag result;

		result = new Tag();

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
		UserAccount userAccount;
		Administrator administrator;

		Assert.notNull(tag, "tag.notNull");

		userAccount = LoginService.getPrincipal();

		administrator = this.administratorService.findByUserAccountId(userAccount.getId());
		Assert.notNull(administrator, "tag.notNull.administrator");
		//Solo un administrator puede modificarla y crearla
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(userAccount.getAuthorities().contains(authority), "tag.authority.administrator");

		//Solo modificar si no es referenciada.
		if (tag.getId() != 0)
			Assert.isTrue(this.tagValueService.findByTagId(tag.getId()).size() == 0, "tag.have.tagValue");

		result = this.tagRepository.save(tag);

		return result;
	}

	public void delete(final Tag tag) {
		Authority authority;
		UserAccount userAccount;

		Assert.notNull(tag);
		userAccount = LoginService.getPrincipal();

		//Solo un administrator puede borrarla
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(userAccount.getAuthorities().contains(authority), "tag.authority.administrator");

		for (final TagValue tagValue : this.tagValueService.findByTagId(tag.getId()))
			this.tagValueService.deleteFromTag(tagValue.getId());

		this.tagRepository.delete(tag);
	}

	//Other business methods -------------------------------------------------

	public boolean tagToSomeTrip(final int tagId) {
		boolean result = false;

		if (this.tagValueService.findByTagId(tagId).size() > 0)
			result = true;

		return result;

	}

	public boolean checkSpamWords(final Tag tag) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (tag.getName() != null && tag.getName().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}
}
