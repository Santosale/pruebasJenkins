
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Explorer;
import domain.Finder;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class FinderServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private FinderService	finderService;

	@Autowired
	private ExplorerService	explorerService;

	@Autowired
	private TripService		tripService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo Finder y comprobamos que sus atributos tengan el valor
	 * esperado
	 */
	@Test
	public void testCreate() {
		Finder finder;

		finder = this.finderService.create(null);

		Assert.isNull(finder.getFinishedDate());
		Assert.isNull(finder.getKeyWord());
		Assert.isNull(finder.getMaxPrice());
		Assert.isNull(finder.getMinPrice());
		Assert.isNull(finder.getStartedDate());

	}

	@Test
	public void testSave1() {
		Finder finder, finder1, result;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());

		finder1 = this.copyFinder(finder);

		finder1.setKeyWord("andalucía");
		finder1.setFinishedDate(null);
		finder1.setMaxPrice(null);
		finder1.setMinPrice(null);
		finder1.setStartedDate(null);

		result = this.finderService.save(finder1);

		Assert.notNull(result.getTrips());

		super.authenticate(null);

	}

	/*
	 * Creamos un Finder logeados como un Explorer y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSave2() {
		Finder finder, finder1, result;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());

		finder1 = this.copyFinder(finder);

		finder1.setKeyWord("hola");
		finder1.setFinishedDate(null);
		finder1.setMaxPrice(null);
		finder1.setMinPrice(null);
		finder1.setStartedDate(null);

		result = this.finderService.save(finder1);

		Assert.notNull(result);

		super.authenticate(null);
	}

	/*
	 * Creamos un Finder logeados como un Explorer, lo guardamos en la base de
	 * datos y lo borramos
	 */
	@Test
	public void testDelete() {
		Finder finder;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());

		this.finderService.delete(finder);

		Assert.isTrue(!this.finderService.findAll().contains(finder));

		super.authenticate(null);
	}

	/*
	 * Creamos un Finder logeados como un Explorer y lo guardamos en la base de
	 * datos, comprobamos que el resultado sea correcto. En este caso, se hará
	 * una busqueda con KeyWord 'Viaje'
	 */
	@Test
	public void testGetTrips1() {
		Finder finder, saved;
		Explorer explorer;
		Integer result;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());
		finder = this.copyFinder(finder);
		finder.setKeyWord("Viaje");

		saved = this.finderService.save(finder);

		result = saved.getTrips().size();

		Assert.isTrue(result == 2);

		super.authenticate(null);
	}

	/*
	 * Creamos un Finder logeados como un Explorer y lo guardamos en la base de
	 * datos, comprobamos que el resultado sea correcto. En este caso, se hará
	 * una busqueda con los precios entre 0 y 300.
	 */
	@Test
	public void testGetTrips2() {
		Finder finder, saved;
		Explorer explorer;
		Integer result;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());
		finder = this.copyFinder(finder);
		finder.setMinPrice(0.0);
		finder.setMaxPrice(300.0);

		saved = this.finderService.save(finder);

		result = saved.getTrips().size();

		Assert.isTrue(result == 1);

		super.authenticate(null);
	}

	/*
	 * Creamos un Finder logeados como un Explorer y lo guardamos en la base de
	 * datos, comprobamos que el resultado sea correcto. En este caso, se hará
	 * una busqueda con las fechas entre 01/01/2010 y 31/12/2019.
	 */
	@Test
	public void testGetTrips3() {
		Finder finder, saved;
		Explorer explorer;
		Integer result;
		Date startDateDefault;
		Date finishDateDefault;
		String dateInString;
		String dateInString2;
		SimpleDateFormat format;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());
		finder = this.copyFinder(finder);

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "01/01/2010";
		dateInString2 = "31/12/2019";
		finishDateDefault = new Date();
		startDateDefault = new Date();

		try {
			finishDateDefault = format.parse(dateInString2);
			startDateDefault = format.parse(dateInString);
		} catch (final ParseException e) {

		}

		finder.setStartedDate(startDateDefault);
		finder.setFinishedDate(finishDateDefault);

		saved = this.finderService.save(finder);

		result = saved.getTrips().size();

		Assert.isTrue(result == 2);

		super.authenticate(null);
	}

	/*
	 * Creamos un Finder logeados como un Explorer y lo guardamos en la base de
	 * datos, comprobamos que el resultado sea correcto. En este caso, se hará
	 * una busqueda con KeyWord 'Viaje' y fecha entre 01/01/2015 y 31/12/2018
	 */
	@Test
	public void testGetTrips4() {
		Finder finder, saved;
		Explorer explorer;
		Integer result;
		String dateInString;
		String dateInString2;
		Date startDateDefault;
		Date finishDateDefault;
		SimpleDateFormat format;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());
		finder = this.copyFinder(finder);

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "01/01/2015";
		dateInString2 = "31/12/2018";
		finishDateDefault = new Date();
		startDateDefault = new Date();

		try {
			finishDateDefault = format.parse(dateInString2);
			startDateDefault = format.parse(dateInString);
		} catch (final ParseException e) {

		}

		finder.setStartedDate(startDateDefault);
		finder.setFinishedDate(finishDateDefault);

		finder.setKeyWord("Viaje");

		saved = this.finderService.save(finder);

		result = saved.getTrips().size();

		Assert.isTrue(result == 1);

		super.authenticate(null);
	}

	/*
	 * Se cogen todos los Finder y vemos si tiene el valor es el esperado.
	 */
	@Test
	public void testFindAll() {
		Integer result;

		result = this.finderService.findAll().size();

		Assert.notNull(result == 3);
	}

	// Un Finder accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		Finder finderCollection;
		Finder finder;
		Collection<Finder> saved;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		saved = this.finderService.findAll();

		finderCollection = this.finderService.create(explorer);

		for (final Finder finders : saved) {
			finderCollection = finders;
			break;
		}

		finder = this.finderService.findOne(finderCollection.getId());

		Assert.isTrue(finderCollection.getId() == finder.getId());

		super.authenticate(null);

	}

	/* Comprobamos el metodo findByExplorerId */
	@Test
	public void testFindByExplorerId() {
		Explorer explorer;
		Finder finder;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		finder = this.finderService.findByExplorerId(explorer.getId());

		Assert.isTrue(finder.getExplorer() == explorer);

		super.authenticate(null);

	}

	/* Comprobamos el metodo findByTripId */
	@Test
	public void findByTripId() {
		Collection<Trip> trips;
		Collection<Finder> finders;
		Trip trip;

		trips = this.tripService.findAll();

		trip = null;

		for (final Trip t : trips)
			if (t.getTicker().equals("141214-ASDF")) {
				trip = t;
				break;
			}

		finders = this.finderService.findByTripId(trip.getId());

		Assert.isTrue(finders.size() == 3);
	}

	public Finder copyFinder(final Finder finder) {
		Finder result;

		result = new Finder();
		result.setId(finder.getId());
		result.setVersion(finder.getVersion());
		result.setExplorer(finder.getExplorer());
		result.setFinishedDate(finder.getFinishedDate());
		result.setKeyWord(finder.getKeyWord());
		result.setMaxPrice(finder.getMaxPrice());
		result.setMinPrice(finder.getMinPrice());
		result.setMoment(finder.getMoment());
		result.setStartedDate(finder.getStartedDate());
		result.setTrips(finder.getTrips());

		return result;
	}

}
