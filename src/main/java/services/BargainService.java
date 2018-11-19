
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.BargainRepository;
import security.Authority;
import security.LoginService;
import domain.Actor;
import domain.Bargain;
import domain.Category;
import domain.Comment;
import domain.Company;
import domain.Plan;
import domain.User;
import forms.BargainForm;

@Service
@Transactional
public class BargainService {

	// Managed repository
	@Autowired
	private BargainRepository	bargainRepository;

	// Supporting services
	@Autowired
	private CompanyService		companyService;

	@Autowired
	private PlanService			planService;

	@Autowired
	private UserService			userService;

	@Autowired
	private NotificationService	notificationService;

	@Autowired
	private TagService			tagService;

	@Autowired
	private CommentService		commentService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private Validator			validator;


	// Simple CRUD methods
	public Bargain create() {
		Bargain result;
		Company company;

		company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(company);

		result = new Bargain();
		result.setCompany(company);

		return result;
	}

	public Bargain findOne(final int bargainId) {
		Bargain result;

		Assert.isTrue(bargainId != 0);

		result = this.bargainRepository.findOne(bargainId);

		return result;
	}

	public Bargain save(final Bargain bargain, final List<String> tagsName, final Integer categoryId) {
		Bargain result;
		Collection<Actor> actors;
		Bargain saved;
		Company company;
		Double amount;
		Category category;
		Collection<Bargain> bargains;

		Assert.notNull(bargain);

		company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(company);

		//Restricciones

		//*La compañía debe ser la que está creandolo
		Assert.isTrue(company.equals(bargain.getCompany()));

		//*Precio original mayor o igual al mínimo
		Assert.isTrue(bargain.getOriginalPrice() >= bargain.getMinimumPrice());

		//Creamos el bargain
		if (bargain.getId() == 0)
			//EL precio es el original
			bargain.setPrice(bargain.getOriginalPrice());
		else {

			saved = this.findOne(bargain.getId());
			Assert.notNull(saved);

			//Vemos que no se intente "despublicar"
			Assert.isTrue(!(bargain.getIsPublished() == false && saved.getIsPublished() == true));

			//Vemos que no cambie el precio
			Assert.isTrue(saved.getPrice() == bargain.getPrice());

			//Vemos si hay que modificar precio
			if (saved.getMinimumPrice() != bargain.getMinimumPrice() || saved.getOriginalPrice() != bargain.getOriginalPrice()) {
				amount = this.sponsorshipService.sumAmountByBargainIdAndNotSponsorshipId(bargain.getId(), 0);

				if (amount == null)
					bargain.setPrice(bargain.getOriginalPrice());
				else
					bargain.setPrice(Math.max((bargain.getOriginalPrice() - amount), bargain.getMinimumPrice()));

			}

			//Creamos notificación para los basic premium si se está publicando
			if (bargain.getIsPublished() == true && saved.getIsPublished() == false) {
				actors = this.userService.findWithBasicPremium();
				this.notificationService.send(actors, "Se acaba de publicar un chollo: " + bargain.getProductName(), "/bargain/display.do?bargainId=" + bargain.getId());

			}
		}

		result = this.bargainRepository.save(bargain);

		//Actualizamos etiquetas
		this.tagService.saveByBargain(new ArrayList<String>(tagsName), result);

		//Enviamos la notificación
		if (bargain.getId() == 0) {

			//Creamos notificación para los basic premium si se está publicando
			if (bargain.getIsPublished()) {
				actors = this.userService.findWithBasicPremium();
				this.notificationService.send(actors, "Se acaba de publicar un chollo: " + result.getProductName(), "/bargain/display.do?bargainId=" + result.getId());

			}

			//Vemos que la categoría sea válida y la actualizamos
			category = this.categoryService.findOne(categoryId);
			Assert.notNull(category);

			bargains = category.getBargains();
			bargains.add(result);
			category.setBargains(bargains);

			this.categoryService.saveFromBargain(category);

			//Creamos notificación para los gold premium
			actors = this.userService.findWithGoldPremium();

			this.notificationService.send(actors, "Se acaba de crear un chollo: " + bargain.getProductName(), "/bargain/display.do?bargainId=" + result.getId());
		}

		return result;
	}

