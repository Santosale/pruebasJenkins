
package services;

import java.util.ArrayList;
import java.util.Collection;

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
import domain.Actor;
import domain.Bargain;
import domain.Plan;
import domain.User;
import forms.UserForm;

@Service
@Transactional
public class UserService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private UserRepository	userRepository;
	
	@Autowired
	private BargainService bargainService;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private PlanService		planService;

	@Autowired
	private Validator		validator;


	// Supporting
	// services-----------------------------------------------------------

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

		result = new User();
		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("USER");
		userAccount.addAuthority(authority);

		result.setUserAccount(userAccount);

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
		String defaultAvatar;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		Assert.notNull(user);
		authority.setAuthority("MODERATOR");

		/* Si el user ya existe, debe ser el que este logueado */
		if (user.getId() != 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			Assert.isTrue(userAccount.getAuthorities().contains(authority) || user.getUserAccount().equals(LoginService.getPrincipal()));
			saved = this.userRepository.findOne(user.getId());
			Assert.notNull(saved);
			Assert.isTrue(saved.getUserAccount().getUsername().equals(user.getUserAccount().getUsername()));
			Assert.isTrue(user.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		} else {
			/* Si no existe, debe tratarse de anonimo */
			Assert.isTrue(!LoginService.isAuthenticated());
			user.getUserAccount().setPassword(encoder.encodePassword(user.getUserAccount().getPassword(), null));
		}
		
		defaultAvatar = this.configurationService.findDefaultAvatar();
		Assert.notNull(defaultAvatar);
		if(user.getAvatar() == null || user.getAvatar() != null && user.getAvatar() == "") user.setAvatar(defaultAvatar);

		result = this.userRepository.save(user);

		return result;
	}

	// Other business methods
	public void flush() {
		this.userRepository.flush();
	}
	
	public void updateFromBargain(final Bargain bargain) {
		Collection<User> users;
		Authority authority;
		
		Assert.notNull(bargain);
		
		authority = new Authority();
		authority.setAuthority("COMPANY");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		Assert.isTrue(bargain.getCompany().getUserAccount().equals(LoginService.getPrincipal()));
		
		users = this.userRepository.findByBargainId(bargain.getId());
		Assert.notNull(users);
		
		for(User u: users) {
			u.getWishList().remove(bargain);
			this.userRepository.save(u);
		}
		
	}
	
	public User findByUserAccountId(final int id) {
		User result;

		Assert.isTrue(id != 0);

		result = this.userRepository.findByUserAccountId(id);

		return result;
	}
	
	public User findByUserAccountIdToEdit(final int id) {
		User result;

		Assert.isTrue(id != 0);

		result = this.userRepository.findByUserAccountId(id);
		Assert.notNull(result);
		
		Assert.isTrue(result.getUserAccount().equals(LoginService.getPrincipal()));

		return result;
	}

	public Page<User> findAllPaginated(final int page, final int size) {
		Page<User> result;

		result = this.userRepository.findAllPageable(this.getPageable(page, size));

		return result;
	}
	
	public Page<User> findOrderByPoints(final int page, final int size) {
		Page<User> result;

		result = this.userRepository.findOrderByPoints(this.getPageable(page, size));

		return result;
	}
	
	public void addPoints(final int points) {
		User user;
		
		Assert.isTrue(LoginService.isAuthenticated());
		
		user = this.userRepository.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		user.setPoints(user.getPoints()+points);
		
		this.save(user);
	}
	
	public void addPoints(final User user, final int points) {		
		Assert.isTrue(LoginService.isAuthenticated());
		
		user.setPoints(user.getPoints()+points);
		
		this.userRepository.save(user);
	}
	
	public void ban(final User user) {
		Authority authority;
		
		Assert.notNull(user);
		
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		if(user.getUserAccount().isEnabled()) {
			user.getUserAccount().setEnabled(false);
		} else {
			user.getUserAccount().setEnabled(true);
		}
		
		this.save(user);

	}
	
	public void changeWishList(final User user) {
		
		Assert.notNull(user);
		Assert.isTrue(user.getUserAccount().equals(LoginService.getPrincipal()));

		if(user.getIsPublicWishList()) 
			user.setIsPublicWishList(false);
		else
			user.setIsPublicWishList(true);
		
		this.save(user);
		
	}

	public void addRemoveBargainToWishList(final int bargainId) {
		User user;
		Bargain bargain;
		Plan plan;
		
		Assert.isTrue(bargainId != 0);
		Assert.isTrue(LoginService.isAuthenticated());

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);
		
		user = this.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		if(!bargain.getIsPublished()) {
			plan = this.planService.findByUserId(user.getId());
			Assert.notNull(plan);
			Assert.isTrue(plan.getName().equals("Gold Premium"));
		}
		
		if(user.getWishList().contains(bargain))
			user.getWishList().remove(bargain);
		else
			user.getWishList().add(bargain);
		
		this.save(user);
	}
	
	public void removeBargainFromAllWishList(final Bargain bargain) {		
		Collection<User> users;
		Authority authority;
		
		Assert.notNull(bargain);
		
		Assert.isTrue(LoginService.isAuthenticated());
		
		// Puede ser borrado por el moderador o por su compañía
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority) || bargain.getCompany().getUserAccount().equals(LoginService.getPrincipal()));			
		
		users = this.userRepository.findByBargainId(bargain.getId());
		Assert.notNull(users);
		
		for(User u: users) {
			u.getWishList().remove(bargain);
			this.userRepository.save(u);
		}
	}
	
	public Collection<Actor> findWithGoldPremium() {
		Collection<Actor> result;

		result = this.userRepository.findWithGoldPremium();

		return result;
	}
	
	public Collection<Actor> findWithBasicPremium() {
		Collection<Actor> result;

		result = this.userRepository.findWithBasicPremium();

		return result;
	}
	
	public Collection<Actor> findByMinimumPoints(final int points) {
		Collection<Actor> result;
		
		result = this.userRepository.findByMinimumPoints(points);
		
		return result;
	}
	
	public Page<User> topFiveUsersMoreValorations(final int page, final int size) {
		Page<User> result;
		Authority authority;
		
		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.topFiveUsersMoreValorations(this.getPageable(page, size));

		return result;
	}
	
	public Page<User> purchaseMoreTickets(final int page, final int size) {
		Page<User> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.purchaseMoreTickets(this.getPageable(page, size));

		return result;
	}

	public Page<User> purchaseLessTickets(final int page, final int size) {
		Page<User> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.purchaseLessTickets(this.getPageable(page, size));

		return result;
	}
	
	public Double avgUsersWithParticipationsPerTotal() {
	   Double result;
	   Authority authority;
	   
	   authority = new Authority();
	   authority.setAuthority("ADMIN");
	   Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

	   result = this.userRepository.avgUsersWithParticipationsPerTotal();

	   return result;
	}
	  
	public Double ratioUsersWithComments() {
	   Double result;
	   Authority authority;
	   
	   authority = new Authority();
	   authority.setAuthority("ADMIN");
	   Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
	   
	   result = this.userRepository.ratioUsersWithComments();
	
	   return result;
	}
	
	public Page<User> more10PercentageInteractions(final int page, final int size) {
		Page<User> result;
		Authority authority;

		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.more10PercentageInteractions(this.getPageable(page, size));

		return result;
	}

	public Page<User> moreAverageCharacterLenght(final int page, final int size) {
		Page<User> result;
		Authority authority;

		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.moreAverageCharacterLenght(this.getPageable(page, size));

		return result;
	}

	public Page<User> moreWonRaffles(final int page, final int size) {
		Page<User> result;
		Authority authority;

		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.moreWonRaffles(this.getPageable(page, size));

		return result;
	}

	public Page<User> purchase25PercentageMoreTotalForAllRaffles(final int page, final int size) {
		Page<User> result;
		Authority authority;

		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.userRepository.purchase25PercentageMoreTotalForAllRaffles(this.getPageable(page, size));

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
		Collection<Bargain> wishList;
		String defaultAvatar;
		wishList = new ArrayList<Bargain>();
				
		if (userForm.getId() == 0) {
			result = this.create();

			Assert.notNull(result);
			Assert.isTrue(userForm.getCheckPassword().equals(userForm.getPassword()));
			Assert.isTrue(userForm.isCheck());
			
			result.getUserAccount().setUsername(userForm.getUsername());
			result.getUserAccount().setPassword(userForm.getPassword());
			
			// Por defecto, un usuario tiene 50 puntos
			result.setPoints(50);
			result.setWishList(wishList);
			result.getUserAccount().setEnabled(true);
		} else {
			result = this.findOne(userForm.getId());
			Assert.notNull(result);
			Assert.isTrue(result.getUserAccount().getUsername().equals(userForm.getUsername()));			
		}

		result.setName(userForm.getName());
		result.setSurname(userForm.getSurname());
		result.setAddress(userForm.getAddress());
		result.setEmail(userForm.getEmail());
		result.setPhone(userForm.getPhone());
		result.setIdentifier(userForm.getIdentifier());
		
		defaultAvatar = this.configurationService.findDefaultAvatar();
		if(userForm.getAvatar() == null || userForm.getAvatar() != null && userForm.getAvatar() == "") userForm.setAvatar(defaultAvatar);
		
		result.setAvatar(userForm.getAvatar());

		this.validator.validate(userForm, binding);

		return result;
	}

}
