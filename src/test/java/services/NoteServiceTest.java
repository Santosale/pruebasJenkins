
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Auditor;
import domain.Manager;
import domain.Note;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class NoteServiceTest extends AbstractTest {

	// Service under test
	@Autowired
	private NoteService		noteService;

	// Supporting services
	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private TripService		tripService;

	@Autowired
	private ManagerService 	managerService;


	@Test
	public void testCreate() {
		Note result;
		Auditor auditor;
		Trip trip;

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		result = this.noteService.create(auditor, trip);
		result.setRemark("Este viaje está muy bien hecho.");

		Assert.notNull(result);
		Assert.notNull(result.getRemark());
		Assert.notNull(result.getAuditor());
		Assert.notNull(result.getTrip());
	}

	@Test
	public void testFindAll() {
		Collection<Note> result;

		result = this.noteService.findAll();

		Assert.notEmpty(result);
	}

	@Test
	public void testMinMaxAvgStandardDerivationNotePerTrip() {
		Double[] result;

		super.authenticate("admin");

		result = this.noteService.minMaxAvgStandardDerivationNotePerTrip();

		Assert.notNull(result);
		Assert.isTrue(result.length == 4);

		super.authenticate(null);
	}

	// Persistimos una nota con todos los valores y relaciones correctos
	@Test
	public void testSave1() {
		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		Assert.isTrue(this.noteService.findAll().contains(result));

		super.authenticate(null);

	}

	@Test
	public void testSave2() {

		// Provocamos un error porque no hemos iniciado sesión o si la hemos iniciado como otro actor

		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate(null);

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		try {
			result = this.noteService.save(note);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate("admin");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		try {
			result = this.noteService.save(note);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

	}

	@Test
	public void testSave3() {

		// Provocar un error rellenando el managerReply desde el principio

		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");
		note.setManagerReply("Esto es una respuesta de un manager");

		try {
			result = this.noteService.save(note);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testSave4() {

		// Añadiremos correctamente la respuesta del manager a la nota

		Note note;
		Note result;
		Note result2;
		Note saved;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		Assert.isTrue(this.noteService.findAll().contains(result));

		super.authenticate(null);

		super.authenticate("manager1");

		saved = this.copyNote(result);

		saved.setManagerReply("Esto es una respuesta de un manager en el testSave4");

		result2 = this.noteService.save(saved);

		Assert.isTrue(this.noteService.findAll().contains(result2));
		Assert.notNull(result2.getManagerReply());
		Assert.notNull(result2.getMomentReply());

		super.authenticate(null);

	}

	@Test
	public void testSave5() {

		// Una vez creada la nota y añadida la respuesta del manager, comprobamos que ya no se pueda modificar

		Note note;
		Note result;
		Note result2;
		Note result3;
		Note saved;
		Note saved2;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		Assert.isTrue(this.noteService.findAll().contains(result));

		super.authenticate(null);

		super.authenticate("manager1");

		saved = this.copyNote(result);

		saved.setManagerReply("Esto es una respuesta de un manager en el testSave4");

		result2 = this.noteService.save(saved);

		Assert.isTrue(this.noteService.findAll().contains(result2));
		Assert.notNull(result2.getManagerReply());
		Assert.notNull(result2.getMomentReply());

		saved2 = this.copyNote(result2);
		saved2.setRemark("I'm a super start");

		try {
			result3 = this.noteService.save(saved2);
			Assert.isNull(result3);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

		super.authenticate("auditor1");

		super.authenticate(null);

	}

	@Test
	public void testFindByAuditorId1() {

		// Persistimos una nota y luego vemos que findByAuditorId te la recupere

		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		Assert.isTrue(this.noteService.findByAuditorId(auditor.getId()).contains(result));

		super.authenticate(null);

	}

	@Test
	public void testFindByAuditorId2() {

		// Persistimos una nota y luego en findByAuditor metemos el id de un auditor diferente al que lo creó, entonces debe saltar excepción

		Note note;
		Collection<Note> result;
		Auditor auditor;
		Auditor anotherAuditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		anotherAuditor = (Auditor) this.auditorService.findAll().toArray()[1];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		this.noteService.save(note);

		try {
			result = this.noteService.findByAuditorId(anotherAuditor.getId());
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}
	
	@Test
	public void testFindByManagerId1() {

		// Persistimos una nota y luego vemos que findByManagerId te la recupere

		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		Assert.isTrue(this.noteService.findByManagerId(trip.getManager().getId()).contains(result));

		super.authenticate(null);

	}

	@Test
	public void testFindByManagerId2() {

		// Persistimos una nota y luego en findByManagerId metemos el id de un manager diferente al que lo creó, entonces debe saltar excepción

		Note note;
		Collection<Note> result;
		Auditor auditor;
		Manager anotherManager;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		anotherManager = (Manager) this.managerService.findAll().toArray()[1];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		this.noteService.save(note);

		try {
			result = this.noteService.findByManagerId(anotherManager.getId());
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testFindByManagerIdAndAuditorId1() {

		// Persistimos la nota y la recuperamos. Para este método hay que ser manager

		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		super.authenticate(null);

		super.authenticate("manager1");

		Assert.isTrue(this.noteService.findByManagerIdAndAuditorId(trip.getManager().getId(), auditor.getId()).contains(result));

		super.authenticate(null);

	}

	@Test
	public void testFindByManagerIdAndAuditorId2() {

		// Persistimos una nota y luego en findByManagerIdAndAuditorId metemos el id de un auditor diferente al que lo creó

		Note note;
		Note result;
		Auditor auditor;
		Auditor anotherAuditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		anotherAuditor = (Auditor) this.auditorService.findAll().toArray()[1];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		super.authenticate(null);

		super.authenticate("manager1");

		Assert.isTrue(!this.noteService.findByManagerIdAndAuditorId(trip.getManager().getId(), anotherAuditor.getId()).contains(result));

		super.authenticate(null);

	}
	
	@Test
	public void testCheckSpamWords() {
		
		Note note;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");
		note.setManagerReply("Todo SEX.");
		
		Assert.isTrue(this.noteService.checkSpamWords(note));
		
	}
	
	@Test
	public void testFindByTripId() {
		
		Note note;
		Note result;
		Auditor auditor;
		Trip trip;

		super.authenticate("auditor1");

		auditor = (Auditor) this.auditorService.findAll().toArray()[0];
		trip = (Trip) this.tripService.findAll().toArray()[0];

		note = this.noteService.create(auditor, trip);
		note.setRemark("Este viaje está muy bien hecho.");

		result = this.noteService.save(note);

		super.authenticate(null);

		super.authenticate("manager1");

		Assert.isTrue(this.noteService.findByTripId(trip.getId()).contains(result));

		super.authenticate(null);
		
	}

	public Note copyNote(final Note note) {
		Note result;

		result = new Note();
		result.setId(note.getId());
		result.setVersion(note.getVersion());
		result.setMoment(note.getMoment());
		result.setRemark(note.getRemark());
		result.setManagerReply(note.getManagerReply());
		result.setMomentReply(note.getMomentReply());
		result.setAuditor(note.getAuditor());
		result.setTrip(note.getTrip());

		return result;
	}

}
