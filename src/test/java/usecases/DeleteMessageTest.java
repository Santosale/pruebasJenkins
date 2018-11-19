package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Message;
import security.LoginService;
import services.MessageService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteMessageTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private MessageService		messageService;

	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como CUSTOMER trata de borrar un mensaje por primera vez y lo manda a la trash box.
	 * 		2. Un actor autenticado como CUSTOMER borra desde la trashbox un mensaje y se borra definitivamente del sistema.
	 * Requisitos:
	 * 		
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"customer1", "message1", null, null
			}, {
				"customer1", "message1", "delete", null
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
	 * 		1. Un usuario intenta borrar un mensaje que no es suyo.
	 * 
	 * Requisitos:
	 * 		
	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
				{
					"user1", "message1", null, IllegalArgumentException.class
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
	 * Borrar un mensaje
	 * Pasos:
	 * 		1. Autenticar actor
	 * 		2. Listar mensaje
	 * 		2. Eliminar mensaje
	 * 		3. Volver al listado de mensajes
	 */
	protected void template(final String actorBean, final String messageBean, final String action, final Class<?> expected) {
		Class<?> caught;
		Integer messageId, pageResult;
		Page<Message> messages;
		Message message, messageAux;

		caught = null;
		try {
			
			// 1. Autenticar actor
			super.authenticate(actorBean);

			// 2. Listar messages
			messageId = super.getEntityId(messageBean);
			Assert.notNull(messageId);
			message = this.messageService.findOne(messageId);
			Assert.notNull(message);
			
			messages = this.messageService.findByActorUserAccountId(LoginService.getPrincipal().getId(), this.getPage(message), 5);
			Assert.notNull(messages);
			
			// 3. Eliminar message
			this.messageService.delete(message);
			
			this.messageService.flush();

			messageAux = this.messageService.findOne(messageId);
			Assert.notNull(messageAux);
			Assert.isTrue(messageAux.getFolder().getName().equals("trash box"));
			
			if(action != null && action.equals("delete")){
				this.messageService.delete(message);
				this.messageService.flush();
				// 4. Volver al listado de messages
				pageResult = this.getPage(message);
				Assert.isNull(pageResult);
			}
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Integer getPage(final Message message) {
		Integer result;
		Page<Message> pageMessage, pageMessageAux;

		pageMessage = this.messageService.findByActorUserAccountId(message.getSender().getUserAccount().getId(), 0, 5);

		result = null;
		for (int i = 0; i <= pageMessage.getTotalPages(); i++) {
			pageMessageAux = this.messageService.findByActorUserAccountId(message.getSender().getUserAccount().getId(), i, 5);
			if (pageMessageAux.getContent().contains(message)) {
				result = i;
				break;
			}
		}

		return result;
	}


}

