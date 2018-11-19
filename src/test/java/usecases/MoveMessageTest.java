package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Folder;
import domain.Message;
import services.FolderService;
import services.MessageService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MoveMessageTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private MessageService		messageService;
	
	@Autowired
	private FolderService		folderService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como USER envia un mensaje con prioridad LOW
	 * 		2. Un actor autenticado como USER envia un mensaje con prioridad NEUTRAL
	 * 		3. Un actor autenticado como USER envia un mensaje con prioridad HIGH
	 * 		4. Un actor autenticado como ADMIN difunde un mensaje
	 *
	 *
	 * Requisitos:
	 * 		
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"customer1", "message1", "folder3c1", null
			}, {
				"user2", "message3", "folder2u2", null
			}, {
				"administrator", "message2", "folder3ad1", null
			}, {
				"agent2", "message4", "folder3a2", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
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
	//@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"user1", "LOWWW", "Test subject", "Test body", "user1", "user2", ConstraintViolationException.class
				}, {
					"user1", "LOW", null, "Test body", "user1", "user2", ConstraintViolationException.class
				}, {
					"user1", "LOW", "Test subject", null, "user1", "user2", ConstraintViolationException.class
				}, {
					"user1", "LOW", "Test subject", "Test body", null, "user2", IllegalArgumentException.class
				}, {
					"user1", "LOW", "Test subject", "Test body", "user1", null, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
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
	protected void template(final String actorBean, final String messageBean, final String folderBean, final Class<?> expected) {
		Class<?> caught;
		Message message;
		Integer folderId, messageId;
		Folder folder;

		caught = null;
		try {
			
			Assert.notNull(messageBean);
			messageId = super.getEntityId(messageBean);
			Assert.notNull(messageId);
			
			message = this.messageService.findOne(messageId);
			Assert.notNull(message);
			
			Assert.notNull(folderBean);
			folderId = super.getEntityId(folderBean);
			Assert.notNull(folderId);
			
			folder = this.folderService.findOne(folderId);
			Assert.notNull(folder);
			
			// 1. Autenticar como usuario o admin
			if(actorBean.equals("administrator"))
				super.authenticate("admin");
			else
				super.authenticate(actorBean);
			/*if(!actorBean.equals("admin")) {
				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(user);
			}*/
			
			// 2. Mover mensaje
			this.messageService.moveMessage(message, message.getFolder(), folder);
			
			this.messageService.flush();
			
			// 3. Volver al listado de mensajes
			// Comprobar que el mensaje ha cambiado de carpeta
			Assert.isTrue(this.messageService.findOne(messageId).getFolder().equals(folder));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

