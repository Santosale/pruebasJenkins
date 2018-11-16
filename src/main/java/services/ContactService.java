
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ContactRepository;
import security.Authority;
import security.LoginService;
import domain.Contact;
import domain.Explorer;

@Service
@Transactional
public class ContactService {

	// Managed repository
	@Autowired
	private ContactRepository		contactRepository;

	// Supporting Services----------
	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor----------
	public ContactService() {
		super();
	}

	// Simple CRUD methods----------
	public Contact create(final Explorer explorer) {
		Contact result;
		Authority authority;

		Assert.notNull(explorer);
		authority = new Authority();
		authority.setAuthority("EXPLORER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = new Contact();
		result.setExplorer(explorer);

		return result;
	}

	public Collection<Contact> findAll() {
		Collection<Contact> result;

		result = this.contactRepository.findAll();

		return result;
	}

	public Contact findOne(final int contactId) {
		Contact result;

		Assert.isTrue(contactId != 0);

		result = this.contactRepository.findOne(contactId);

		return result;
	}
	public Contact save(final Contact contact) {
		Contact result;
		Authority authority;
		Explorer explorer;

		Assert.notNull(contact);

		authority = new Authority();
		authority.setAuthority("EXPLORER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		if (contact.getId() != 0)
			// modificarla un explorer que la tenga
			Assert.isTrue(this.findByExplorerId(explorer.getId()).contains(contact));
		else
			Assert.isTrue(explorer.equals(contact.getExplorer()));

		Assert.isTrue((contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) || (contact.getPhone() != null && !contact.getPhone().trim().isEmpty()));

		result = this.contactRepository.save(contact);

		return result;
	}

	public void delete(final Contact contact) {
		Explorer explorer;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(contact);
		Assert.notNull(explorer);
		Assert.isTrue(this.contactRepository.findByExplorerId(explorer.getId()).contains(contact));

		this.contactRepository.delete(contact);

	}

	// Other business methods
	public Collection<Contact> findByExplorerId(final int explorerId) {
		List<Contact> result;
		Explorer explorer;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		result = new ArrayList<Contact>();
		Assert.isTrue(explorerId != 0);
		explorer = this.explorerService.findOne(explorerId);
		if (explorer != null) {
			Assert.isTrue(LoginService.getPrincipal().equals(explorer.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));
			result.addAll(this.contactRepository.findByExplorerId(explorerId));
		}

		return result;

	}

	public boolean checkSpamWords(final Contact contact) {
		Collection<String> spamWords;
		boolean result;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = contact.getName() != null && contact.getName().contains(spamWord) || contact.getEmail() != null && contact.getEmail().contains(spamWord);
			if (result == true)
				break;
		}
		return result;
	}

}
