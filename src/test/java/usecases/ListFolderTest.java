
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.FolderService;
import utilities.AbstractTest;
import domain.Folder;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListFolderTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private FolderService	folderService;

	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un customer entra en una vista donde se llama al método findByUserAccountId
	 * 		2. Un customer entra en una vista donde se llama al método findChildrenFoldersByFolderId
	 * 
	 * Requisitos:
	 * 		
	 */
	@Test
	public void driverTest() {
		final Object testingData[][] = {
			{
				"customer1", null, "findByActorUserAccountId", 2, 2, 5, null
			}, {
				"customer1", "folder6c1", "findChildrenFoldersByFolderId", 1, 0, 5, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 		1. Autenticar usuario
	 * 		2. Listar
	 */
	protected void template(final String actorBean, final String folderBean, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<Folder> folders;
		Integer folderId;

		caught = null;
		folders = null;
		try {
			
			if(actorBean != null) {
				// 1. Autenticar usuario
				super.authenticate(actorBean);
			}

			// 2. Listar
			if (method.equals("findByActorUserAccountId")) {
				folders = this.folderService.findByActorUserAccountId(LoginService.getPrincipal().getId(), page, size).getContent();
			} else if (method.equals("findChildrenFoldersByFolderId")) {
				folderId = super.getEntityId(folderBean);
				Assert.notNull(folderId);
				folders = this.folderService.findChildrenFoldersByFolderId(folderId, page, size).getContent();
			}
			
			// Comprobación
			Assert.isTrue(folders.size() == tamano);
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
}
