
package services;

import java.util.ArrayList;
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
import domain.CreditCard;
import domain.Sponsor;
import domain.Sponsorship;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class SponsorshipServiceTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private SponsorshipService	sponsorshipService;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private SponsorService		sponsorService;

	@Autowired
	private TripService			tripService;

	@Autowired
	private CreditCardService	creditCardService;


	// Tests ----------------------------------------------
	@Test
	public void testCreate() {
		Sponsor sponsor;
		List<Trip> trips;
		CreditCard creditCard;
		Sponsorship sponsorship;

		this.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);

		//Cogemos uno de los tres trips
		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		sponsorship = this.sponsorshipService.create(sponsor, trips.get(0));
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCard);

		Assert.notNull(sponsorship.getCreditCard());
		Assert.notNull(sponsorship.getSponsor());
		Assert.notNull(sponsorship.getCreditCard());

		this.authenticate(null);
	}

	@Test
	public void testFindAll() {
		Integer oldCountSponsorship;
		Integer newCountSponsorship;
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Sponsorship sponsorship;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);

		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);
		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		oldCountSponsorship = this.sponsorshipService.findAll().size();

		//Lo guardamos estando autenticado
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);
		super.authenticate(null);

		newCountSponsorship = this.sponsorshipService.findAll().size();
		Assert.isTrue(newCountSponsorship == oldCountSponsorship + 1);
		Assert.isTrue(this.sponsorshipService.findAll().contains(saved));

	}

	@Test
	public void testFindOne() {
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Sponsorship sponsorship;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		//super.authenticate(null);

		creditCard = this.exampleCreditCard(sponsor);
		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);
		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		//Lo guardamos estando autenticado
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);
		super.authenticate(null);

		Assert.notNull(this.sponsorshipService.findOne(saved.getId()));

	}

	@Test
	public void testSave1() {
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Sponsorship sponsorship;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);
		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);
		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		//Lo intentamos guardar sin estar autenticado el sponsor
		try {
			saved = this.sponsorshipService.save(sponsorship);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
		}

		//Lo guardamos estando autenticado
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);

		super.authenticate(null);

		//Miramos que el sponsor tenga el sponsorship
		Assert.isTrue(this.sponsorshipService.findBySponsorId(sponsor.getId()).contains(saved));

		//Intentamos modificarlo sin estar autenticado
		saved.setBanner("http://ads.com");
		try {
			saved = this.sponsorshipService.save(saved);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {

		}

		//Intentamos modificarlo estando autenticado
		super.authenticate("sponsor1");
		saved.setBanner("http://ads.es");
		saved = this.sponsorshipService.save(saved);
		Assert.isTrue(this.sponsorshipService.findOne(saved.getId()).getBanner().equals("http://ads.es"));

	}

	//Intentamos modificar un sponsorship estando autenticado con otro. Intentamos cambiar el trip y la creditCard
	@Test
	public void testSave2() {
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCard2;
		Sponsorship sponsorship;
		CreditCard creditCardSaved;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);

		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		creditCard2 = this.creditCardService.create(sponsor);
		creditCard2.setBrandName("Visa");
		creditCard2.setHolderName(sponsor.getName());
		creditCard2.setCvvcode(426);
		creditCard2.setExpirationMonth(11);
		creditCard2.setExpirationYear(2017);
		creditCard2.setNumber("4840075316677325");

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);

		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		super.authenticate(null);

		//Intentamos guardarlo con un sponsor distinto al que lo ha creado
		super.authenticate("sponsor2");
		try {
			saved = this.sponsorshipService.save(sponsorship);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
		}
		super.authenticate(null);

		//Lo guardamos correctamente
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);

		//Intentamos modificar el trip
		saved = this.copySponsorship(saved);
		saved.setTrip(trips.get(1));
		this.sponsorshipService.save(saved);

		super.authenticate(null);

		//Intentamos modificar el creditCard
		super.authenticate("sponsor1");
		saved = this.copySponsorship(saved);
		saved.getCreditCard().setHolderName("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);
		super.authenticate(null);
		Assert.isTrue(this.sponsorshipService.findOne(saved.getId()).getCreditCard().equals(saved.getCreditCard()));

	}

	//Intentamos modificar su sponsor una vez creado
	@Test
	public void testSave3() {
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Sponsorship sponsorship;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);

		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);

		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		//Lo guardamos correctamente
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);
		//Intentamos modificar el sponsor
		saved = this.copySponsorship(saved);
		super.authenticate(null);

		//Obtenemos el nuevo sponsor
		super.authenticate("sponsor2");
		saved.setSponsor(this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId()));
		try {
			saved = this.sponsorshipService.save(saved);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
		}
		super.authenticate(null);

	}

	@Test
	public void testDelete() {
		Sponsor sponsor;
		List<Trip> trips;
		Sponsorship saved;
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Sponsorship sponsorship;
		Trip trip;

		//Creamos un Sponsorship
		super.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.exampleCreditCard(sponsor);

		//Guardamos la creditCard
		creditCardSaved = this.creditCardService.save(creditCard);
		super.authenticate(null);

		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		trip = trips.get(0);
		sponsorship = this.sponsorshipService.create(sponsor, trip);
		sponsorship.setBanner("http://www.mybanner.com");
		sponsorship.setLinkInfoPage("http://moreinfo.com");
		sponsorship.setCreditCard(creditCardSaved);

		//Lo guardamos estando autenticado
		super.authenticate("sponsor1");
		saved = this.sponsorshipService.save(sponsorship);
		super.authenticate(null);

		Assert.isTrue(this.sponsorshipService.findBySponsorId(saved.getSponsor().getId()).contains(saved));
		Assert.isTrue(this.creditCardService.findAll().contains(saved.getCreditCard()));
		//Vemos que no se pueda borrar si no está autenticado
		try {
			this.sponsorshipService.delete(saved);
			Assert.isTrue(this.sponsorService.findAll().contains(saved));
		} catch (final IllegalArgumentException e) {
		}

		//Vemos que se borre, que no borre su creditCard y se borre del trip y del sponsor
		super.authenticate("sponsor1");
		this.sponsorshipService.delete(saved);
		super.authenticate(null);

		Assert.isTrue(!this.sponsorshipService.findBySponsorId(saved.getSponsor().getId()).contains(saved));
	}

	@Test
	public void testFindByTripId() {
		Sponsorship sponsorship;
		Trip trip;

		sponsorship = (Sponsorship) this.sponsorshipService.findAll().toArray()[0];
		trip = sponsorship.getTrip();

		Assert.isTrue(this.sponsorshipService.findByTripId(trip.getId()).contains(sponsorship));
	}

	@Test
	public void testFindBySponsorId() {
		Sponsorship sponsorship;
		Sponsor sponsor;

		sponsorship = (Sponsorship) this.sponsorshipService.findAll().toArray()[0];
		sponsor = sponsorship.getSponsor();

		Assert.isTrue(this.sponsorshipService.findBySponsorId(sponsor.getId()).contains(sponsorship));
	}

	@Test
	public void testFindRandomSponsorship() {

		Sponsorship sponsorship;

		sponsorship = (Sponsorship) this.sponsorshipService.findAll().toArray()[0];
		sponsorship = this.sponsorshipService.findRandomSponsorship(sponsorship.getTrip().getId());
		Assert.notNull(sponsorship);
	}

	//Hacemos este metodo para evitar que Spring salve sin usar save, si no con simples setPropiedad...
	private Sponsorship copySponsorship(final Sponsorship sponsorship) {
		Sponsorship result;

		result = new Sponsorship();
		result.setBanner(sponsorship.getBanner());
		result.setCreditCard(sponsorship.getCreditCard());
		result.setId(sponsorship.getId());
		result.setLinkInfoPage(sponsorship.getLinkInfoPage());
		result.setSponsor(sponsorship.getSponsor());
		result.setTrip(sponsorship.getTrip());
		result.setVersion(sponsorship.getVersion());

		return result;
	}

	private CreditCard exampleCreditCard(final Sponsor sponsor) {
		CreditCard creditCard;

		creditCard = this.creditCardService.create(sponsor);
		creditCard.setBrandName("Visa");
		creditCard.setHolderName(sponsor.getName());
		creditCard.setCvvcode(456);
		creditCard.setExpirationMonth(12);
		creditCard.setExpirationYear(2018);
		creditCard.setNumber("4490760974405753");

		return creditCard;
	}
}
