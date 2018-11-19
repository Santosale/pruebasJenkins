
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.NotificationRepository;
import security.LoginService;
import domain.Actor;
import domain.Notification;

@Service
@Transactional
public class NotificationService {

	// Managed repository
	@Autowired
	private NotificationRepository	notificationRepository;

	// Supporting services
	@Autowired
	private ActorService			actorService;

	@Autowired
	private Validator				validator;


	// Constructor
	public NotificationService() {
		super();
	}

	public Notification create(final Actor actor) {
		Notification result;

		Assert.notNull(actor);

		result = new Notification();

		result.setActor(actor);
		result.setVisited(false);

		return result;
	}

	public Notification findOne(final int notificationId) {
		Notification result;

		Assert.isTrue(notificationId != 0);

		result = this.notificationRepository.findOne(notificationId);

		return result;
	}

	public Notification findOneToDisplayAndDelete(final int notificationId, final String action) {
		Notification result;
		Actor actor;

		Assert.isTrue(notificationId != 0);

		result = this.notificationRepository.findOne(notificationId);
		Assert.notNull(result);

		//Solo lo ve su actor
		Assert.isTrue(LoginService.isAuthenticated());
		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		Assert.isTrue(result.getActor().getId() == actor.getId());

		//Actualizamos la visita
		if (!result.isVisited() && action == "display")
			this.putVisited(result);

		return result;
	}
	public Notification save(final Notification notification) {
		Notification result;

		Assert.notNull(notification);

		Assert.isTrue(notification.getId() == 0);

		Assert.isTrue(!notification.isVisited());

		result = this.notificationRepository.save(notification);

		return result;
	}

	public void putVisited(final Notification notification) {

		notification.setVisited(true);

		this.notificationRepository.save(notification);
	}

	public void delete(final Notification notification) {
		Actor actor;
		Notification saved;

		Assert.notNull(notification);

		saved = this.findOne(notification.getId());
		Assert.notNull(saved);

		// Su actor la puede borrar
		Assert.isTrue(LoginService.isAuthenticated());
		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		Assert.isTrue(actor.getUserAccount().getId() == saved.getActor().getUserAccount().getId());

		this.notificationRepository.delete(saved);
	}

	public Page<Notification> findByActorId(final int page, final int size) {
		Page<Notification> result;
		Actor actor;

		Assert.isTrue(LoginService.isAuthenticated());
		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		result = this.notificationRepository.findByActorId(actor.getId(), this.getPageable(page, size));

		return result;
	}

	public Integer countNotVisitedByActorId() {
		Integer result;
		Actor actor;

		result = null;
		actor = null;

		if (LoginService.isAuthenticated())
			actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		if (actor != null)
			result = this.notificationRepository.countNotVisitedByActorId(actor.getId());

		return result;
	}

	//Usada en los tests
	public Integer countNotVisitedByActorId(final Integer actorId) {
		Integer result;
		Actor actor;

		result = null;
		actor = null;

		if (LoginService.isAuthenticated())
			actor = this.actorService.findOne(actorId);

		if (actor != null)
			result = this.notificationRepository.countNotVisitedByActorId(actor.getId());

		return result;
	}

	public void flush() {
		this.notificationRepository.flush();
	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;
	}

	public Notification reconstruct(final Notification notification, final BindingResult binding) {
		Actor actor;

		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);
		notification.setActor(actor);

		this.validator.validate(notification, binding);

		return notification;
	}

	public Double ratioNotificationsPerTotal() {
		Double result;

		result = this.notificationRepository.ratioNotificationsPerTotal();

		return result;
	}

	public void send(final Collection<Actor> actors, final String subject, final String url) {
		Notification notification;

		for (final Actor actor : actors) {
			notification = this.create(actor);
			notification.setSubject(subject);
			notification.setUrl(url);
			notification.setVisited(false);

			this.save(notification);
		}
	}

}
