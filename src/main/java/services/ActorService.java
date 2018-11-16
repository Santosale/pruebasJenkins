
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import domain.Actor;
import domain.Folder;
import domain.Message;
import domain.SocialIdentity;

@Service
@Transactional
public class ActorService {

	// Managed repository
	@Autowired
	private ActorRepository			actorRepository;

	// Supporting services
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private SocialIdentityService	socialIdentityService;

	@Autowired
	private FolderService			folderService;

	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private RangerService			rangerService;


	// Simple CRUD methods
	public Collection<Actor> findAll() {
		Collection<Actor> result;

		result = this.actorRepository.findAll();

		return result;
	}

	public Actor findOne(final int actorId) {
		Actor result;

		Assert.isTrue(actorId != 0);

		result = this.actorRepository.findOne(actorId);

		return result;
	}

	public Actor save(final Actor actor) {
		Actor result;

		result = this.actorRepository.save(actor);

		return result;
	}

	public Collection<Actor> findAllSuspicious() {
		Collection<Actor> result;

		result = this.actorRepository.findAllSuspicious();

		return result;
	}

	public void banUnBanActors(final Actor actor, final Boolean ban) {

		if (ban == true) {
			Assert.isTrue(actor.getSuspicious());
			actor.getUserAccount().setEnabled(false);
			this.save(actor);
		} else {
			actor.getUserAccount().setEnabled(true);
			this.save(actor);
		}

	}

	@Async
	public void searchAllSuspicious() {
		this.administratorService.searchSuspicious();
		this.auditorService.searchSuspicious();
		this.sponsorService.searchSuspicious();
		this.managerService.searchSuspicious();
		this.rangerService.searchSuspicious();
		this.explorerService.searchSuspicious();
	}

	public Actor findByUserAccountId(final int id) {
		Actor result;

		Assert.isTrue(id != 0);

		result = this.actorRepository.findByUserAccountId(id);

		return result;
	}
	// Other bussiness methods
	public boolean checkSpamWords(final Actor actor) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {

			result = actor.getAddress() != null && actor.getAddress().toLowerCase().contains(spamWord) || actor.getEmail().toLowerCase().contains(spamWord) || actor.getName().toLowerCase().contains(spamWord)
				|| actor.getSurname().toLowerCase().contains(spamWord) || actor.getUserAccount().getUsername().toLowerCase().contains(spamWord);

			// Método alternativo utilizando la implementación del método findMessageBySenderId
			// creado especificamente para esto.
			for (final Message m : this.messageService.findBySenderId(actor.getId()))
				result = result || this.messageService.checkSpamWords(m);

			for (final SocialIdentity socialIdentity : this.socialIdentityService.findByActorId(actor.getId()))
				result = result || this.socialIdentityService.checkSpamWords(socialIdentity);

			for (final Folder f : this.folderService.findByActorId(actor.getId()))
				result = result || this.folderService.checkSpamWords(f);

			if (result)
				break;
		}

		return result;
	}

}
