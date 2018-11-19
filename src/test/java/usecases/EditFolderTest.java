package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Folder;
import security.LoginService;
import services.ActorService;
import services.FolderService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditFolderTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private FolderService		folderService;
	
	@Autowired
	private ActorService		actorService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como CUSTOMER editar el nombre de una de sus carpetas
	 *
	 * Requisitos:
	 * 		
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"customer1", "folder6c1", "test folder", null, null, null
			}, {
				"customer1", "folder7c1", "test folder2", null, null, null
			}, {
				"customer1", "folder7c1", "test folder", "folder5c1", null, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un customer trata de editar una carpeta quitando el campo name
	 * 		2. Un customer trata de editar una carpeta que no es suya
	 * 		3. Un customer trata de editar una carpeta del sistema
	 * 		5. Un customer trata de editar una carpeta editando el usuario
	 * 
	 * Requisitos:
	 * 		
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"customer1", "folder6c1", null, null, null, IllegalArgumentException.class
				}, {
					"customer2", "folder6c1", "adsf", null, null, IllegalArgumentException.class
				}, {
					"customer1", "folder5c1", "test folder", null, null, IllegalArgumentException.class
				}, {
					"customer1", "folder6c1", "test folder", null, "customer2", IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Editar carpeta
	 * Pasos:
	 * 		1. Autenticar actor
	 * 		2. Listar carpetas
	 * 		3. Editar carpeta
	 * 		3. Volver al listado de mensajes
	 */
	protected void template(final String actorBean, final String folderBean, final String name, final String fatherFolderBean, final String actorHackingBean, final Class<?> expected) {
		Class<?> caught;
		Folder folder, fatherFolder, savedFolder, persistedFolder, folderCopy;
		Page<Folder> folders;
		Actor actor;
		Integer folderId, fatherFolderId, folderPage, actorId;

		caught = null;
		fatherFolder = null;
		actor = null;
		try {
			
			if(fatherFolderBean != null) {
				fatherFolderId = super.getEntityId(fatherFolderBean);
				Assert.notNull(fatherFolderId);
				fatherFolder = this.folderService.findOne(fatherFolderId);
				Assert.notNull(fatherFolder);
			}
			
			if(actorHackingBean != null) {
				actorId = super.getEntityId(actorHackingBean);
				Assert.notNull(actorId);
				actor = this.actorService.findOne(actorId);
				Assert.notNull(actor);
			}


			folderId = super.getEntityId(folderBean);
			Assert.notNull(folderId);
			
			// 1. Autenticar como usuario
			super.authenticate(actorBean);
//			if(!actorBean.equals("admin")) {
//				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
//				Assert.notNull(user);
//			}
					
			// 2. Listar carpetas
			folders = this.folderService.findByActorUserAccountId(LoginService.getPrincipal().getId(), this.getPage(null), 5);
			Assert.notNull(folders);
						
			// 3. Editar carpeta
			folder = this.folderService.findOneToEdit(folderId);
			folderCopy = this.copyFolder(folder);
			if(name == null) Assert.notNull(name);
			else folderCopy.setName(name);
			if(fatherFolderBean != null) {
				folderCopy.setFatherFolder(fatherFolder);
			}
			if(actorHackingBean != null) {
				folderCopy.setActor(actor);
			}
								
			savedFolder = this.folderService.save(folderCopy);
			
			this.folderService.flush();
						
			// 3. Volver al listado de mensajes
			folderPage = this.getPage(savedFolder);
			Assert.notNull(folderPage);
			
			persistedFolder = this.folderService.findOne(savedFolder.getId());
			Assert.isTrue(persistedFolder.getName().equals(name));
			if(actorHackingBean != null) Assert.isTrue(persistedFolder.getActor().equals(folder.getActor()));
			if(fatherFolderBean != null) Assert.isTrue(persistedFolder.getFatherFolder().equals(folder.getFatherFolder()));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Folder copyFolder(final Folder folder) {
		Folder result;
		
		result = new Folder();
		result.setId(folder.getId());
		result.setVersion(folder.getVersion());
		result.setActor(folder.getActor());
		result.setChildrenFolders(folder.getChildrenFolders());
		result.setFatherFolder(folder.getFatherFolder());
		result.setName(folder.getName());
		result.setSystem(folder.getSystem());
		
		return result;
	}
	
	private Integer getPage(final Folder folder) {
		Integer result;
		Page<Folder> pageFolder, pageFolderAux;
		
		if(folder == null)
			result = 0;
		else {
			pageFolder = this.folderService.findByActorUserAccountId(LoginService.getPrincipal().getId(), 0, 5);
			Assert.notNull(pageFolder);
			result = null;
			for (int i = 0; i <= pageFolder.getTotalPages(); i++) {
				pageFolderAux = this.folderService.findByActorUserAccountId(LoginService.getPrincipal().getId(), i, 5);
				if (pageFolderAux.getContent().contains(folder)) {
					result = i;
					break;
				}
			}
		}

		return result;
	}

}

