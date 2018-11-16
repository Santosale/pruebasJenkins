
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import domain.Location;
import domain.Manager;
import domain.SurvivalClass;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class SurvivalClassServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ManagerService			managerService;


	// Tests ----------------------------------------------

	/*
	 * Creamos una nueva SurvivalClass y comprobamos que sus atributos tengan el
	 * valor esperado
	 */
	@Test
	public void testCreate() {
		SurvivalClass survivalClass;
		Trip trip;
		Manager manager;

		super.authenticate("manager1");
		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());
		trip = this.tripService.create(manager);
		super.authenticate(null);
		survivalClass = this.survivalClassService.create(trip);

		Assert.notNull(survivalClass);

		Assert.isNull(survivalClass.getDescription());
		Assert.isNull(survivalClass.getLocation());
		Assert.isNull(survivalClass.getTitle());
	}

	// Se cogen todas los contacts y vemos si tiene el valor es el esperado.
	@Test
	public void testFindAll() {
		Integer allSurvivalClass;

		allSurvivalClass = this.survivalClassService.findAll().size();

		Assert.isTrue(allSurvivalClass == 2);
	}

	@Test
	public void testFindOne() {
		SurvivalClass survivalClass1;
		SurvivalClass survivalClass2;

		survivalClass1 = (SurvivalClass) this.survivalClassService.findAll().toArray()[0];

		survivalClass2 = this.survivalClassService.findOne(survivalClass1.getId());

		Assert.isTrue(survivalClass1.getId() == survivalClass2.getId());

	}

	/*
	 * Creamos una SurvivalClass logeados como un Manager y lo guardamos en la
	 * base de datos
	 */
	@Test
	public void testSave1() {
		SurvivalClass survivalClass, result;
		Trip trip;
		int oldCountSurvivalClass;
		Location location;
		SimpleDateFormat format;
		String momentString;
		Date momentDate;

		format = new SimpleDateFormat("dd/MM/yyyy");
		momentString = "02/02/2019";

		momentDate = new Date();

		try {
			momentDate = format.parse(momentString);
		} catch (final ParseException e) {

		}

		trip = this.getTripPublicate(false);

		super.authenticate(trip.getManager().getUserAccount().getUsername());

		oldCountSurvivalClass = this.survivalClassService.findAll().size();

		survivalClass = this.survivalClassService.create(trip);

		survivalClass.setDescription("Acampadas en lugares inospitos");
		location = new Location();
		location.setName("Tanzanika");
		location.setLatitude(5);
		location.setLongitude(10);
		survivalClass.setLocation(location);
		survivalClass.setTitle("Viajes por lugares novedosos");
		survivalClass.setMoment(momentDate);

		result = this.survivalClassService.save(survivalClass);

		Assert.isTrue(this.survivalClassService.findAll().size() == oldCountSurvivalClass + 1);

		Assert.isTrue(this.survivalClassService.findByManagerId(trip.getManager().getId()).contains(result));

		super.authenticate(null);

	}

	/*
	 * Creamos una SurvivalClass sin autenticarnos, por lo que esperamos
	 * que salte la excepcion
	 */
	@Test
	public void testSave2() {
		SurvivalClass survivalClass, result;
		Trip trip;
		Location location;
		SimpleDateFormat format;
		String momentString;
		Date momentDate;

		format = new SimpleDateFormat("dd/MM/yyyy");
		momentString = "02/02/2019";

		momentDate = new Date();

		try {
			momentDate = format.parse(momentString);
		} catch (final ParseException e) {

		}

		trip = this.getTripPublicate(false);

		survivalClass = this.survivalClassService.create(trip);

		survivalClass.setDescription("Acampadas en lugares inospitos");
		location = new Location();
		location.setName("Tanzanika");
		location.setLatitude(5);
		location.setLongitude(10);
		survivalClass.setLocation(location);
		survivalClass.setTitle("Viajes por lugares novedosos");
		survivalClass.setMoment(momentDate);
		result = null;

		try {
			result = this.survivalClassService.save(survivalClass);
		} catch (final IllegalArgumentException e) {
		}

		Assert.isNull(result);

	}

	/*
	 * Creamos una SurvivalClass logeados como un Manager, la guardamos en la
	 * base de datos y la borramos
	 */
	@Test
	public void testDelete1() {
		SurvivalClass survivalClass, result;
		Trip trip;
		int oldCountSurvivalClass;
		Location location;
		SimpleDateFormat format;
		String momentString;
		Date momentDate;

		format = new SimpleDateFormat("dd/MM/yyyy");
		momentString = "02/02/2019";

		momentDate = new Date();

		try {
			momentDate = format.parse(momentString);
		} catch (final ParseException e) {

		}

		trip = this.getTripPublicate(false);

		super.authenticate(trip.getManager().getUserAccount().getUsername());

		oldCountSurvivalClass = this.survivalClassService.findAll().size();

		survivalClass = this.survivalClassService.create(trip);

		survivalClass.setDescription("Acampadas en lugares inospitos");
		location = new Location();
		location.setName("Tanzanika");
		location.setLatitude(5);
		location.setLongitude(10);
		survivalClass.setLocation(location);
		survivalClass.setTitle("Viajes por lugares novedosos");
		survivalClass.setMoment(momentDate);

		result = this.survivalClassService.save(survivalClass);

		this.survivalClassService.delete(result);

		Assert.isTrue(this.survivalClassService.findAll().size() == oldCountSurvivalClass);

		Assert.isTrue(!this.survivalClassService.findByManagerId(trip.getManager().getId()).contains(result));

		super.authenticate(null);
	}

	/*
	 * Borramos una SurvivalClass sin estar logeados, por lo que esperamos
	 * que salte una excepcion
	 */
	@Test
	public void testDelete2() {
		SurvivalClass survivalClass, result;
		Trip trip;
		Location location;
		SimpleDateFormat format;
		String momentString;
		Date momentDate;

		format = new SimpleDateFormat("dd/MM/yyyy");
		momentString = "02/02/2019";

		momentDate = new Date();

		try {
			momentDate = format.parse(momentString);
		} catch (final ParseException e) {

		}

		trip = this.getTripPublicate(false);

		super.authenticate(trip.getManager().getUserAccount().getUsername());

		survivalClass = this.survivalClassService.create(trip);

		survivalClass.setDescription("Acampadas en lugares inospitos");
		location = new Location();
		location.setName("Tanzanika");
		location.setLatitude(5);
		location.setLongitude(10);
		survivalClass.setLocation(location);
		survivalClass.setTitle("Viajes por lugares novedosos");
		survivalClass.setMoment(momentDate);

		result = this.survivalClassService.save(survivalClass);

		super.authenticate(null);

		try {
			this.survivalClassService.delete(result);

		} catch (final IllegalArgumentException e) {

		}

		Assert.isTrue(this.survivalClassService.findAll().contains(result));

	}

	/*
	 * Comprobamos que el metodo findByManagerId devuelve las survivalclass
	 * correspondientes
	 */
	@Test
	public void findByManagerId() {
		Collection<SurvivalClass> survivalClass;
		Manager manager;

		super.authenticate("manager1");

		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());

		survivalClass = this.survivalClassService.findByManagerId(manager.getId());

		Assert.isTrue(survivalClass != null);

		super.authenticate(null);

	}

	@Test
	public void testFindByTripId() {
		SurvivalClass survivalClass;
		Trip trip;

		survivalClass = (SurvivalClass) this.survivalClassService.findAll().toArray()[0];
		trip = survivalClass.getTrip();

		Assert.isTrue(this.survivalClassService.findByTripId(trip.getId()).contains(survivalClass));

	}

	private Trip getTripPublicate(final boolean publicate) {
		List<Trip> trips;
		Trip result;
		Date currentMoment;

		currentMoment = new Date();
		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		result = null;

		for (final Trip tripIterator : trips)
			if (tripIterator.getPublicationDate().compareTo(currentMoment) > 0 && !publicate) {
				result = tripIterator;

				break;
			} else if (tripIterator.getPublicationDate().compareTo(currentMoment) < 0 && publicate) {
				result = tripIterator;
				break;
			}

		return result;
	}

}
