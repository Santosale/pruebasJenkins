
package services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.NoteRepository;
import security.Authority;
import security.LoginService;
import domain.Auditor;
import domain.Manager;
import domain.Note;
import domain.Trip;

@Service
@Transactional
public class NoteService {

	// Managed repository
	@Autowired
	private NoteRepository			noteRepository;

	// Supporting services
	@Autowired
	private ManagerService			managerService;

	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public NoteService() {
		super();
	}

	// Simple CRUD methods
	public Note create(final Auditor auditor, final Trip trip) {
		Note result;

		result = new Note();
		result.setMoment(new Date(System.currentTimeMillis() - 1));
		result.setAuditor(auditor);
		result.setTrip(trip);

		return result;
	}

	public Collection<Note> findAll() {
		Collection<Note> result;

		result = this.noteRepository.findAll();

		return result;
	}

	public Note findOne(final int noteId) {
		Note result;

		Assert.isTrue(noteId != 0);

		result = this.noteRepository.findOne(noteId);

		return result;
	}

	public Note save(final Note note) {
		Note result = null;
		Note saved;

		if (note.getId() != 0) {

			saved = this.findOne(note.getId());

			// Si es la primera vez que metemos esa nota, ni managerReply ni momentReply pueden estar escritos
			// pues solo el manager puede.
			// Si es no es la primera vez que se mete esa nota, managerReply y momentReply no pueden ser nulos
			// porque no se puede modificar salvo que el manager escriba una respuesta.
			// Tambi�n, si es el primero, este debe ser introducido por el auditor que lleve la nota.
			if (saved.getManagerReply() == null && note.getManagerReply() != null) {

				Assert.isTrue(LoginService.getPrincipal().equals(note.getTrip().getManager().getUserAccount()), "note.notEquals.userAccount");

				note.setMomentReply(new Date(System.currentTimeMillis() - 1));

				result = this.noteRepository.save(note);

			} else {
				//				Assert.isTrue(saved.getManagerReply() != null || note.getManagerReply() == null);
				Assert.isTrue(saved.getManagerReply().equals(note.getManagerReply()), "note.notChange.managerReply");
				Assert.isTrue(saved.getAuditor().equals(note.getAuditor()), "note.notChange.auditor");
				Assert.isTrue(saved.getTrip().equals(note.getTrip()), "note.notChange.trip");
				Assert.isTrue(saved.getRemark().equals(note.getRemark()), "note.notChange.remark");
			}

		} else {

			Assert.isTrue(LoginService.getPrincipal().equals(note.getAuditor().getUserAccount()), "note.notEquals.auditorUserAccount");
			Assert.isNull(note.getManagerReply(), "note.null.managerReply");
			Assert.isNull(note.getMomentReply(), "note.null.momentReply");

			note.setMoment(new Date(System.currentTimeMillis() - 1));

			result = this.noteRepository.save(note);

		}

		return result;
	}

	// Other business methods
	public void saveFromTrip(final Trip trip, final int noteId) {
		Note note;

		note = this.findOne(noteId);

		note.setTrip(trip);

		this.noteRepository.save(note);
	}

	public Collection<Note> findByAuditorId(final int auditorId) {
		Collection<Note> result;
		Auditor auditor;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(auditorId != 0);

		auditor = this.auditorService.findOne(auditorId);
		Assert.isTrue(LoginService.getPrincipal().equals(auditor.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.noteRepository.findByAuditorId(auditorId);

		return result;
	}

	public Collection<Note> findByManagerId(final int managerId) {
		Collection<Note> result;

		Assert.isTrue(managerId != 0);

		result = this.noteRepository.findByManagerId(managerId);

		return result;
	}

	public Collection<Note> findByManagerIdAndAuditorId(final int managerId, final int auditorId) {
		Collection<Note> result;
		Manager manager;

		Assert.isTrue(managerId != 0);

		manager = this.managerService.findOne(managerId);
		Assert.notNull(manager);
		Assert.isTrue(LoginService.getPrincipal().equals(manager.getUserAccount()));

		result = this.noteRepository.findByManagerIdAndAuditorId(managerId, auditorId);

		return result;
	}

	public Double[] minMaxAvgStandardDerivationNotePerTrip() {
		Double[] result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.noteRepository.minMaxAvgStandardDerivationNotePerTrip();

		return result;
	}

	//Listar las notes de un trip
	public Collection<Note> findByTripId(final int tripId) {
		Collection<Note> result;

		Assert.isTrue(tripId != 0);
		result = this.noteRepository.findByTripId(tripId);

		return result;
	}

	public boolean checkSpamWords(final Note note) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = note.getManagerReply() != null && note.getManagerReply().toLowerCase().contains(spamWord) || note.getRemark() != null && note.getRemark().toLowerCase().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
