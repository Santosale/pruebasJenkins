
package services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ParticipationRepository;
import security.Authority;
import security.LoginService;
import domain.Groupon;
import domain.Participation;
import domain.User;

@Service
@Transactional
public class ParticipationService {

	// Managed repository
	@Autowired
	private ParticipationRepository	participationRepository;

	//Managed services
	@Autowired
	private UserService				userService;

	@Autowired
	private PlanService				planService;

	@Autowired
	private GrouponService			grouponService;

	// Supporting services
	@Autowired
	private Validator				validator;


	// Constructor
	public ParticipationService() {
		super();
	}

	// Simple CRUD methods
	public Participation create(final int grouponId) {
		Participation result;
		Groupon groupon;
		Authority authority;
		User user;

		result = new Participation();
		groupon = this.grouponService.findOne(grouponId);
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.notNull(groupon);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.findByGrouponIdAndUserId(groupon.getId(), this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null);
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(groupon.getCreator().getId() != user.getId());
		if (this.planService.findByUserId(user.getId()) == null)
			Assert.isTrue(groupon.getMaxDate().compareTo(new Date()) > 0);
		result.setUser(user);
		result.setGroupon(groupon);

		return result;
	}
	public Collection<Participation> findAll() {
		Collection<Participation> result;

		result = this.participationRepository.findAll();

		return result;
	}

	public Participation findOne(final int participationId) {
		Participation result;

		Assert.isTrue(participationId != 0);

		result = this.participationRepository.findOne(participationId);

		return result;
	}

	public Participation findOneToEdit(final int participationId) {
		Participation result;

		result = this.findOne(participationId);
		Assert.notNull(result);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == result.getUser().getUserAccount().getId());

		return result;
	}

	public Participation save(final Participation participation) {
		Participation result;
		User user;

		Assert.notNull(participation);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == participation.getUser().getUserAccount().getId());
		//points = this.requestedProductsByGrouponId(participation.getGroupon().getId());

		if (participation.getId() == 0) {
			Assert.isTrue(this.findByGrouponIdAndUserId(participation.getGroupon().getId(), this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null);
			Assert.isTrue(participation.getGroupon().getCreator().getId() != participation.getUser().getId());

			if (this.planService.findByUserId(participation.getUser().getId()) == null)
				Assert.isTrue(participation.getGroupon().getMaxDate().compareTo(new Date()) > 0);

			user = participation.getUser();
			this.userService.addPoints(user, 20);

		}

		result = this.participationRepository.save(participation);

		return result;
	}

	public void delete(final Participation participation) {
		Participation participationToDelete;

		Assert.notNull(participation);
		participationToDelete = this.findOne(participation.getId());
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == participationToDelete.getUser().getUserAccount().getId());

		this.participationRepository.delete(participationToDelete);

	}

	public void deleteFromGroupon(final Participation participation) {
		Assert.notNull(participation);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == participation.getGroupon().getCreator().getUserAccount().getId());
		this.participationRepository.delete(participation);
	}

	public void deleteFromGrouponModerator(final Participation participation) {
		Authority authority;

		Assert.notNull(participation);
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		this.participationRepository.delete(participation);
	}

	public Integer requestedProductsByGrouponId(final int grouponId) {
		Integer result;

		Assert.isTrue(grouponId != 0);

		result = this.participationRepository.requestedProductsByGrouponId(grouponId);

		if (result == null)
			result = 0;

		return result;
	}

	public Page<Participation> findByUserId(final int userId, final int page, final int size) {
		Page<Participation> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(userId != 0);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == userId);

		result = this.participationRepository.findByUserId(userId, this.getPageable(page, size));

		return result;

	}

	public Collection<Participation> findByGrouponId(final int grouponId) {
		Collection<Participation> result;

		Assert.isTrue(grouponId != 0);

		result = this.participationRepository.findByGrouponId(grouponId);

		return result;

	}

	public Participation findByGrouponIdAndUserId(final int grouponId, final int userId) {
		Participation result;

		Assert.isTrue(grouponId != 0);
		Assert.isTrue(userId != 0);

		result = this.participationRepository.findByGrouponIdAndUserId(grouponId, userId);

		return result;

	}

	public Participation reconstruct(final Participation participation, final BindingResult binding) {
		Participation aux;
		Participation result;

		result = participation;

		if (participation.getId() == 0)
			result.setUser(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		else {
			aux = this.findOne(participation.getId());
			result.setVersion(aux.getVersion());
			result.setGroupon(aux.getGroupon());
			result.setUser(aux.getUser());
		}

		this.validator.validate(result, binding);

		return result;
	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}

}
