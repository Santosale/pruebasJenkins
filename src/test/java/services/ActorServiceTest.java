
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
import domain.Actor;
import domain.Administrator;
import domain.Auditor;
import domain.Folder;
import domain.Ranger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ActorServiceTest extends AbstractTest {

	//Service under test----------------
	@Autowired
	private ActorService			actorService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private FolderService			folderService;

	@Autowired
	private RangerService			rangerService;


	//Test -------------------------------
	//Se cogen todos los administradores y vemos si uno cogido al azar está dentro.
	@Test
	public void testFindAll() {
		final Collection<Actor> allActors;
		Administrator saved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		allActors = this.actorService.findAll();

		Assert.isTrue(allActors.contains(saved));

		super.authenticate(null);

	}

	@Test
	public void testfindOne() {
		Administrator saved;
		Actor actor;

		super.authenticate("admin");
		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		actor = this.actorService.findOne(saved.getId());

		Assert.isTrue(saved.getUserAccount().equals(actor.getUserAccount()));

		super.authenticate(null);

	}
	@Test
	public void testSave() {
		Auditor created;
		Actor saved;

		super.authenticate("admin");

		created = this.auditorService.create();
		created.setName("Antonio");
		created.setSurname("Ruíz García");
		created.setEmail("antoniorgarci@gamil.com");
		created.getUserAccount().setUsername("auditorAntonio");
		created.getUserAccount().setPassword("auditorAntonio");

		saved = this.actorService.save(created);

		for (final Folder f : this.folderService.findByActorId(saved.getId())) {
			Assert.notNull(f);
			Assert.isTrue(f.getActor().equals(saved));
		}

		Assert.isTrue(this.actorService.findAll().contains(saved));
		//		Assert.isTrue(this.folderService.findAll().containsAll(saved.getFolders()));

		super.authenticate(null);

	}
	@Test
	public void testFindAllSuspicious() {
		Collection<Actor> suspicious;
		super.authenticate("admin");
		suspicious = this.actorService.findAllSuspicious();

		Assert.isTrue(suspicious.size() == 3);
		super.authenticate(null);

	}

	@Test
	public void testBanUnBan() {
		Ranger ranger;

		super.authenticate("ranger1");
		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());
		super.authenticate(null);
		super.authenticate("admin");

		this.actorService.banUnBanActors(ranger, true);

		Assert.isTrue(this.rangerService.findByUserAccountId(ranger.getUserAccount().getId()).getUserAccount().isEnabled() == false);
		super.authenticate(null);

	}
	@Test
	public void testSearchAllSuspicious() {
		Auditor created;
		Auditor saved;
		Auditor copyCreated;

		super.authenticate("auditor1");

		created = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(created.getSuspicious() == false);
		copyCreated = this.auditorService.findOne(created.getId());
		copyCreated.setName("sex");
		saved = this.auditorService.save(copyCreated);
		super.authenticate(null);
		super.authenticate("admin");

		Assert.isTrue(this.auditorService.findAll().contains(saved));
		this.actorService.searchAllSuspicious();
		Assert.isTrue(this.actorService.findAllSuspicious().contains(saved));
		super.authenticate(null);
	}
}
