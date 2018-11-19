package usecases;

import java.util.Collection;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Administrator;
import domain.Message;
import security.LoginService;
import services.ActorService;
import services.AdministratorService;
import services.MessageService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveMessageTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private MessageService		messageService;

	@Autowired
	private UserService			userService;
	
	@Autowired
	private ActorService		actorService;
	
	@Autowired
	private AdministratorService		administratorService;
	
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
				"user1", "LOW", "Test subject", "Test body", "user1", "user2", null
			}, {
				"user2", "NEUTRAL", "Test subject", "Test body", "user2", "user3", null
			}, {
				"user2", "HIGH", "Test subject", "Test body", "user2", "user3", null
			}, {
				"admin", "HIGH", "Test subject", "Test body", null, null, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
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
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
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
	protected void template(final String actorBean, final String priority, final String subject, final String body, final String senderBean, final String recipientBean, final Class<?> expected) {
		Class<?> caught;
		Message message;
		Map<String, Message> saved;
		Collection<Message> savedCollection;
		Actor user, sender, recipient;
		Integer senderId, recipientId;
		Integer numberMessageByRecipient, numberMessageBySender;
		Integer numberMessageByRecipientNew, numberMessageBySenderNew;
		Administrator admin;
		
		caught = null;
		recipientId = null;
		numberMessageByRecipient = null;
		numberMessageBySender = null;
		recipient = null;
		senderId = null;
		sender = null;
		admin = null;
		try {
			
			if(actorBean.equals("admin")) {
				senderId = this.getEntityId("administrator");
				sender = this.actorService.findOne(senderId);
				numberMessageBySender = this.messageService.findByActorId(senderId).size();
			} else {
				
				if(senderBean != null) {
					senderId = this.getEntityId(senderBean);
					sender = this.actorService.findOne(senderId);
					numberMessageBySender = this.messageService.findByActorId(senderId).size();
				} else {
					Assert.notNull(senderBean);

				}
				
				if(recipientBean != null) {
					recipientId = this.getEntityId(recipientBean);
					recipient = this.actorService.findOne(recipientId);
					numberMessageByRecipient = this.messageService.findByActorId(recipientId).size();
				} else {
					Assert.notNull(recipientBean);
				}

			}
			
			// 1. Autenticar como usuario o admin
			super.authenticate(actorBean);
			if(!actorBean.equals("admin")) {
				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(user);
			} else {
				admin = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(admin);
			}
			
			// 2. Enviar/difundir mensaje
			if(actorBean.equals("admin")) 
				message = this.messageService.create(admin);
			else 
				message = this.messageService.create(sender, recipient);
			
			message.setPriority(priority);
			message.setSubject(subject);
			message.setBody(body);
			
			if(actorBean.equals("admin")) {
				savedCollection = this.messageService.broadcastNotification(message);
				Assert.notNull(savedCollection);
			} else {
				saved = this.messageService.sendMessage(message);
				Assert.notNull(saved);
			}
			
			this.messageService.flush();
			
			// 3. Volver al listado de mensajes
			if(actorBean.equals("admin")) {
				numberMessageBySenderNew = this.messageService.findByActorId(senderId).size();
				Assert.isTrue(numberMessageBySenderNew != numberMessageBySender);
			} else {
				numberMessageBySenderNew = this.messageService.findByActorId(senderId).size();
				numberMessageByRecipientNew = this.messageService.findByActorId(recipientId).size();
				
				Assert.isTrue(numberMessageBySenderNew != numberMessageBySender);
				Assert.isTrue(numberMessageByRecipientNew != numberMessageByRecipient);
			}
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

