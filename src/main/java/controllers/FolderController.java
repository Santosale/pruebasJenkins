
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("/folder/actor")
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
		Assert.notNull(userAccount);
		
		actor = this.actorService.findByUserAccountId(userAccount.getId());
		Assert.notNull(actor);
		
		folder = this.folderService.create(actor);
		Assert.notNull(folder);

		result = this.createEditModelAndView(folder);

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(defaultValue="1", required=false) final int page) {
		ModelAndView result;
		Page<Folder> folderPage;
		UserAccount userAccount;
	
		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		
		folderPage = this.folderService.findByActorUserAccountId(userAccount.getId(), page, 5);
		Assert.notNull(folderPage);

		result = new ModelAndView("folder/list");
		result.addObject("folders", folderPage.getContent());
		result.addObject("pageNumber", folderPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "folder/actor/list.do");
		result.addObject("isChildren", false);

		return result;
	}

	@RequestMapping(value = "/childrenFolders/list", method = RequestMethod.GET)
	public ModelAndView childrenFoldersList(@RequestParam final int folderId, @RequestParam(defaultValue="1", required=false) final int page) {
		ModelAndView result;
		Page<Folder> folderPage;
		Folder folder;
		boolean isChildren;

		folder = this.folderService.findOne(folderId);
		Assert.notNull(folder);
		
		isChildren = true;
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;
		
		folderPage = this.folderService.findChildrenFoldersByFolderId(folder.getId(), page, 5);
		Assert.notNull(folderPage);
		
		result = new ModelAndView("folder/list");
		result.addObject("folders", folderPage.getContent());
		result.addObject("pageNumber", folderPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "folder/actor/childrenFolders/list.do");
		result.addObject("isChildren", isChildren);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int folderId, @RequestParam(required=false, defaultValue="1") final int pageFolders, @RequestParam(required=false, defaultValue="1") final int pageMessages) {
		ModelAndView result;
		Folder folder;
		Page<Folder> folderPage;
		boolean anyFolder, anyMessage;
		Page<Message> messagePage;
		boolean isChildren;

		folder = this.folderService.findOneToEdit(folderId);
		Assert.notNull(folder);
		
		isChildren = true;
		if (folder.getActor().getUserAccount().equals(LoginService.getPrincipal()))
			isChildren = false;

		messagePage = this.messageService.findByFolderIdPaginated(folderId, pageMessages, 5);
		Assert.notNull(messagePage);

		anyMessage = messagePage.getContent().isEmpty();

		folderPage = this.folderService.findChildrenFoldersByFolderId(folderId, pageFolders, 5);
		anyFolder = folderPage.getContent().isEmpty();

		result = new ModelAndView("folder/display");
		
		result.addObject("folder", folder);
		
		result.addObject("messages", messagePage.getContent());
		result.addObject("pageNumberMessages", messagePage.getTotalPages());
		result.addObject("pageMessages", pageMessages);
		
		result.addObject("isChildren", isChildren);
		result.addObject("requestURI", "folder/actor/display.do");
		
		result.addObject("folders", folderPage.getContent());
		result.addObject("pageNumberFolders", folderPage.getTotalPages());
		result.addObject("pageFolders", pageFolders);
		
		result.addObject("isChildren", isChildren);
		result.addObject("anyFolder", anyFolder);
		result.addObject("anyMessage", anyMessage);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int folderId) {
		ModelAndView result;
		Folder folder;

		folder = this.folderService.findOneToEdit(folderId);
		Assert.notNull(folder);

		result = this.createEditModelAndView(folder);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Folder folder, final BindingResult binding) {
		ModelAndView result;
		
		folder = this.folderService.reconstruct(folder, binding);
		
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
	public ModelAndView delete(Folder folder, final BindingResult binding) {
		ModelAndView result;
		
		try {
			this.folderService.delete(folder);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			folder = this.folderService.reconstruct(folder, binding);
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
			
		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);
		folders = this.folderService.findByActorId(actor.getId());
		Assert.notNull(folders);
			
		if (folder.getId() > 0) {
			numeroDeMensajes = this.messageService.findByFolderId(folder.getId()).size();
			Assert.notNull(numeroDeMensajes);
			result.addObject("numeroDeMensajes", numeroDeMensajes);
		}
		
		result.addObject("folder", folder);
		result.addObject("message", messageCode);
		result.addObject("canCreateAndEdit", canCreateAndEdit);
		result.addObject("folders", folders);

		return result;
	}
}
