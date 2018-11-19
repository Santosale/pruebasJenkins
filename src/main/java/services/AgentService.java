
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AgentRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Agent;
import forms.AgentForm;

@Service
@Transactional
public class AgentService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private AgentRepository	agentRepository;

	// Supporting services-----------------------------------------------------------
	@Autowired
	private Validator		validator;
	
	@Autowired
	private FolderService 	folderService;

	// Constructors -----------------------------------------------------------
	public AgentService() {
		super();
	}

	// Simple CRUD
	// methods-----------------------------------------------------------
	public Agent create() {
		Agent result;
		UserAccount userAccount;
		Authority authority;

		result = new Agent();
		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("AGENT");
		userAccount.addAuthority(authority);
		result.setUserAccount(userAccount);

		return result;
	}

	public Collection<Agent> findAll() {
		Collection<Agent> result;

		result = this.agentRepository.findAll();

		return result;
	}

	public Agent findOne(final int agentId) {
		Agent result;

		Assert.isTrue(agentId != 0);

		result = this.agentRepository.findOne(agentId);

		return result;
	}

	public Agent save(final Agent agent) {
		Agent result, saved;
		UserAccount userAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		Assert.notNull(agent);
		authority.setAuthority("AGENT");

		/* Si el agent ya existe, debe ser el que este logueado */
		if (agent.getId() != 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			Assert.isTrue(userAccount.getAuthorities().contains(authority));
			Assert.isTrue(userAccount.equals(agent.getUserAccount()));
			saved = this.agentRepository.findOne(agent.getId());
			Assert.notNull(saved);
			Assert.isTrue(saved.getUserAccount().getUsername().equals(agent.getUserAccount().getUsername()));
			Assert.isTrue(agent.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		} else {
			/* Si no existe, debe tratarse de anonimo */
			Assert.isTrue(!LoginService.isAuthenticated());
			agent.getUserAccount().setPassword(encoder.encodePassword(agent.getUserAccount().getPassword(), null));
		}

		result = this.agentRepository.save(agent);
		
		//Guardamos los folders por defecto cuando creamos el actor
		if (agent.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	// Other business methods
	public Agent findByUserAccountId(final int id) {
		Agent result;

		Assert.isTrue(id != 0);

		result = this.agentRepository.findByUserAccountId(id);

		return result;
	}

	public Agent reconstruct(final AgentForm agentForm, final BindingResult binding) {
		Agent result;

		if (agentForm.getId() == 0) {
			result = this.create();

			Assert.notNull(result);
			Assert.isTrue(agentForm.getCheckPassword().equals(agentForm.getPassword()));
			Assert.isTrue(agentForm.isCheck());

			result.getUserAccount().setUsername(agentForm.getUsername());
			result.getUserAccount().setPassword(agentForm.getPassword());
		} else {
			result = this.findOne(agentForm.getId());
			Assert.notNull(result);
			Assert.isTrue(result.getUserAccount().getUsername().equals(agentForm.getUsername()));
		}

		result.setName(agentForm.getName());
		result.setSurname(agentForm.getSurname());
		result.setPostalAddress(agentForm.getPostalAddress());
		result.setEmailAddress(agentForm.getEmailAddress());
		result.setPhoneNumber(agentForm.getPhoneNumber());

		this.validator.validate(agentForm, binding);

		return result;
	}

}