	public void delete(final Bargain bargain) {
		Bargain saved;
		Authority authority;
		Page<Comment> comments;
		Collection<Category> categories;
		Collection<Bargain> bargains;

		authority = new Authority();
		authority.setAuthority("MODERATOR");

		saved = this.findOne(bargain.getId());

		//Solo lo borra su empresa o un moderador
		Assert.isTrue(saved.getCompany().getUserAccount().equals(LoginService.getPrincipal()) || LoginService.getPrincipal().getAuthorities().contains(authority));

		//Actualizamos etiquetas
		this.tagService.deleteByBargain(saved);

		//Actualizamos comentarios
		comments = this.commentService.findByBargainId(saved.getId(), 1, 1);
		comments = this.commentService.findByBargainId(saved.getId(), 1, comments.getTotalPages());
		for (final Comment comment : comments)
			if (this.commentService.findOne(comment.getId()) != null)
				this.commentService.delete(comment);

		//Actualizamos usuarios
		this.userService.removeBargainFromAllWishList(saved);

		//Actualizamos sponsorships
		this.sponsorshipService.deleteFromBargain(saved.getId());

		//Actualizamos las categorias
		categories = this.categoryService.findAllByBargainId(saved.getId());
		for (final Category category : categories) {
			bargains = category.getBargains();
			bargains.remove(saved);
			category.setBargains(bargains);
			this.categoryService.saveFromBargain(category);
		}

		//Borramos el bargain
		this.bargainRepository.delete(saved);

	}
	// Other business methods
	public Page<Bargain> findBargainByActorId(final int userAccountId, final int page, final int size) {
		Page<Bargain> result;

		Assert.isTrue(userAccountId != 0);

		result = this.bargainRepository.findBargainByActorId(userAccountId, this.getPageable(page, size));

		return result;
	}

	public Page<Bargain> findBySponsorIdWithNoSponsorship(final int sponsorId, final int page, final int size) {
		Page<Bargain> result;

		Assert.isTrue(sponsorId != 0);

		result = this.bargainRepository.findBySponsorIdWithNoSponsorship(sponsorId, this.getPageable(page, size));

		return result;
	}

	//Actualizar precio desde sponsorship
	public Bargain saveFromSponsorship(final Bargain bargain) {
		Bargain result;

		result = this.bargainRepository.save(bargain);

		return result;
	}

	//Método para el listado de todos los bargain
	public Page<Bargain> findBargains(final int page, final int size, final String type, final int entityId) {
		Page<Bargain> result;
		Authority authorityCompany;
		Authority authorityUser;
		Authority authoritySponsor;
		Authority authorityAdmin;
		Authority authorityModerator;
		Company company;
		User user;
		Plan plan;

		authorityCompany = new Authority();
		authorityCompany.setAuthority("COMPANY");

		authorityUser = new Authority();
		authorityUser.setAuthority("USER");

		authorityAdmin = new Authority();
		authorityAdmin.setAuthority("ADMIN");

		authorityModerator = new Authority();
		authorityModerator.setAuthority("MODERATOR");

		authoritySponsor = new Authority();
		authoritySponsor.setAuthority("SPONSOR");

		result = null;

		//Si no está autenticado o es un admin solo ve los públicos
		if (!LoginService.isAuthenticated() || LoginService.getPrincipal().getAuthorities().contains(authorityAdmin)) {

			//Elegimos el listado que queremos
			if (type.equals("sponsorship"))
				result = this.bargainRepository.findWithMoreSponsorshipsAllPublished(this.getPageable(page, size));

			else if (type.equals("all"))
				result = this.bargainRepository.findAllPublished(this.getPageable(page, size));

			else if (type.equals("category"))
				result = this.bargainRepository.findAllPublishedByCategoryId(entityId, (this.getPageable(page, size)));

			else if (type.equals("tag"))
				result = this.bargainRepository.findAllPublishedByTagId(entityId, this.getPageable(page, size));

			//Si es una compañía ve los suyos y los publicados
		} else if (LoginService.getPrincipal().getAuthorities().contains(authorityCompany)) {
			company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());

			//Elegimos el listado que queremos
			if (type.equals("sponsorship"))
				result = this.bargainRepository.findWithMoreSponsorshipsAllPublishedOrMine(company.getId(), this.getPageable(page, size));

			else if (type.equals("all"))
				result = this.bargainRepository.findAllPublishedOrMine(company.getId(), this.getPageable(page, size));

			else if (type.equals("category"))
				result = this.bargainRepository.findAllPublishedOrMineByCategoryId(company.getId(), entityId, this.getPageable(page, size));

			else if (type.equals("tag"))
				result = this.bargainRepository.findAllPublishedOrMineByTagId(company.getId(), entityId, this.getPageable(page, size));

			//Si es un usuario vemos si es premium	
		} else if (LoginService.getPrincipal().getAuthorities().contains(authorityUser)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			plan = this.planService.findByUserId(user.getId());

			//Si es gold premium los ve todos
			if (plan != null && plan.getName().equals("Gold Premium")) {

				//Elegimos el listado que queremos
				if (type.equals("sponsorship"))
					result = this.bargainRepository.findWithMoreSponsorshipsAllPaginated(this.getPageable(page, size));

				else if (type.equals("all"))
					result = this.bargainRepository.findAllPaginated(this.getPageable(page, size));

				else if (type.equals("category"))
					result = this.bargainRepository.findAllPaginatedByCategoryId(entityId, this.getPageable(page, size));

				else if (type.equals("tag"))
					result = this.bargainRepository.findAllPaginatedByTagId(entityId, this.getPageable(page, size));

				//Si no los publicados
			} else if (plan == null || plan.getName().equals("Basic Premium"))

				if (type.equals("sponsorship"))
					result = this.bargainRepository.findWithMoreSponsorshipsAllPublished(this.getPageable(page, size));

				else if (type.equals("all"))
					result = this.bargainRepository.findAllPublished(this.getPageable(page, size));

				else if (type.equals("category"))
					result = this.bargainRepository.findAllPublishedByCategoryId(entityId, this.getPageable(page, size));

				else if (type.equals("tag"))
					result = this.bargainRepository.findAllPublishedByTagId(entityId, this.getPageable(page, size));

		} else if (LoginService.getPrincipal().getAuthorities().contains(authorityModerator) || LoginService.getPrincipal().getAuthorities().contains(authoritySponsor))
			//Elegimos el listado que queremos
			if (type.equals("sponsorship"))
				result = this.bargainRepository.findWithMoreSponsorshipsAllPaginated(this.getPageable(page, size));

