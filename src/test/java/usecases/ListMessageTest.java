
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.MessageService;
import utilities.AbstractTest;
import domain.Message;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListMessageTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private MessageService	messageService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como USER entra a una vista que llama al método findByFolderIdPaginated
	 * 		2. Un actor autenticado como USER entra a una vista que llama al método findByActorUserAccountId
	 * Requisitos:
	 * 
	 */
	@Test
	public void driverTest() {
		final Object testingData[][] = {
			{
				"user1", "findByFolderIdPaginated", "folder1u1", 1, 0, 0, null
			}, {
				"user2", "findByActorUserAccountId", "userAccountUser2", 1, 0, 2, null
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
	protected void template(final String userBean, final String method, final String entityBean, final int page, final int size, final int tamano, final Class<?> expected) {
		Class<?> caught;
		Collection<Message> messages;
		Integer userId, entityId;

		caught = null;
		messages = null;
		userId = null;
		try {
			
			if(userBean != null) {
				// 1. Autenticar usuario
				super.authenticate(userBean);
				if(userBean.equals("admin"))
					userId = super.getEntityId("administrator");
				else
					userId = super.getEntityId(userBean);
			}
			
			Assert.notNull(userId);
			
			entityId = super.getEntityId(entityBean);
			Assert.notNull(entityId);

			// 2. Listar
			if (method.equals("findByFolderIdPaginated")) {
				messages = this.messageService.findByFolderIdPaginated(entityId, page, size).getContent();
			} else if (method.equals("findByActorUserAccountId")) {
				messages = this.messageService.findByActorUserAccountId(entityId, page, size).getContent();
			}
			
			// Comprobación
			Assert.isTrue(messages.size() == tamano);
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
}
