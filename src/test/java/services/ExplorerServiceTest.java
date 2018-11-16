
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
import domain.Application;
import domain.Contact;
import domain.Explorer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ExplorerServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private ExplorerService		explorerService;

	// Managed services
	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ContactService		contactService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo Explorer y comprobamos que sus atributos tengan el valor
	 * esperado
	 */
	@Test
	public void testCreate() {
		Explorer explorer;

		explorer = this.explorerService.create();
		Assert.isNull(explorer.getAddress());
		Assert.isNull(explorer.getEmail());
		Assert.isNull(explorer.getName());
		Assert.isNull(explorer.getSurname());
		Assert.isNull(explorer.getPhone());
	}

	/*
	 * Creamos un Explorer logeados como un administrador y lo guardamos en la
	 * base de datos
	 */
	@Test
	public void testSave1() {

		Explorer explorer;
		int oldCountExplorer;

		super.authenticate("admin");

		oldCountExplorer = this.explorerService.findAll().size();

		explorer = this.explorerService.create();
		explorer.setName("Antonio");
		explorer.setSurname("Sanchez");
		explorer.setAddress("Calle Monte");
		explorer.setEmail("an@hotmail.com");
		explorer.setPhone("632145879");

		explorer.getUserAccount().setUsername("antonio");
		explorer.getUserAccount().setPassword("antonio123");

		this.explorerService.save(explorer);

		Assert.isTrue(this.explorerService.findAll().size() == oldCountExplorer + 1);
		// Assert.isTrue(result.getFolders().size() == 5);

		super.authenticate(null);
	}

	/*
	 * Creamos un Explorer sin loguearse y lo guardamos en la base de datos
	 */
	@Test
	public void testSave2() {

		Explorer explorer;
		int oldCountExplorer;

		oldCountExplorer = this.explorerService.findAll().size();

		explorer = this.explorerService.create();
		explorer.setName("Antonio");
		explorer.setSurname("Sanchez");
		explorer.setAddress("Calle Monte");
		explorer.setEmail("an@hotmail.com");
		explorer.setPhone("632145879");

		explorer.getUserAccount().setUsername("antonio");
		explorer.getUserAccount().setPassword("antonio123");

		this.explorerService.save(explorer);

		Assert.isTrue(this.explorerService.findAll().size() == oldCountExplorer + 1);

	}

	/* Se cogen todos los explorers y el tamaño es el esperado. */
	@Test
	public void testFindAll() {
		Integer allExplorers;

		allExplorers = this.explorerService.findAll().size();

		Assert.isTrue(allExplorers == 4);

		super.authenticate(null);

	}

	/* Un explorer accede a él mismo a traves de findOne */
	@Test
	public void testFindOne() {
		Explorer saved;
		Explorer explorer;

		super.authenticate("explorer1");
		saved = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		explorer = this.explorerService.findOne(saved.getId());

		Assert.isTrue(saved.getUserAccount().equals(explorer.getUserAccount()));

		super.authenticate(null);

	}

	/*
	 * Comprobamos la funcionalidad de searchSuspicious, debe poner como
	 * sospechoso al explorer cuando se realiza un cambio con una palabra spam
	 */
	@Test
	public void testSearchSuspicious() {
		Collection<Contact> contacts;
		Contact savedContact;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		contacts = this.contactService.findByExplorerId(explorer.getId());

		savedContact = new Contact();

		for (final Contact contact : contacts) {
			savedContact = contact;
			break;
		}

		savedContact.setName("sex");

		this.contactService.save(savedContact);

		super.authenticate(null);

		super.authenticate("ADMIN");

		Assert.isTrue(explorer.getSuspicious() == false);

		this.explorerService.searchSuspicious();

		Assert.isTrue(explorer.getSuspicious() == true);

		super.authenticate(null);

	}

	/*
	 * Probamos el metodo findByUserAccountId que debe devolver el Explorer que
	 * se corresponga con la cuenta de usuario
	 */
	@Test
	public void testFindByUserAccountId() {
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.explorerService.findAll().contains(explorer));

		super.authenticate(null);

	}

	/*
	 * Probamos el metodo findByApplicationId que debe devolver el Explorer que
	 * se corresponga con la aplicacion
	 */
	@Test
	public void findByApplicationId() {
		Explorer explorer;
		Collection<Application> applications;
		Application application;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		applications = this.applicationService.findAll();

		application = new Application();

		for (final Application app : applications)
			if (app.getApplicant() == explorer) {
				application = app;
				break;
			}

		Assert.isTrue(this.explorerService.findByApplicationId(application.getId()) == explorer);

		super.authenticate(null);

	}
}
