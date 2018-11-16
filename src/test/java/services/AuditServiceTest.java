
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
import domain.Audit;
import domain.Auditor;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class AuditServiceTest extends AbstractTest {

	//Service under test----------------

	@Autowired
	private AuditService	auditService;

	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private TripService		tripService;

	@Autowired
	private ActorService	actorService;


	//TEST------------

	//Creamos una nueva audit y comprobamos sus propiedades
	@Test
	public void testCreate() {
		Auditor auditor;
		Trip trip;
		List<Trip> trips;
		Audit created;

		super.authenticate("auditor2");
		trips = new ArrayList<Trip>();
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(auditor);

		trips.addAll(this.tripService.findAll());

		for (final Audit a : this.auditService.findByAuditorId(auditor.getId()))
			trips.remove(a.getTrip());

		trip = trips.get(0);
		created = this.auditService.create(auditor, trip);
		Assert.isNull(created.getMoment());
		Assert.isNull(created.getTitle());
		Assert.isNull(created.getDescription());
		Assert.isTrue(created.getAttachments().isEmpty());
		Assert.isTrue(created.getAuditor().equals(auditor));
		Assert.isTrue(created.getTrip().equals(trip));
		Assert.notNull(created.isDraft());

		super.authenticate(null);

	}

	//Se cogen todos los audits y vemos si uno cogido al azar está dentro.
	@Test
	public void testFindAll() {
		Collection<Audit> allAudits;
		Auditor saved;

		super.authenticate("auditor1");
		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		allAudits = this.auditService.findAll();

		Assert.isTrue(allAudits.containsAll(this.auditService.findByAuditorId(saved.getId())));
		super.authenticate(null);

	}

	@Test
	public void testfindOne() {
		Auditor saved;
		Audit audit;
		Audit auditFound;
		super.authenticate("auditor1");

		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		audit = null;
		for (final Audit au : this.auditService.findByAuditorId(saved.getId())) {
			audit = au;
			break;
		}
		auditFound = this.auditService.findOne(audit.getId());

		Assert.isTrue(auditFound.equals(audit));

		super.authenticate(null);

	}

	//Vamos a persistir un nuevo audit
	@Test
	public void testSave() {
		final Audit created;
		Auditor saved;
		Trip trip;
		Collection<Audit> audits;
		Collection<Trip> trips;
		Collection<Trip> allTrips;
		Date date;
		SimpleDateFormat format;
		String dateInString;
		Audit saved2;

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "03/01/2016";

		super.authenticate("auditor2");
		date = null;
		trip = null;
		trips = new ArrayList<Trip>();
		allTrips = new ArrayList<Trip>();
		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		audits = this.auditService.findByAuditorId(saved.getId());
		for (final Audit au : audits)
			trips.add(au.getTrip());

		allTrips.addAll(this.tripService.findAll());
		allTrips.removeAll(trips);
		for (final Trip t : allTrips) {
			trip = t;
			break;
		}

		try {
			date = format.parse(dateInString);
		} catch (final ParseException e) {

			e.printStackTrace();
		}

		created = this.auditService.create(saved, trip);
		created.setMoment(date);
		created.setTitle("titulo");
		created.setDescription("descripcion del audit");

		saved2 = this.auditService.save(created);

		Assert.isTrue(this.auditService.findAll().contains(saved2));

		super.authenticate(null);

	}
	//Probamos las spamWords
	@Test
	public void testSave2() {
		final Audit created;
		Auditor saved;
		Trip trip;
		Collection<Audit> audits;
		Collection<Trip> trips;
		Collection<Trip> allTrips;
		Date date;
		SimpleDateFormat format;
		String dateInString;
		Audit saved2;

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "03/01/2016";

		super.authenticate("auditor2");
		date = null;
		trip = null;
		trips = new ArrayList<Trip>();
		allTrips = new ArrayList<Trip>();
		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		audits = this.auditService.findByAuditorId(saved.getId());
		for (final Audit au : audits)
			trips.add(au.getTrip());

		allTrips.addAll(this.tripService.findAll());
		allTrips.removeAll(trips);
		for (final Trip t : allTrips) {
			trip = t;
			break;
		}

		try {
			date = format.parse(dateInString);
		} catch (final ParseException e) {

			e.printStackTrace();
		}

		created = this.auditService.create(saved, trip);
		created.setMoment(date);
		created.setTitle("sex");
		created.setDescription("descripcion del audit");

		saved2 = this.auditService.save(created);

		Assert.isTrue(this.auditService.findAll().contains(saved2));
		Assert.isTrue(this.auditService.findByAuditorId(saved2.getAuditor().getId()).contains(saved2));
		super.authenticate(null);
		super.authenticate("admin");
		this.auditorService.searchSuspicious();
		Assert.isTrue(this.actorService.findAllSuspicious().contains(saved2.getAuditor()));

		super.authenticate(null);

	}
	//Modificamos un audit
	@Test
	public void testSave3() {
		Auditor created;
		Audit saved;
		Audit audit;

		super.authenticate("auditor1");
		audit = null;
		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Audit au : this.auditService.findByAuditorId(created.getId())) {
			audit = au;
			break;
		}
		audit.setTitle("nuevo titulo");
		audit.setDraft(true);
		saved = this.auditService.save(audit);

		Assert.isTrue(this.auditService.findAll().contains(saved));
		Assert.isTrue(saved.getTitle().equals("nuevo titulo"));

		super.authenticate(null);

	}

	//Vamos a modificar un audit con un actor que no es él. Salta una excepción
	@Test
	public void testSave4() {
		Auditor created;
		Audit saved;
		Audit audit;

		super.authenticate("auditor1");
		audit = null;
		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Audit au : this.auditService.findByAuditorId(created.getId())) {
			audit = au;
			break;
		}
		audit.setTitle("nuevo titulo");
		audit.setDraft(true);

		super.authenticate(null);
		super.authenticate("auditor2");
		try {
			saved = this.auditService.save(audit);
			Assert.isNull(saved);
			Assert.isTrue(this.auditService.findAll().contains(saved));
			Assert.isTrue(saved.getTitle().equals("nuevo titulo"));
			super.authenticate(null);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}

	}
	//Vamos a ver que no se puede modificar si no está en drafMode
	@Test
	public void testSave5() {
		Auditor created;
		Audit saved;
		Audit audit;

		super.authenticate("auditor1");
		audit = null;
		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Audit au : this.auditService.findByAuditorId(created.getId())) {
			audit = au;
			break;
		}
		audit.setTitle("nuevo titulo");
		Assert.isTrue(audit.isDraft() == false);
		try {
			saved = this.auditService.save(audit);

			Assert.isTrue(this.auditService.findAll().contains(saved));
			Assert.isTrue(saved.getTitle().equals("nuevo titulo"));
			super.authenticate(null);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}

	}
	//Borramos un audit
	@Test
	public void testDelete() {
		Auditor auditor;
		Audit audit;

		super.authenticate("auditor2");
		audit = null;
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Audit au : this.auditService.findByAuditorId(auditor.getId())) {
			audit = au;
			break;
		}

		this.auditService.delete(audit);

		Assert.isTrue(!this.auditService.findAll().contains(auditor));

		super.authenticate(null);

	}
	//Intentamos borrar uno en finalMode
	@Test
	public void testDelete2() {
		Auditor auditor;
		Audit audit;

		super.authenticate("auditor1");
		audit = null;
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		for (final Audit au : this.auditService.findByAuditorId(auditor.getId())) {
			audit = au;
			break;
		}
		try {
			this.auditService.delete(audit);

			Assert.isTrue(!this.auditService.findAll().contains(auditor));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	@Test
	public void testFindbyAuditorId() {
		Collection<Audit> audits;
		Auditor auditor;

		super.authenticate("auditor1");
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		audits = this.auditService.findByAuditorId(auditor.getId());

		Assert.isTrue(audits.equals(this.auditService.findByAuditorId(auditor.getId())));

		super.authenticate(null);

	}

	@Test
	public void testFindbyTripId() {
		Collection<Audit> audits;
		Collection<Trip> trips;
		Trip trip;

		super.authenticate("manager1");
		trip = null;
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());
		for (final Trip t : trips) {
			trip = t;
			break;
		}

		audits = this.auditService.findByTripId(trip.getId());

		Assert.isTrue(audits.size() >= 1);

		super.authenticate(null);

	}

	@Test
	public void testFindByTripIdAndAuditorId() {
		Audit auditSaved;
		Auditor auditor;
		Audit audit;
		Trip trip;

		super.authenticate("auditor1");
		audit = null;
		trip = null;
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		for (final Audit au : this.auditService.findByAuditorId(auditor.getId())) {
			audit = au;
			trip = au.getTrip();
			break;

		}
		auditSaved = this.auditService.findByTripIdAndAuditorId(trip.getId(), audit.getAuditor().getId());

		Assert.isTrue(auditSaved.equals(audit));

		super.authenticate(null);

	}
	@Test
	public void minMaxAvgStandard() {
		Double[] numeros;
		super.authenticate("admin");

		numeros = new Double[4];
		numeros = this.auditService.minMaxAvgStandardDAuditsPerTrip();

		Assert.notNull(numeros);
		super.authenticate(null);
	}
}
