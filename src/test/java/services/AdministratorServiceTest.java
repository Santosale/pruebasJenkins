
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
import domain.Administrator;
import domain.Folder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class AdministratorServiceTest extends AbstractTest {

	//Service under test----------------
	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private FolderService			folderService;

	@Autowired
	private ActorService			actorService;


	//Test -------------------------------

	//Se cogen todos los administradores y vemos si uno cogido al azar está dentro.
	@Test
	public void testFindAll() {
		Collection<Administrator> allAdministrators;
		Administrator saved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		allAdministrators = this.administratorService.findAll();

		Assert.isTrue(allAdministrators.contains(saved));

		super.authenticate(null);

	}
	//Un administrador accede a él mismo
	@Test
	public void testfindOne() {
		Administrator saved;
		Administrator administrator;

		super.authenticate("admin");
		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		administrator = this.administratorService.findOne(saved.getId());

		Assert.isTrue(saved.getUserAccount().equals(administrator.getUserAccount()));

		super.authenticate(null);

	}

	//Vamos a modificar un actor
	@Test
	public void testSave() {
		Administrator created;
		Administrator saved;
		Administrator copyCreated;

		super.authenticate("admin");

		created = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		copyCreated = this.copyAdministrator(created);
		copyCreated.setName("Carlos José");
		saved = this.administratorService.save(copyCreated);

		Assert.isTrue(this.administratorService.findAll().contains(saved));
		Assert.isTrue(saved.getName().equals("Carlos José"));

		super.authenticate(null);

	}

	//Vamos a modificar un administrador con un actor que no es él. Salta una excepción
	@Test
	public void testSave2() {
		Administrator created;
		Administrator copyCreated;

		super.authenticate("admin");

		created = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		copyCreated = this.copyAdministrator(created);
		copyCreated.setName("Carlos José");
		super.authenticate(null);
		super.authenticate("manager1");
		try {
			Assert.isNull(this.administratorService.save(copyCreated));
			super.authenticate(null);
		} catch (final IllegalArgumentException n) {
			super.authenticate(null);
		}

	}

	//Vamos a modificar un actor añadiendo una nueva carpeta del sistema. Salta una excepción
	@Test
	public void testSave3() {
		Folder folder;
		Administrator saved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

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

	//Cogemos un administrador, lo ponemos como sospechoso cambiando su nombre, y posteriormente vemos si existe en la lista de todos los sospechosos
	@Test
	public void testFindSuspicious() {
		Administrator created;
		Administrator saved;
		Administrator copyCreated;

		super.authenticate("admin");

		created = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(created.getSuspicious() == false);
		copyCreated = this.copyAdministrator(created);
		copyCreated.setName("sex");
		saved = this.administratorService.save(copyCreated);

		this.administratorService.searchSuspicious();
		Assert.isTrue(this.actorService.findAllSuspicious().contains(saved));
	}

	//intentamos que alguien que no sea administrador acceda al método findSuspicious. Salta una excepción
	@Test
	public void testFindSuspicious2() {
		super.authenticate("manager1");
		try {
			this.administratorService.searchSuspicious();
		} catch (final IllegalArgumentException n) {
			super.authenticate(null);
		}

	}

	@Test
	public void testFindByUserAccountId() {
		Administrator administrator;

		super.authenticate("admin");

		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.administratorService.findAll().contains(administrator));

		super.authenticate(null);

	}
	private Administrator copyAdministrator(final Administrator administrator) {
		Administrator result;

		result = new Administrator();
		result.setAddress(administrator.getAddress());
		result.setEmail(administrator.getEmail());
		//		result.setFolders(administrator.getFolders());
		result.setId(administrator.getId());
		result.setName(administrator.getName());
		result.setPhone(administrator.getPhone());
		result.setSurname(administrator.getSurname());
		result.setSuspicious(administrator.getSuspicious());
		result.setUserAccount(administrator.getUserAccount());
		result.setVersion(administrator.getVersion());

		return result;
	}

}
