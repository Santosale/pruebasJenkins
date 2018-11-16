
package services;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.LegalTextRepository;
import security.LoginService;
import security.UserAccount;
import domain.Administrator;
import domain.LegalText;

@Service
@Transactional
public class LegalTextService {

	// Constructors -----------------------------------------------------------
	public LegalTextService() {
		super();
	}


	// Managed repository -----------------------------------------------------
	@Autowired
	private LegalTextRepository		legalTextRepository;

	// Supporting services
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private AdministratorService	administratorService;


	// Simple CRUD methods
	// -----------------------------------------------------------
	public LegalText create() {
		LegalText result;

		result = new LegalText();
		result.setLaws(new HashSet<String>());
		result.setMoment(new Date());

		return result;
	}

	public Collection<LegalText> findAll() {
		Collection<LegalText> result;

		result = this.legalTextRepository.findAll();

		return result;
	}

	public LegalText findOne(final int legalTextId) {
		LegalText result;

		Assert.isTrue(legalTextId != 0);

		result = this.legalTextRepository.findOne(legalTextId);

		return result;
	}

	public LegalText save(final LegalText legalText) {
		final LegalText result;
		Administrator administrator;
		LegalText saved;
		UserAccount userAccount;
		Calendar calendar;

		Assert.notNull(legalText, "legalText.not.null");

		userAccount = LoginService.getPrincipal();
		administrator = this.administratorService.findByUserAccountId(userAccount.getId());

		Assert.notNull(administrator, "legalText.edit.administrator");

		if (legalText.getId() == 0) {
			calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
			calendar.getTime().setTime(calendar.getTimeInMillis() - 1);
			legalText.setMoment(calendar.getTime());

		} else {

			saved = this.findOne(legalText.getId());

			//El objeto que estaba guardado debe existir
			Assert.notNull(saved, "legalText.try.edit.administrator");

			//Si se esta actualizando el moment es igual

			Assert.isTrue(legalText.getMoment().compareTo(saved.getMoment()) == 0, "legalText.edit.moment");
			//Si se esta actualizando, el anterior debia ser draft
			Assert.isTrue(saved.getDraft(), "legalText.save.draft");
		}
		result = this.legalTextRepository.save(legalText);

		return result;
	}

	public void delete(final LegalText legalText) {
		Administrator administrator;
		UserAccount userAccount;
		LegalText saved;

		Assert.notNull(legalText, "legalText.not.null");

		userAccount = LoginService.getPrincipal();
		administrator = this.administratorService.findByUserAccountId(userAccount.getId());

		Assert.notNull(administrator, "legalText.edit.administrator");
		saved = this.findOne(legalText.getId());
		Assert.notNull(saved, "legalText.try.edit.administrator");
		Assert.isTrue(saved.getDraft(), "legalText.save.draft");

		this.legalTextRepository.delete(legalText);
	}

	// Other business methods -------------------------------------------------
	public Collection<LegalText> findFinalMode() {
		Collection<LegalText> result;

		result = this.legalTextRepository.findFinalMode();

		return result;
	}

	public boolean checkSpamWords(final LegalText legalText) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = legalText.getBody() != null && legalText.getBody().contains(spamWord) || legalText.getTitle() != null && legalText.getTitle().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}
}
