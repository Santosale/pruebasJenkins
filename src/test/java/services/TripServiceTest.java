
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Category;
import domain.LegalText;
import domain.Manager;
import domain.Ranger;
import domain.Stage;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class TripServiceTest extends AbstractTest {

	// Service under test
	@Autowired
	private TripService				tripService;

	// Managed services
	@Autowired
	private LegalTextService		legalTextService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private RangerService			rangerService;

	@Autowired
	private CategoryService			categoryService;

	@Autowired
	private StageService			stageService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private ConfigurationService	configurationService;


	@Test
	public void testCreate() {
		Trip result;
		Manager manager;

		super.authenticate("manager1");
		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = this.tripService.create(manager);

		Assert.notNull(result);
		Assert.notNull(result.getTicker());
		Assert.isNull(result.getTitle());
		Assert.notNull(result.getPrice());
		Assert.notNull(result.getStages());

	}

	@Test
	public void testFindAll() {
		List<Trip> result;
		Trip trip;

		result = new ArrayList<Trip>();
		result.addAll(this.tripService.findAllVisible());

		//Cogemos un trip y vemos que este contenido
		trip = result.get(0);

		Assert.notEmpty(result);
		Assert.isTrue(result.contains(this.tripService.findOne(trip.getId())));
		Assert.isTrue(this.tripService.findAll().containsAll(result));
	}

	@Test
	public void testFindOne() {
		Trip result;
		Trip trip;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		result = this.tripService.save(trip);

		Assert.isTrue(this.tripService.findOne(result.getId()).equals(result));
		this.authenticate(null);
	}

	//Guardamos un trip y comprobamos que se ha guardado. Intentamos actualizar un trip cambiando su ticker, no nos lo permite. Intentamos cambiar un trip sin estar autenticado, no nos deja
	@Test
	public void testSave1() {
		Trip trip;
		Trip saved;
		Trip copy;
		Trip copySave;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.tripService.save(trip);
		Assert.isTrue(this.tripService.findAll().contains(saved));

		//Cambiamos ahora el ticker
		copy = this.copyTrip(saved);
		copy.setTicker("120420-ASDF");

		//COmprobamos que no se cambie
		copySave = null;
		try {
			copySave = this.tripService.save(copy);
		} catch (final IllegalArgumentException e) {
		}
		Assert.isNull(copySave);

		Assert.isTrue(saved.getTicker().equals(this.tripService.findOne(saved.getId()).getTicker()));

		this.authenticate(null);

		//Intentamos modificar un campo sin estar autenticados
		copy = this.copyTrip(saved);
		copy.setTitle("Nuevo título");

		try {
			copySave = this.tripService.save(copy);
		} catch (final IllegalArgumentException e) {
		}

		Assert.isNull(copySave);
	}

	//Actualizamos un trip, estando autenticado como su manager
	@Test
	public void testSave2() {
		Trip trip;
		Trip saved;
		Trip copySaved;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.tripService.save(trip);

		Assert.notNull(saved.getTicker());

		//Cambiamos el trip
		saved.setTitle("Nuevo título");
		copySaved = this.tripService.save(saved);
		Assert.isTrue(copySaved.getTitle().equals("Nuevo título"));

	}

	//Intentamos guardar un trip con fecha de inicio posterior a la de fin. Intentamos guardar un trip con fecha de inicio anterior a la de publicacion. Intentamos guardar una cancelación con el método save. En todos los casos, encontramos un fallo 
	@Test
	public void testSave3() {
		Trip trip;
		Trip saved;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2021";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());

		//Intentamos guardar un trip con fecha inicio posterior a la de fin, vemos que falla
		saved = null;
		try {
			saved = this.tripService.save(trip);
		} catch (final IllegalArgumentException e) {
		}
		Assert.isNull(saved);

		//Intentamos guardar un trip con fecha de inicio anterior a la de publicacion. Falla
		// Inicializamos las fechas
		dateStartString = "02/02/2019";
		datePublicationString = "01/01/2020";

		startDate = new Date();
		publicationDate = new Date();

		try {
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}
		trip.setStartDate(startDate);
		trip.setPublicationDate(publicationDate);
		saved = null;
		try {
			saved = this.tripService.save(trip);
		} catch (final IllegalArgumentException e) {
		}
		Assert.isNull(saved);

		//Ponemos las fechas bien, y lo intentamos cancelar 
		datePublicationString = "01/01/2019";

		publicationDate = new Date();

		try {
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}
		trip.setPublicationDate(publicationDate);

		//Cancelamos el viaje
		saved = null;
		trip.setCancellationReason("Una razón");
		try {
			saved = this.tripService.save(trip);
		} catch (final IllegalArgumentException e) {
		}
		Assert.isNull(saved);
	}

	//Comprobamos que el precio se actualice correctamente. Al meter dos stages a un trip, y más tarde borrando una.
	@Test
	public void testSave4() {
		Trip trip;
		Trip saved;
		Trip copyTrip;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;
		Stage stage1;
		Stage stage2;
		Stage savedStage1;
		Double price;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());

		//Guardamos el viaje
		saved = this.tripService.save(trip);

		//Guardamos dos stages
		stage1 = this.stageService.create(saved);
		stage1.setDescription("Escalaremos montañas bajas.");
		stage1.setPrice(300);
		stage1.setTitle("Tramo I Escalada Tanzanika.");
		savedStage1 = this.stageService.save(stage1);

		stage2 = this.stageService.create(saved);
		stage2.setDescription("Escalaremos montañas altas.");
		stage2.setPrice(300);
		stage2.setTitle("Tramo II Escalada Tanzanika.");
		this.stageService.save(stage2);

		//Recuperamos de nuevo el viaje ya con sus stages
		saved = this.tripService.findOne(saved.getId());

		//Vemos que el trip tenga dos stages
		Assert.isTrue(saved.getStages().size() == 2);

		//Calculamos el precio que debe dar
		price = stage1.getPrice() + stage2.getPrice();
		price = price + (price * (this.configurationService.findVat() / 100));

		//Vemos que el precio del trip es el mismo que el ya calculado
		Assert.isTrue(saved.getPrice() == price);

		//Borramos una stage y vemos que se actualice su precio
		this.stageService.delete(savedStage1);

		//Recuperamos de nuevo el viaje
		copyTrip = this.copyTrip(saved);
		copyTrip = this.tripService.findOne(saved.getId());

		//Actualizamos el price
		price = stage2.getPrice() + (stage2.getPrice() * (this.configurationService.findVat() / 100));
		Assert.isTrue(copyTrip.getPrice() == price);

	}

	@Test
	public void testDelete() {
		final Trip result;
		Trip trip;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;
		String datePublicationString;
		Date publicationDate;
		Date startDate;
		Date finishDate;
		Ranger ranger;
		Category category;
		Manager manager;
		LegalText legalText;

		// Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "02/03/2020";
		datePublicationString = "01/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		// Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		// Inicializamos el trip
		super.authenticate(manager.getUserAccount().getUsername());
		trip = this.tripService.create(manager);
		super.authenticate(null);

		trip.setDescription("Aprende a escalar disfrutando el momento.");
		trip.setEndDate(finishDate);
		trip.setExplorerRequirements("Debe llevar calzado adecuado");
		trip.setPublicationDate(publicationDate);
		trip.setRanger(ranger);
		trip.setCategory(category);
		trip.setManager(manager);
		trip.setLegalText(legalText);
		trip.setStartDate(startDate);
		trip.setTitle("Viaje escalada por Tanzania");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		result = this.tripService.save(trip);
		this.authenticate(null);

		super.authenticate("manager1");

		this.tripService.delete(result);

		Assert.isNull(this.tripService.findOne(result.getId()));

		super.authenticate(null);

	}

	//Intentamos cancelar un viaje que ha sido publicado pero no ha comenzado
	//Intentamos cancelar un viaje que no ha sido publicado, debe fallar
	//Intentamos cancelar un viaje que ya ha empezado, debe fallar
	@Test
	public void testCancellTrip1() {
		Trip trip;

		//Intentamos cancelar un viaje que ha sido publicado pero no ha comenzado
		trip = this.getTripPublicate(true, false);
		this.authenticate(trip.getManager().getUserAccount().getUsername());

		this.tripService.cancellTrip(trip.getId(), "No tenemos solicitudes");

		Assert.isTrue(trip.getCancellationReason() != null);

		//Intentamos cancelar un viaje que no ha sido publicado, debe fallar
		trip = this.getTripPublicate(false, false);
		this.authenticate(trip.getManager().getUserAccount().getUsername());

		try {

		} catch (final IllegalArgumentException e) {
			this.tripService.cancellTrip(trip.getId(), "No tenemos solicitudes");
		}

		Assert.isTrue(trip.getCancellationReason() == null);

		//Intentamos cancelar un viaje que ya ha empezado, debe fallar
		trip = this.getTripPublicate(true, true);
		this.authenticate(trip.getManager().getUserAccount().getUsername());

		try {

		} catch (final IllegalArgumentException e) {
			this.tripService.cancellTrip(trip.getId(), "No tenemos solicitudes");
		}

		Assert.isTrue(trip.getCancellationReason() == null);
	}

	//Vemos que si no es su manager el que lo modifica, debe fallar
	@Test
	public void testCancellTrip2() {
		Trip trip;

		//Intentamos cancelar un viaje que ha sido publicado pero no ha comenzado
		trip = this.getTripPublicate(true, false);

		try {
			this.tripService.cancellTrip(trip.getId(), "No tenemos solicitudes");
		} catch (final IllegalArgumentException e) {
		}

		Assert.isTrue(trip.getCancellationReason() == null);
	}

	//Buscamos sin parámetros, con un rango de fechas y otro de precios
	@Test
	public void testFindByPriceRangeAndDateRangeAndKeyword() {
		Collection<Trip> trips;
		Date finishDate;
		Date startDate;
		SimpleDateFormat format;
		String dateStartString;
		String dateFinishString;

		//Si no especificamos parametros 
		trips = this.tripService.findByPriceRangeAndDateRangeAndKeyword(null, null, null, null, null);
		Assert.isTrue(trips.size() == 3);

		//Dado un rango de precio
		trips = this.tripService.findByPriceRangeAndDateRangeAndKeyword(260.0, 0.0, null, null, null);
		Assert.isTrue(trips.size() == 1);

		//Dado un rango de fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "12/07/2019";
		dateFinishString = "12/08/2019";

		finishDate = new Date();
		startDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
		} catch (final ParseException e) {

		}
		trips = this.tripService.findByPriceRangeAndDateRangeAndKeyword(null, null, startDate, finishDate, null);
		Assert.isTrue(trips.size() == 1);

	}

	@Test
	public void testFindTenPerCentMoreApplicationThanAverage() {
		Collection<Trip> result;
		Integer i;
		Integer amountApplication;

		super.authenticate("admin");

		result = this.tripService.findTenPerCentMoreApplicationThanAverage();

		Assert.notEmpty(result);

		i = 0;
		for (final Trip t : result) {

			//Hay viajes que no son publicos, nos autenticamos como cada mnager para poder verlos todos
			this.authenticate(t.getManager().getUserAccount().getUsername());
			amountApplication = this.applicationService.findByTripId(t.getId()).size();

			if (i == 0)
				Assert.isTrue(amountApplication == 2);
			else if (i == 1)
				Assert.isTrue(amountApplication == 3);

			i++;

			this.authenticate(null);
		}

		super.authenticate(null);

	}
	@Test
	public void testRatioTripCancelledVsTotal() {
		Double result;

		super.authenticate("admin");

		result = this.tripService.ratioTripCancelledVsTotal();

		super.authenticate(null);

		Assert.notNull(result);
	}

	@Test
	public void testRatioTripOneAuditRecordVsTotal() {
		Double result;

		super.authenticate("admin");

		result = this.tripService.ratioTripOneAuditRecordVsTotal();

		super.authenticate(null);

		Assert.notNull(result);
	}

	@Test
	public void testFindAllVisible() {
		Collection<Trip> result;
		Date currentMoment;

		currentMoment = new Date();
		result = this.tripService.findAllVisible();
		Assert.notNull(result);

		for (final Trip trip : result) {
			Assert.isTrue(this.tripService.findAll().contains(trip));
			Assert.isTrue(trip.getPublicationDate().compareTo(currentMoment) < 0);
		}

	}

	@Test
	public void testFindOneVisible() {
		Collection<Trip> trips;
		Integer count;
		Trip aux;

		trips = this.tripService.findAll();
		count = 0;

		for (final Trip trip : trips) {
			aux = this.tripService.findOneVisible(trip.getId());

			if (aux != null)
				count++;
		}

		Assert.isTrue(count == 3);
	}

	@Test
	public void testFindByCategoryId() {
		Trip trip;
		Category category;

		trip = this.getTripPublicate(true, false);
		category = trip.getCategory();

		Assert.isTrue(this.tripService.findByCategoryId(category.getId()).contains(trip));
	}

	@Test
	public void testFindByCategoryIdAllTrips() {
		Trip trip;
		Category category;

		trip = this.getTripPublicate(false, false);
		category = trip.getCategory();

		Assert.isTrue(this.tripService.findByCategoryIdAllTrips(category.getId()).contains(trip));
	}

	@Test
	public void testFindByKeyWord() {
		Collection<Trip> result;

		//Ponemos una palabra que se encuentra en dos viajes
		result = this.tripService.findByKeyWord("viaje");
		Assert.notNull(result);
		Assert.isTrue(result.size() == 2);

		//Ponemos una palabra que no se encuentre
		result = this.tripService.findByKeyWord("viajecasjlasnDSK");
		Assert.notNull(result);
		Assert.isTrue(result.size() == 0);

	}

	@Test
	public void testAvgMinMaxStandardDTripsPerManager() {
		Double[] result;

		super.authenticate("admin");

		result = this.tripService.avgMinMaxStandardDTripsPerManager();

		super.authenticate(null);

		Assert.notNull(result);
		Assert.isTrue(result.length == 4);
	}

	@Test
	public void testAvgMinMaxStandardDPriceOfTrips() {
		Double[] result;

		super.authenticate("admin");

		result = this.tripService.avgMinMaxStandardDPriceOfTrips();

		super.authenticate(null);

		Assert.notNull(result);
		Assert.isTrue(result.length == 4);
	}

	@Test
	public void testAvgMinMaxStandardDTripsPerRanger() {
		Double[] result;

		super.authenticate("admin");

		result = this.tripService.avgMinMaxStandardDTripsPerRanger();

		super.authenticate(null);

		Assert.notNull(result);
		Assert.isTrue(result.length == 4);
	}

	@Test
	public void testFindByLegalTextId() {
		Trip trip;
		LegalText legalText;

		trip = this.getTripPublicate(false, false);
		legalText = trip.getLegalText();

		Assert.isTrue(this.tripService.findByLegalTextId(legalText.getId()).contains(trip));
	}

	@Test
	public void testCountLegalTextReferences() {
		Map<LegalText, Long> result;
		LegalText legalText1;
		LegalText legalText2;

		super.authenticate("admin");

		result = this.tripService.countLegalTextReferences();

		legalText1 = (LegalText) result.keySet().toArray()[0];
		legalText2 = (LegalText) result.keySet().toArray()[1];

		Assert.isTrue(result.size() == 2);

		Assert.isTrue(result.get(legalText1) == 1 || result.get(legalText1) == 2);
		Assert.isTrue(result.get(legalText2) == 1 || result.get(legalText2) == 2);

		super.authenticate(null);

	}

	@Test
	public void testFindByManagerUserAccountId() {
		Trip trip;
		Manager manager;

		trip = this.getTripPublicate(false, false);
		manager = trip.getManager();

		Assert.isTrue(this.tripService.findByManagerUserAccountId(manager.getUserAccount().getId()).contains(trip));
	}

	@Test
	public void testPublishTrip() {
		Trip trip;
		Calendar calendar;

		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

		trip = this.getTripPublicate(false, false);

		this.tripService.publishTrip(trip.getId());

		trip = this.tripService.findOne(trip.getId());

		Assert.isTrue(calendar.getTime().compareTo(trip.getPublicationDate()) <= 0);
	}

	@Test
	public void testFindByRangerUserAccountId() {
		Trip trip;
		Ranger ranger;

		trip = this.getTripPublicate(false, false);
		ranger = trip.getRanger();

		Assert.isTrue(this.tripService.findByRangerUserAccountId(ranger.getUserAccount().getId()).contains(trip));
	}

	private Trip getTripPublicate(final boolean publicate, final boolean startedTrip) {
		List<Trip> trips;
		Trip result;
		Date currentMoment;

		currentMoment = new Date();
		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		result = null;

		for (final Trip tripIterator : trips)
			//Te da un viaje que no se haya publicado aún
			if (tripIterator.getPublicationDate().compareTo(currentMoment) > 0 && !publicate) {
				result = tripIterator;
				break;

				//Te da un viaje que se haya publicado y no haya empezado
			} else if (tripIterator.getPublicationDate().compareTo(currentMoment) < 0 && publicate && !startedTrip && tripIterator.getStartDate().compareTo(currentMoment) > 0) {
				result = tripIterator;
				break;

				//Te da un viaje que se haya publicado y haya empezado
			} else if (tripIterator.getPublicationDate().compareTo(currentMoment) < 0 && publicate && startedTrip && tripIterator.getStartDate().compareTo(currentMoment) < 0) {
				result = tripIterator;
				break;
			}

		return result;
	}

	/*
	 * Comprobamos que una petición básica de paginación se realiza bien.
	 * En concreto, se prueba a pedir los elementos de la página dos
	 * habiendo dos elementos página. Habiendo cuatro viajes, debe obtener
	 * un viaje en la página dos perfectamente.
	 */
	@Test
	public void testPaginatedFindAll1() {
		Page<Trip> result;

		// Número de página a consultar
		Integer pageNumber;
		pageNumber = 2;

		// Número de elementos por página
		Integer pageSize;
		pageSize = 2;

		// Realizamos la petición al findAll
		result = this.tripService.findAll(pageNumber, pageSize);

		// Primero comprobamos que no está vacío el resultado la consulta
		Assert.notEmpty(result.getContent());

		// Segundo comprobamos que el número de elementos devueltos sea
		// el mismo que el que requerimos en la variable pageSize.
		Assert.isTrue(result.getContent().size() == pageSize);

		// Tercero, comprobamos que este devuelvo la segunda página.
		// Se le suma uno debido a que Hibernate pagina empezando en el cero.
		Assert.isTrue(result.getNumber() + 1 == pageNumber);

		// Por último, comprobamos que el número total de páginas coincide con lo esperado
		// que se obtiene de la división del número de trips entre el tamaño de la página.
		Assert.isTrue(result.getTotalPages() == Math.ceil(new Double(result.getTotalElements()) / new Double(pageSize)));

	}

	/*
	 * Comprobamos que una petición básica de paginación se realiza bien.
	 * En concreto, se prueba a pedir los elementos de la página dos
	 * habiendo dos elementos página. Habiendo cuatro viajes, debe obtener
	 * un viaje en la página dos perfectamente.
	 */
	@Test
	public void testPaginatedFindAllVisible1() {
		Page<Trip> result;

		// Número de página a consultar
		Integer pageNumber;
		pageNumber = 1;

		// Número de elementos por página
		Integer pageSize;
		pageSize = 2;

		// Realizamos la petición al findAll
		result = this.tripService.findAllVisible(pageNumber, pageSize);

		// Primero comprobamos que no está vacío el resultado la consulta
		Assert.notEmpty(result.getContent());

		// Segundo comprobamos que el número de elementos devueltos sea
		// el mismo que el que requerimos en la variable pageSize.
		Assert.isTrue(result.getContent().size() == pageSize);

		// Tercero, comprobamos que este devuelvo la segunda página.
		// Se le suma uno debido a que Hibernate pagina empezando en el cero.
		Assert.isTrue(result.getNumber() + 1 == pageNumber);

		// Por último, comprobamos que el número total de páginas coincide con lo esperado
		// que se obtiene de la división del número de trips entre el tamaño de la página.
		Assert.isTrue(result.getTotalPages() == Math.ceil(new Double(result.getTotalElements()) / new Double(pageSize)));

	}

	//Hacemos este metodo para evitar que Spring salve sin usar save, si no con simples setPropiedad...
	private Trip copyTrip(final Trip trip) {
		Trip result;

		result = new Trip();
		result.setCancellationReason(trip.getCancellationReason());
		result.setCategory(trip.getCategory());
		result.setDescription(trip.getDescription());
		result.setEndDate(trip.getEndDate());
		result.setExplorerRequirements(trip.getExplorerRequirements());
		result.setId(trip.getId());
		result.setLegalText(trip.getLegalText());
		result.setManager(trip.getManager());
		result.setPrice(trip.getPrice());
		result.setPublicationDate(trip.getPublicationDate());
		result.setRanger(trip.getRanger());
		result.setStages(trip.getStages());
		result.setStartDate(trip.getStartDate());
		result.setTicker(trip.getTicker());
		result.setTitle(trip.getTitle());
		result.setVersion(trip.getVersion());

		return result;
	}

}
