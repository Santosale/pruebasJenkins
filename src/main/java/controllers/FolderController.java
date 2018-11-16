
package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/folder")
public class FolderController extends AbstractController {

	// Services
	@Autowired
	ActorService	actorService;

	@Autowired
	MessageService	messageService;

	@Autowired
	FolderService	folderService;


	// Constructor
	public FolderController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Folder folder;
		UserAccount userAccount;
		Actor actor;

		userAccount = LoginService.getPrincipal();

		actor = this.actorService.findByUserAccountId(userAccount.getId());

		folder = this.folderService.create(actor);
		folder.setSystem(false);

		result = this.createEditModelAndView(folder);

		return result;

	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Actor actor;
		UserAccount userAccount;
		Collection<Folder> folders;
		String requestURI;
		boolean isChildren;

		requestURI = "folder/list.do";
		isChildren = false;
		userAccount = LoginService.getPrincipal();

		actor = this.actorService.findByUserAccountId(userAccount.getId());
		folders = this.folderService.findByActorId(actor.getId());

		result = new ModelAndView("folder/list");
		result.addObject("folders", folders);
		result.addObject("requestURI", requestURI);
		result.addObject("isChildren", isChildren);

		return result;
	}

	@RequestMapping(value = "/childrenFolders/list", method = RequestMethod.GET)
	public ModelAndView childrenFoldersList(@RequestParam final int folderId) {
		ModelAndView result;
		Collection<Folder> folders;
		Folder folder;
		String requestURI;
		boolean isChildren;

		requestURI = "folder/childrenFolders/list.do";
		isChildren = true;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;
		folders = folder.getChildrenFolders();

		result = new ModelAndView("folder/list");
		result.addObject("folders", folders);
		result.addObject("requestURI", requestURI);
		result.addObject("isChildren", isChildren);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int folderId) {
		ModelAndView result;
		Folder folder;
		Collection<Folder> folders;
		boolean anyFolder, anyMessage;
		Collection<Message> messages;
		boolean isChildren;
		String requestURI;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);
		isChildren = true;
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;

		requestURI = "folder/display.do";

		messages = this.messageService.findByFolderId(folderId);
		anyMessage = messages.isEmpty();
		Assert.notNull(messages);

		Assert.notNull(folder);

		folders = folder.getChildrenFolders();
		anyFolder = folders.isEmpty();

		result = new ModelAndView("folder/display");

		result.addObject("folder", folder);
		result.addObject("messages", messages);
		result.addObject("isChildren", isChildren);
		result.addObject("requestURI", requestURI);
		result.addObject("folders", folders);
		result.addObject("isChildren", isChildren);
		result.addObject("anyFolder", anyFolder);
		result.addObject("anyMessage", anyMessage);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int folderId) {
		ModelAndView result;
		Folder folder;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);

		result = this.createEditModelAndView(folder);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Folder folder, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditModelAndView(folder);
		else
			try {
				this.folderService.save(folder);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(folder, "folder.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Folder folder, final BindingResult binding) {
		ModelAndView result;

		try {
			this.folderService.delete(folder);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(folder, "folder.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Folder folder) {
		ModelAndView result;

		result = this.createEditModelAndView(folder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Folder folder, final String messageCode) {
		ModelAndView result;
		Collection<Folder> folders;
		Actor actor;
		UserAccount userAccount;
		int numeroDeMensajes;
		boolean canCreateAndEdit;

		canCreateAndEdit = false;
		if (folder.getId() > 0) {
			if (folder.getActor().getUserAccount().getId() == LoginService.getPrincipal().getId())
				canCreateAndEdit = true;
		} else
			canCreateAndEdit = true;

		if (folder.getId() > 0)
			result = new ModelAndView("folder/edit");
		else
			result = new ModelAndView("folder/create");

		if (folder.getId() == 0) {
			userAccount = LoginService.getPrincipal();
			actor = this.actorService.findByUserAccountId(userAccount.getId());
			folders = this.folderService.findByActorId(actor.getId());

			result.addObject("folders", folders);

		}
		if (folder.getId() > 0) {
			numeroDeMensajes = this.messageService.findByFolderId(folder.getId()).size();
			result.addObject("numeroDeMensajes", numeroDeMensajes);
		}
		result.addObject("folder", folder);
		result.addObject("message", messageCode);
		result.addObject("canCreateAndEdit", canCreateAndEdit);

		return result;
	}
}
