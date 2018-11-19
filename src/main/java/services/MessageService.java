
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MessageRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Folder;
import domain.Message;

@Service
@Transactional
public class MessageService {

	// Managed repository
	@Autowired
	private MessageRepository		messageRepository;

	// Supporting services
	@Autowired
	private FolderService			folderService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private Validator				validator;
	
	@Autowired
	private ActorService			actorService;
	
	// Constructor
	public MessageService() {
		super();
	}

	// Simple CRUD methods
	public Message create(final Actor sender, final Actor recipient) {
		Message result;

		result = new Message();
		result.setSender(sender);
		result.setRecipient(recipient);
		result.setMoment(new Date(System.currentTimeMillis() - 1));

		return result;
	}

	public Message create(final Actor sender) {
		Message result;

		result = new Message();
		result.setSender(sender);
		result.setMoment(new Date(System.currentTimeMillis() - 1));

		return result;
	}

	public Collection<Message> findAll() {
		Collection<Message> result;

		result = this.messageRepository.findAll();

		return result;
	}

	public Message findOne(final int messageId) {
		Message result;

		Assert.isTrue(messageId != 0);

		result = this.messageRepository.findOne(messageId);

		return result;
	}

	public Message save(final Message message) {
		Message result;
		Message saved;

		Assert.notNull(message);

		// Si modificamos, nada debe haber cambiado salvo los folders, porque hayamos movido el mensaje
		if (message.getId() != 0) {
			saved = this.messageRepository.findOne(message.getId());
			Assert.isTrue(
				saved.getBody().equals(message.getBody()) && saved.getSubject().equals(message.getSubject()) && saved.getPriority().equals(message.getPriority()) && saved.getRecipient().equals(message.getRecipient())
					&& saved.getSender().equals(message.getSender()));
		}

		// Actualizamos el momento del envio
		if (message.getId() == 0)
			message.setMoment(new Date(System.currentTimeMillis() - 1));

		result = this.messageRepository.save(message);

		return result;
	}

	public void delete(final Message message) {
		Actor actor;
		Message saved;
		
		Assert.notNull(message);
		
		saved = this.messageRepository.findOne(message.getId());

		Assert.isTrue(LoginService.getPrincipal().equals(saved.getFolder().getActor().getUserAccount()));

		actor = saved.getFolder().getActor();

		if (!saved.getFolder().getName().equals("trash box"))
			this.moveMessage(saved, saved.getFolder(), this.folderService.findByActorIdAndFolderName(actor.getId(), "trash box"));
		else
			this.messageRepository.delete(saved);
	}

	// Other business methods
	public void flush() {
		this.messageRepository.flush();
	}
	
	public Message findOneToEdit(final int messageId) {
		Message result;

		Assert.isTrue(messageId != 0);

		result = this.messageRepository.findOne(messageId);
		Assert.notNull(result);
		
		Assert.isTrue(result.getFolder().getActor().getUserAccount().equals(LoginService.getPrincipal()));
		
		return result;
	}
	
	public Map<String, Message> sendMessage(final Message message) {

		// Declaramos las variables
		Map<String, Message> result;
		Collection<Folder> folders;
		Message senderMessage;
		Message recipientMessage;
		String destinationFolder;

		Assert.notNull(message, "message.notNull");

		result = new HashMap<String, Message>();
		destinationFolder = "in box";

		senderMessage = message;
		recipientMessage = message;

		// Comprobamos que el actor logeado sea el que está enviando el mensaje
		Assert.isTrue(LoginService.getPrincipal().equals(message.getSender().getUserAccount()));

		folders = this.folderService.findByActorId(senderMessage.getSender().getId());

		for (final Folder senderFolder : folders)
			if (senderFolder.getName().equals("out box")) {
				senderMessage.setFolder(senderFolder);
				senderMessage = this.save(senderMessage);
				result.put("senderMessage", senderMessage);
				break;
			}

		// Sobrescribimos las carpetas del actor que lo envia por las del actor
		// que recibe el mensaje
		folders = this.folderService.findByActorId(message.getRecipient().getId());

		if (this.checkSpamWords(recipientMessage))
			destinationFolder = "spam box";

		// Recorremos las carpetas en busca de "in box"
		for (final Folder recipientFolder : folders)
			if (recipientFolder.getName().equals(destinationFolder)) {
				// Una vez encontrada "in box", añadimos a dicho carpeta, el mensaje
				recipientMessage.setFolder(recipientFolder);
				recipientMessage = this.save(recipientMessage);
				result.put("recipientMessage", recipientMessage);
				break;
			}

		return result;

	}
	
