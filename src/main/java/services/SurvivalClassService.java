
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SurvivalClassRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.SurvivalClass;
import domain.Trip;

@Service
@Transactional
public class SurvivalClassService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SurvivalClassRepository	survivalClassRepository;

	//Supporting services -----------------------------------------------------------

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ManagerService			managerService;


	// Constructors -----------------------------------------------------------
	public SurvivalClassService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public SurvivalClass create(final Trip trip) {
		SurvivalClass result;

		Assert.notNull(trip);

		result = new SurvivalClass();
		result.setTrip(trip);

		return result;
	}

	public SurvivalClass findOne(final int survivalClassId) {
		SurvivalClass result;

		Assert.isTrue(survivalClassId != 0);
		result = this.survivalClassRepository.findOne(survivalClassId);

		return result;
	}

	public Collection<SurvivalClass> findAll() {
		Collection<SurvivalClass> result;

		result = this.survivalClassRepository.findAll();

		return result;
	}

	public SurvivalClass save(final SurvivalClass survivalClass) {
		SurvivalClass result;
		UserAccount userAccount;

		Assert.notNull(survivalClass, "survivalClass.notNull");

		//La modifica un manager
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(survivalClass.getTrip().getManager().getUserAccount().equals(userAccount), "survivalClass.equals.manager");

		result = this.survivalClassRepository.save(survivalClass);

		return result;
	}

	public void delete(final SurvivalClass survivalClass) {
		UserAccount userAccount;

		Assert.notNull(survivalClass, "survivalClass.notNull");

		//La borra su manager
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(survivalClass.getTrip().getManager().getUserAccount().equals(userAccount), "survivalClass.equals.manager");

		this.survivalClassRepository.delete(survivalClass);
	}

	//Other business methods -------------------------------------------------
	public boolean checkSpamWords(final SurvivalClass survivalClass) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (survivalClass.getTitle() != null && survivalClass.getTitle().toLowerCase().contains(spamWord)) || (survivalClass.getDescription() != null && survivalClass.getDescription().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}

	//Listamos las survivalClass de un manager
	public Collection<SurvivalClass> findByManagerId(final int managerId) {
		Collection<SurvivalClass> result;
		UserAccount userAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(managerId != 0);
		userAccount = LoginService.getPrincipal();
		//Debe estar un manager logeado
		Assert.isTrue(this.managerService.findByUserAccountId(userAccount.getId()) != null || userAccount.getAuthorities().contains(authority));
		result = this.survivalClassRepository.findByManagerId(managerId);

		return result;
	}

	//Listamos las survivalClass de un trip
	public Collection<SurvivalClass> findByTripId(final int tripId) {
		Collection<SurvivalClass> result;

		Assert.isTrue(tripId != 0);
		result = this.survivalClassRepository.findByTripId(tripId);

		return result;
	}
}
