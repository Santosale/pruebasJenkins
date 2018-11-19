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
import services.FolderService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveFolderTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private FolderService		folderService;

	@Autowired
	private UserService			userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como USER crea una carpeta
	 * 		2. Un actor autenticado como USER crea una carpeta hija de la carpeta IN BOX
	 *
	 * Requisitos:
	 * 		
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"user1", "test folder", null, false, null
			}, {
				"user1", "test folder", "folder1u1", false, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Class<?>) testingData[i][4]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. 
	 * 
	 * Requisitos:
	 * 		
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"user1", null, null, false, IllegalArgumentException.class
				}, {
					"user1", "in box", null, false, IllegalArgumentException.class
				}, {
					"user2", "test folder", "folder1u1", false, IllegalArgumentException.class
				}, {
					"user1", "test folder", "folder1u1", true, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Enviar/Difundir mensaje
	 * Pasos:
	 * 		1. Autenticar como usuario o admin
	 * 		2. Enviar/difundir mensaje
	 * 		3. Volver al listado de mensajes
	 */
	protected void template(final String actorBean, final String name, final String fatherFolderBean, final Boolean duplicate, final Class<?> expected) {
		Class<?> caught;
		Folder folder, fatherFolder, savedFolder;
		Page<Folder> folders;
		Actor user;
		Integer fatherFolderId, folderPage;

		caught = null;
		user = null;
		fatherFolder = null;
		try {
			
			if(fatherFolderBean != null) {
				fatherFolderId = super.getEntityId(fatherFolderBean);
				Assert.notNull(fatherFolderId);
				fatherFolder = this.folderService.findOne(fatherFolderId);
			}

			// 1. Autenticar como usuario
			super.authenticate(actorBean);
			if(!actorBean.equals("admin")) {
				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(user);
			}
			
			// 2. Listar carpetas
			folders = this.folderService.findByActorUserAccountId(LoginService.getPrincipal().getId(), this.getPage(null), 5);
			Assert.notNull(folders);
			
			// 3. Crear carpeta
			folder = this.folderService.create(user);
			if(name == null) Assert.notNull(name);
			else folder.setName(name);
			if(fatherFolderBean != null) {
				folder.setFatherFolder(fatherFolder);
			}
					
			savedFolder = this.folderService.save(folder);
			
			this.folderService.flush();
			
			if(duplicate) this.folderService.save(folder);
			
			this.folderService.flush();
			
			// 3. Volver al listado de mensajes
			folderPage = this.getPage(savedFolder);
			Assert.notNull(folderPage);
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
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

