package controllers.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import controllers.AbstractController;
import domain.Company;
import domain.Raffle;
import security.LoginService;
import services.CompanyService;
import services.RaffleService;
import services.TicketService;


@Controller
@RequestMapping(value="/raffle/company")
public class RaffleCompanyController extends AbstractController {

    // Supporting services
	@Autowired
	private RaffleService raffleService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private TicketService ticketService;
	
	public RaffleCompanyController(){
		super();
    }
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Raffle> rafflePage;
		
		rafflePage = this.raffleService.findByCompanyAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(rafflePage);
		
		result = new ModelAndView("raffle/list");
		result.addObject("raffles", rafflePage.getContent());
		result.addObject("pageNumber", rafflePage.getTotalPages());
		result.addObject("page", page);
		
		result.addObject("requestURI", "raffle/company/list.do");
		
		return result;
	}
	
	@RequestMapping(value="/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Raffle raffle;
		Company company;
		
		company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(company);
		
		raffle = this.raffleService.create(company);
		Assert.notNull(raffle);
		
		result = this.createEditModelAndView(raffle);
		
		return result;
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int raffleId) {
		ModelAndView result;
		Raffle raffle;
		Integer countTickets;
		
		raffle = this.raffleService.findOneToEdit(raffleId);
		Assert.notNull(raffle);
		
		countTickets = this.ticketService.countByRaffleId(raffleId);
		Assert.notNull(countTickets);
		
		result = this.createEditModelAndView(raffle);
		result.addObject("countTickets", countTickets);
		
		return result;
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView edit(Raffle raffle, BindingResult binding) {
		ModelAndView result;
		
		raffle = this.raffleService.reconstruct(raffle, binding);
		
		if(binding.hasErrors()) {
			result = createEditModelAndView(raffle);
		} else {
			try {
				this.raffleService.save(raffle);
				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
//				result = super.panic(oops);
				result = createEditModelAndView(raffle, "raffle.commit.error");
			}
		}
		
		return result;
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Raffle raffle) {
		ModelAndView result;
		
		try {
			this.raffleService.delete(raffle);
			result = new ModelAndView("redirect:list.do");
		} catch (Throwable oops) {
			result = this.createEditModelAndView(raffle, "raffle.commit.error");
		}
		
		return result;
	}

	protected ModelAndView createEditModelAndView(final Raffle raffle) {
		ModelAndView result;

		result = this.createEditModelAndView(raffle, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Raffle raffle, final String messageCode) {
		ModelAndView result;
		int countTickets;

		result = new ModelAndView("raffle/edit");
		
		if(raffle.getId() != 0) {
			countTickets = this.ticketService.countByRaffleId(raffle.getId());
			Assert.notNull(countTickets);
			result.addObject("countTickets", countTickets);
		}

		result.addObject("raffle", raffle);
		result.addObject("message", messageCode);

		return result;
	}
	
}
