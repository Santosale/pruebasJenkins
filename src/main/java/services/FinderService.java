
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.FinderRepository;
import security.Authority;
import security.LoginService;
import domain.Explorer;
import domain.Finder;
import domain.Trip;

@Service
@Transactional
public class FinderService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private FinderRepository		finderRepository;

	// Supporting services
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ExplorerService			explorerService;


	// Constructors -----------------------------------------------------------
	public FinderService() {
		super();
	}

	// Simple CRUD methods
	// -----------------------------------------------------------

	// En los requisitos pone que solo puede ser accedido por el Explorer.

	public Finder create(final Explorer explorer) {
		Finder result;
		List<Trip> trips;
		Calendar calendar;
		
		trips = new ArrayList<Trip>();
		result = new Finder();
		
		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
		calendar.getTime().setTime(calendar.getTimeInMillis() - 1);

		result.setTrips(trips);
		result.setMoment(calendar.getTime());
		result.setKeyWord(null);
		result.setStartedDate(null);
		result.setFinishedDate(null);
		result.setMinPrice(null);
		result.setMaxPrice(null);
		result.setExplorer(explorer);

		return result;
	}

	public Collection<Finder> findAll() {
		Collection<Finder> result;

		result = this.finderRepository.findAll();

		return result;
	}

	public Finder findOne(final int finderId) {
		Finder result;

		Assert.isTrue(finderId != 0);

		result = this.finderRepository.findOne(finderId);

		return result;
	}

	public Finder save(final Finder finder) {
		Finder result;
		Finder saved;
		Explorer explorer;
		Calendar calendar; 
		Collection<Trip> tripsResult;
		
		Assert.notNull(finder);

		if (finder.getId() != 0) {
			explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(explorer);
			Assert.isTrue(explorer.equals(finder.getExplorer()));
		}
				
		saved = this.finderRepository.findByExplorerId(finder.getExplorer().getId());
	
		if (finder.getId() == 0 || saved.getMoment().getTime() + this.configurationService.findCachedTime() * 3600000 < System.currentTimeMillis() || !this.compareFinder(saved, finder)){
					
			tripsResult = this.tripService.findByPriceRangeAndDateRangeAndKeyword(finder.getMaxPrice(), finder.getMinPrice(), finder.getStartedDate(), finder.getFinishedDate(), finder.getKeyWord());
	
			finder.setTrips(tripsResult);
			
			calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
			calendar.getTime().setTime(calendar.getTimeInMillis() - 1);
			finder.setMoment(calendar.getTime());
			
			result = this.finderRepository.save(finder);
		
		}else
			result = saved;

		return result;
	}

	public void delete(final Finder finder) {
		Assert.notNull(finder);

		this.finderRepository.delete(finder);
	}

	// newFinder business methods

	public Collection<Finder> findByTripId(final int tripId) {
		Collection<Finder> result;

		result = this.finderRepository.findByTripId(tripId);

		return result;
	}

	public void saveFromTrip(final Finder finder) {
		Assert.notNull(finder);

		this.finderRepository.save(finder);
	}

	public Finder findByExplorerId(final int explorerId) {
		Finder result;
		Explorer explorer;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(explorerId != 0);
		explorer = this.explorerService.findOne(explorerId);
		Assert.notNull(explorer);
		Assert.isTrue(LoginService.getPrincipal().equals(explorer.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));
		
		result = this.finderRepository.findByExplorerId(explorerId);

		return result;
	}

	public boolean checkSpamWords(final Finder finder) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = finder.getKeyWord() != null && finder.getKeyWord().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}
	
	public boolean compareFinder(final Finder oldFinder, final Finder newFinder) {
		if (oldFinder.getExplorer() == null) {
			if (newFinder.getExplorer() != null)
				return false;
		} else if (!oldFinder.getExplorer().equals(newFinder.getExplorer()))
			return false;
		if (oldFinder.getFinishedDate() == null) {
			if (newFinder.getFinishedDate() != null)
				return false;
		} else if (!oldFinder.getFinishedDate().equals(newFinder.getFinishedDate()))
			return false;
		if (oldFinder.getKeyWord() == null) {
			if (newFinder.getKeyWord() != null)
				return false;
		} else if (!oldFinder.getKeyWord().equals(newFinder.getKeyWord()))
			return false;
		if (oldFinder.getMaxPrice() == null) {
			if (newFinder.getMaxPrice() != null)
				return false;
		} else if (!oldFinder.getMaxPrice().equals(newFinder.getMaxPrice()))
			return false;
		if (oldFinder.getMinPrice() == null) {
			if (newFinder.getMinPrice() != null)
				return false;
		} else if (!oldFinder.getMinPrice().equals(newFinder.getMinPrice()))
			return false;
		if (oldFinder.getMoment() == null) {
			if (newFinder.getMoment() != null)
				return false;
		} else if (!oldFinder.getMoment().equals(newFinder.getMoment()))
			return false;
		if (oldFinder.getStartedDate() == null) {
			if (newFinder.getStartedDate() != null)
				return false;
		} else if (!oldFinder.getStartedDate().equals(newFinder.getStartedDate()))
			return false;
		if (oldFinder.getTrips() == null) {
			if (newFinder.getTrips() != null)
				return false;
		} else if (!oldFinder.getTrips().equals(newFinder.getTrips()))
			return false;
		return true;
	}

}
