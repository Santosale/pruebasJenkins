
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
import domain.Folder;
import domain.Manager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ManagerServiceTest extends AbstractTest {

	// Service under test
	@Autowired
	private ManagerService	managerService;

	// Supporting services
	@Autowired
	private FolderService	folderService;


	@Test
	public void testCreate() {

		super.authenticate("admin");

		Manager manager;

		manager = this.managerService.create();

		Assert.notNull(manager);

		super.authenticate(null);

	}

	@Test
	public void testFindAll() {

		Collection<Manager> manager;

		manager = this.managerService.findAll();

		Assert.notEmpty(manager);

	}

	@Test
	public void testFindOne1() {

		super.authenticate("admin");

		Manager result;
		Manager savedManager;
		Manager manager;

		manager = this.managerService.create();
		manager.setName("Sergio");
		manager.setSurname("Clebal");
		manager.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		manager.setEmail("sergioclebal@gmail.com");
		manager.setPhone("+34618936001");

		manager.getUserAccount().setUsername("sergioclebal");
		manager.getUserAccount().setPassword("sergioclebal");

		savedManager = this.managerService.save(manager);

		result = this.managerService.findOne(savedManager.getId());

		Assert.notNull(result);

		super.authenticate(null);
	}

	@Test
	public void testFindOne2() {

		Manager manager;

		try {
			manager = this.managerService.findOne(0);
			Assert.isNull(manager);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

	}

	@Test
	public void testSave1() {
		Manager result;
		Manager manager;
		int oldCountManager;

		super.authenticate("admin");

		oldCountManager = this.managerService.findAll().size();

		manager = this.managerService.create();
		manager.setName("Sergio");
		manager.setSurname("Clebal");
		manager.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		manager.setEmail("sergioclebal@gmail.com");
		manager.setPhone("+34618936001");

		manager.getUserAccount().setUsername("sergioclebal");
		manager.getUserAccount().setPassword("sergioclebal");

		result = this.managerService.save(manager);

		Assert.isTrue(this.managerService.findAll().size() == oldCountManager + 1);

		for (final Folder f : this.folderService.findByActorId(result.getId())) {
			Assert.notNull(f);
			Assert.isTrue(f.getActor().equals(result));
		}
		//        Assert.isTrue(this.folderService.findAll().containsAll(result.getFolders()));

		super.authenticate(null);

	}

	@Test
	public void testSave2() {
		Manager manager;
		Manager result;

		super.authenticate(null);

		manager = this.managerService.create();
		manager.setName("Sergio");
		manager.setSurname("Clebal");
		manager.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		manager.setEmail("sergioclebal@gmail.com");
		manager.setPhone("+34618936001");

		manager.getUserAccount().setUsername("sergioclebal");
		manager.getUserAccount().setPassword("sergioclebal");

		try {
			result = this.managerService.save(manager);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate("manager1");

		try {
			result = this.managerService.save(manager);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

	}

	@Test
	public void testSearchSuspicious() {
		Manager result;
		Manager manager;

		super.authenticate("admin");

		manager = this.managerService.create();
		manager.setName("Sex");
		manager.setSurname("Viagra");
		manager.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		manager.setEmail("sergioclebal@gmail.com");
		manager.setPhone("+34618936001");

		manager.getUserAccount().setUsername("sergioclebal");
		manager.getUserAccount().setPassword("sergioclebal");

		result = this.managerService.save(manager);

		this.managerService.searchSuspicious();

		Assert.isTrue(this.managerService.findOne(result.getId()).getSuspicious());

		super.authenticate(null);

	}

	@Test
	public void testRatioSuspicious() {
		Double result;

		super.authenticate("admin");

		result = this.managerService.ratioSuspicious();

		Assert.notNull(result);

		super.authenticate(null);
	}

	@Test
	public void testFindByUserAccountId() {
		Manager manager;
		final String logedUsername = "manager1";

		super.authenticate(logedUsername);

		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(manager.getUserAccount().getUsername().equals(logedUsername));

		super.authenticate(null);
	}

}
