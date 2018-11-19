
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.FolderService;
import services.MessageService;
import domain.Actor;
import domain.Folder;
import domain.Message;

@Controller
@RequestMapping("/message/actor")
public class MessageController extends AbstractController {

	// Services
	@Autowired
	private ActorService	actorService;

	@Autowired
	private MessageService	messageService;

	@Autowired
	private FolderService	folderService;


	// Constructor
	public MessageController() {
		super();
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Message message;
		UserAccount userAccount;
		Actor actor;

		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		
		actor = this.actorService.findByUserAccountId(userAccount.getId());
		Assert.notNull(actor);
		
		message = this.messageService.create(actor);
		Assert.notNull(message);
		
		result = this.createEditModelAndView(message);

		return result;

	}

	@RequestMapping(value = "/move", method = RequestMethod.GET)
	public ModelAndView move(@RequestParam final int messageId) {
		ModelAndView result;
		Message message;

		message = this.messageService.findOneToEdit(messageId);
		Assert.notNull(message);

		result = this.moveModelAndView(message);

		return result;

	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int messageId) {
		ModelAndView result;
		Message messageObject;

		messageObject = this.messageService.findOneToEdit(messageId);
		Assert.notNull(messageObject);
		
		result = new ModelAndView("message/display");

		result.addObject("messageObject", messageObject);

		return result;

	}

	// List / Navigate
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(defaultValue="1", required=false) final int page) {
		ModelAndView result;
		UserAccount userAccount;
		Page<Message> messagePage;

		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		
		messagePage = this.messageService.findByActorUserAccountId(userAccount.getId(), page, 5);
		Assert.notNull(messagePage);

		result = new ModelAndView("message/list");
		result.addObject("messages", messagePage.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", messagePage.getTotalPages());
		result.addObject("isChildren", false);
		result.addObject("requestURI", "message/actor/list.do");

		return result;
	}

	@RequestMapping(value = "/folder/list", method = RequestMethod.GET)
	public ModelAndView messagesFolderList(@RequestParam final int folderId, @RequestParam(defaultValue="1", required=false) final int page) {
		ModelAndView result;
		Page<Message> messagePage;
		boolean isChildren;
		Folder folder;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);
		
		isChildren = true;
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;

		messagePage = this.messageService.findByFolderIdPaginated(folderId, page, 5);
		Assert.notNull(messagePage);

		result = new ModelAndView("message/list");
		result.addObject("messages", messagePage.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", messagePage.getTotalPages());
		result.addObject("isChildren", isChildren);
		result.addObject("requestURI", "message/actor/folder/list.do");

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int messageId) {
		ModelAndView result;
		Message message;

		message = this.messageService.findOneToEdit(messageId);
		Assert.notNull(message);

		result = this.createEditModelAndView(message);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "myMessage") Message message, final BindingResult binding) {
		ModelAndView result;
		
		message = this.messageService.reconstruct(message, binding);
		
		if (binding.hasErrors())
			result = this.createEditModelAndView(message);
		else
			try {
				this.messageService.sendMessage(message);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(message, "message.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(@ModelAttribute(value = "myMessage") Message message, final BindingResult binding) {
		ModelAndView result;

		try {
			this.messageService.delete(message);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(message, "message.commit.error");
		}

		return result;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST, params = "save")
	public ModelAndView move(@ModelAttribute(value = "myMessage") Message message, final BindingResult binding) {
		ModelAndView result;
		Message oldMessage;
		
		message = this.messageService.reconstructMove(message, binding);
		
		oldMessage = this.messageService.findOneToEdit(message.getId());
		if (binding.hasErrors())
			result = this.moveModelAndView(message);
		else
			try {
				this.messageService.moveMessage(oldMessage, oldMessage.getFolder(), message.getFolder());
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.moveModelAndView(message, "message.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Message message) {
		ModelAndView result;

		result = this.createEditModelAndView(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message message, final String messageCode) {
		ModelAndView result;
		Collection<Actor> actors;
		UserAccount userAccount;
		boolean canEdit;

		canEdit = false;
		userAccount = LoginService.getPrincipal();

		result = new ModelAndView("message/edit");
		
		if(message.getId() != 0) 
			message = this.messageService.findOne(message.getId());

		if (message.getId() == 0) {
			actors = this.actorService.findAll();
			Assert.notNull(actors);
			result.addObject("actors", actors);
		} else if (message.getFolder().getActor().getUserAccount().getId() == userAccount.getId())
			canEdit = true;

		result.addObject("myMessage", message);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		
		if(message.getId() != 0)
			result.addObject("showRecipients", false);
		else
			result.addObject("showRecipients", true);

		return result;
	}
	
	protected ModelAndView moveModelAndView(final Message message) {
		ModelAndView result;

		result = this.moveModelAndView(message, null);

		return result;
	}
	
	protected ModelAndView moveModelAndView(Message message, final String messageCode) {
		ModelAndView result;
		Collection<Folder> folders;
		UserAccount userAccount;
		boolean canEdit;
		Folder defaultFolder;

		canEdit = false;
		
		if(message.getId() != 0) 
			message = this.messageService.findOne(message.getId());
		
		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		
		result = new ModelAndView("message/move");
		
		folders = this.folderService.findByActorId(message.getFolder().getActor().getId());
		Assert.notNull(folders);
		
		folders.remove(message.getFolder());
		defaultFolder = (Folder) folders.toArray()[0];
		folders.remove(defaultFolder);

		if (message.getFolder().getActor().getUserAccount().getId() == userAccount.getId())
			canEdit = true;

		result.addObject("folders", folders);
		result.addObject("myMessage", message);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("defaultFolder", defaultFolder);
		
		if(message.getId() != 0)
			result.addObject("showRecipients", false);


		return result;
	}
}
