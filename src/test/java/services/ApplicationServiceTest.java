
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Application;
import domain.CreditCard;
import domain.Explorer;
import domain.Manager;
import domain.SurvivalClass;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ApplicationServiceTest extends AbstractTest {

	//Service under test----------------
	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private SurvivalClassService	survivalClassService;


	//Test -------------------------------
	//Creamos una nueva application y comprobamos sus propiedades
	@Test
	public void testCreate() {
		Explorer explorer;
		Trip trip;
		List<Trip> trips;
		Application created;

		super.authenticate("explorer1");
		trips = new ArrayList<Trip>();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(explorer);

		trips.addAll(this.tripService.findAll());

		for (final Application a : this.applicationService.findByExplorerId(explorer.getId()))
			trips.remove(a.getTrip());

		trip = trips.get(0);
		created = this.applicationService.create(explorer, trip);
		Assert.notNull(created.getMoment());
		Assert.notNull(created.getStatus());
		Assert.isNull(created.getDeniedReason());
		Assert.isNull(created.getCreditCard());
		Assert.isNull(created.getComments());
		Assert.isTrue(created.getApplicant().equals(explorer));
		Assert.isTrue(created.getTrip().equals(trip));
		Assert.isTrue(created.getSurvivalClasses().isEmpty());

		super.authenticate(null);

	}
	//Cojo una aplicación y veo que este dentro de todas obtenidas con el findAll
	@Test
	public void testFindAll() {
		Collection<Application> applications;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);
		applications = this.applicationService.findAll();
		Assert.notEmpty(applications);
		Assert.isTrue(applications.containsAll(this.applicationService.findByExplorerId(explorer.getId())));

		super.authenticate(null);

	}
	//Buscamos una de las aplicaciones del explorer logeado y vemos que esté contenida en todas las aplicaciones
	@Test
	public void testFindOne() {
		Explorer explorer;
		Collection<Application> allApplications;
		Application application;
		Application foundApplication;

		application = null;
		super.authenticate("admin");
		if (this.applicationService.ratioApplicantionsAccepted() > 0) {
			super.authenticate(null);
			for (final Application ap : this.applicationService.findAll())
				if (ap.getStatus().equals("ACCEPTED")) {
					application = ap;
					break;
				}
			explorer = application.getApplicant();
			allApplications = this.applicationService.findAll();
			super.authenticate(explorer.getUserAccount().getUsername());
			foundApplication = this.applicationService.findOne(application.getId());
			Assert.isTrue(allApplications.contains(foundApplication));
			super.authenticate(null);

		}
	}
	//Cogemos una aplicación con un manager
	@Test
	public void testFindOne2() {
		Manager manager;
		Collection<Application> allApplications;
		Application application;
		Application foundApplication;

		application = null;
		super.authenticate("admin");
		if (this.applicationService.ratioApplicantionsPending() > 0) {
			super.authenticate(null);
			for (final Application ap : this.applicationService.findAll())
				if (ap.getStatus().equals("PENDING")) {
					application = ap;
					break;
				}
			manager = application.getTrip().getManager();
			allApplications = this.applicationService.findAll();
			super.authenticate(manager.getUserAccount().getUsername());
			foundApplication = this.applicationService.findOne(application.getId());
			Assert.isTrue(allApplications.contains(foundApplication));
			super.authenticate(null);

		}
	}
	//Guardamos una aplicación para un trip cancelado salta una excepción
	@Test
	public void testSave1() {
		Application created;
		Application saved;
		Explorer explorer;
		Trip trip;
		Collection<Application> explorerApplications;
		Collection<Trip> explorerTrips;
		final Date currentMoment;
		super.authenticate("explorer4");

		currentMoment = new Date();

		trip = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		explorerApplications = this.applicationService.findByExplorerId(explorer.getId());
		explorerTrips = new ArrayList<Trip>();
		for (final Application ap : explorerApplications)
			explorerTrips.add(ap.getTrip());
		for (final Trip t : this.tripService.findAllVisible())
			if (!explorerTrips.contains(t) && t.getStartDate().compareTo(currentMoment) > 0 && t.getPublicationDate().compareTo(currentMoment) < 0 && t.getCancellationReason() == null) {
				trip = t;
				break;
			}
		Assert.notNull(trip);
		created = this.applicationService.create(explorer, trip);
		created.setComments("El viaje parece realmente interesante, por eso he echado la aplicación");
		saved = this.applicationService.save(created);
		Assert.isTrue(this.applicationService.findAll().contains(saved));

		super.authenticate(null);

	}
	//Vemos que alguien no autentcado no puede guardarlo
	@Test
	public void testSave2() {
		Application created;
		Application saved;
		Explorer explorer;
		Trip trip;
		Collection<Application> explorerApplications;
		Collection<Trip> explorerTrips;
		super.authenticate("explorer1");
		trip = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		explorerApplications = this.applicationService.findByExplorerId(explorer.getId());
		explorerTrips = new ArrayList<Trip>();
		for (final Application ap : explorerApplications)
			explorerTrips.add(ap.getTrip());
		for (final Trip t : this.tripService.findAll())
			if (!explorerTrips.contains(t)) {
				trip = t;
				break;
			}
		created = this.applicationService.create(explorer, trip);
		super.authenticate(null);
		try {
			created.setComments("El viaje parece realmente interesante, por eso he echado la aplicación");
			saved = this.applicationService.save(created);
			Assert.isTrue(this.applicationService.findAll().contains(saved));
		} catch (final IllegalArgumentException n) {

		}

	}

	//Vemos que un manager no puede crear uno nuevo
	@Test
	public void testSave3() {
		Application created;
		Application saved;
		Explorer explorer;
		Trip trip;
		Collection<Application> explorerApplications;
		Collection<Trip> explorerTrips;
		super.authenticate("explorer1");
		trip = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		explorerApplications = this.applicationService.findByExplorerId(explorer.getId());
		explorerTrips = new ArrayList<Trip>();
		for (final Application ap : explorerApplications)
			explorerTrips.add(ap.getTrip());
		for (final Trip t : this.tripService.findAll())
			if (!explorerTrips.contains(t)) {
				trip = t;
				break;
			}
		created = this.applicationService.create(explorer, trip);
		super.authenticate("manager1");
		try {
			created.setComments("El viaje parece realmente interesante, por eso he echado la aplicación");
			saved = this.applicationService.save(created);
			Assert.isTrue(this.applicationService.findAll().contains(saved));
			super.authenticate(null);
		} catch (final IllegalArgumentException n) {
			super.authenticate(null);
		}

	}

	//Pasamos una aplicación de PENDING A DUE
	@Test
	public void testSave4() {
		Application application;
		Application saved;
		Application copyApplication;

		super.authenticate("manager1");

		application = null;

		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("PENDING")) {
				application = ap;
				break;
			}

		copyApplication = this.copyApplication(application);
		copyApplication.setStatus("DUE");

		super.authenticate(null);
		super.authenticate(application.getTrip().getManager().getUserAccount().getUsername());

		saved = this.applicationService.save(copyApplication);

		Assert.isTrue(saved.getStatus().equals("DUE"));

		super.authenticate(null);

	}
	//Intentamos pasar de Pending a Due con un explorer
	@Test
	public void testSave5() {
		Application application;
		Application saved;
		Application copyApplication;
		super.authenticate("manager1");

		application = null;

		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("PENDING")) {
				application = ap;
				break;
			}
		copyApplication = application;
		copyApplication.setStatus("DUE");

		super.authenticate(null);
		super.authenticate("explorer1");
		try {
			saved = this.applicationService.save(copyApplication);
			Assert.isTrue(saved.getStatus().equals("DUE"));
			super.authenticate(null);

		} catch (final IllegalArgumentException n) {
			super.authenticate(null);

		}

	}
	//Pasamos de PENDING a Rejected
	@Test
	public void testSave6() {
		Application application;
		Application saved;
		Application copyApplication;
		super.authenticate("manager1");

		application = null;

		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("PENDING")) {
				application = ap;
				break;
			}

		copyApplication = this.copyApplication(application);
		copyApplication.setStatus("REJECTED");
		copyApplication.setDeniedReason("Es demasiado caro");

		super.authenticate(null);
		super.authenticate(application.getTrip().getManager().getUserAccount().getUsername());

		saved = this.applicationService.save(copyApplication);

		Assert.isTrue(saved.getStatus().equals("REJECTED"));
		Assert.notNull(saved.getDeniedReason());

		super.authenticate(null);

	}
	//Pasamos una aplicación de Due a Accepted
	@Test
	public void testSave7() {
		Application application;
		Application saved;
		CreditCard creditCard;
		Explorer explorer;
		CreditCard savedCreditCard;

		super.authenticate("explorer2");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		creditCard = this.creditCardService.create(explorer);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		savedCreditCard = this.creditCardService.save(creditCard);
		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("DUE")) {
				application = ap;
				break;
			}
		application.setCreditCard(savedCreditCard);
		//		for (final CreditCard creditC : this.creditCardService.findAll()) {
		//			creditCard = creditC;
		//			break;
		//		}

		this.applicationService.addCreditCard(application);

		saved = this.applicationService.findOne(application.getId());
		Assert.isTrue(saved.getStatus().equals("ACCEPTED"));
		Assert.notNull(saved.getCreditCard());

		super.authenticate(null);

	}
	//Pasamos una aplicación de Accepted a Cancelled
	@Test
	public void testSave8() {
		Application application;
		Application saved;

		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("ACCEPTED")) {
				application = ap;
				break;
			}

		super.authenticate(application.getApplicant().getUserAccount().getUsername());

		this.applicationService.cancelApplication(application.getId());

		saved = this.applicationService.findOne(application.getId());
		Assert.isTrue(saved.getStatus().equals("CANCELLED"));
		Assert.notNull(saved.getCreditCard());

		super.authenticate(null);

	}

	//Intentamos pasar una Due a Pending. Salta una excepción
	@Test
	public void testSave9() {
		Application application;
		Application saved;
		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("DUE")) {
				application = ap;
				break;
			}

		saved = this.copyApplication(application);

		saved.setStatus("PENDING");
		super.authenticate(application.getTrip().getManager().getUserAccount().getUsername());

		try {

			Assert.isNull(this.applicationService.save(saved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	//Intentamos pasar una Pending a Cancelled. Salta una excepción
	@Test
	public void testSave10() {
		Application application;
		Application saved;
		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("PENDING")) {
				application = ap;
				break;
			}

		saved = this.copyApplication(application);

		saved.setStatus("CANCELLED");
		super.authenticate(application.getTrip().getManager().getUserAccount().getUsername());

		try {

			Assert.isNull(this.applicationService.save(saved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	//Crear una aplicación para un viaje que ya ha pasado
	@Test
	public void testSave11() {
		Application created;
		Application saved;
		Explorer explorer;
		Trip trip;
		Collection<Application> explorerApplications;
		Collection<Trip> explorerTrips;
		final Date currentMoment;

		super.authenticate("explorer1");
		currentMoment = new Date();
		trip = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		explorerApplications = this.applicationService.findByExplorerId(explorer.getId());
		explorerTrips = new ArrayList<Trip>();
		for (final Application ap : explorerApplications)
			explorerTrips.add(ap.getTrip());
		for (final Trip t : this.tripService.findAll())
			if (!explorerTrips.contains(t) && t.getStartDate().compareTo(currentMoment) < 0) {
				trip = t;
				break;
			}
		created = this.applicationService.create(explorer, trip);
		created.setComments("El viaje parece realmente interesante, por eso he echado la aplicación");
		try {
			saved = this.applicationService.save(created);

			Assert.isTrue(this.applicationService.findAll().contains(saved));
			super.authenticate(null);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}

	}
	//Borramos una aplicación del explorer1
	@Test
	public void testDelete() {
		Application application;
		Explorer explorer;

		super.authenticate("explorer1");
		application = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Application ap : this.applicationService.findByExplorerId(explorer.getId())) {
			application = ap;
			break;
		}
		Assert.notNull(application);
		this.applicationService.delete(application);
		Assert.isTrue(!this.applicationService.findByExplorerId(explorer.getId()).contains(application));
		super.authenticate(null);

	}
	//Intentamos que alguien no logeado borre la aplicación de otro. Salta una excepción
	@Test
	public void testDelete2() {
		Application application;
		Explorer explorer;

		super.authenticate("explorer1");
		application = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Application ap : this.applicationService.findByExplorerId(explorer.getId())) {
			application = ap;
			break;
		}
		Assert.notNull(application);
		super.authenticate(null);
		try {
			this.applicationService.delete(application);

		} catch (final IllegalArgumentException e) {

		}

	}
	//Intentamos que otro explorer borre la aplicación de otro. Salta una excepción
	@Test
	public void testDelete3() {
		Application application;
		Explorer explorer;

		super.authenticate("explorer1");
		application = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Application ap : this.applicationService.findByExplorerId(explorer.getId())) {
			application = ap;
			break;
		}
		Assert.notNull(application);
		super.authenticate(null);
		super.authenticate("explorer2");
		try {
			this.applicationService.delete(application);
			super.authenticate(null);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	@Test
	public void testRegisterToASurvivalClass() {
		Explorer explorer;
		Application application;
		SurvivalClass survivalClass;
		CreditCard creditCard;
		CreditCard savedCreditCard;

		survivalClass = null;
		application = null;

		super.authenticate("explorer2");
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		creditCard = this.creditCardService.create(explorer);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		savedCreditCard = this.creditCardService.save(creditCard);
		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("DUE")) {
				application = ap;
				break;
			}
		application.setCreditCard(savedCreditCard);
		//		for (final CreditCard creditC : this.creditCardService.findAll()) {
		//			creditCard = creditC;
		//			break;
		//		}

		this.applicationService.addCreditCard(application);

		for (final Application ap : this.applicationService.findByExplorerId(explorer.getId())) {
			application = ap;
			break;
		}
		for (final SurvivalClass s : this.survivalClassService.findByTripId(application.getTrip().getId()))
			survivalClass = s;

		this.applicationService.registerToASurvivalClass(application, survivalClass);

		Assert.isTrue(this.applicationService.findOne(application.getId()).getSurvivalClasses().size() > 0);

		super.authenticate(null);

	}

	@Test
	public void testUnRegisterToASurvivalClass() {
		Explorer explorer;
		Application application;
		SurvivalClass survivalClass;
		CreditCard creditCard;
		CreditCard savedCreditCard;

		survivalClass = null;
		application = null;

		super.authenticate("explorer2");
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		creditCard = this.creditCardService.create(explorer);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		savedCreditCard = this.creditCardService.save(creditCard);
		application = null;
		for (final Application ap : this.applicationService.findAll())
			if (ap.getStatus().equals("DUE")) {
				application = ap;
				break;
			}
		application.setCreditCard(savedCreditCard);
		//		for (final CreditCard creditC : this.creditCardService.findAll()) {
		//			creditCard = creditC;
		//			break;
		//		}

		this.applicationService.addCreditCard(application);

		for (final Application ap : this.applicationService.findByExplorerId(explorer.getId())) {
			application = ap;
			break;
		}
		for (final SurvivalClass s : this.survivalClassService.findByTripId(application.getTrip().getId()))
			survivalClass = s;

		this.applicationService.registerToASurvivalClass(application, survivalClass);

		Assert.isTrue(this.applicationService.findOne(application.getId()).getSurvivalClasses().size() > 0);

		this.applicationService.unRegisterToASurvivalClass(application, survivalClass);

		Assert.isTrue(this.applicationService.findOne(application.getId()).getSurvivalClasses().size() == 0);

		super.authenticate(null);

	}
	//Probamos el método findByManagerId
	@Test
	public void testFindByManagerId() {
		Manager manager;
		Collection<Application> applications;
		Collection<Trip> trips;
		Collection<Application> saved;

		super.authenticate("manager1");
		applications = new ArrayList<Application>();
		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());
		for (final Trip t : trips)
			applications.addAll(this.applicationService.findByTripId(t.getId()));

		saved = this.applicationService.findByManagerId(manager.getId());

		Assert.isTrue(applications.containsAll(saved));

		super.authenticate(null);

	}

	//Probamos el método findByManagerId pero con otro manager. Salta una excepción
	@Test
	public void testFindByManagerId2() {
		Manager manager;
		Collection<Application> applications;
		Collection<Trip> trips;

		super.authenticate("manager1");
		applications = new ArrayList<Application>();
		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());
		for (final Trip t : trips)
			applications.addAll(this.applicationService.findByTripId(t.getId()));

		super.authenticate(null);
		super.authenticate("manager2");
		try {
			Assert.isNull(this.applicationService.findByManagerId(manager.getId()));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	//Probamos el método findByExplorerId
	@Test
	public void testFindByExplorerId() {
		Explorer explorer;
		Collection<Application> applications;
		Collection<Application> saved;

		super.authenticate("explorer1");
		applications = new ArrayList<Application>();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		applications.addAll(this.applicationService.findByExplorerId(explorer.getId()));

		saved = this.applicationService.findByExplorerId(explorer.getId());

		Assert.isTrue(applications.containsAll(saved));

		super.authenticate(null);

	}

	//Probamos el método findByexplorerId pero con otro explorer. Salta una excepción
	@Test
	public void testFindByExplorerId2() {
		Explorer explorer;
		Collection<Application> applications;

		super.authenticate("explorer1");
		applications = new ArrayList<Application>();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		applications.addAll(this.applicationService.findByExplorerId(explorer.getId()));

		super.authenticate(null);
		super.authenticate("explorer2");
		try {
			Assert.isNull(this.applicationService.findByExplorerId(explorer.getId()));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	//Probamos el método findByTripId
	@Test
	public void testFindByTripId() {
		Collection<Application> applications;
		Collection<Trip> trips;
		Collection<Application> saved;
		Trip trip;

		super.authenticate("manager1");
		trip = null;
		applications = new ArrayList<Application>();
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());
		for (final Trip t : trips) {
			applications.addAll(this.applicationService.findByTripId(t.getId()));
			trip = t;
			break;

		}

		saved = this.applicationService.findByTripId(trip.getId());

		Assert.isTrue(applications.containsAll(saved));

		super.authenticate(null);

	}

	//Probamos el método findByTripId pero con otro manager. Salta una excepción
	@Test
	public void testFindByTripId2() {
		Collection<Application> applications;
		Collection<Trip> trips;
		Trip trip;

		super.authenticate("manager1");
		trip = null;
		applications = new ArrayList<Application>();
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());
		for (final Trip t : trips) {
			applications.addAll(this.applicationService.findByTripId(t.getId()));
			trip = t;
			break;
		}
		super.authenticate(null);
		super.authenticate("manager2");
		try {
			Assert.isNull(this.applicationService.findByTripId(trip.getId()));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	//Probamos el método findByTripIdAndExplorer
	@Test
	public void testFindByTripIdAndExplorerId() {
		Explorer explorer;
		Collection<Application> applications;
		Application application;
		Application saved;

		super.authenticate("explorer1");
		application = null;
		applications = new ArrayList<Application>();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		applications = this.applicationService.findByExplorerId(explorer.getId());
		Assert.isTrue(!applications.isEmpty());

		for (final Application ap : applications) {
			application = ap;
			break;
		}

		saved = this.applicationService.findByTripIdAndExplorerId(application.getTrip().getId(), application.getApplicant().getId());

		Assert.isTrue(saved.equals(application));

		super.authenticate(null);

	}

	@Test
	public void testRatioDue() {
		Double result;
		super.authenticate("admin");

		result = this.applicationService.ratioApplicantionsDue();

		Assert.isTrue(result > 0);

		super.authenticate(null);
	}

	@Test
	public void testRatioAccepted() {
		Double result;
		super.authenticate("admin");

		result = this.applicationService.ratioApplicantionsAccepted();

		Assert.isTrue(result > 0);

		super.authenticate(null);
	}

	@Test
	public void testRatioCancelled() {
		Double result;
		super.authenticate("admin");

		result = this.applicationService.ratioApplicantionsCancelled();

		Assert.isTrue(result > 0);

		super.authenticate(null);
	}

	@Test
	public void testRatioPending() {
		Double result;
		super.authenticate("admin");

		result = this.applicationService.ratioApplicantionsPending();

		Assert.isTrue(result > 0);

		super.authenticate(null);
	}

	@Test
	public void testAvgMinMaxStandard() {
		Double[] numeros;
		super.authenticate("admin");

		numeros = new Double[4];
		numeros = this.applicationService.avgMinMaxStandardDNumberApplications();

		Assert.notNull(numeros);
		super.authenticate(null);
	}

	private Application copyApplication(final Application application) {
		Application result;

		result = new Application();
		result.setApplicant(application.getApplicant());
		result.setComments(application.getComments());
		result.setCreditCard(application.getCreditCard());
		result.setDeniedReason(application.getDeniedReason());
		result.setId(application.getId());
		result.setMoment(application.getMoment());
		result.setStatus(application.getStatus());
		result.setSurvivalClasses(application.getSurvivalClasses());
		result.setTrip(application.getTrip());
		result.setVersion(application.getVersion());

		return result;
	}
}
