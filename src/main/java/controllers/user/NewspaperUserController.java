
package controllers.user;

import java.util.ArrayList;
import java.util.List;

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
import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Newspaper;
import domain.Volume;

@Controller
@RequestMapping("/newspaper/user")
public class NewspaperUserController extends AbstractController {

	// Services
	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private UserService			userService;

	@Autowired
	private VolumeService		volumeService;


	// Constructor
	public NewspaperUserController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/user/list.do");

		return result;
	}

	@RequestMapping(value = "/listPublished", method = RequestMethod.GET)
	public ModelAndView listPublished(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findPublished(page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/user/listPublished.do");

		return result;
	}

	@RequestMapping(value = "/addNewspaper", method = RequestMethod.GET)
	public ModelAndView listAddNewspaper(@RequestParam final int volumeId, @RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findAddNewspaper(volumeId, page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/user/addNewspaper.do");
		result.addObject("volumeId", volumeId);

		return result;
	}

	@RequestMapping(value = "/deleteNewspaper", method = RequestMethod.GET)
	public ModelAndView listDeleteNewspaper(@RequestParam final int volumeId, @RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		List<Newspaper> newspapers;
		Volume volume;
		Integer pageNumber, fromId, toId;

		volume = this.volumeService.findOneToEdit(volumeId);
		Assert.isTrue(volume.getNewspapers().size() > 1);

		newspapers = new ArrayList<Newspaper>(volume.getNewspapers());
		Assert.notNull(newspapers);

		fromId = this.fromIdAndToId(newspapers.size(), page)[0];
		toId = this.fromIdAndToId(newspapers.size(), page)[1];

		pageNumber = newspapers.size();

		result = new ModelAndView("newspaper/list");

		pageNumber = (int) Math.floor(((pageNumber / (5 + 0.0)) - 0.1) + 1);
		result.addObject("pageNumber", pageNumber);
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.subList(fromId, toId));
		result.addObject("requestURI", "newspaper/user/deleteNewspaper.do");
		result.addObject("volumeId", volumeId);

		return result;
	}
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Newspaper newspaper;

		newspaper = this.newspaperService.create();

		result = this.createEditModelAndView(newspaper);

		return result;

	}

	// Edit
	@RequestMapping(value = "/editDate", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int newspaperId) {
		ModelAndView result;
		Newspaper newspaper;

		newspaper = this.newspaperService.findOneToEdit(newspaperId);
		Assert.notNull(newspaper);
		Assert.isTrue(newspaper.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId());

		result = this.createEditModelAndView(newspaper);

		return result;
	}

	//	@RequestMapping(value = "/publish", method = RequestMethod.GET)
	//	public ModelAndView addCategory(@RequestParam final int newspaperId) {
	//		ModelAndView result;
	//
	//		Assert.isTrue(newspaperId != 0);
	//
	//		this.newspaperService.publish(newspaperId);
	//
	//		result = new ModelAndView("redirect:list.do");
	//
	//		return result;
	//
	//	}
	@RequestMapping(value = "/putPublic", method = RequestMethod.GET)
	public ModelAndView putPublic(@RequestParam final int newspaperId) {
		ModelAndView result;

		Assert.isTrue(newspaperId != 0);

		this.newspaperService.putPublic(newspaperId);

		result = new ModelAndView("redirect:list.do");

		return result;

	}

	@RequestMapping(value = "/putPrivate", method = RequestMethod.GET)
	public ModelAndView putPrivate(@RequestParam final int newspaperId) {
		ModelAndView result;

		Assert.isTrue(newspaperId != 0);

		this.newspaperService.putPrivate(newspaperId);

		result = new ModelAndView("redirect:list.do");

		return result;

	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Newspaper newspaper, final BindingResult binding) {
		ModelAndView result;
		newspaper = this.newspaperService.reconstruct(newspaper, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(newspaper);
		else
			try {
				this.newspaperService.save(newspaper);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(newspaper, "newspaper.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Newspaper newspaper) {
		ModelAndView result;

		result = this.createEditModelAndView(newspaper, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Newspaper newspaper, final String messageCode) {
		ModelAndView result;

		if (newspaper.getId() == 0)
			result = new ModelAndView("newspaper/create");
		else
			result = new ModelAndView("newspaper/edit");

		result.addObject("newspaper", newspaper);
		result.addObject("message", messageCode);

		return result;
	}

	private Integer[] fromIdAndToId(final Integer tamañoAux, final Integer page) {
		Integer tamaño, pageAux, fromId, toId;
		tamaño = tamañoAux;
		Integer[] result;

		result = new Integer[2];

		pageAux = page;
		if (page <= 0)
			pageAux = 1;

		fromId = (pageAux - 1) * 5;
		if (fromId > tamaño)
			fromId = 0;
		toId = (pageAux * 5);
		if (tamaño > 5) {
			if (toId > tamaño && fromId == 0)
				toId = 5;
			else if (toId > tamaño && fromId != 0)
				toId = tamaño;
		} else
			toId = tamaño;

		result[0] = fromId;
		result[1] = toId;

		return result;
	}

}
