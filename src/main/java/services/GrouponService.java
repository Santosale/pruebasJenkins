
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

import repositories.GrouponRepository;
import security.Authority;
import security.LoginService;
import domain.Groupon;
import domain.Participation;
import domain.User;

@Service
@Transactional
public class GrouponService {

	// Managed repository
	@Autowired
	private GrouponRepository		grouponRepository;

	//Managed services
	@Autowired
	private UserService				userService;

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private PlanService				planService;

	// Supporting services
	@Autowired
	private Validator				validator;


	// Constructor
	public GrouponService() {
		super();
	}

	// Simple CRUD methods
	public Groupon create() {
		Groupon result;
		Authority authority;
		User user;

		result = new Groupon();
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		result.setCreator(user);

		return result;
	}

	public Collection<Groupon> findAll() {
		Collection<Groupon> result;

		result = this.grouponRepository.findAll();

		return result;
	}

	public Groupon findOne(final int grouponId) {
		Groupon result;

		Assert.isTrue(grouponId != 0);

		result = this.grouponRepository.findOne(grouponId);

		return result;
	}

	public Groupon findOneToEdit(final int grouponId) {
		Groupon result;

		result = this.findOne(grouponId);
		Assert.notNull(result);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == result.getCreator().getUserAccount().getId());

		return result;
	}

	public Groupon save(final Groupon groupon) {
		Groupon result, saved;
		User user;

		Assert.notNull(groupon);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == groupon.getCreator().getUserAccount().getId());
		Assert.isTrue(groupon.getPrice() < groupon.getOriginalPrice());

		if (groupon.getId() == 0) {
			Assert.isTrue(groupon.getMaxDate().compareTo(new Date()) > 0);
			Assert.isNull(groupon.getDiscountCode());
			user = groupon.getCreator();
			this.userService.addPoints(user, 50);
		} else {
			saved = this.findOne(groupon.getId());
			if (groupon.getMaxDate().compareTo(saved.getMaxDate()) != 0)
				Assert.isTrue(groupon.getMaxDate().compareTo(new Date()) > 0);
			if (this.participationService.requestedProductsByGrouponId(groupon.getId()) >= saved.getMinAmountProduct())
				if (saved.getDiscountCode() == null)
					Assert.isTrue(groupon.getDiscountCode() != null && !groupon.getDiscountCode().equals(""));

		}

		result = this.grouponRepository.save(groupon);

		return result;
	}

	public void delete(final Groupon groupon) {
		Groupon grouponToDelete;

		Assert.notNull(groupon);
		grouponToDelete = this.findOne(groupon.getId());
		Assert.notNull(grouponToDelete);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == grouponToDelete.getCreator().getUserAccount().getId());

		for (final Participation p : this.participationService.findByGrouponId(grouponToDelete.getId()))
			this.participationService.deleteFromGroupon(p);

		this.grouponRepository.delete(grouponToDelete);

	}

	public void deleteFromModerator(final Groupon groupon) {
		Groupon grouponToDelete;
		Authority authority;
		User user;

		Assert.notNull(groupon);
		grouponToDelete = this.findOne(groupon.getId());
		Assert.notNull(grouponToDelete);
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		for (final Participation p : this.participationService.findByGrouponId(grouponToDelete.getId()))
			this.participationService.deleteFromGrouponModerator(p);

		user = grouponToDelete.getCreator();
		this.userService.addPoints(user, -10);

		this.grouponRepository.delete(grouponToDelete);

	}

	public void saveFromParticipation(final Groupon groupon) {
		Assert.notNull(groupon);
		this.grouponRepository.save(groupon);
	}

	public Page<Groupon> findWithMaxDateFuture(final int page, final int size) {
		Page<Groupon> result;

		result = this.grouponRepository.findWithMaxDateFuture(this.getPageable(page, size));

		return result;

	}

	public Page<Groupon> findAllPaginated(final int page, final int size) {
		Page<Groupon> result;
		Authority authority;
		Authority authority2;

		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");

		Assert.isTrue(LoginService.isAuthenticated() && (LoginService.getPrincipal().getAuthorities().contains(authority) || LoginService.getPrincipal().getAuthorities().contains(authority2)));
		if (LoginService.getPrincipal().getAuthorities().contains(authority))
			Assert.isTrue(this.planService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) != null);

		result = this.grouponRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	public Page<Groupon> findByCreatorId(final int creatorId, final int page, final int size) {
		Page<Groupon> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(creatorId != 0);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == creatorId);
		result = this.grouponRepository.findByCreatorId(creatorId, this.getPageable(page, size));

		return result;

	}

	public Double[] minMaxAvgStandarDesviationDiscountPerGroupon() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.grouponRepository.minMaxAvgStandarDesviationDiscountPerGroupon();

		return result;
	}

	public Page<Groupon> tenPercentageMoreParticipationsThanAverage(final int page, final int size) {
		Page<Groupon> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.grouponRepository.tenPercentageMoreParticipationsThanAverage(this.getPageable(page, size));

		return result;
	}

	public Groupon reconstruct(final Groupon groupon, final BindingResult binding) {
		Groupon aux;
		Groupon result;

		result = groupon;

		if (groupon.getId() == 0)
			result.setCreator(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		else {
			aux = this.findOne(groupon.getId());
			result.setVersion(aux.getVersion());
			result.setCreator(aux.getCreator());
			if (this.participationService.requestedProductsByGrouponId(groupon.getId()) < aux.getMinAmountProduct())
				result.setDiscountCode(aux.getDiscountCode());

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
