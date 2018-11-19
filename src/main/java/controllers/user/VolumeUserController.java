
package controllers.user;

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
import services.VolumeService;
import controllers.AbstractController;
import domain.Volume;

@Controller
@RequestMapping("/volume/user")
public class VolumeUserController extends AbstractController {

	// Services
	@Autowired
	private VolumeService	volumeService;


	// Constructor
	public VolumeUserController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Volume> volumes;

		volumes = this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(volumes);

		result = new ModelAndView("volume/list");
		result.addObject("pageNumber", volumes.getTotalPages());
		result.addObject("page", page);
		result.addObject("volumes", volumes.getContent());
		result.addObject("requestURI", "volume/user/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int newspaperId) {
		ModelAndView result;
		Volume volume;

		volume = this.volumeService.create(newspaperId);

		result = this.createEditModelAndView(volume);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Volume volume, final BindingResult binding) {
		ModelAndView result;
		volume = this.volumeService.reconstruct(volume, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(volume);
		else
			try {
				this.volumeService.save(volume);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(volume, "volume.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/addNewspaper", method = RequestMethod.GET)
	public ModelAndView addNewspaper(@RequestParam final int newspaperId, @RequestParam final int volumeId) {
		ModelAndView result;

		Assert.isTrue(newspaperId != 0);

		this.volumeService.addNewspaper(volumeId, newspaperId);

		result = new ModelAndView("redirect:/volume/display.do?volumeId=" + volumeId);

		return result;

	}

	@RequestMapping(value = "/deleteNewspaper", method = RequestMethod.GET)
	public ModelAndView deleteNewspaper(@RequestParam final int newspaperId, @RequestParam final int volumeId) {
		ModelAndView result;

		Assert.isTrue(newspaperId != 0);

		this.volumeService.deleteNewspaper(volumeId, newspaperId);

		result = new ModelAndView("redirect:/volume/display.do?volumeId=" + volumeId);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int volumeId) {
		ModelAndView result;
		Volume volume;

		volume = this.volumeService.findOneToEdit(volumeId);
		Assert.notNull(volume);

		result = this.createEditModelAndView(volume);

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Volume volume) {
		ModelAndView result;

		result = this.createEditModelAndView(volume, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Volume volume, final String messageCode) {
		ModelAndView result;

		if (volume.getId() == 0)
			result = new ModelAndView("volume/create");
		else
			result = new ModelAndView("volume/edit");

		result.addObject("volume", volume);
		result.addObject("message", messageCode);

		return result;
	}

}
