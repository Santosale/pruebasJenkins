package controllers.user;

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
import security.LoginService;
import services.RaffleService;
import services.res.PaypalClient;


@Controller
@RequestMapping(value="/raffle/user")
public class RaffleUserController extends AbstractController {

    // Supporting services
	@Autowired
	private RaffleService raffleService;
	
	public RaffleUserController(){
        new PaypalClient();
    }
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Raffle> rafflePage;
		
		rafflePage = this.raffleService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(rafflePage);
		
		result = new ModelAndView("raffle/list");
		result.addObject("raffles", rafflePage.getContent());
		result.addObject("pageNumber", rafflePage.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "raffle/user/list.do");
		
		return result;
	}
		
}
