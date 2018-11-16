
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.StoryRepository;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Explorer;
import domain.Story;
import domain.Trip;

@Service
@Transactional
public class StoryService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private StoryRepository			storyRepository;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------
	public StoryService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Story create(final Trip trip, final Explorer explorer) {
		Story story;
		List<String> attachments;

		Assert.notNull(trip);
		Assert.notNull(explorer);

		attachments = new ArrayList<String>();

		story = new Story();
		story.setTrip(trip);
		story.setWriter(explorer);
		story.setAttachments(attachments);

		return story;
	}

	public Story findOne(final int storyId) {
		Story result;

		Assert.isTrue(storyId != 0);
		result = this.storyRepository.findOne(storyId);

		return result;
	}

	public Collection<Story> findAll() {
		Collection<Story> result;

		result = this.storyRepository.findAll();

		return result;
	}

	public Story save(final Story story) {
		Story result;
		UserAccount userAccount;
		Date currentMoment;
		Application application;

		Assert.notNull(story, "story.not.null");

		userAccount = LoginService.getPrincipal();
		// Solo su explorer puede crearse o actualizar stories
		Assert.isTrue(story.getWriter().getUserAccount().equals(userAccount), "story.writer.login");

		// Si el viaje no ha terminado no se puede escribir ni modificar
		currentMoment = new Date();
		Assert.isTrue(story.getTrip().getEndDate().compareTo(currentMoment) < 0, "story.trip.end");

		// Si no tiene application para el viaje o no está en estado accepted
		application = this.applicationService.findByTripIdAndExplorerId(story.getTrip().getId(), story.getWriter().getId());
		Assert.notNull(application, "story.application.not.null");
		Assert.isTrue(application != null && application.getStatus().equals("ACCEPTED"), "story.application.accepted");

		result = this.storyRepository.save(story);

		return result;
	}

	public void delete(final Story story) {
		UserAccount userAccount;

		Assert.notNull(story, "story.not.null");
		//Solo la puede borrar el explorer que la ha creado
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(story.getWriter().getUserAccount().equals(userAccount), "story.writer.login");

		this.storyRepository.delete(story);
	}

	//Other business methods -------------------------------------------------

	//Listamos las stories de un trip
	public Collection<Story> findByTripId(final int tripId) {
		Collection<Story> result;

		Assert.isTrue(tripId != 0);
		result = this.storyRepository.findByTripId(tripId);

		return result;
	}

	//Listamos las stories de un explorer
	public Collection<Story> findByExplorerId(final int explorerId) {
		Collection<Story> result;

		Assert.isTrue(explorerId != 0);
		result = this.storyRepository.findByExplorerId(explorerId);

		return result;
	}

	public void deleteFromTrip(final int storyId) {
		Story story;
		Assert.isTrue(storyId != 0);
		story = this.findOne(storyId);
		this.storyRepository.delete(story);

	}

	public boolean checkSpamWords(final Story story) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (story.getTitle() != null && story.getTitle().toLowerCase().contains(spamWord)) || (story.getText() != null && story.getText().toLowerCase().contains(spamWord));
			for (final String attachement : story.getAttachments()) {
				result = attachement.toLowerCase().contains(spamWord);
				if (result)
					break;
			}
			if (result)
				break;
		}

		return result;
	}
}
