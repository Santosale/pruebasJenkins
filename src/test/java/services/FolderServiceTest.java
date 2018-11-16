
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
import domain.Explorer;
import domain.Folder;
import domain.Manager;
import domain.Message;
import domain.Ranger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class FolderServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private FolderService			folderService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private RangerService			rangerService;

	@Autowired
	private MessageService			messageService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo Folder y comprobamos que sus atributos tengan el valor
	 * esperado
	 */
	@Test
	public void testCreate() {
		Folder folder;
		Administrator saved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);

		Assert.isNull(folder.getName());
	}

	/*
	 * Creamos un Folder logeados como un administrador y lo guardamos en la
	 * base de datos
	 */
	@Test
	public void testSave1() {
		Folder folder;
		Administrator saved;
		Folder folderSaved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("Carpeta de fotos");
		folder.setSystem(false);
		folderSaved = this.folderService.save(folder);

		Assert.isTrue(this.folderService.findAll().contains(folderSaved));

		super.authenticate(null);
	}

	/*
	 * Creamos un Folder logeados como un explorer y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSave2() {
		Folder folder;
		Explorer saved;
		Folder folderSaved;

		super.authenticate("explorer1");

		saved = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("Carpeta de fotos");
		folder.setSystem(false);
		folderSaved = this.folderService.save(folder);

		Assert.isTrue(this.folderService.findAll().contains(folderSaved));

		super.authenticate(null);
	}

	/*
	 * Creamos un Folder logeados como un Administrador, lo guardamos en la base
	 * de datos y lo borramos
	 */
	@Test
	public void testDelete1() {
		Folder folder;
		Administrator saved;
		Folder folderSaved;

		super.authenticate("admin");

		saved = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("Carpeta de fotos");
		folder.setSystem(false);

		folderSaved = this.folderService.save(folder);

		this.folderService.delete(folderSaved);

		Assert.isTrue(!this.folderService.findAll().contains(folderSaved));

		super.authenticate(null);
	}

	/* Probamos que al borrar una carpeta se borren los mensajes de ella */
	@Test
	public void testDeleteMessage() {
		Folder folder;
		Collection<Message> messages;
		Manager manager;

		super.authenticate("manager1");

		manager = this.managerService.findByUserAccountId(LoginService.getPrincipal().getId());
		folder = this.folderService.findByActorIdAndFolderName(manager.getId(), "carpeta2");
		//folder = this.folderService.findOne(5127);

		messages = this.messageService.findByFolderId(folder.getId());

		Assert.isTrue(!messages.isEmpty());

		this.folderService.delete(folder);

		messages = this.messageService.findByFolderId(folder.getId());

		Assert.isTrue(messages.isEmpty());

		super.authenticate(null);
	}

	/*
	 * Creamos un Folder logeados como un Ranger, lo guardamos en la base de
	 * datos y lo borramos
	 */
	@Test
	public void testDelete2() {
		Folder folder;
		Ranger saved;
		Folder folderSaved;

		super.authenticate("ranger1");

		saved = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("Carpeta de fotos");
		folder.setSystem(false);

		folderSaved = this.folderService.save(folder);

		this.folderService.delete(folderSaved);

		Assert.isTrue(!this.folderService.findAll().contains(folderSaved));

		super.authenticate(null);
	}

	/*
	 * Creamos un Folder logeado como un Ranger, lo guardamos en la base de
	 * datos y lo borramos, esperando así que se borren todas las carpetas hijas
	 */
	@Test
	public void testDelete3() {
		Folder folder;
		Ranger saved;
		Folder folderSaved;
		Collection<Folder> collectionFolder;

		super.authenticate("ranger1");

		saved = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		folder = this.folderService.create(saved);
		folder.setName("Carpeta de fotos");
		folder.setSystem(false);

		folderSaved = this.folderService.save(folder);

		collectionFolder = folderSaved.getChildrenFolders();

		this.folderService.delete(folderSaved);

		for (final Folder folders : collectionFolder)
			Assert.isNull(folders);

		super.authenticate(null);
	}

	/*
	 * Se cogen todos los Folders y vemos si tiene el valor es el esperado.
	 */
	@Test
	public void testFindAll() {
		Integer result;

		result = this.folderService.findAll().size();

		Assert.isTrue(result == 72);
	}

	// Un Folder accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		Folder folderCollection;
		Folder folder;
		Collection<Folder> saved;
		Actor actor;

		super.authenticate("admin");

		actor = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());

		saved = this.folderService.findAll();

		folderCollection = this.folderService.create(actor);

		for (final Folder folders : saved) {
			folderCollection = folders;
			break;
		}

		folder = this.folderService.findOne(folderCollection.getId());

		Assert.isTrue(folderCollection.getId() == folder.getId());

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo findByActor devuelva las carpetas asoaciadas a
	 * un actor en concreto
	 */
	@Test
	public void testFindByActor1() {
		Collection<Folder> result, saved;
		Actor actor;

		super.authenticate("explorer1");

		actor = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		saved = this.folderService.findAll();

		result = this.folderService.findByActorId(actor.getId());

		Assert.isTrue(saved.containsAll(result));

		for (final Folder folders : result)
			Assert.isTrue(folders.getActor().equals(actor));

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo findByActor devuelva las carpetas asoaciadas a
	 * un actor en concreto
	 */
	@Test
	public void testFindByActor2() {
		Collection<Folder> result, saved;
		Actor actor;

		super.authenticate("ranger1");

		actor = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		saved = this.folderService.findAll();

		result = this.folderService.findByActorId(actor.getId());

		Assert.isTrue(saved.containsAll(result));

		for (final Folder folders : result)
			Assert.isTrue(folders.getActor().equals(actor));

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo createDefaultFolders devuelva el numero de
	 * carpetas por defecto esperadas (5)
	 */
	@Test
	public void testCreateDefaultFolders() {
		Explorer explorer, result;

		super.authenticate("admin");
		explorer = this.explorerService.create();
		explorer.setName("Antonio");
		explorer.setSurname("Sanchez");
		explorer.setAddress("Calle Monte");
		explorer.setEmail("an@hotmail.com");
		explorer.setPhone("632145879");

		explorer.getUserAccount().setUsername("antonio");
		explorer.getUserAccount().setPassword("antonio123");

		result = this.explorerService.save(explorer);

		Assert.isTrue(this.folderService.findByActorId(result.getId()).size() == 5);
		super.authenticate(null);

	}

	// @Test
	// public void testCreateDefaultFolders() {
	// Explorer explorer;
	//
	// super.authenticate("explorer1");
	//
	// explorer = this.explorerService.findByUserAccountId(LoginService
	// .getPrincipal().getId());
	//
	// this.folderService.createDefaultFolders(explorer);
	//
	// Assert.isTrue(this.folderService.findByActorId(explorer.getId()).size()
	// == 5);
	// }

}
