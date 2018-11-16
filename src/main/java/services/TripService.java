
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.TripRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Audit;
import domain.Finder;
import domain.LegalText;
import domain.Manager;
import domain.Sponsorship;
import domain.Stage;
import domain.Story;
import domain.SurvivalClass;
import domain.TagValue;
import domain.Trip;

@Service
@Transactional
public class TripService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private TripRepository			tripRepository;

	//Supporting services -----------------------------------------------------------

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private TagValueService			tagValueService;

	@Autowired
	private NoteService				noteService;

	@Autowired
	private AuditService			auditService;

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private StoryService			storyService;

	@Autowired
	private FinderService			finderService;


	// Constructors -----------------------------------------------------------
	public TripService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Trip create(final Manager manager) {
		Trip result;
		List<Stage> stages;
		String ticker;

		result = new Trip();
		//Create ticker
		ticker = "000000-AAAA";
		result.setTicker(ticker);
		stages = new ArrayList<Stage>();
		result.setStages(stages);
		result.setManager(manager);
		result.setPrice(0.0);

		return result;
	}

	public Collection<Trip> findAll() {
		Collection<Trip> result;

		result = this.tripRepository.findAll();

		return result;
	}

	public Page<Trip> findAll(final Integer pageNumber, final Integer pageSize) {
		Page<Trip> result;
		PageRequest pageRequest;

		pageRequest = new PageRequest(pageNumber - 1, pageSize);
		result = this.tripRepository.findAll(pageRequest);

		return result;
	}

	public Trip findOne(final int tripId) {
		Trip result;

		Assert.isTrue(tripId != 0);

		//Vemos que solo pueda acceder al viaje si está publicado 
		result = this.tripRepository.findOne(tripId);

		//Si nos devuelve un viaje y no esta autenticado, el viaje debe estar publicado
		if (result != null && result.getPublicationDate().compareTo(new Date()) > 0)
			Assert.isTrue(LoginService.getPrincipal().equals(result.getManager().getUserAccount()));
		//Si esta autenticado, vemos que sea el propio manager
		else if (result != null)
			Assert.isTrue(result.getPublicationDate().compareTo(new Date()) <= 0);
		return result;
	}

	public Trip save(final Trip trip) {
		Trip result;
		Date currentMoment;
		UserAccount userAccount;
		Authority authority;
		Double price;
		Trip saved;
		Double priceDouble;

		Assert.notNull(trip);
		currentMoment = new Date();
		//Miramos si se esta creando el trip que el ticker sea unico
		if (trip.getId() != 0) {
			//Miramos si ya existe el trip. El trip nuevo debe tener el mismo ticker que el guardado
			saved = this.tripRepository.findOne(trip.getId());
			Assert.notNull(saved, "trip.not.null");
			Assert.isTrue(saved.getTicker().equals(trip.getTicker()), "trip.equals.ticker");
		}

		if (trip.getId() == 0)
			trip.setTicker("000000-AAAA");

		//Vemos quien esta autenticado
		authority = new Authority();
		authority.setAuthority("MANAGER");
		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount, "trip.userAccount.not.null");
		//Solo su manager puede cambiarlo
		Assert.isTrue(trip.getManager().getUserAccount().equals(userAccount), "trip.equals.manager");

		//Fecha inicio anterior a la de fin
		Assert.isTrue(trip.getStartDate().compareTo(trip.getEndDate()) < 0, "trip.start.end.date");

		//if (trip.getId() != 0)
		//Despues de la fecha de publicacion no se puede modificar 
		Assert.isTrue(trip.getPublicationDate().compareTo(currentMoment) > 0, "trip.publication.current");

		//Fecha inicio posterior a la fecha de publicacion
		Assert.isTrue(trip.getStartDate().compareTo(trip.getPublicationDate()) > 0, "trip.start.publication");

		//Vemos que el viaje sea cancelado correctamente
		Assert.isTrue(trip.getCancellationReason() == null, "trip.cancellation.null");

		//El legal text debe estar en NO draft mode
		Assert.isTrue(!trip.getLegalText().getDraft(), "trip.legaltext.no.draft");

		//Autocalcular precio . Se calcula siempre
		price = 0.0;
		for (final Stage stage : trip.getStages())
			price += stage.getPrice();

		priceDouble = price + (price * this.configurationService.findVat() / 100);
		priceDouble = new Double(String.format("%.2f", priceDouble));

		// Cambiamos el precio y evitamos que sobrepase la cantidad fijada
		Assert.isTrue(priceDouble <= 99999.99, "trip.price.lower");
		trip.setPrice(priceDouble);

		result = this.tripRepository.save(trip);

		//Si es la primera vez calculamos el ticker
		if (trip.getId() == 0)
			result.setTicker(this.createTicker(result.getId()));

		return result;

	}

	public void delete(final Trip trip) {
		Date currentMoment;
		UserAccount userAccount;

		Assert.notNull(trip, "trip.not.null");
		currentMoment = new Date();

		//Si tiene notes no se puede borrar
		Assert.isTrue(this.noteService.findByTripId(trip.getId()).size() == 0, "trip.no.note");

		//Se puede borrar si aún no se ha publicado
		Assert.isTrue(trip.getPublicationDate().compareTo(currentMoment) > 0, "trip.publication.current");
		userAccount = LoginService.getPrincipal();

		//Solo su manager puede borrarlo
		Assert.isTrue(trip.getManager().getUserAccount().equals(userAccount), "trip.equals.manager");

		//Borramos las applications
		for (final Application application : this.applicationService.findByTripId(trip.getId()))
			this.applicationService.deleteFromTrip(application.getId());

		//Borramos las survivalClass
		for (final SurvivalClass survivalClass : this.survivalClassService.findByTripId(trip.getId()))
			this.survivalClassService.delete(survivalClass);

		//Borramos los tagValues
		for (final TagValue tagValue : this.tagValueService.findByTripId(trip.getId()))
			this.tagValueService.delete(tagValue);

		//Borramos los audits
		for (final Audit audit : this.auditService.findByTripId(trip.getId()))
			this.auditService.deleteFromTrip(audit.getId());

		//Borramos los sponsorship
		for (final Sponsorship sponsosrship : this.sponsorshipService.findByTripId(trip.getId()))
			this.sponsorshipService.deleteFromTrip(sponsosrship.getId());

		//Borramos las stories
		for (final Story story : this.storyService.findByTripId(trip.getId()))
			this.storyService.deleteFromTrip(story.getId());

		//Actualizamos el finder
		for (final Finder finder : this.finderService.findByTripId(trip.getId())) {
			finder.getTrips().remove(trip);
			this.finderService.saveFromTrip(finder);
		}

		this.tripRepository.delete(trip);

	}
	//Other business methods -------------------------------------------------

	public void cancellTrip(final int tripId, final String cancellationReason) {
		Trip saved;
		Date currentMoment;
		UserAccount userAccount;

		Assert.isTrue(tripId != 0);
		Assert.notNull(cancellationReason);
		Assert.isTrue(!cancellationReason.trim().equals(""));
		saved = this.findOne(tripId);
		Assert.notNull(saved);
		userAccount = LoginService.getPrincipal();

		//Solo su manager puede cancelarlo
		Assert.isTrue(saved.getManager().getUserAccount().equals(userAccount));

		currentMoment = new Date();

		//Miramos si se ha publicado y si no ha empezado aún
		Assert.isTrue(saved.getPublicationDate().compareTo(currentMoment) < 0 && saved.getStartDate().compareTo(currentMoment) > 0);
		saved.setCancellationReason(cancellationReason);

		this.tripRepository.save(saved);

	}

	//Publicar un trip de forma instantanea si tiene stages.
	public void publishTrip(final int tripId) {
		Trip trip;

		trip = this.tripRepository.findOne(tripId);
		Assert.notNull(trip);
		Assert.isTrue(trip.getPublicationDate().compareTo(new Date()) > 0);
		Assert.isTrue(!trip.getStages().isEmpty());

		trip.setPublicationDate(new Date());

		this.tripRepository.save(trip);
	}

	public Collection<Trip> findTenPerCentMoreApplicationThanAverage() {
		Collection<Trip> result;
		Authority authority;
		List<Object[]> repositoryAnswer;

		result = new ArrayList<Trip>();

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		repositoryAnswer = this.tripRepository.findTenPerCentMoreApplicationThanAverage();

		for (final Object[] aux2 : repositoryAnswer)
			result.add((Trip) aux2[0]);

		return result;
	}

	public Double ratioTripCancelledVsTotal() {
		Double result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.tripRepository.ratioTripCancelledVsTotal();

		return result;
	}

	public Double ratioTripOneAuditRecordVsTotal() {
		Double result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.tripRepository.ratioTripOneAuditRecordVsTotal();

		return result;
	}

	//Listamos todos los viajes visibles
	public Collection<Trip> findAllVisible() {
		Collection<Trip> result;

		result = this.tripRepository.findAllVisible();

		return result;
	}

	public Page<Trip> findAllVisible(final Integer pageNumber, final Integer pageSize) {
		Page<Trip> result;
		PageRequest pageRequest;

		pageRequest = new PageRequest(pageNumber - 1, pageSize);
		result = this.tripRepository.findAllVisible(pageRequest);

		return result;
	}

	//Listar las categories de un trip
	public Collection<Trip> findByCategoryId(final int categoryId) {
		Collection<Trip> result;

		Assert.isTrue(categoryId != 0);
		result = this.tripRepository.findByCategoryId(categoryId);

		return result;
	}

	public Collection<Trip> findByCategoryIdAllTrips(final int categoryId) {
		Collection<Trip> result;

		Assert.isTrue(categoryId != 0);
		result = this.tripRepository.findByCategoryIdAllTrips(categoryId);

		return result;
	}

	//Buscador trips user no autenticados
	public Collection<Trip> findByKeyWord(String keyWord) {
		Collection<Trip> result;

		//Si es null devolvemos todos los viajes
		if (keyWord == null)
			keyWord = " ";

		result = this.tripRepository.findByKeyWord(keyWord);

		return result;
	}
	//Dashboard Administrator
	public Double[] avgMinMaxStandardDTripsPerManager() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.tripRepository.avgMinMaxStandardDTripsPerManager();

		return result;
	}

	public Double[] avgMinMaxStandardDPriceOfTrips() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.tripRepository.avgMinMaxStandardDPriceOfTrips();

		return result;
	}

	public Double[] avgMinMaxStandardDTripsPerRanger() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.tripRepository.avgMinMaxStandardDTripsPerRanger();

		return result;
	}

	//Listar los trips de un legalText
	public Collection<Trip> findByLegalTextId(final int legalTextId) {
		Collection<Trip> result;

		Assert.isTrue(legalTextId != 0);
		result = this.tripRepository.findByLegalTextId(legalTextId);

		return result;
	}

	public Map<LegalText, Long> countLegalTextReferences() {
		Map<LegalText, Long> result;
		Authority authority;
		List<Object[]> repositoryAnswer;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		repositoryAnswer = this.tripRepository.countLegalTextReferences();

		result = new HashMap<LegalText, Long>(repositoryAnswer.size());

		for (final Object[] aux2 : repositoryAnswer)
			result.put((LegalText) aux2[0], (Long) aux2[1]);

		return result;
	}

	//Listar los viajes de un manager
	public Collection<Trip> findByManagerUserAccountId(final int managerUserAccountId) {
		Collection<Trip> result;

		Assert.isTrue(managerUserAccountId != 0);
		result = this.tripRepository.findByManagerUserAccountId(managerUserAccountId);

		return result;
	}

	//Listar los viajes de un ranger
	public Collection<Trip> findByRangerUserAccountId(final int rangerUserAccountId) {
		Collection<Trip> result;

		Assert.isTrue(rangerUserAccountId != 0);
		result = this.tripRepository.findByRangerUserAccountId(rangerUserAccountId);

		return result;
	}

	//Consultar Trip visible
	public Trip findOneVisible(final int tripId) {
		Trip result;

		Assert.isTrue(tripId != 0);

		result = this.tripRepository.findOne(tripId);

		//Vemos que solo pueda acceder al viaje si está publicado 
		if (result != null && result.getPublicationDate().compareTo(new Date()) > 0)
			result = null;

		return result;
	}

	public boolean checkSpamWords(final Trip trip) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (trip.getCancellationReason() != null && trip.getCancellationReason().toLowerCase().contains(spamWord)) || (trip.getDescription() != null && trip.getDescription().toLowerCase().contains(spamWord))
				|| (trip.getExplorerRequirements() != null && trip.getExplorerRequirements().toLowerCase().contains(spamWord)) || trip.getTitle().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

	private String createTicker(int tripId) {
		String result = "";
		Calendar calendar;
		String day;
		String month;
		String year;

		calendar = Calendar.getInstance();
		//Calculamos año
		year = Integer.toString(calendar.get(Calendar.YEAR));
		year = year.substring(2, 4);

		//Calculamos mes
		month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;

		//Calculamos el dia
		day = Integer.toString(calendar.get(Calendar.DATE));
		if (day.length() == 1)
			day = "0" + day;

		result += year + month + day;
		result += "-";
		final char[] chr = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'Ñ', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
		};

		tripId %= Math.pow(chr.length, 4);

		for (int i = 0; i <= 3; i++) {
			result += chr[tripId % chr.length];
			tripId = (tripId - tripId % chr.length) / chr.length;
		}

		return result;
	}

	public void saveFromCategory(final Trip trip) {
		UserAccount userAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.notNull(trip);
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(userAccount.getAuthorities().contains(authority));

		this.tripRepository.save(trip);
	}

	public Collection<Trip> findByPriceRangeAndDateRangeAndKeyword(Double maxPrice, Double minPrice, Date startDate, Date finishDate, String keyWord) {
		Page<Trip> result;
		Date startDateDefault;
		Date finishDateDefault;
		SimpleDateFormat format;
		String dateInString;
		String dateInString2;
		PageRequest request;

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "31/12/2999";
		dateInString2 = "01/01/0000";

		finishDateDefault = new Date();
		startDateDefault = new Date();

		try {
			finishDateDefault = format.parse(dateInString);
			startDateDefault = format.parse(dateInString2);
		} catch (final ParseException e) {

		}

		//Comprobamos todos los valores y los ajustamos a la búsqueda
		if (maxPrice == null)
			maxPrice = 9999.9;

		if (minPrice == null)
			minPrice = 0.0;

		if (startDate == null)
			startDate = startDateDefault;

		if (finishDate == null)
			finishDate = finishDateDefault;

		if (keyWord == null)
			keyWord = " ";

		request = new PageRequest(0, this.configurationService.findFinderNumber());

		result = this.tripRepository.findByPriceRangeAndDateRangeAndKeyword(maxPrice, minPrice, startDate, finishDate, keyWord, request);

		return result.getContent();
	}

}
