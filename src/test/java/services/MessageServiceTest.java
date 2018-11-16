
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Actor;
import domain.Administrator;
import domain.Folder;
import domain.Manager;
import domain.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class MessageServiceTest extends AbstractTest {

	// Service under test
	@Autowired
	private MessageService			messageService;
	// Supporting services
	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private ManagerService			managerService;
	
	@Autowired
	private FolderService folderService;


	@Test
	public void testCreateMessage() {
		Message result;
		Administrator sender;
		Manager recipient;

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		result = this.messageService.create(sender, recipient);

		Assert.notNull(result);

		Assert.notNull(result.getSender());
		Assert.isTrue(result.getSender().equals(sender));

		Assert.notNull(result.getRecipient());
		Assert.isTrue(result.getRecipient().equals(recipient));

	}

	@Test
	public void testCreateNotification() {
		Message result;
		Administrator sender;

		sender = (Administrator) this.administratorService.findAll().toArray()[0];

		result = this.messageService.create(sender);

		Assert.notNull(result);

		Assert.notNull(result.getSender());
		Assert.isTrue(result.getSender().equals(sender));

	}

	@Test
	public void testFindAll() {
		Collection<Message> result;

		result = this.messageService.findAll();

		Assert.notNull(result);
		Assert.notEmpty(result);
	}

	@Test
	public void testSave() {

		// Comprobamos que un message no se puede modificar

		Message message;
		Message senderMessage;
		Message senderMessageSaved;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		senderMessage = this.copyMessage(result.get("senderMessage"));

		senderMessage.setSubject("I've changed the subject!");

		try {
			senderMessageSaved = this.messageService.save(senderMessage);
			Assert.isNull(senderMessageSaved);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testDelete1() {

		// Comprobamos que cuando borras un mensaje se va a trash box

		Message message;
		Message senderMessage;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		senderMessage = this.copyMessage(result.get("senderMessage"));
		
		this.messageService.delete(senderMessage);
		
//		for (final Folder f : sender.getFolders())
		for (final Folder f : this.folderService.findByActorId(sender.getId()))
			if (f.getName().equals("trash box"))
				Assert.isTrue(this.messageService.findByFolderId(f.getId()).contains(senderMessage));

		super.authenticate(null);

	}

	@Test
	public void testDelete2() {

		// Comprobamos que un message está eliminado totalmente

		Message message;
		Message senderMessage;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		senderMessage = this.copyMessage(result.get("senderMessage"));

		this.messageService.delete(senderMessage);

		this.messageService.delete(senderMessage);

		Assert.isTrue(!this.messageService.findAll().contains(senderMessage));

		super.authenticate(null);

	}

	@Test
	public void testSend1() {

		// En este test un admin enviará un message a un manager

		Message message;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		// Comprobamos que el message este entre todos los message persistidos.
		Assert.isTrue(this.messageService.findAll().contains(result.get("senderMessage")) && this.messageService.findAll().contains(result.get("recipientMessage")));

		// Comprobamos que este en la carpeta out box del sender
		for (final Folder f : this.folderService.findByActorId(sender.getId()))
			if (f.getName().equals("out box"))
				Assert.isTrue(this.messageService.findByFolderId(f.getId()).contains(result.get("senderMessage")));

		// Comprobamos que este en la carpeta in box del recipient
		for (final Folder f : this.folderService.findByActorId(recipient.getId())) {
			if (f.getName().equals("in box"))
				Assert.isTrue(this.messageService.findByFolderId(f.getId()).contains(result.get("recipientMessage")));
			break;
		}

		super.authenticate(null);

	}

	@Test
	public void testSend2() {

		Message message;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		try {
			result = this.messageService.sendMessage(message);
			Assert.isNull(result.get("senderMessage"));
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testSend3() {

		Message message;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		super.authenticate("manager1");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		try {
			result = this.messageService.sendMessage(message);
			Assert.isNull(result.get("senderMessage"));
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testMoveMessage1() {

		Message resultMove;
		Message message;
		Message recipientMessage;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;
		Folder originFolder;
		Folder destinationFolder;

		originFolder = null;
		destinationFolder = null;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		super.authenticate(null);

		super.authenticate("manager1");

		recipientMessage = result.get("recipientMessage");

		for (final Folder f : this.folderService.findByActorId(recipient.getId())){
			if (f.getName().equals("in box")) {
				originFolder = f;
				continue;
			}
			if (f.getName().equals("spam box")) {
				destinationFolder = f;
				continue;
			}
		}

		Assert.notNull(originFolder);
		Assert.notNull(destinationFolder);

		Assert.isTrue(recipientMessage.getFolder().equals(originFolder) && !recipientMessage.getFolder().equals(destinationFolder));

		resultMove = this.messageService.moveMessage(recipientMessage, originFolder, destinationFolder);

		Assert.isTrue(!resultMove.getFolder().equals(originFolder) && resultMove.getFolder().equals(destinationFolder));

	}
	
	@Test
	public void testFindMessageBySenderId() {
		Collection<Message> messages;
		Message message;
		Map<String, Message> result;
		Administrator sender;
		Manager recipient;

		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];

		// Creamos el message en cuestión
		message = this.messageService.create(sender, recipient);
		message.setSubject("Esto es una prueba");
		message.setBody("It works!");
		message.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendMessage(message);

		
		messages = this.messageService.findBySenderId(sender.getId());
		Assert.isTrue(messages.contains(result.get("senderMessage")));

		super.authenticate(null);

	}
	
	@Test
	public void testSendNotification1() {

		// En este test un admin enviará un message a un manager

		Message notification;
		Collection<Message> result;
		Administrator sender;
		Manager recipient;
		List<Actor> recipients;
		Folder notificationBox;
		
		recipients = new ArrayList<Actor>();
		
		// Nos autenticamos como el usuario que va a enviar el message.
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];
		
		recipients.add(recipient);

		// Creamos el message en cuestión
		notification = this.messageService.create(sender);
		notification.setSubject("Esto es una prueba");
		notification.setBody("It works!");
		notification.setPriority("HIGH");

		// Envíamos el message y nos devolverá el message correspondiente a la copia del sender
		result = this.messageService.sendNotification(notification, recipients);

		// Comprobamos que el message este entre todos los message persistidos.
		Assert.isTrue(this.messageService.findAll().contains((Message) result.toArray()[0]) && this.messageService.findAll().contains((Message) result.toArray()[0]));

		// Comprobamos que este en la carpeta out box del sender
		notificationBox = this.folderService.findByActorIdAndFolderName(recipient.getId(), "notification box");

		Assert.isTrue(this.messageService.findByFolderId(notificationBox.getId()).contains((Message) result.toArray()[0]));
		
		super.authenticate(null);

	}

	@Test
	public void testSendNotification2() {

		Message notification;
		Collection<Message> result;
		Administrator sender;
		Manager recipient;
		List<Actor> recipients;
		
		recipients = new ArrayList<Actor>();
		
		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];
		recipient = (Manager) this.managerService.findAll().toArray()[0];
		
		recipients.add(recipient);

		notification = this.messageService.create(sender);
		notification.setSubject("Esto es una prueba");
		notification.setBody("It works!");
		notification.setPriority("HIGH");

		try {
			result = this.messageService.sendNotification(notification, recipients);
			Assert.isNull((Message) result.toArray()[0]);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);

	}

	@Test
	public void testBroadcastNotification() {

		Message notification;
		Collection<Message> result;
		Administrator sender;

		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];

		notification = this.messageService.create(sender);
		notification.setSubject("Esto es una prueba");
		notification.setBody("It works!");
		notification.setPriority("HIGH");

		result = this.messageService.broadcastNotification(notification);

		Assert.isTrue(!this.messageService.findAll().contains(result));

		super.authenticate(null);

	}
	
	
	@Test
	public void testCheckSpamWords() {
		Message message;
		Administrator sender;

		super.authenticate("admin");

		sender = (Administrator) this.administratorService.findAll().toArray()[0];

		message = this.messageService.create(sender);
		message.setSubject("Esto es SEX SEX");
		message.setBody("FREE VIAGRA!");
		message.setPriority("HIGH");
		
		Assert.isTrue(this.messageService.checkSpamWords(message));
	}

	private Message copyMessage(final Message message) {
		Message result;

		result = new Message();
		result.setId(message.getId());
		result.setVersion(message.getVersion());
		result.setMoment(message.getMoment());
		result.setSubject(message.getSubject());
		result.setBody(message.getBody());
		result.setPriority(message.getPriority());
		result.setFolder(message.getFolder());
		result.setSender(message.getSender());
		result.setRecipient(message.getRecipient());

		return result;
	}

}
