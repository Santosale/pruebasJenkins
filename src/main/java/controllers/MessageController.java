
package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/message")
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

		actor = this.actorService.findByUserAccountId(userAccount.getId());

		message = this.messageService.create(actor);

		result = this.createEditModelAndView(message);

		return result;

	}

	@RequestMapping(value = "/move", method = RequestMethod.GET)
	public ModelAndView move(@RequestParam final int messageId) {
		ModelAndView result;
		Message message;

		message = this.messageService.findOne(messageId);
		Assert.notNull(message);

		result = this.moveModelAndView(message);

		return result;

	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int messageId) {
		ModelAndView result;
		Message messageObject;

		messageObject = this.messageService.findOne(messageId);
		Assert.notNull(messageObject);
		result = new ModelAndView("message/display");

		result.addObject("messageObject", messageObject);

		return result;

	}

	// List / Navigate
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Actor actor;
		UserAccount userAccount;
		Collection<Message> messages;
		boolean isChildren;
		String requestURI;

		requestURI = "message/list.do";
		isChildren = false;
		userAccount = LoginService.getPrincipal();

		actor = this.actorService.findByUserAccountId(userAccount.getId());
		messages = this.messageService.findByActorId(actor.getId());

		result = new ModelAndView("message/list");
		result.addObject("messages", messages);
		result.addObject("isChildren", isChildren);
		result.addObject("requestURI", requestURI);

		return result;
	}

	@RequestMapping(value = "/folder/list", method = RequestMethod.GET)
	public ModelAndView messagesFolderList(@RequestParam final int folderId) {
		ModelAndView result;
		Collection<Message> messages;
		boolean isChildren;
		String requestURI;
		Folder folder;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);
		isChildren = true;
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;

		requestURI = "message/folder/list.do";

		messages = this.messageService.findByFolderId(folderId);
		Assert.notNull(messages);

		result = new ModelAndView("message/list");
		result.addObject("messages", messages);
		result.addObject("isChildren", isChildren);
		result.addObject("requestURI", requestURI);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int messageId) {
		ModelAndView result;
		Message message;

		message = this.messageService.findOne(messageId);
		Assert.notNull(message);

		result = this.createEditModelAndView(message);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid @ModelAttribute(value = "myMessage") final Message message, final BindingResult binding) {
		ModelAndView result;
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
	public ModelAndView delete(final Message message, final BindingResult binding) {
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
	public ModelAndView move(@Valid @ModelAttribute(value = "myMessage") final Message message, final BindingResult binding) {
		ModelAndView result;
		Message oldMessage;
		oldMessage = this.messageService.findOne(message.getId());
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

	protected ModelAndView createEditModelAndView(final Message message, final String messageCode) {
		ModelAndView result;
		Collection<Actor> actors;
		UserAccount userAccount;
		boolean canEdit;
		Actor defaultActor;

		canEdit = false;
		userAccount = LoginService.getPrincipal();

		if (message.getId() > 0)
			result = new ModelAndView("message/edit");
		else
			result = new ModelAndView("message/create");

		if (message.getId() == 0) {
			actors = this.actorService.findAll();
			actors.remove(message.getSender());
			defaultActor = (Actor) actors.toArray()[0];
			actors.remove(defaultActor);

			result.addObject("actors", actors);
			result.addObject("defaultActor", defaultActor);

		} else if (message.getFolder().getActor().getUserAccount().getId() == userAccount.getId())
			canEdit = true;

		result.addObject("myMessage", message);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);

		return result;
	}
	protected ModelAndView moveModelAndView(final Message message) {
		ModelAndView result;

		result = this.moveModelAndView(message, null);

		return result;
	}
	protected ModelAndView moveModelAndView(final Message message, final String messageCode) {
		ModelAndView result;
		Collection<Folder> folders;
		UserAccount userAccount;
		boolean canEdit;
		Folder defaultFolder;

		canEdit = false;
		userAccount = LoginService.getPrincipal();
		result = new ModelAndView("message/move");
		folders = this.folderService.findByActorId(message.getFolder().getActor().getId());
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

		return result;
	}
}