			else if (type.equals("all"))
				result = this.bargainRepository.findAllPaginated(this.getPageable(page, size));

			else if (type.equals("category"))
				result = this.bargainRepository.findAllPaginatedByCategoryId(entityId, this.getPageable(page, size));

			else if (type.equals("tag"))
				result = this.bargainRepository.findAllPaginatedByTagId(entityId, this.getPageable(page, size));

		return result;
	}

	//Método para el display de un bargain
	public Bargain findOneToDisplay(final int bargainId) {
		Bargain result;

		result = this.findOne(bargainId);
		Assert.notNull(result);

		Assert.isTrue(this.canDisplay(result));

		return result;
	}

	//Dashboard
	public Page<Bargain> findAreInMoreWishList(final int page, final int size) {
		Page<Bargain> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.bargainRepository.findAreInMoreWishList(this.getPageable(page, size));

		return result;
	}

	public Double[] minMaxAvgStandarDesviationDiscountPerBargain() {
		Double[] result;

		result = this.bargainRepository.minMaxAvgStandarDesviationDiscountPerBargain();

		return result;
	}

	public Page<Bargain> listWithMoreSponsorships(final int page, final int size) {
		Page<Bargain> result;

		result = this.bargainRepository.listWithMoreSponsorships(this.getPageable(page, size));

		return result;
	}

	public Page<Bargain> listWithLessSponsorships(final int page, final int size) {
		Page<Bargain> result;

		result = this.bargainRepository.listWithLessSponsorships(this.getPageable(page, size));

		return result;
	}

	public Double avgRatioBargainPerCategory() {
		Double result;

		result = this.bargainRepository.avgRatioBargainPerCategory();

		return result;
	}

	//Puede ver el bargain
	public Boolean canDisplay(final Bargain bargain) {
		Boolean result;
		Authority authorityCompany;
		Authority authorityUser;
		Authority authorityModerator;
		User user;
		Plan plan;

		authorityCompany = new Authority();
		authorityCompany.setAuthority("COMPANY");

		authorityUser = new Authority();
		authorityUser.setAuthority("USER");

		authorityModerator = new Authority();
		authorityModerator.setAuthority("MODERATOR");

		result = false;

		//Si no está publicado
		if (!bargain.getIsPublished()) {

			//Si es una empresa tiene que ser suyo
			if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authorityCompany)) {
				if (bargain.getCompany().getUserAccount().getId() == LoginService.getPrincipal().getId())
					result = true;

				//Si es un usuario tiene que tener plan gold premium
			} else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authorityUser)) {
				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(user);
				plan = this.planService.findByUserId(user.getId());

				if (plan != null && plan.getName().equals("Gold Premium"))
					result = true;

				//Si es un moderador siempre lo ve
			} else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authorityModerator))
				result = true;

			//Si esta publicado todos lo ven	
		} else
			result = true;

		return result;
	}

	//Metodo para seleccionar el que se va a editar
	public Bargain findOneToEdit(final int bargainId) {
		Bargain result;
		Company company;

		company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());

		result = this.findOne(bargainId);
		Assert.notNull(result);

		Assert.isTrue(result.getCompany().equals(company));

		return result;
	}

	public void flush() {
		this.bargainRepository.flush();
	}

	//Método para ver los bargains de una compañía
	public Page<Bargain> findByCompanyId(final int page, final int size) {
		Page<Bargain> result;
		Company company;

		company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(company);

		result = this.bargainRepository.findByCompanyId(company.getId(), this.getPageable(page, size));

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

	// Pruned object domain
	public BargainForm reconstruct(final BargainForm bargainForm, final BindingResult binding) {
		Bargain aux;

		//Editamos el bargain
		if (bargainForm.getBargain().getId() != 0) {
			aux = this.findOne(bargainForm.getBargain().getId());
			Assert.notNull(aux);

			bargainForm.getBargain().setCompany(aux.getCompany());

			if (aux.getIsPublished())
				bargainForm.getBargain().setIsPublished(true);

			//Creamos el bargain
		} else {

			aux = this.create();
			bargainForm.getBargain().setCompany(aux.getCompany());
		}

		bargainForm.getBargain().setVersion(aux.getVersion());
		bargainForm.getBargain().setPrice(aux.getPrice());

		this.validator.validate(bargainForm, binding);

		return bargainForm;
	}

}
