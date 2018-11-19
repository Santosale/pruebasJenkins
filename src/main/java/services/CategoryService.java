
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CategoryRepository;
import security.Authority;
import security.LoginService;
import domain.Actor;
import domain.Bargain;
import domain.Category;

@org.springframework.stereotype.Service
public class CategoryService {

	// Managed repository
	@Autowired
	private CategoryRepository	categoryRepository;

	// Services
	@Autowired
	private ActorService		actorService;

	@Autowired
	private Validator			validator;


	// Constructor
	public CategoryService() {
		super();
	}

	// Simple CRUD methods----------
	public Category create(final Category category) {
		Category result;

		result = new Category();

		//Guardamos categoria padre si la hay
		if (category != null)
			result.setFatherCategory(category);

		result.setDefaultCategory(false);
		result.setBargains(new ArrayList<Bargain>());

		return result;
	}

	public Category findOne(final int categoryId) {
		Category result;

		Assert.isTrue(categoryId != 0);

		result = this.categoryRepository.findOne(categoryId);

		return result;
	}

	public Category save(final Category category) {
		Authority authority;
		Actor actor;
		Category result;
		Category defaultCategory;
		Collection<Category> categories;

		//Vemos que sea un moderador el que modifica las categorias
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authority));

		//Cogemos la categoría por defecto. Solo se puede actualizar, no crear ni cambiar una a default.
		defaultCategory = this.categoryRepository.findByDefaultCategory();
		Assert.notNull(defaultCategory);
		if (category.getDefaultCategory())
			Assert.isTrue(defaultCategory.getId() == category.getId());
		if (defaultCategory.getId() != category.getId())
			Assert.isTrue(!category.getDefaultCategory());

		//Vemos que su padre no sea ella misma
		if (category.getFatherCategory() != null)
			Assert.isTrue(category.getFatherCategory().getId() != category.getId());

		//Vemos que no tenga el mismo nombre que una de su nivel
		if (category.getFatherCategory() == null) {
			categories = this.findOneLevelByFatherAndNameRoot(category.getName());
			Assert.isTrue((categories.size() == 0) || (categories.size() == 1 && categories.contains(category)));
		} else {
			categories = this.findOneLevelByFatherAndName(category.getFatherCategory(), category.getName());
			Assert.isTrue((categories.size() == 0) || (categories.size() == 1 && categories.contains(category)));

		}
		//Guardamos la categoría
		result = this.categoryRepository.save(category);

		return result;
	}

	public void delete(final Category category) {
		Authority authority;
		Category saved;
		Collection<Category> childrenCategories;
		Category defaultCategory;
		Collection<Category> categories;

		Assert.notNull(category);

		//Solo borra categorias un moderador
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		//Cogemos la categoría por defecto
		defaultCategory = this.categoryRepository.findByDefaultCategory();
		Assert.notNull(defaultCategory);

		//Sacamos la categoría
		saved = this.findOne(category.getId());

		//Vemos que el moderador no borre la categoría por defecto
		Assert.isTrue(saved.getId() != defaultCategory.getId());

		childrenCategories = this.findAllByFatherCategoryId(saved.getId());
		Assert.notNull(childrenCategories);

		//Si tiene hijos usamos recursión
		for (final Category c : childrenCategories)
			this.delete(c);

		//Cuando ya no tiene hijos, actualizamos qué categoría le queda al bargain
		for (final Bargain bargain : saved.getBargains()) {
			categories = this.findAllByBargainId(bargain.getId());
			//La categoría que le queda es la que se va a borrar, le metemos la por defecto
			if (categories.size() == 1) {
				defaultCategory.getBargains().add(bargain);
				this.save(defaultCategory);
			}
		}

		saved = this.findOne(saved.getId());
		Assert.notNull(saved);
		this.categoryRepository.delete(saved);

	}

	public void flush() {
		this.categoryRepository.flush();
	}

	//Other business methods

	public Collection<Category> findOneLevelByFatherAndName(final Category fatherCategory, final String name) {
		Collection<Category> result;

		result = this.categoryRepository.findOneLevelByFatherAndName(fatherCategory, name);

		return result;
	}

	public Collection<Category> findOneLevelByFatherAndNameRoot(final String name) {
		Collection<Category> result;

		result = this.categoryRepository.findOneLevelByFatherAndNameRoot(name);

		return result;
	}

	public void reorganising(final Category category, final Category newFather) {
		Authority authority;
		Collection<Category> childrenCategory;
		Category defaultCategory;
		Collection<Category> categories;

		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		//La categoría a mover no puede ser null
		Assert.notNull(category);

		//Vemos que no tenga el mismo nombre que una de su nivel
		if (newFather == null) {
			categories = this.findOneLevelByFatherAndNameRoot(category.getName());
			Assert.isTrue((categories.size() == 0) || (categories.size() == 1 && categories.contains(category)));
		} else {
			categories = this.findOneLevelByFatherAndName(newFather, category.getName());
			Assert.isTrue((categories.size() == 0) || (categories.size() == 1 && categories.contains(category)));
		}
		//No se puede mover la categoría por defecto
		//Cogemos la categoría por defecto
		defaultCategory = this.categoryRepository.findByDefaultCategory();
		Assert.notNull(defaultCategory);
		Assert.isTrue(!category.equals(defaultCategory));

		//Si quisieramos que solo se moviera una categoría sin sus hijas

		childrenCategory = this.findAllByFatherCategoryId(category.getId());

		//A los hijos le ponemos el abuelo como padre
		for (final Category childCategory : childrenCategory) {
			childCategory.setFatherCategory(category.getFatherCategory());
			this.categoryRepository.save(childCategory);
		}

		//Si son distintas de null, no sean iguales
		if (newFather != null)
			Assert.isTrue(!(category.equals(newFather)));

		category.setFatherCategory(newFather);
		//Movemos una categoría
		this.save(category);

	}

	//Usados para las consultas del usuario para paginar
	public Page<Category> findByFatherCategoryId(final int fatherCategoryId, final int page, final int size) {
		Page<Category> result;

		Assert.isTrue(fatherCategoryId != 0);

		result = this.categoryRepository.findByFatherCategoryId(fatherCategoryId, this.getPageable(page, size));

		return result;
	}

	public Page<Category> findWithoutFather(final int page, final int size) {
		Page<Category> result;

		result = this.categoryRepository.findWithoutFather(this.getPageable(page, size));

		return result;
	}

	//Usados para borrar categorías
	public Collection<Category> findAllByFatherCategoryId(final int fatherCategoryId) {
		Collection<Category> result;

		Assert.isTrue(fatherCategoryId != 0);

		result = this.categoryRepository.findAllByFatherCategoryId(fatherCategoryId);

		return result;
	}

	//Usado en los test
	public Collection<Category> findAllWithoutFather() {
		Collection<Category> result;

		result = this.categoryRepository.findAllWithoutFather();

		return result;
	}

	public Page<Category> findByBargainId(final int bargainId, final int page, final int size) {
		Page<Category> result;

		Assert.isTrue(bargainId != 0);
		result = this.categoryRepository.findByBargainId(bargainId, this.getPageable(page, size));

		return result;
	}

	public Page<Category> findByNotBargainId(final Bargain bargain, final int page, final int size) {
		Page<Category> result;

		Assert.notNull(bargain);
		result = this.categoryRepository.findByNotBargainId(bargain, this.getPageable(page, size));

		return result;
	}

	public Collection<Category> findAllByBargainId(final int bargainId) {
		Collection<Category> result;

		Assert.isTrue(bargainId != 0);
		result = this.categoryRepository.findAllByBargainId(bargainId);

		return result;
	}

	public Page<Category> findAllPaginated(final int page, final int size) {
		Page<Category> result;

		result = this.categoryRepository.findAllPaginated(this.getPageable(page, size));

		return result;
	}

	public Category findByDefaultCategory() {
		Category result;

		result = this.categoryRepository.findByDefaultCategory();

		return result;
	}

	public void addBargain(final Bargain bargain, final Category category) {

		Assert.notNull(bargain);

		Assert.notNull(category);

		Assert.isTrue(!category.getBargains().contains(bargain));

		Assert.isTrue(LoginService.isAuthenticated());

		Assert.isTrue(bargain.getCompany().getUserAccount().getId() == LoginService.getPrincipal().getId());

		category.getBargains().add(bargain);

		this.categoryRepository.save(category);

	}

	public void removeBargain(final Bargain bargain, final Category category) {
		Collection<Category> categories;
		Category defaultCategory;

		Assert.notNull(bargain);

		Assert.notNull(category);

		defaultCategory = this.findByDefaultCategory();
		categories = this.findAllByBargainId(bargain.getId());

		Assert.isTrue(category.getBargains().contains(bargain));

		Assert.isTrue(LoginService.isAuthenticated());

		Assert.isTrue(bargain.getCompany().getUserAccount().getId() == LoginService.getPrincipal().getId());

		if (categories.size() == 1) {
			if (category.getDefaultCategory() == false) {
				defaultCategory.getBargains().add(bargain);
				category.getBargains().remove(bargain);
			}
		} else
			category.getBargains().remove(bargain);

		this.categoryRepository.save(category);

	}

	public Category saveFromBargain(final Category category) {
		Category result;

		result = this.categoryRepository.save(category);

		return result;
	}

	public Page<Category> moreBargainThanAverage(final int page, final int size) {
		Page<Category> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.categoryRepository.moreBargainThanAverage(this.getPageable(page, size));

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
	public Category reconstruct(final Category category, final BindingResult binding) {
		Category aux;
		List<Bargain> bargains;

		if (category.getId() != 0) {
			aux = this.categoryRepository.findOne(category.getId());
			category.setVersion(aux.getVersion());
			category.setFatherCategory(aux.getFatherCategory());
			category.setDefaultCategory(aux.getDefaultCategory());
			category.setBargains(aux.getBargains());
		} else {
			aux = this.create(category.getFatherCategory());
			category.setVersion(aux.getVersion());
			category.setDefaultCategory(false);
			bargains = new ArrayList<Bargain>();
			category.setBargains(bargains);
		}

		this.validator.validate(category, binding);

		return category;
	}
}
