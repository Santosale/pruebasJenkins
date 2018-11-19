
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.UserRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.User;
import forms.UserForm;

@Service
@Transactional
public class UserService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private UserRepository	userRepository;

	// Supporting services-----------------------------------------------------------
	@Autowired
	private Validator		validator;
	
	@Autowired
	private FolderService 	folderService;

	// Constructors -----------------------------------------------------------
	public UserService() {
		super();
	}

	// Simple CRUD
	// methods-----------------------------------------------------------
	public User create() {
		User result;
		UserAccount userAccount;
		Authority authority;
		List<User> followers;
		
		result = new User();
		userAccount = new UserAccount();
		authority = new Authority();
		followers = new ArrayList<User>();

		authority.setAuthority("USER");
		userAccount.addAuthority(authority);

		result.setUserAccount(userAccount);
		result.setFollowers(followers);

		return result;
	}

	public Collection<User> findAll() {
		Collection<User> result;

		result = this.userRepository.findAll();

		return result;
	}

	public User findOne(final int userId) {
		User result;

		Assert.isTrue(userId != 0);

		result = this.userRepository.findOne(userId);

		return result;
	}

	public User save(final User user) {
		User result, saved;
		UserAccount userAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		Assert.notNull(user);
		authority.setAuthority("USER");

		/* Si el user ya existe, debe ser el que este logueado */
		if (user.getId() != 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			Assert.isTrue(userAccount.getAuthorities().contains(authority));
			Assert.isTrue(userAccount.equals(user.getUserAccount()));
			saved = this.userRepository.findOne(user.getId());
			Assert.notNull(saved);
			Assert.isTrue(saved.getUserAccount().getUsername().equals(user.getUserAccount().getUsername()));
			Assert.isTrue(user.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		} else {
			/* Si no existe, debe tratarse de anonimo */
			Assert.isTrue(!LoginService.isAuthenticated());
			user.getUserAccount().setPassword(encoder.encodePassword(user.getUserAccount().getPassword(), null));
		}

		result = this.userRepository.save(user);
		
		//Guardamos los folders por defecto cuando creamos el actor
		if (user.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	// Other business methods
	public User addFollower(final int userToFollowId) {
		User user, userToFollow, result;
		
		user = this.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		userToFollow = this.findOneToDisplay(userToFollowId);
		Assert.notNull(userToFollow);
		
		userToFollow.getFollowers().add(user);
		result = this.save(user);
		
		return result;
	}
	
	public User removeFollower(final int userToFollowId) {
		User user, userToFollow, result;
		
		user = this.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		userToFollow = this.findOneToDisplay(userToFollowId);
		Assert.notNull(userToFollow);
		
		userToFollow.getFollowers().remove(user);
		result = this.save(user);
		
		return result;
	}
	
	public Page<User> findAllPaginated(final int page, final int size) {
		Page<User> result;
				
		result = this.userRepository.findAllPaginated(this.getPageable(page, size));
		
		return result;
	}
	
	public User findOneToDisplay(final int userId) {
		User result;

		Assert.isTrue(userId != 0);

		result = this.userRepository.findOne(userId);
		Assert.notNull(userId);

		return result;
	}
	
	public User findByUserAccountId(final int id) {
		User result;

		Assert.isTrue(id != 0);

		result = this.userRepository.findByUserAccountId(id);

		return result;
	}
	
	public Page<User> findFollowersByUserId(final int userId, final int page, final int size) {		
		Page<User> result;
		
		Assert.isTrue(userId != 0);
		
		result = this.userRepository.findFollowersByUserId(userId, this.getPageable(page, size));
		
		return result;
	}
	
	public Page<User> findFollowedsByUserId(final int userId, final int page, final int size) {		
		Page<User> result;
		
		Assert.isTrue(userId != 0);
		
		result = this.userRepository.findFollowedsByUserId(userId, this.getPageable(page, size));
		
		return result;
	}
	
	public Integer countFollowersByUserId(final int userId) {
		Integer result;
		
		Assert.isTrue(userId != 0);
		
		result = this.userRepository.countFollowersByUserId(userId);
		
		return result;
	}
	
	public Integer countFollowedsByUserId(final int userId) {
		Integer result;
		
		Assert.isTrue(userId != 0);
		
		result = this.userRepository.countFollowedsByUserId(userId);
		
		return result;
	}
	
	
	public Double ratioUserWhoHaveWrittenArticle() {
		Double result;
		
		result = this.userRepository.ratioUserWhoHaveWrittenArticle();
		
		return result;
	}
	
	public Double ratioUserWhoHavePostedAbove75Chirps() {
		Double result;
		
		result = this.userRepository.ratioUserWhoHavePostedAbove75Chirps();
		
		return result;
	}
	
	public Double ratioUsersWhoHaveCreatedNewspaper() {
		Double result;
		
		result = this.userRepository.ratioUsersWhoHaveCreatedNewspaper();
		
		return result;
	}
	
	// Auxiliary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;
		
		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);
		
		return result;
	}

	public User reconstruct(final UserForm userForm, final BindingResult binding) {
		User result;

		if (userForm.getId() == 0) {
			result = this.create();

			Assert.notNull(result);
			Assert.isTrue(userForm.getCheckPassword().equals(userForm.getPassword()));
			Assert.isTrue(userForm.isCheck());

			result.getUserAccount().setUsername(userForm.getUsername());
			result.getUserAccount().setPassword(userForm.getPassword());
			
			userForm.setFollowers(result.getFollowers());
		} else {
			result = this.findOne(userForm.getId());
			Assert.notNull(result);
			Assert.isTrue(result.getUserAccount().getUsername().equals(userForm.getUsername()));
			
			userForm.setFollowers(result.getFollowers());
		}

		result.setName(userForm.getName());
		result.setSurname(userForm.getSurname());
		result.setPostalAddress(userForm.getPostalAddress());
		result.setEmailAddress(userForm.getEmailAddress());
		result.setPhoneNumber(userForm.getPhoneNumber());

		this.validator.validate(userForm, binding);

		return result;
	}

}
