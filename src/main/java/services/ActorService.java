package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import domain.Actor;

@Service
@Transactional
public class ActorService {

	// Managed repository
	@Autowired
	private ActorRepository actorRepository;

	// Supporting services

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

	public Actor findByUserAccountId(final int id) {
		Actor result;

		Assert.isTrue(id != 0);

		result = this.actorRepository.findByUserAccountId(id);

		return result;
	}

}