	public Message moveMessage(final Message message, final Folder originFolder, final Folder destinationFolder) {

		Message result;

		Assert.notNull(destinationFolder);
		Assert.notNull(originFolder);
		Assert.notNull(message);

		// Las carpetas deben ser del mismo actor
		Assert.isTrue(originFolder.getActor().equals(destinationFolder.getActor()));

		Assert.isTrue(message.getFolder().equals(originFolder));
		Assert.isTrue(!message.getFolder().equals(destinationFolder));

		if (LoginService.getPrincipal().equals(message.getSender().getUserAccount())) {

			Assert.isTrue(message.getSender().equals(originFolder.getActor()));
			Assert.isTrue(message.getSender().equals(destinationFolder.getActor()));

		} else if (LoginService.getPrincipal().equals(message.getRecipient().getUserAccount())) {

			Assert.isTrue(message.getRecipient().equals(originFolder.getActor()));
			Assert.isTrue(message.getRecipient().equals(destinationFolder.getActor()));

		} else
			Assert.isTrue(LoginService.getPrincipal().equals(message.getSender().getUserAccount()) || LoginService.getPrincipal().equals(message.getRecipient().getUserAccount()));

		message.setFolder(destinationFolder);
		result = this.messageRepository.save(message);

		result = this.messageRepository.save(message);

		return result;
	}

	public Collection<Message> broadcastNotification(final Message notification) {
		Collection<Actor> actors;
		Authority authority;
		UserAccount userAccount;
		Collection<Message> result;

		result = new ArrayList<Message>();

		//
		Assert.notNull(notification);

		// El sender no puede ser nulo
		Assert.notNull(notification.getSender());

		// Hay que comprobar que sea un administrador el que lo envia
		authority = new Authority();
		authority.setAuthority("ADMIN");

		userAccount = notification.getSender().getUserAccount();
		Assert.isTrue(LoginService.getPrincipal().equals(userAccount) && LoginService.getPrincipal().getAuthorities().contains(authority));

		// Inicializamos la lista de actores
		actors = new ArrayList<Actor>();

		// Metemos todos los actores del sistema
		actors = this.actorService.findAll();

		result = this.sendNotification(notification, actors);

		return result;

	}
	
	private Collection<Message> sendNotification(final Message notification, final Collection<Actor> actors) {
		Collection<Folder> folders;
		Message saved;
		Collection<Message> result;
		Authority authority;

		result = new ArrayList<Message>();

		// Cuando es enviado por el sistema para informar de un cambio de estado, puede enviarlo un explorer o un manager
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(notification.getSender().getUserAccount().getAuthorities().contains(authority));

		for (final Actor actor : actors) {

			folders = this.folderService.findByActorId(actor.getId());

			notification.setRecipient(actor);

			for (final Folder actorFolder : folders)
				if (actorFolder.getName().equals("notification box")) {
					notification.setFolder(actorFolder);
					saved = this.save(notification);
					result.add(saved);
					break;
				}
		}

		return result;

	}

	public Collection<Message> findByActorId(final int actorId) {
		Collection<Message> result;

		Assert.isTrue(actorId != 0);

		result = this.messageRepository.findByActorId(actorId);

		return result;
	}

	public Collection<Message> findByFolderId(final int folderId) {
		Collection<Message> result;

		Assert.isTrue(folderId != 0);

		result = this.messageRepository.findByFolderId(folderId);

		return result;
	}
	
	public Page<Message> findByFolderIdPaginated(final int folderId, final int page, final int size) {
		Page<Message> result;

		Assert.isTrue(folderId != 0);

		result = this.messageRepository.findByFolderIdPaginated(folderId, this.getPageable(page, size));

		return result;
	}
	
	public Page<Message> findByActorUserAccountId(final int userAccountId, final int page, final int size) {
		Page<Message> result;

		Assert.isTrue(userAccountId != 0);

		result = this.messageRepository.findByActorUserAccountId(userAccountId, this.getPageable(page, size));

		return result;
	}
	
	// Auxiliary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;
		
		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);
		
		return result;
	}
	
	public Message reconstruct(final Message message, final BindingResult binding) {
		Message saved;
		UserAccount userAccount;
		Actor actor;
		
		if (message.getId() == 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			
			actor = this.actorService.findByUserAccountId(userAccount.getId());
			Assert.notNull(actor);
			
			message.setSender(actor);
		} else {
			saved = this.messageRepository.findOne(message.getId());
			message.setVersion(saved.getVersion());
			message.setSender(saved.getSender());
			message.setRecipient(saved.getRecipient());
			if(saved.getFolder() != null) message.setFolder(saved.getFolder());
		}

		this.validator.validate(message, binding);

		return message;
	}
	
	public Message reconstructMove(final Message message, final BindingResult binding) {
		Message saved;
		
		saved = this.messageRepository.findOne(message.getId());
		message.setVersion(saved.getVersion());
		message.setSender(saved.getSender());
		message.setRecipient(saved.getRecipient());
		message.setMoment(saved.getMoment());
		message.setPriority(saved.getPriority());
		message.setSubject(saved.getSubject());
		message.setBody(saved.getBody());

		this.validator.validate(message, binding);

		return message;
	}


	public boolean checkSpamWords(final Message message) {
		boolean result;
		Collection<String> tabooWords;

		result = false;
		tabooWords = this.configurationService.findTabooWords();

		for (final String tabooWord : tabooWords) {
			result = message.getBody() != null && message.getBody().toLowerCase().contains(tabooWord) || message.getSubject() != null && message.getSubject().toLowerCase().contains(tabooWord);
			if (result)
				break;
		}

		return result;
	}

}
