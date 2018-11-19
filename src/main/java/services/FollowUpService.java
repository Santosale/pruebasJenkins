
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.FollowUpRepository;
import security.LoginService;
import domain.Article;
import domain.FollowUp;
import domain.User;

@Service
@Transactional
public class FollowUpService {

	// Managed repository
	@Autowired
	private FollowUpRepository	followUpRepository;

	// Services
	@Autowired
	private UserService			userService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private Validator			validator;


	// Constructor
	public FollowUpService() {
		super();
	}

	// Simple CRUD methods----------
	public FollowUp create(final Article article) {
		FollowUp result;
		User user;

		result = new FollowUp();

		//Metemos el momentos
		result.setPublicationMoment(new Date(System.currentTimeMillis() - 1));

		//Inicializamos las imagenes en vacío
		result.setPictures(new ArrayList<String>());

		//Inicializamos el usuario
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		result.setUser(user);

		//Inicializamos el artículo
		Assert.notNull(article);
		result.setArticle(article);

		return result;
	}

	//Para los test
	public Collection<FollowUp> findAll() {
		Collection<FollowUp> result;

		result = this.followUpRepository.findAll();

		return result;
	}

	public FollowUp findOne(final int followUpId) {
		FollowUp result;

		Assert.isTrue(followUpId != 0);

		result = this.followUpRepository.findOne(followUpId);

		return result;
	}

	public FollowUp save(final FollowUp followUp) {
		User user;
		FollowUp result;

		//Solo se puede crear
		Assert.isTrue(followUp.getId() == 0);

		//Comprobamos que el usuario sea el que está autenticado
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		Assert.isTrue(followUp.getUser() != null && followUp.getUser().equals(user));
		followUp.setUser(user);

		//Comprobamos que el usuario sea el escritor del artículo
		Assert.notNull(followUp.getArticle().getWriter());
		Assert.isTrue(followUp.getUser().equals(followUp.getArticle().getWriter()));

		//Comprobamos que el newspaper haya sido publicado y el article guardado en saveMode
		Assert.notNull(followUp.getArticle());
		Assert.isTrue(followUp.getArticle().getIsFinalMode() && followUp.getArticle().getNewspaper().getIsPublished() && followUp.getArticle().getNewspaper().getPublicationDate().compareTo(new Date()) < 0);

		//Metemos el momento
		followUp.setPublicationMoment(new Date(System.currentTimeMillis() - 1));

		result = this.followUpRepository.save(followUp);

		return result;
	}

	public void delete(final FollowUp followUp) {
		FollowUp saved;
		User user;

		Assert.notNull(followUp);

		//Solo borra follow Up su escritor
		saved = this.findOne(followUp.getId());

		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		Assert.isTrue(user.equals(saved.getUser()));

		this.followUpRepository.delete(followUp);

	}

	public void deleteFromArticle(final int followUpId) {
		FollowUp followUp;

		followUp = this.findOne(followUpId);
		Assert.notNull(followUp);

		this.followUpRepository.delete(followUp);
	}

	public void flush() {
		this.followUpRepository.flush();
	}

	//Ancillary methods
	public FollowUp findOneToDisplay(final int followUpId) {
		FollowUp result;

		Assert.isTrue(followUpId != 0);

		result = this.followUpRepository.findOne(followUpId);

		//Vemos que el artículo se pueda ver
		Assert.isTrue(this.articleService.checkVisibleArticle(result.getArticle()));

		return result;
	}

	public Page<FollowUp> findByUserIdPaginated(final int userId, final int page, final int size) {
		Page<FollowUp> result;

		result = this.followUpRepository.findByUserIdPaginated(userId, this.getPageable(page, size));

		return result;
	}

	public Page<FollowUp> findByArticleIdPaginated(final int articleId, final int page, final int size) {
		Page<FollowUp> result;

		result = this.followUpRepository.findByArticleIdPaginated(articleId, this.getPageable(page, size));

		return result;
	}

	//Coleccion para borrar los follow up de un article
	public Collection<FollowUp> findByArticleId(final int articleId) {
		Collection<FollowUp> result;

		result = this.followUpRepository.findByArticleId(articleId);

		return result;
	}

	public Double numberFollowUpPerArticle() {
		Double result;

		result = this.followUpRepository.numberFollowUpPerArticle();

		return result;
	}

	public Double averageFollowUpPerArticleOneWeek() {
		Double result;

		result = this.followUpRepository.averageFollowUpPerArticleOneWeek();

		return result;
	}

	public Double averageFollowUpPerArticleTwoWeek() {
		Double result;

		result = this.followUpRepository.averageFollowUpPerArticleTwoWeek();

		return result;
	}

	//Auxilary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;
	}

	// Pruned object domain
	public FollowUp reconstruct(final FollowUp followUp, final BindingResult binding) {
		FollowUp aux;
		Article article;
		User user;

		Assert.notNull(followUp);
		//Sacamos el artículo para asegurar que no se haya cambiado el valor de alguna propiedad
		article = this.articleService.findOne(followUp.getArticle().getId());
		Assert.notNull(article);

		//Metemos el user
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		followUp.setUser(user);

		aux = this.create(article);
		followUp.setVersion(aux.getVersion());
		followUp.setArticle(article);
		//followUp.setPublicationMoment(new Date(System.currentTimeMillis() - 1));

		this.validator.validate(followUp, binding);

		return followUp;
	}
}
