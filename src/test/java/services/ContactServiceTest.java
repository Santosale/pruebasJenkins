
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
import domain.Contact;
import domain.Explorer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ContactServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private ContactService	contactService;

	@Autowired
	private ExplorerService	explorerService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo Contact y comprobamos que sus atributos tengan el valor
	 * esperado
	 */
	@Test
	public void testCreate() {
		final Contact contact;
		Explorer explorer;

		super.authenticate("explorer1");
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contact = this.contactService.create(explorer);

		Assert.notNull(contact);
		Assert.notNull(contact.getExplorer());
		Assert.isNull(contact.getName());
		Assert.isNull(contact.getEmail());
		Assert.isNull(contact.getPhone());
	}

	/*
	 * Creamos un Contact desde un Explorer y lo guardamos en la base de datos
	 */
	@Test
	public void testSave1() {
		Explorer explorer;

		super.authenticate("explorer1");

		Contact contact;
		int oldCountContact;

		oldCountContact = this.contactService.findAll().size();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contact = this.contactService.create(explorer);

		contact.setName("Antonio");
		contact.setEmail("antonio@gmail.com");
		contact.setPhone("632145987");

		this.contactService.save(contact);

		Assert.isTrue(this.contactService.findAll().size() == oldCountContact + 1);

		super.authenticate(null);

	}

	//INtentamos guardar un contacto a través de otro explorer
	@Test
	public void testSave2() {
		Explorer explorer;

		super.authenticate("explorer1");

		Contact contact;
		int oldCountContact;

		oldCountContact = this.contactService.findAll().size();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contact = this.contactService.create(explorer);

		contact.setName("Antonio");
		contact.setEmail("antonio@gmail.com");
		contact.setPhone("632145987");
		super.authenticate(null);
		super.authenticate("explorer2");
		try {
			this.contactService.save(contact);
			Assert.isTrue(this.contactService.findAll().size() == oldCountContact + 1);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

		super.authenticate(null);

	}

	/*
	 * Creamos un Contact desde un Explorer, lo guardamos en la base de datos y
	 * la borramos
	 */
	@Test
	public void testDelete1() {
		Contact result;
		Explorer explorer;

		super.authenticate("explorer1");

		Contact contact;
		int oldCountContact;

		oldCountContact = this.contactService.findAll().size();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contact = this.contactService.create(explorer);

		contact.setName("Antonio");
		contact.setEmail("antonio@gmail.com");
		contact.setPhone("632145987");

		result = this.contactService.save(contact);
		this.contactService.delete(result);

		Assert.isTrue(this.contactService.findAll().size() == oldCountContact);

		super.authenticate(null);
	}
	//Lo borramos con otro explorer
	@Test
	public void testDelete2() {
		Contact result;
		Explorer explorer;

		super.authenticate("explorer1");

		Contact contact;
		int oldCountContact;

		oldCountContact = this.contactService.findAll().size();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contact = this.contactService.create(explorer);

		contact.setName("Antonio");
		contact.setEmail("antonio@gmail.com");
		contact.setPhone("632145987");

		result = this.contactService.save(contact);
		super.authenticate(null);
		super.authenticate("explorer2");
		try {
			this.contactService.delete(result);

			Assert.isTrue(this.contactService.findAll().size() == oldCountContact);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}
		super.authenticate(null);
	}

	// Se cogen todas los contacts y vemos si tiene el valor es el esperado.
	@Test
	public void testFindAll() {
		Integer alContacts;

		alContacts = this.contactService.findAll().size();

		Assert.isTrue(alContacts == 2);
	}

	// Un contact accede a el misma a traves de findOne
	@Test
	public void testFindOne() {
		Contact contactCollection;
		Contact contact;
		Collection<Contact> saved;
		Explorer explorer;

		super.authenticate("explorer1");

		saved = this.contactService.findAll();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		contactCollection = this.contactService.create(explorer);

		for (final Contact contacts : saved) {
			contactCollection = contacts;
			break;
		}

		contact = this.contactService.findOne(contactCollection.getId());

		Assert.isTrue(contactCollection.getId() == contact.getId());

		super.authenticate(null);

	}

	//Comprobamos que los contactos devueltos por el metodo findByExplorerId sean correctos
	@Test
	public void testFindByExplorerId() {
		Explorer explorer;
		Collection<Contact> contact;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		contact = this.contactService.findByExplorerId(explorer.getId());

		Assert.isTrue(this.contactService.findByExplorerId(explorer.getId()).containsAll(contact));

		super.authenticate(null);
	}

	//Comprobamos que al borrar un contacto se borre tambien de Explorer
	@Test
	public void TestFindDeleteExplorer() {
		Explorer explorer;
		Collection<Contact> contacts;
		Contact contacto;

		super.authenticate("explorer2");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		contacts = this.contactService.findByExplorerId(explorer.getId());

		contacto = this.contactService.create(explorer);

		for (final Contact contact : contacts) {
			contacto = contact;
			this.contactService.delete(contact);
			break;
		}

		Assert.isTrue(!this.contactService.findByExplorerId(explorer.getId()).contains(contacto));

		super.authenticate(null);
	}

}
