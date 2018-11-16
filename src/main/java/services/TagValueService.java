
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.TagValueRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.TagValue;
import domain.Trip;

@Service
@Transactional
public class TagValueService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private TagValueRepository		tagValueRepository;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------
	public TagValueService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public TagValue create(final Trip trip) {
		TagValue result;

		Assert.notNull(trip);
		result = new TagValue();
		result.setTrip(trip);

		return result;
	}

	public TagValue findOne(final int tagValueId) {
		TagValue result;

		Assert.isTrue(tagValueId != 0);
		result = this.tagValueRepository.findOne(tagValueId);

		return result;
	}

	public Collection<TagValue> findAll() {
		Collection<TagValue> result;

		result = this.tagValueRepository.findAll();

		return result;
	}

	public TagValue save(final TagValue tagValue) {
		TagValue result;
		UserAccount userAccount;
		TagValue tagValueAux;

		Assert.notNull(tagValue, "tagValue.notNull");
		userAccount = LoginService.getPrincipal();

		// Modificada y creada por su Manager
		Assert.isTrue(tagValue.getTrip().getManager().getUserAccount().equals(userAccount), "tagValue.notEquals.manager");

		if (tagValue.getId() == 0)
			Assert.isNull(this.tagValueRepository.findByTagAndTrip(tagValue.getTag().getId(), tagValue.getTrip().getId()), "tagValue.notTag");
		else {
			tagValueAux = this.tagValueRepository.findByTagAndTrip(tagValue.getTag().getId(), tagValue.getTrip().getId());
			Assert.notNull(tagValueAux, "tagValue.notNull");
			Assert.isTrue(tagValueAux.getId() == tagValue.getId(), "tagValue.sameTagValue");
		}

		result = this.tagValueRepository.save(tagValue);

		return result;
	}

	public void delete(final TagValue tagValue) {
		Assert.notNull(tagValue, "tagValue.notNull");
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();

		Assert.isTrue(tagValue.getTrip().getManager().getUserAccount().equals(userAccount), "tagValue.notEquals.manager");

		//Eliminamos el tagValue
		this.tagValueRepository.delete(tagValue);
	}

	//Other business methods -------------------------------------------------

	//Listar los tagValues de un trip
	public Collection<TagValue> findByTripId(final int tripId) {
		Collection<TagValue> result;

		Assert.isTrue(tripId != 0);
		result = this.tagValueRepository.findByTripId(tripId);

		return result;
	}

	//Listar los tag values de un tag
	public Collection<TagValue> findByTagId(final int tagId) {
		Collection<TagValue> result;

		Assert.isTrue(tagId != 0);
		result = this.tagValueRepository.findByTagId(tagId);

		return result;
	}

	public boolean checkSpamWords(final TagValue tagValue) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (tagValue.getValue() != null && tagValue.getValue().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}

	public void deleteFromTag(final int tagValueId) {
		TagValue tagValue;
		UserAccount userAccount;
		Authority authority;

		Assert.isTrue(tagValueId != 0, "tagValue.moreOne");
		userAccount = LoginService.getPrincipal();
		//Solo un administrator puede borrarla
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(userAccount.getAuthorities().contains(authority), "tagValue.authority.administrator");

		tagValue = this.findOne(tagValueId);
		Assert.notNull(tagValue, "tagValue.notNull");

		this.tagValueRepository.delete(tagValue);
	}
}
