
package services;

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

import repositories.ArticleRepository;
import security.Authority;
import security.LoginService;
import domain.Article;
import domain.FollowUp;
import domain.Newspaper;
import domain.User;

@Service
@Transactional
public class ArticleService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private ArticleRepository				articleRepository;

	@Autowired
	private Validator						validator;

	// Supporting Service
	@Autowired
	private ConfigurationService			configurationService;

	@Autowired
	private FollowUpService					followUpService;

	@Autowired
	private SubscriptionNewspaperService	subscriptionNewspaperService;

	@Autowired
	private VolumeService					volumeService;

	@Autowired
	private CustomerService					customerService;

	@Autowired
	private UserService						userService;


	// Constructors -----------------------------------------------------------
	public ArticleService() {
		super();
	}

	public Article create(final User writer, final Newspaper newspaper) {
		Article result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = new Article();
		result.setWriter(writer);
		result.setNewspaper(newspaper);
		result.setMoment(newspaper.getPublicationDate());

		return result;
	}

	public Collection<Article> findAll() {
		Collection<Article> result;

		result = this.articleRepository.findAll();

		return result;
	}

	public Article findOne(final int articleId) {
		Article result;

		Assert.isTrue(articleId != 0);

		result = this.articleRepository.findOne(articleId);

		return result;
	}

	public Article findOneToEdit(final int articleId) {
		Article result;

		Assert.isTrue(articleId != 0);

		result = this.articleRepository.findOne(articleId);

		Assert.notNull(result);

		Assert.isTrue(LoginService.isAuthenticated());

		Assert.isTrue(result.getWriter().getUserAccount().getId() == LoginService.getPrincipal().getId());

		return result;
	}

	public Article findOneToDisplay(final int articleId) {
		Article result;

		Assert.isTrue(articleId != 0);

		result = this.articleRepository.findOne(articleId);

		Assert.notNull(result);

		Assert.isTrue(this.checkVisibleArticle(result));

		return result;
	}

	public Article save(final Article article) {
		Article result;
		Article saved;
		boolean isTaboo;
		Collection<Article> articles;
		boolean isFinal;

		Assert.notNull(article);

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(article.getWriter().getUserAccount().getId() == LoginService.getPrincipal().getId());
		if (article.getId() == 0) {
			Assert.isTrue(!(article.getNewspaper().getIsPublished() && article.getNewspaper().getPublicationDate().compareTo(new Date()) <= 0));
			if (!article.getIsFinalMode()) {
				article.getNewspaper().setIsPublished(false);
				article.getNewspaper().getArticles().add(article);
			}
		} else {
			saved = this.findOne(article.getId());
			Assert.isTrue(!saved.getIsFinalMode());
			Assert.isTrue(article.getWriter().getId() == saved.getWriter().getId()); //No puede cambiar de user
			Assert.isTrue(article.getNewspaper().equals(saved.getNewspaper())); //No puede cambiar el newspaper

			isFinal = true;

			if (article.getIsFinalMode()) {
				articles = this.findByNewspaperId(article.getNewspaper().getId());
				for (final Article a : articles) {
					isFinal = true;
					if (!a.getIsFinalMode()) {
						isFinal = false;
						break;
					}
				}
				if (isFinal)
					article.getNewspaper().setIsPublished(true);
			}
		}

		isTaboo = this.checkTabooWords(article);

		article.setHasTaboo(isTaboo);

		result = this.articleRepository.save(article);

		return result;

	}

	public Article saveFromNewspaper(final Article article) {
		Article result;
		Article saved;
		boolean isTaboo;
		Collection<Article> articles;
		boolean isFinal;

		Assert.notNull(article);

		Assert.isTrue(LoginService.isAuthenticated());

		saved = this.findOne(article.getId());
		Assert.isTrue(article.getWriter().getId() == saved.getWriter().getId()); //No puede cambiar de user
		Assert.isTrue(article.getNewspaper().equals(saved.getNewspaper())); //No puede cambiar el newspaper

		isFinal = true;

		if (article.getIsFinalMode()) {
			articles = this.findByNewspaperId(article.getNewspaper().getId());
			for (final Article a : articles) {
				isFinal = true;
				if (!a.getIsFinalMode()) {
					isFinal = false;
					break;
				}
			}
			if (isFinal)
				article.getNewspaper().setIsPublished(true);
		}

		isTaboo = this.checkTabooWords(article);

		article.setHasTaboo(isTaboo);

		result = this.articleRepository.save(article);

		return result;

	}

	public void delete(final Article article) {
		Article articleToDelete;
		Collection<FollowUp> followUps;
		Collection<Article> articles;
		boolean isFinal;

		Assert.notNull(article);

		articleToDelete = this.findOne(article.getId());

		Assert.notNull(articleToDelete);

		Assert.isTrue(LoginService.isAuthenticated());

		Assert.isTrue(articleToDelete.getWriter().getUserAccount().getId() == LoginService.getPrincipal().getId());

		Assert.isTrue(!articleToDelete.getIsFinalMode());

		followUps = this.followUpService.findByArticleId(articleToDelete.getId());

		for (final FollowUp f : followUps)
			this.followUpService.deleteFromArticle(f.getId());

		isFinal = true;

		if (!articleToDelete.getIsFinalMode()) {
			articles = this.findByNewspaperId(articleToDelete.getNewspaper().getId());
			for (final Article a : articles) {
				isFinal = true;
				if (!a.getIsFinalMode()) {
					isFinal = false;
					break;
				}
			}
			if (isFinal)
				articleToDelete.getNewspaper().setIsPublished(true);
		}

		articleToDelete.getNewspaper().getArticles().remove(articleToDelete);

		this.articleRepository.delete(articleToDelete);

	}

	public void deleteFromNewspaper(final Article article) {
		Authority authority;
		Collection<FollowUp> followUps;
		Collection<Article> articles;
		boolean isFinal;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.notNull(article);
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		followUps = this.followUpService.findByArticleId(article.getId());

		for (final FollowUp f : followUps)
			this.followUpService.deleteFromArticle(f.getId());

		isFinal = true;

		if (!article.getIsFinalMode()) {
			articles = this.findByNewspaperId(article.getNewspaper().getId());
			for (final Article a : articles) {
				isFinal = true;
				if (!a.getIsFinalMode()) {
					isFinal = false;
					break;
				}
			}
			if (isFinal)
				article.getNewspaper().setIsPublished(true);
		}

		article.getNewspaper().getArticles().remove(article);

		this.articleRepository.delete(article);

	}

	public void flush() {
		this.articleRepository.flush();
	}

	//Auxiliare methods
	public Page<Article> findAllUserPaginated(final int userId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);

		result = this.articleRepository.findAllUserPaginated(userId, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findAllUserPaginatedByCustomer(final int userId, final int principalId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);

		result = this.articleRepository.findAllUserPaginatedByCustomer(userId, principalId, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findAllUserPaginatedByAdmin(final int userId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);

		result = this.articleRepository.findAllUserPaginatedByAdmin(userId, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findAllNewspaperPaginated(final int userId, final int newspaperId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(userId != 0);

		result = this.articleRepository.findAllNewspaperPaginated(userId, newspaperId, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findByWritterId(final int userId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);
		Assert.isTrue(LoginService.isAuthenticated());

		result = this.articleRepository.findByWritterId(userId, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findAllPaginated(final int page, final int size) {
		Page<Article> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.articleRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	public Page<Article> findAllTabooPaginated(final int page, final int size) {
		Page<Article> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.articleRepository.findAllTabooPaginated(this.getPageable(page, size));

		return result;

	}

	public Collection<Article> findByUserIdAndNewspaperId(final int userId, final int newspaperId) {
		Collection<Article> result;

		Assert.isTrue(userId != 0);
		Assert.isTrue(newspaperId != 0);

		result = this.articleRepository.findByUserIdAndNewspaperId(userId, newspaperId);

		return result;
	}

	public Collection<Article> findByNewspaperId(final int newspaperId) {
		Collection<Article> result;

		Assert.isTrue(newspaperId != 0);

		result = this.articleRepository.findByNewspaperId(newspaperId);

		return result;
	}

	public Page<Article> findByNewspaperIdPaginated(final int newspaperId, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(newspaperId != 0);

		result = this.articleRepository.findByNewspaperIdPaginated(newspaperId, this.getPageable(page, size));

		return result;
	}

	public Article reconstruct(final Article article, final BindingResult binding) {
		Article saved;
		User user;

		if (article.getId() == 0) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			article.setWriter(user);
		} else {
			saved = this.articleRepository.findOne(article.getId());
			Assert.notNull(saved);
			article.setVersion(saved.getVersion());
			article.setWriter(saved.getWriter());
			article.setNewspaper(saved.getNewspaper());
			article.setMoment(saved.getNewspaper().getPublicationDate());
		}

		this.validator.validate(article, binding);

		return article;
	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}

	public boolean checkTabooWords(final Article article) {
		Collection<String> tabooWords;
		boolean result;

		result = false;
		tabooWords = this.configurationService.findTabooWords();

		for (final String tabooWord : tabooWords) {
			result = article.getTitle() != null && article.getTitle().toLowerCase().contains(tabooWord.toLowerCase()) || article.getBody() != null && article.getBody().toLowerCase().contains(tabooWord.toLowerCase()) || article.getSummary() != null
				&& article.getSummary().toLowerCase().contains(tabooWord.toLowerCase());
			;
			if (result == true)
				break;
		}

		return result;
	}

	public Boolean checkVisibleArticle(final Article article) {
		Boolean result;
		Authority authority;
		Authority authority2;
		Authority authority3;
		Authority authority4;
		Date currentMoment;

		result = false;

		Assert.notNull(article);

		Assert.notNull(article.getNewspaper());
		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("CUSTOMER");
		authority3 = new Authority();
		authority3.setAuthority("ADMIN");
		authority4 = new Authority();
		authority4.setAuthority("AGENT");
		currentMoment = new Date();
		result = false;

		//Si el usuario esta autenticado
		if (LoginService.isAuthenticated()) {

			//Si es un USER
			if (LoginService.getPrincipal().getAuthorities().contains(authority)) {

				//Si es el creador del artículo
				if (article.getWriter().getUserAccount().getId() == LoginService.getPrincipal().getId())
					result = true;
				//Si el newspaper no es privado y está publicado. El articulo es finalMode
				else if (article.getNewspaper().getPublicationDate().compareTo(currentMoment) <= 0 && article.getNewspaper().getIsPublished() == true && !article.getNewspaper().getIsPrivate() && article.getIsFinalMode())
					result = true;

				//Si es un CUSTOMER y el articulo es final mode
			} else if (LoginService.getPrincipal().getAuthorities().contains(authority2) && article.getIsFinalMode()) {
				//Si está publicado y no es privado
				if (article.getNewspaper().getPublicationDate().compareTo(currentMoment) <= 0 && article.getNewspaper().getIsPublished() == true && !article.getNewspaper().getIsPrivate())
					result = true;

				//Si esta publicado y tiene una suscripción
				else if (article.getNewspaper().getPublicationDate().compareTo(currentMoment) <= 0
					&& article.getNewspaper().getIsPublished() == true
					&& ((this.subscriptionNewspaperService.findByCustomerAndNewspaperId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), article.getNewspaper().getId()) != null && this.subscriptionNewspaperService
						.findByCustomerAndNewspaperId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), article.getNewspaper().getId()).size() > 0) || this.volumeService.findByCustomerIdAndNewspaperId(
						this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), article.getNewspaper().getId()).size() > 0))
					result = true;

				//Si es un AGENT
			} else if (LoginService.getPrincipal().getAuthorities().contains(authority4) && article.getIsFinalMode()) {
				//Si está publicado y no es privado
				if (article.getNewspaper().getPublicationDate().compareTo(currentMoment) <= 0 && article.getNewspaper().getIsPublished() == true && !article.getNewspaper().getIsPrivate())
					result = true;

				//Si es un ADMIN
			} else if (LoginService.getPrincipal().getAuthorities().contains(authority3) && article.getIsFinalMode())
				result = true;

			//Si no está autenticado
			//Si esta públicado y no es privado
		} else if (article.getNewspaper().getPublicationDate().compareTo(currentMoment) <= 0 && article.getNewspaper().getIsPublished() == true && article.getNewspaper().getIsPrivate() == false && article.getIsFinalMode())
			result = true;

		return result;
	}
	public Double[] avgStandartDerivationArticlesPerWriter() {
		Double[] result;

		result = this.articleRepository.avgStandartDerivationArticlesPerWriter();

		return result;
	}

	public Double[] avgStandartDerivationArticlesPerNewspaper() {
		Double[] result;

		result = this.articleRepository.avgStandartDerivationArticlesPerNewspaper();

		return result;
	}

	public Double avgArticlesPerPrivateNewpaper() {
		Double result;

		result = this.articleRepository.avgArticlesPerPrivateNewpaper();

		return result;
	}

	public Double avgArticlesPerPublicNewpaper() {
		Double result;

		result = this.articleRepository.avgArticlesPerPublicNewpaper();

		return result;
	}

	public Page<Article> findPublicsPublishedSearch(final int userId, final String keyword, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);

		result = this.articleRepository.findPublicsPublishedSearch(userId, keyword, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findPublishedSearch(final int userId, final String keyword, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(userId != 0);
		Assert.isTrue(LoginService.isAuthenticated());

		result = this.articleRepository.findPublishedSearch(userId, keyword, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findPublishedSearchNoAuth(final String keyword, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(LoginService.isAuthenticated());

		result = this.articleRepository.findPublishedSearchNoAuth(keyword, this.getPageable(page, size));

		return result;

	}

	public Page<Article> findPublishedSearchTaboo(final String keyword, final int page, final int size) {
		Page<Article> result;

		Assert.isTrue(LoginService.isAuthenticated());

		result = this.articleRepository.findPublishedSearchTaboo(keyword, this.getPageable(page, size));

		return result;

	}

	public void findTaboos() {
		Collection<Article> articles;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		articles = this.findAll();

		for (final Article article : articles)
			if (article.getHasTaboo() == false && this.checkTabooWords(article) == true) {
				article.setHasTaboo(true);
				this.articleRepository.save(article);
			} else if (article.getHasTaboo() == true && this.checkTabooWords(article) == false) {
				article.setHasTaboo(false);
				this.articleRepository.save(article);
			}

	}

}
