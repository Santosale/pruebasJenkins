
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.NewspaperRepository;
import security.Authority;
import security.LoginService;
import domain.Advertisement;
import domain.Article;
import domain.Newspaper;
import domain.SubscriptionNewspaper;
import domain.Volume;

@Service
@Transactional
public class NewspaperService {

	// Managed repository
	@Autowired
	private NewspaperRepository				newspaperRepository;

	// Supporting services
	@Autowired
	private UserService						userService;

	@Autowired
	private ArticleService					articleService;

	@Autowired
	private SubscriptionNewspaperService	subscriptionNewspaperService;

	@Autowired
	private CustomerService					customerService;

	@Autowired
	private ConfigurationService			configurationService;

	@Autowired
	private VolumeService					volumeService;

	@Autowired
	private AdvertisementService			advertisementService;

	@Autowired
	private Validator						validator;


	// Constructor
	public NewspaperService() {
		super();
	}

	public Newspaper create() {
		Newspaper result;
		Authority authority;
		Collection<Article> articles;
		Collection<Advertisement> advertisements;

		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		articles = new ArrayList<Article>();
		advertisements = new ArrayList<Advertisement>();
		result = new Newspaper();
		result.setPublisher(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		result.setIsPrivate(false);
		result.setHasTaboo(false);
		result.setIsPublished(true);
		result.setArticles(articles);
		result.setAdvertisements(advertisements);

		return result;
	}

	public Collection<Newspaper> findAll() {
		Collection<Newspaper> result;

		result = this.newspaperRepository.findAll();

		return result;
	}

	public Newspaper findOne(final int newspaperId) {
		Newspaper result;

		Assert.isTrue(newspaperId != 0);

		result = this.newspaperRepository.findOne(newspaperId);

		return result;
	}

	public Newspaper findOneToEdit(final int newspaperId) {
		Newspaper result;

		result = this.findOne(newspaperId);

		Assert.isTrue(newspaperId != 0);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == result.getPublisher().getUserAccount().getId());
		Assert.isTrue(result.getPublicationDate().compareTo(new Date()) > 0);

		return result;
	}

