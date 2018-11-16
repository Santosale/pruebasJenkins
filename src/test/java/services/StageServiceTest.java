
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
public class StageServiceTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private StageService		stageService;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private RangerService		rangerService;

	@Autowired
	private ManagerService		managerService;

	@Autowired
	private TripService			tripService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private LegalTextService	legalTextService;


	// Tests ----------------------------------------------
	@Test
	public void testCreate() {
		Stage stage;
		Trip trip;

		trip = this.getTripPublicate(false);
		stage = this.stageService.create(trip);

		Assert.isNull(stage.getDescription());
		Assert.notNull(stage.getNumStage());
		Assert.notNull(stage.getPrice());
		Assert.isNull(stage.getTitle());
		Assert.notNull(stage.getTrip());
	}

	@Test
	public void testFindOne() {
		Trip trip;
		Stage stage;

		trip = this.getTripPublicate(false);

		stage = (Stage) trip.getStages().toArray()[0];

		Assert.isTrue(stage.equals(this.stageService.findOne(stage.getId())));

	}

	@Test
	public void testFindAll() {
		Trip trip;
		Stage stage;

		trip = this.getTripPublicate(false);

		stage = (Stage) trip.getStages().toArray()[0];

		Assert.isTrue(this.stageService.findAll().contains(stage));

	}

	//Actualizar una stage del trip
	@Test
	public void testSave1() {
		final Trip trip;
		Stage stage;
		Stage saved;

		trip = this.getTripPublicate(false);

		//Añadimos un stage y vemos que se recupere
		stage = this.stageService.create(trip);
		stage.setDescription("Escalaremos montañas bajas.");
		stage.setPrice(300);
		stage.setTitle("Tramo I Escalada Tanzanika.");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.stageService.save(stage);
		this.authenticate(null);

		Assert.isTrue(this.stageService.findAll().contains(saved));

		//Vemos que el trip tenga la nueva stage
		Assert.isTrue(trip.getStages().contains(saved));
	}

	//Actualizar una stage del trip, nos deja. Actualizar el numStage, no nos permite, no nos deja tampoco actualizar una stage si no es el manager del trip
	@Test
	public void testSave2() {
		final Trip trip;
		Stage stage;
		Stage saved;
		Stage copyStage;

		trip = this.getTripPublicate(false);

		//Añadimos un stage y vemos que se recupere
		stage = this.stageService.create(trip);
		stage.setDescription("Escalaremos montañas bajas.");
		stage.setPrice(300);
		stage.setTitle("Tramo I Escalada Tanzanika.");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.stageService.save(stage);
		this.authenticate(null);

		//Modificamos el stage y vemos que se modifica
		saved.setTitle("Nuevo titulo");
		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.stageService.save(saved);

		Assert.isTrue(saved.getTitle().equals("Nuevo titulo"));

		//Vemos que no nos deje modificar la stage si no esta logeado su manager
		try {
			saved = this.stageService.save(saved);
		} catch (final IllegalArgumentException e) {
		}
		Assert.isTrue(saved.getTitle().equals("Nuevo titulo"));

		//Vemos que no deje modificar el numStage
		copyStage = this.copyStage(saved);
		copyStage.setNumStage(100);

		try {
			saved = null;
			saved = this.stageService.save(copyStage);

		} catch (final IllegalArgumentException e) {
		}
		this.authenticate(null);

		Assert.isNull(saved);

	}

	@Test
	public void testSave3() {
		Stage stage;
		Stage saved;
		Trip trip;
		Integer numStagesOld;

		trip = this.getTripPublicate(false);
		Assert.notNull(trip);

		numStagesOld = trip.getStages().size();

		//Metemos un nuevo Stage y vemos que su num sea el correcto, el consecutivo
		stage = this.stageService.create(trip);
		stage.setDescription("Escalaremos montañas altas.");
		stage.setPrice(200);
		stage.setTitle("Tramo II Escalada Tanzanika.");

		this.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.stageService.save(stage);
		this.authenticate(null);

		Assert.isTrue(saved.getNumStage() == numStagesOld + 1);
	}

	//Creamos un trip le metemos un stage y vemos que su numStage sea 1
	@Test
	public void testSave4() {
		Stage stage;
		Stage stageSaved;
		Trip trip;
		Trip tripSaved;
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

		//Inicializamos las fechas
		format = new SimpleDateFormat("dd/MM/yyyy");
		dateStartString = "02/02/2019";
		dateFinishString = "20/03/2019";
		datePublicationString = "12/01/2019";

		finishDate = new Date();
		startDate = new Date();
		publicationDate = new Date();

		try {
			finishDate = format.parse(dateFinishString);
			startDate = format.parse(dateStartString);
			publicationDate = format.parse(datePublicationString);
		} catch (final ParseException e) {

		}

		//Cogemos las entidades necesarias
		legalText = (LegalText) this.legalTextService.findAll().toArray()[0];
		manager = (Manager) this.managerService.findAll().toArray()[0];
		ranger = (Ranger) this.rangerService.findAll().toArray()[0];
		category = (Category) this.categoryService.findAll().toArray()[0];

		//Inicializamos el trip
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
		tripSaved = this.tripService.save(trip);

		//Creamos el stage
		stage = this.stageService.create(tripSaved);
		stage.setDescription("Escalaremos montañas altas.");
		stage.setPrice(200);
		stage.setTitle("Tramo II Escalada Tanzanika.");

		stageSaved = this.stageService.save(stage);
		this.authenticate(null);

		Assert.isTrue(stageSaved.getNumStage() == 1);
	}

	@Test
	public void testDelete() {
		Trip trip;
		Stage stage;

		trip = this.getTripPublicate(false);

		//Borrar autenticado como el manager del viaje
		this.authenticate(trip.getManager().getUserAccount().getUsername());
		stage = (Stage) trip.getStages().toArray()[0];

		Assert.notNull(this.stageService.findOne(stage.getId()));
		Assert.isTrue(trip.getStages().contains(stage));

		//Borramos la stage
		this.stageService.delete(stage);
		Assert.isNull(this.stageService.findOne(stage.getId()));
		Assert.isTrue(!trip.getStages().contains(stage));

	}

	//Tengo los stages que son mayores de uno
	@Test
	public void testFindByTripIdMinNumStage() {
		Trip trip;
		Collection<Stage> stages;
		Integer count;

		trip = this.getTripPublicate(false);
		stages = this.stageService.findByTripIdMinNumStage(1, trip.getId());

		//El primero que devuelve es el segundo
		count = 2;

		for (final Stage stageIterator : stages) {
			Assert.isTrue(stageIterator.getNumStage() == count);
			count++;
		}
	}

	//Comprobamos que el orden comience en uno sea consecutivo y números positivos.
	@Test
	public void testFindByTripIdOrderByNumStage() {
		Trip trip;
		Integer count;

		trip = this.getTripPublicate(false);
		count = 1;

		for (final Stage stageIterator : trip.getStages()) {
			Assert.isTrue(stageIterator.getNumStage() == count);
			count++;
		}

	}

	@Test
	public void testFindByTripId() {
		Trip trip;

		trip = this.getTripPublicate(false);

		Assert.notNull(this.stageService.findByTripId(trip.getId()));
	}

	//Borramos una stage que no sea la última, vemos que se actualice correctamente las demás. 
	@Test
	public void testUpdateNumStage1() {
		Trip trip;
		Stage stage;
		List<Stage> stagesOrdered;
		List<Stage> newStagesOrdered;
		Integer count;

		trip = this.getTripPublicate(false);
		stagesOrdered = new ArrayList<Stage>();
		stagesOrdered.addAll(this.stageService.findByTripIdOrderByNumStage(trip.getId()));

		if (stagesOrdered.size() >= 2) {
			stage = null;
			//Cogemos la primera stage
			stage = stagesOrdered.get(0);

			//La borramos
			this.authenticate(trip.getManager().getUserAccount().getUsername());
			this.stageService.delete(stage);

			//Sacamos los nuevos stages ordenados
			trip = this.tripService.findOne(trip.getId());
			newStagesOrdered = new ArrayList<Stage>();
			newStagesOrdered.addAll(this.stageService.findByTripIdOrderByNumStage(trip.getId()));

			//Debe haber uno menos
			Assert.isTrue(newStagesOrdered.size() + 1 == stagesOrdered.size());
			count = 1;

			//Vemos que se hayan actualizado los números
			for (final Stage stageIterator : newStagesOrdered) {
				Assert.isTrue(stageIterator.getId() == stagesOrdered.get(count).getId());
				Assert.isTrue(stageIterator.getNumStage() == count);
				count++;
			}

		}

	}

	//Borramos la última stage, vemos que nada cambie
	@Test
	public void testUpdateNumStage2() {
		Trip trip;
		Stage stage;
		List<Stage> stagesOrdered;
		List<Stage> newStagesOrdered;
		Integer count;

		trip = this.getTripPublicate(false);
		stagesOrdered = new ArrayList<Stage>();
		stagesOrdered.addAll(this.stageService.findByTripIdOrderByNumStage(trip.getId()));

		if (stagesOrdered.size() >= 2) {
			stage = null;
			//Cogemos la primera stage
			stage = stagesOrdered.get(stagesOrdered.size() - 1);

			//La borramos
			this.authenticate(trip.getManager().getUserAccount().getUsername());
			this.stageService.delete(stage);

			//Sacamos los nuevos stages ordenados
			trip = this.tripService.findOne(trip.getId());
			newStagesOrdered = new ArrayList<Stage>();
			newStagesOrdered.addAll(this.stageService.findByTripIdOrderByNumStage(trip.getId()));

			//Debe haber uno menos
			Assert.isTrue(newStagesOrdered.size() + 1 == stagesOrdered.size());
			count = 1;

			//Vemos que no haya cambiado nada
			for (final Stage stageIterator : newStagesOrdered) {
				Assert.isTrue(stageIterator.getId() == stagesOrdered.get(count - 1).getId());
				Assert.isTrue(stageIterator.getNumStage() == count);
				count++;
			}

		}

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

	//Hacemos este metodo para evitar que Spring salve sin usar save, si no con simples setPropiedad...
	private Stage copyStage(final Stage stage) {
		Stage result;

		result = new Stage();
		result.setDescription(stage.getDescription());
		result.setNumStage(stage.getNumStage());
		result.setPrice(stage.getPrice());
		result.setTrip(stage.getTrip());
		result.setTitle(stage.getTitle());
		result.setId(stage.getId());
		result.setVersion(stage.getVersion());

		return result;
	}
}
