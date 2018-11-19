package controllers.moderator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import controllers.AbstractController;
import domain.Raffle;
import services.RaffleService;


@Controller
@RequestMapping(value="/raffle/moderator")
public class RaffleModeratorController extends AbstractController {

    // Supporting services
	@Autowired
	private RaffleService raffleService;
	
	public RaffleModeratorController(){
		super();
    }
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Raffle> rafflePage;
		
		rafflePage = this.raffleService.findAllPaginated(page, 5);
		Assert.notNull(rafflePage);
		
		result = new ModelAndView("raffle/list");
		result.addObject("raffles", rafflePage.getContent());
		result.addObject("pageNumber", rafflePage.getTotalPages());
		result.addObject("page", page);
		
		result.addObject("requestURI", "raffle/moderator/list.do");
		
		return result;
	}
	
	@RequestMapping(value="/toraffle", method = RequestMethod.GET)
	public ModelAndView raffle(@RequestParam final int raffleId) {
		ModelAndView result;
		
		this.raffleService.toRaffle(raffleId);
		
		result = new ModelAndView("redirect:list.do");
				
		return result;
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int raffleId) {
		ModelAndView result;
		Raffle raffle;
		
		raffle = this.raffleService.findOneToDelete(raffleId);
		Assert.notNull(raffle);
		
		this.raffleService.delete(raffle);
		result = new ModelAndView("redirect:list.do");
		
		return result;
	}
	
}
