
package services;

import java.util.Collection;

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
import domain.Folder;
import domain.Note;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class AuditorServiceTest extends AbstractTest {

	//Service under test----------------
	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private FolderService	folderService;

	@Autowired
	private NoteService		noteService;

	@Autowired
	private AuditService	auditService;

	@Autowired
	private ActorService	actorService;


	//TEST------------

	//Creamos un auditor y vemos sus propiedades si cumplen las respectivas restricciones
	@Test
	public void testCreateAuditor() {
		Auditor saved;
		super.authenticate("admin");
		saved = this.auditorService.create();

		//		Assert.isTrue(saved.getFolders().isEmpty());
		Assert.isNull(saved.getName());
		Assert.isNull(saved.getSurname());
		Assert.isNull(saved.getEmail());
		Assert.isNull(saved.getPhone());
		Assert.isNull(saved.getAddress());
		Assert.notNull(saved.getSuspicious());

		super.authenticate(null);

	}

	//Se cogen todos los auditors y vemos si uno cogido al azar está dentro.
	@Test
	public void testFindAll() {
		Collection<Auditor> allAuditors;
		Auditor saved;

		super.authenticate("auditor1");
		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		allAuditors = this.auditorService.findAll();

		Assert.isTrue(allAuditors.contains(saved));
		super.authenticate(null);

	}
	//Un auditor accede a él mismo
	@Test
	public void testfindOne() {
		Auditor saved;
		Auditor auditor;
		super.authenticate("auditor1");

		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		auditor = this.auditorService.findOne(saved.getId());

		Assert.isTrue(saved.getUserAccount().equals(auditor.getUserAccount()));

		super.authenticate(null);

	}

	//Vamos a persistir un nuevo auditor
	@Test
	public void testSave() {
		Auditor created;
		Auditor saved;

		super.authenticate("admin");

		created = this.auditorService.create();
		created.setName("Antonio");
		created.setSurname("Ruíz García");
		created.setEmail("antoniorgarci@gamil.com");
		created.getUserAccount().setUsername("auditorAntonio");
		created.getUserAccount().setPassword("auditorAntonio");

		saved = this.auditorService.save(created);

		for (final Folder f : this.folderService.findByActorId(saved.getId())) {
			Assert.notNull(f);
			Assert.isTrue(f.getActor().equals(saved));
		}

		Assert.isTrue(this.auditorService.findAll().contains(saved));
		//		Assert.isTrue(this.folderService.findAll().containsAll(saved.getFolders()));

		super.authenticate(null);

	}

	//Vamos a modificar un actor
	@Test
	public void testSave2() {
		Auditor created;
		Auditor copyCreated;
		Auditor saved;

		super.authenticate("auditor1");

		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		copyCreated = this.copyAuditor(created);
		copyCreated.setName("Carlos José");
		saved = this.auditorService.save(copyCreated);

		Assert.isTrue(this.auditorService.findAll().contains(saved));
		Assert.isTrue(saved.getName().equals("Carlos José"));

		super.authenticate(null);

	}

	//
	//	//Vamos a modificar un actor añadiendo una nueva carpeta del sistema. Salta una excepción
	@Test
	public void testSave3() {
		Folder folder;
		Auditor saved;

		super.authenticate("auditor1");

		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		try {

			folder = this.folderService.create(saved);
			folder.setName("carpeta del sistema");
			folder.setSystem(true);
			this.folderService.save(folder);
			super.authenticate(null);

		} catch (final IllegalArgumentException n) {
			super.authenticate(null);
		}

	}
	//
	//	//Vamos a añadir una nueva carpeta
	@Test
	public void testSave4() {
		Folder folder;
		Auditor saved;
		Folder folderSaved;

		super.authenticate("auditor1");

		saved = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("carpeta del sistema");
		folder.setSystem(false);
		folderSaved = this.folderService.save(folder);

		Assert.isTrue(this.folderService.findAll().contains(folderSaved));

		super.authenticate(null);

	}

	@Test
	public void testFindbyNoteId() {
		Auditor auditor;
		Note note;
		Auditor saved;

		super.authenticate("auditor1");
		note = null;
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		for (final Note n : this.noteService.findByAuditorId(auditor.getId())) {
			note = n;
			break;
		}
		saved = this.auditorService.findByNoteId(note.getId());

		Assert.isTrue(auditor.getUserAccount().equals(saved.getUserAccount()));

		super.authenticate(null);

	}
	@Test
	public void testFindSuspicious() {
		Auditor created;
		Auditor saved;
		Auditor copyCreated;

		super.authenticate("auditor1");

		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(created.getSuspicious() == false);
		copyCreated = this.copyAuditor(created);
		copyCreated.setName("sex");
		saved = this.auditorService.save(copyCreated);
		super.authenticate(null);
		super.authenticate("admin");

		Assert.isTrue(this.auditorService.findAll().contains(saved));
		this.auditorService.searchSuspicious();
		Assert.isTrue(this.actorService.findAllSuspicious().contains(saved));
		super.authenticate(null);
	}

	@Test
	public void testFindByUserAccountId() {
		Auditor auditor;

		super.authenticate("auditor1");

		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.auditorService.findAll().contains(auditor));

		super.authenticate(null);

	}
	@Test
	public void testFindbyTripId() {
		Auditor auditor;
		Collection<Auditor> saved;
		Trip trip;

		super.authenticate("auditor1");
		trip = null;
		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

		for (final Audit au : this.auditService.findByAuditorId(auditor.getId())) {
			trip = au.getTrip();
			break;
		}
		super.authenticate(null);
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.auditorService.findByTripId(trip.getId());

		Assert.isTrue(saved.contains(auditor));

		super.authenticate(null);

	}
	private Auditor copyAuditor(final Auditor auditor) {
		Auditor result;

		result = new Auditor();
		result.setAddress(auditor.getAddress());
		result.setEmail(auditor.getEmail());
		//		result.setFolders(auditor.getFolders());
		result.setId(auditor.getId());
		result.setName(auditor.getName());
		result.setPhone(auditor.getPhone());
		result.setSurname(auditor.getSurname());
		result.setSuspicious(auditor.getSuspicious());
		result.setUserAccount(auditor.getUserAccount());
		result.setVersion(auditor.getVersion());

		return result;
	}

}
