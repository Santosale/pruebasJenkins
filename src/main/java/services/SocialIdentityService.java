
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SocialIdentityRepository;
import security.LoginService;
import domain.Actor;
import domain.SocialIdentity;

@Service
@Transactional
public class SocialIdentityService {

	// Managed repository
	@Autowired
	private SocialIdentityRepository	socialIdentityRepository;

	// Supporting services
	@Autowired
	private ConfigurationService		configurationService;


	// Constructor
	public SocialIdentityService() {
		super();
	}

	// Simple CRUD methods
	public SocialIdentity create(final Actor actor) {
		SocialIdentity result;

		result = new SocialIdentity();
		result.setActor(actor);

		return result;
	}

	public Collection<SocialIdentity> findAll() {
		Collection<SocialIdentity> result;

		result = this.socialIdentityRepository.findAll();

		return result;
	}

	public SocialIdentity findOne(final int socialIdentityId) {
		SocialIdentity result;

		Assert.isTrue(socialIdentityId != 0);

		result = this.socialIdentityRepository.findOne(socialIdentityId);

		return result;
	}

	public SocialIdentity save(final SocialIdentity socialIdentity) {
		SocialIdentity result;

		Assert.notNull(socialIdentity);

		Assert.isTrue(LoginService.isAuthenticated());

		// Un social identity solo puede ser guardado por el actor al que le pertenece y que está logeado
		Assert.isTrue(LoginService.getPrincipal().equals(socialIdentity.getActor().getUserAccount()));

		// Actualizar el actor cada vez que se añada o se actualice una socialIdentity
		result = this.socialIdentityRepository.save(socialIdentity);

		return result;
	}

	public void delete(final SocialIdentity socialIdentity) {
		Assert.notNull(socialIdentity);

		// Un social identity solo puede ser borrada por el actor al que le
		// pertenece
		Assert.isTrue(LoginService.getPrincipal().equals(socialIdentity.getActor().getUserAccount()));

		this.socialIdentityRepository.delete(socialIdentity);
	}

	// Other business methods
	public Collection<SocialIdentity> findByActorId(final int actorId) {
		Collection<SocialIdentity> result;

		Assert.isTrue(actorId != 0);
		result = this.socialIdentityRepository.findByActorId(actorId);

		return result;
	}

	public boolean checkSpamWords(final SocialIdentity socialIdentity) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = socialIdentity.getLink().contains(spamWord) || socialIdentity.getNick().contains(spamWord) || socialIdentity.getPhoto() != null && socialIdentity.getPhoto().contains(spamWord) || socialIdentity.getSocialNetwork().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
