
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.AuditRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Audit;
import domain.Auditor;
import domain.Trip;

@Service
@Transactional
public class AuditService {

	//Managed repository ---------------

	@Autowired
	private AuditRepository			auditRepository;

	//Supporting service--------------
	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ConfigurationService	configurationService;


	//Constructor---------------
	public AuditService() {
		super();
	}

	//Simple CRUD Methods -------------------

	public Audit create(final Auditor auditor, final Trip trip) {
		Audit result;
		List<String> attachments;

		attachments = new ArrayList<String>();

		Assert.notNull(auditor);
		Assert.notNull(trip);

		result = new Audit();
		result.setAttachments(attachments);
		result.setAuditor(auditor);
		result.setTrip(trip);
		result.setDraft(true);

		return result;
	}

	public Collection<Audit> findAll() {
		Collection<Audit> result;

		result = this.auditRepository.findAll();

		return result;
	}

	public Audit findOne(final int auditId) {
		Audit result;

		Assert.isTrue(auditId != 0);
		result = this.auditRepository.findOne(auditId);

		return result;
	}

	public Audit save(final Audit audit) {
		Audit result;
		Audit saved;
		UserAccount userAccount;
		Date currentMoment;

		Assert.notNull(audit, "audit.not.null");
		currentMoment = new Date();
		Assert.isTrue(audit.getMoment().compareTo(currentMoment) < 0, "audit.moment.past");

		userAccount = LoginService.getPrincipal();
		saved = this.auditRepository.findOne(audit.getId());
		if (saved == null)
			Assert.isNull(this.findByTripIdAndAuditorId(audit.getTrip().getId(), audit.getAuditor().getId()), "audit.auditor.trip"); //Mira que solo un auditor haga un audit para un mismo trip

		// Si la nota ya guardada esta como final
		if (audit.getId() != 0) {
			Assert.isTrue(saved.isDraft(), "audit.draft.yes"); //Si va a modificar la nota de la BD debe ser draft
			Assert.isTrue(this.auditRepository.findOne(audit.getId()).getAuditor().equals(audit.getAuditor()), "audit.equals.auditor"); //No puede cambiar el auditor
			Assert.isTrue(this.auditRepository.findOne(audit.getId()).getTrip().equals(audit.getTrip()), "audit.equals.trip"); //no puede cambiar el trip
		}
		Assert.isTrue(userAccount.equals(audit.getAuditor().getUserAccount()), "audit.auditor.change"); //Solo el auditor de ese audit puede guardarlo

		result = this.auditRepository.save(audit);// Se guarda el nuevo audit en application

		return result;
	}

	public void delete(final Audit audit) {
		UserAccount userAccount;
		Audit saved;

		userAccount = LoginService.getPrincipal();

		Assert.notNull(audit, "audit.not.null");
		Assert.isTrue(userAccount.equals(audit.getAuditor().getUserAccount()), "audit.auditor.change"); //Solo puede birrarlo su auditor
		saved = this.findOne(audit.getId());
		Assert.notNull(saved, "audit.not.null");
		Assert.isTrue(saved.isDraft(), "audit.draft.yes");

		this.auditRepository.delete(audit);

	}

	//Other business methods
	public void deleteFromTrip(final int auditId) {
		Audit audit;
		Assert.isTrue(auditId != 0);
		audit = this.findOne(auditId);
		this.auditRepository.delete(audit);

	}

	public Collection<Audit> findByAuditorId(final int auditorId) {
		Collection<Audit> result;
		Auditor saved;
		UserAccount userAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(auditorId != 0);
		userAccount = LoginService.getPrincipal();
		saved = this.auditorService.findOne(auditorId);
		if (saved != null)
			Assert.isTrue(userAccount.equals(saved.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority)); //Solo puede hacerlo el auditor
		result = this.auditRepository.findAuditsByAuditorId(auditorId);
		return result;
	}

	//Listar los audits de un trip
	public Collection<Audit> findByTripId(final int tripId) {
		Collection<Audit> result;
		//final UserAccount userAccount;

		Assert.isTrue(tripId != 0);
		//		userAccount = LoginService.getPrincipal();
		//		Assert.isTrue(userAccount.equals(this.tripService.findOne(tripId).getManager().getUserAccount())); //Solo el manager del trip
		result = this.auditRepository.findByTripId(tripId);

		return result;
	}

	public Audit findByTripIdAndAuditorId(final int tripId, final int auditorId) {
		Audit result;
		UserAccount userAccount;
		UserAccount managerUserAccount;
		UserAccount auditorUserAccount;

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(tripId != 0);
		Assert.isTrue(auditorId != 0);

		result = this.auditRepository.findAuditByTripIdAndAuditorId(tripId, auditorId);
		if (result != null) {
			managerUserAccount = this.tripService.findOne(tripId).getManager().getUserAccount();
			auditorUserAccount = this.auditorService.findOne(auditorId).getUserAccount();
			Assert.isTrue(managerUserAccount.equals(userAccount) || auditorUserAccount.equals(userAccount)); //Solo puede usar el metodo el manager del trip o el auditor
		}
		return result;
	}

	public Double[] minMaxAvgStandardDAuditsPerTrip() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.auditRepository.minMaxAvgStandardDAuditsByTrip();

		return result;
	}

	public boolean checkSpamWords(final Audit audit) {
		Collection<String> spamWords;
		boolean result;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = audit.getTitle() != null && audit.getTitle().toLowerCase().contains(spamWord) || audit.getDescription() != null && audit.getDescription().toLowerCase().contains(spamWord);
			if (result == true)
				break;
		}
		return result;
	}
}