	public Newspaper findOneToDisplay(final int newspaperId) {
		Newspaper result;
		Authority authority;
		Authority authority2;
		Authority authority3;
		Authority authority4;
		Boolean canPermit;
		Date currentMoment;

		result = this.findOne(newspaperId);
		Assert.notNull(result);
		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("CUSTOMER");
		authority3 = new Authority();
		authority3.setAuthority("ADMIN");
		authority4 = new Authority();
		authority4.setAuthority("AGENT");
		currentMoment = new Date();
		canPermit = false;
		if (LoginService.isAuthenticated()) {
			if (LoginService.getPrincipal().getAuthorities().contains(authority)) {
				if (result.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId())
					canPermit = true;
				else if (result.getPublicationDate().compareTo(currentMoment) <= 0 && result.getIsPublished() == true)
					canPermit = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority2)) {
				if (result.getPublicationDate().compareTo(currentMoment) <= 0 && result.getIsPublished() == true)
					canPermit = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority4)) {
				if (result.getPublicationDate().compareTo(currentMoment) <= 0 && result.getIsPublished() == true)
					canPermit = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority3))
				canPermit = true;
		} else if (result.getPublicationDate().compareTo(currentMoment) <= 0 && result.getIsPublished() == true && result.getIsPrivate() == false)
			canPermit = true;

		Assert.isTrue(canPermit == true);

		return result;

	}
	public Newspaper save(final Newspaper newspaper) {
		Newspaper result;
		Date currentMoment;
		Calendar calendar;
		Calendar myPublicationDate;

		calendar = Calendar.getInstance();

		Assert.notNull(newspaper);
		Assert.notNull(newspaper.getPublicationDate());
		currentMoment = new Date();
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(newspaper.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId());
		if (newspaper.getId() == 0) {
			Assert.isTrue(newspaper.getPublicationDate().compareTo(currentMoment) > 0);
			Assert.isTrue(newspaper.getIsPublished() == true);
			Assert.isTrue(newspaper.getArticles().isEmpty());
			Assert.isTrue(newspaper.getAdvertisements().isEmpty());
			if (this.checkTabooWords(newspaper) == true)
				newspaper.setHasTaboo(true);
		} else if (newspaper.getPublicationDate().compareTo(currentMoment) < 0) {
			Assert.isTrue(this.findOne(newspaper.getId()).getPublicationDate().compareTo(new Date()) > 0);
			myPublicationDate = NewspaperService.toCalendar(newspaper.getPublicationDate());
			Assert.isTrue(myPublicationDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && myPublicationDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && myPublicationDate.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH));
			for (final Article a : newspaper.getArticles()) {
				a.setMoment(newspaper.getPublicationDate());
				this.articleService.saveFromNewspaper(a);
			}
		}
		result = this.newspaperRepository.save(newspaper);

		return result;
	}
	public static Calendar toCalendar(final Date date) {
		Calendar cal;
		cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public void delete(final Newspaper newspaper) {
		Newspaper newspaperToDelete;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		newspaperToDelete = this.findOne(newspaper.getId());
		Assert.notNull(newspaperToDelete);

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		for (final Article a : this.articleService.findByNewspaperId(newspaperToDelete.getId()))
			this.articleService.deleteFromNewspaper(a);

		for (final SubscriptionNewspaper s : this.subscriptionNewspaperService.findByNewspaperId(newspaperToDelete.getId()))
			this.subscriptionNewspaperService.deleteFromNewspaper(s);

		for (final Volume v : this.volumeService.findByNewspaperId(newspaperToDelete.getId()))
			if (v.getNewspapers().size() == 1)
				this.volumeService.deleteFromNewspaper(v);
			else {
				v.getNewspapers().remove(this.findOne(newspaperToDelete.getId()));
				this.volumeService.saveFromNewspaper(v);
			}

		this.newspaperRepository.delete(this.findOne(newspaperToDelete.getId()));

	}

	public void flush() {
		this.newspaperRepository.flush();
	}

	//	public void publish(final int newspaperId) {
	//		Newspaper newspaperToPublish;
	//		Date currentMoment;
	//		currentMoment = new Date();
	//
	//		newspaperToPublish = this.findOne(newspaperId);
	//		Assert.notNull(newspaperToPublish);
	//		Assert.isTrue(LoginService.isAuthenticated());
	//		Assert.isTrue(newspaperToPublish.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId());
	//		Assert.isTrue(newspaperToPublish.getIsPublished() == true && newspaperToPublish.getPublicationDate().compareTo(currentMoment) > 0);
	//		newspaperToPublish.setPublicationDate(currentMoment);
	//		for (final Article a : newspaperToPublish.getArticles()) {
	//			a.setMoment(currentMoment);
	//			this.articleService.saveFromNewspaper(a);
	//		}
	//		this.newspaperRepository.save(newspaperToPublish);
	//
	//	}

	public void putPublic(final int newspaperId) {
		Newspaper newspaperToPublic;

		newspaperToPublic = this.findOne(newspaperId);
		Assert.notNull(newspaperToPublic);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(newspaperToPublic.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId());
		Assert.isTrue(newspaperToPublic.getIsPrivate() == true);
		newspaperToPublic.setIsPrivate(false);
		this.newspaperRepository.save(newspaperToPublic);

	}

	public void putPrivate(final int newspaperId) {
		Newspaper newspaperToPublic;

		newspaperToPublic = this.findOne(newspaperId);
		Assert.notNull(newspaperToPublic);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(newspaperToPublic.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId());
		Assert.isTrue(newspaperToPublic.getIsPrivate() == false);
		newspaperToPublic.setIsPrivate(true);
		this.newspaperRepository.save(newspaperToPublic);

	}

	public Page<Newspaper> findByUserId(final int userId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		Assert.isTrue(userId != 0);
		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == userId);

		result = this.newspaperRepository.findByUserId(userId, this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findPublished(final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.findPublished(this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findByCustomerId(final int customerId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		Assert.isTrue(customerId != 0);
		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == customerId);

		result = this.newspaperRepository.findByCustomerId(customerId, this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findPublicsAndPublicated(final int page, final int size) {
		Page<Newspaper> result;

		result = this.newspaperRepository.findPublicsAndPublicated(this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findAllPaginated(final int page, final int size) {
		Page<Newspaper> result;

		Assert.isTrue(LoginService.isAuthenticated());
		result = this.newspaperRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findTaboos(final int page, final int size) { //Cambiar y hacer su test
		Page<Newspaper> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.newspaperRepository.findTaboos(this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findPublicsPublishedSearch(final String keyword, final int page, final int size) {
		Page<Newspaper> result;

		result = this.newspaperRepository.findPublicsPublishedSearch(keyword, this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findPublishedSearch(final String keyword, final int page, final int size) {
		Page<Newspaper> result;

		Assert.isTrue(LoginService.isAuthenticated());
		result = this.newspaperRepository.findPublishedSearch(keyword, this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> find10PercentageMoreAvg(final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.newspaperRepository.find10PercentageMoreAvg(this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findAddNewspaper(final int volumeId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;
		Volume volume;
		Assert.isTrue(volumeId != 0);

		volume = this.volumeService.findOne(volumeId);
		Assert.notNull(volume);
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(LoginService.getPrincipal().getId() == volume.getUser().getUserAccount().getId());
		result = this.newspaperRepository.findAddNewspaper(volumeId, this.getPageable(page, size));

		return result;

	}

	public Page<Newspaper> findByVolumeAllPublics(final int volumeId, final int page, final int size) {
		Page<Newspaper> result;

		result = this.newspaperRepository.findByVolumeAllPublics(volumeId, this.getPageable(page, size));

		return result;
	}

	public Page<Newspaper> findForSubscribe(final int customerId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		Assert.isTrue(customerId != 0);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == customerId);

		result = this.newspaperRepository.findForSubscribe(customerId, this.getPageable(page, size));

		return result;
	}

	public Page<Newspaper> findNewspaperWithNoAdvertisements(final int agentId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;
		Assert.isTrue(agentId != 0);
		authority = new Authority();
		authority.setAuthority("AGENT");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.newspaperRepository.findNewspapersWithNoAdvertisements(agentId, this.getPageable(page, size));
		return result;
	}

	public Page<Newspaper> findNewspaperWithAdvertisements(final int agentId, final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;
		Assert.isTrue(agentId != 0);
		authority = new Authority();
		authority.setAuthority("AGENT");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.newspaperRepository.findNewspapersWithAdvertisements(agentId, this.getPageable(page, size));
		return result;
	}

	public Collection<Newspaper> findNewspapersToUpdateAdvertisements(final int advertisementId) {
		Collection<Newspaper> result;
		result = this.newspaperRepository.findNewspapersToUpdateAdvertisements(advertisementId);
		return result;
	}

	public void saveFromAdvertisement(final Advertisement advertisement) {
		Collection<Newspaper> newspapers;
		Collection<Advertisement> advertisements;
		newspapers = this.findNewspapersToUpdateAdvertisements(advertisement.getId());
		for (final Newspaper newspaper : newspapers) {
			advertisements = newspaper.getAdvertisements();
			advertisements.remove(advertisement);
			newspaper.setAdvertisements(advertisements);
			this.newspaperRepository.save(newspaper);
		}
	}

	public void addAdvertisementToNewspaper(final int advertisementId, final int newspaperId) {
		Advertisement advertisement;
		Newspaper newspaper;
		Collection<Advertisement> advertisements;
		newspaper = this.findOne(newspaperId);
		advertisement = this.advertisementService.findOne(advertisementId);
		Assert.notNull(advertisement);
		//No lo puede contener
		Assert.isTrue(!newspaper.getAdvertisements().contains(advertisement));
		Assert.isTrue(advertisement.getAgent().getUserAccount().equals(LoginService.getPrincipal()));
		advertisements = newspaper.getAdvertisements();
		advertisements.add(advertisement);
		newspaper.setAdvertisements(advertisements);
		this.newspaperRepository.save(newspaper);
	}

	public void deleteAdvertisementToNewspaper(final int advertisementId, final int newspaperId) {
		Advertisement advertisement;
		Newspaper newspaper;
		Collection<Advertisement> advertisements;
		newspaper = this.findOne(newspaperId);
		advertisement = this.advertisementService.findOne(advertisementId);
		Assert.notNull(advertisement);

		//Lo tiene que contener
		Assert.isTrue(newspaper.getAdvertisements().contains(advertisement));
		Assert.isTrue(advertisement.getAgent().getUserAccount().equals(LoginService.getPrincipal()));
		advertisements = newspaper.getAdvertisements();
		advertisements.remove(advertisement);
		newspaper.setAdvertisements(advertisements);
		this.newspaperRepository.save(newspaper);
	}

	//	public Integer countFindForSubscribe(final int customerId) {
	//		Integer result;
	//
	//		Assert.isTrue(customerId != 0);
	//
	//		result = this.newspaperRepository.countFindForSubscribe(customerId);
	//
	//		return result;
	//	}

	public Double[] avgStandarDevNewspapersCreatedPerUser() {
		Double[] result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.avgStandarDevNewspapersCreatedPerUser();

		return result;
	}

	public Page<Newspaper> find10PercentageLessAvg(final int page, final int size) {
		Page<Newspaper> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.newspaperRepository.find10PercentageLessAvg(this.getPageable(page, size));

		return result;

	}

	public Double ratioPublicVsPrivateNewspaper() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.ratioPublicVsPrivateNewspaper();

		return result;

	}

	public Double ratioPrivateVersusPublicNewspaperPerPublisher() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.ratioPrivateVersusPublicNewspaperPerPublisher();

		return result;

	}

	public Double ratioNewspapersWithOneAdvertisementVsHaventAny() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.ratioNewspapersWithOneAdvertisementVsHaventAny();

		return result;

	}

	public Double averageNewspaperPerVolume() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.newspaperRepository.averageNewspaperPerVolume();

		return result;

	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}

	public Newspaper reconstruct(final Newspaper newspaper, final BindingResult binding) {
		Newspaper result;
		Newspaper aux;

		if (newspaper.getId() == 0)
			result = newspaper;
		else {
			result = newspaper;
			aux = this.newspaperRepository.findOne(newspaper.getId());
			result.setVersion(aux.getVersion());
			result.setTitle(aux.getTitle());
			result.setDescription(aux.getDescription());
			result.setPicture(aux.getPicture());
			result.setIsPrivate(aux.getIsPrivate());
			result.setHasTaboo(aux.getHasTaboo());
			result.setIsPublished(aux.getIsPublished());
			result.setPublisher(aux.getPublisher());
			result.setArticles(aux.getArticles());
			result.setAdvertisements(aux.getAdvertisements());
			result.setPublicationDate(newspaper.getPublicationDate());
		}

		this.validator.validate(result, binding);

		return result;
	}

	public boolean checkTabooWords(final Newspaper newspaper) {
		Collection<String> tabooWords;
		boolean result;

		result = false;
		tabooWords = this.configurationService.findTabooWords();

		for (final String tabooWord : tabooWords) {
			result = newspaper.getTitle() != null && newspaper.getTitle().toLowerCase().contains(tabooWord.toLowerCase()) || newspaper.getDescription() != null && newspaper.getDescription().toLowerCase().contains(tabooWord.toLowerCase());
			if (result == true)
				break;
		}

		return result;
	}

	public boolean canPermit(final int newspaperId) {
		Newspaper newspaper;
		Authority authority;
		Authority authority2;
		Authority authority3;
		Authority authority4;
		Boolean result;
		Date currentMoment;

		newspaper = this.findOne(newspaperId);
		Assert.notNull(newspaper);
		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("CUSTOMER");
		authority3 = new Authority();
		authority3.setAuthority("ADMIN");
		authority4 = new Authority();
		authority4.setAuthority("AGENT");
		currentMoment = new Date();
		result = false;
		if (LoginService.isAuthenticated()) {
			if (LoginService.getPrincipal().getAuthorities().contains(authority)) {
				if (newspaper.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId()) //Lo ves si eres el publisher del periódico
					result = true;
				else if (newspaper.getPublicationDate().compareTo(currentMoment) <= 0 && newspaper.getIsPublished() == true) //O si está publicado
					result = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority2)) {
				if (newspaper.getPublicationDate().compareTo(currentMoment) <= 0 && newspaper.getIsPublished() == true) //SI está publicado
					result = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority4)) {
				if (newspaper.getPublicationDate().compareTo(currentMoment) <= 0 && newspaper.getIsPublished() == true)
					result = true;

			} else if (LoginService.getPrincipal().getAuthorities().contains(authority3)) //El admin lo ve siempre
				result = true;
		} else if (newspaper.getPublicationDate().compareTo(currentMoment) <= 0 && newspaper.getIsPublished() == true && newspaper.getIsPrivate() == false) //Solo ven los publicados y públicos
			result = true;

		return result;

	}

	public void findTaboos() {
		Collection<Newspaper> newspapers;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		newspapers = this.findAll();

		for (final Newspaper newspaper : newspapers)
			if (newspaper.getHasTaboo() == false && this.checkTabooWords(newspaper) == true) {
				newspaper.setHasTaboo(true);
				this.newspaperRepository.save(newspaper);
			} else if (newspaper.getHasTaboo() == true && this.checkTabooWords(newspaper) == false) {
				newspaper.setHasTaboo(false);
				this.newspaperRepository.save(newspaper);
			}

	}

}
