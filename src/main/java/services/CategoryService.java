
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CategoryRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Category;
import domain.Trip;

@Service
@Transactional
public class CategoryService {

	// Managed repository

	@Autowired
	private CategoryRepository		categoryRepository;

	// Supporting Service
	@Autowired
	private TripService				tripService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public CategoryService() {
		super();
	}

	// Simple CRUD Methods
	public Category create() {
		Category result;
		List<Category> childrenCategories;

		childrenCategories = new ArrayList<Category>();
		result = new Category();
		result.setChildrenCategories(childrenCategories);

		return result;
	}

	public Collection<Category> findAll() {
		Collection<Category> result;

		result = this.categoryRepository.findAll();

		return result;
	}

	public Category findOne(final int categoryId) {
		Category result;

		Assert.isTrue(categoryId != 0);

		result = this.categoryRepository.findOne(categoryId);

		return result;
	}
	public Category save(final Category category) {
		Category result;
		Category currentCategory; //Variable auxiliar con la que voy a ir accediendo a los sucesivos padres en el while
		Collection<String> parentsAndBrothersNames; //Vamos a guardar el nombre de todos los ancestros y hermanos de category
		Collection<Category> parentsCategories; // Vamos a guaradar las categorias padres de category
		Authority authority;
		boolean checkTree; //boolean que mira que ningún hijo de category es a su vez un ancestro de category

		checkTree = false;
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "category.admin");

		Assert.notNull(category, "category.not.null");

		currentCategory = category;
		parentsAndBrothersNames = new ArrayList<String>();

		parentsCategories = new ArrayList<Category>();

		if (category.getFatherCategory() != null) { //Entra cuando tiene al menos un padre

			while (currentCategory.getFatherCategory() != null) { //Cuando currentCategory llege a una categoria sin padre salimos del while
				parentsAndBrothersNames.add(currentCategory.getFatherCategory().getName()); //Añadimos el nombre del padre
				parentsCategories.add(currentCategory.getFatherCategory()); //Añadimos la categoria del padre
				currentCategory = currentCategory.getFatherCategory(); //currentCategory ahora pasa a ser el padre del anterior, y así vuelve a entrar al while siempre que no sea nulo
			}
			Assert.isTrue(currentCategory.getName().equals("CATEGORY"), "category.end.category"); //Si al llegar a la última category, esta no es CATEGORY salta un Assert

			for (final Category c : category.getFatherCategory().getChildrenCategories())
				//Recorro los hijos del padre
				if (c.getId() != category.getId()) //Este if es por si estoy modificando la categoria para que no te cojas a ti mismo
					parentsAndBrothersNames.add(c.getName()); //Guardo el nombre de los hermanos de category

			Assert.isTrue(!parentsAndBrothersNames.contains(category.getName()), "category.brother.name"); //Miro que el nombre de category no coincida con ninguno de sus hermanos o padres

			for (final Category c1 : parentsCategories) { //Recorro las categories padres
				checkTree = category.getChildrenCategories().contains(c1); //Compruebo que no tenga como hijo a ninguno de sus padres
				if (checkTree)
					break;
			}

			Assert.isTrue(!checkTree, "category.children.name"); //Salta un Assert si algún hijo es un ancestro suyo a la vez

		} else
			Assert.isTrue(category.getName().equals("CATEGORY"), "category.end.category"); //Si no tiene padre comprobar que esta sea CATEGORY

		result = this.categoryRepository.save(category);

		result.getFatherCategory().getChildrenCategories().add(result);
		this.categoryRepository.save(result.getFatherCategory());

		return result;
	}

	public void delete(final Category category) {
		Authority authority;
		Trip trip;
		Category saved;

		Assert.notNull(category, "category.not.null");

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "category.admin");
		Assert.notNull(category.getFatherCategory(), "category.father.not.null");

		for (final Category c : category.getChildrenCategories())
			//SI borras una categoria borras sus hijas empezando desde el fondo del ï¿½rbol
			if (c.getChildrenCategories().isEmpty() && this.tripService.findByCategoryIdAllTrips(c.getId()).isEmpty())
				this.categoryRepository.delete(c);
			else
				this.delete(c);
		for (final Trip t : this.tripService.findByCategoryIdAllTrips(category.getId())) {
			trip = t;
			trip.setCategory(category.getFatherCategory());
			this.tripService.saveFromCategory(trip);

		}

		saved = this.findOne(category.getId());
		Assert.notNull(saved, "category.not.null");
		this.categoryRepository.delete(saved);

	}

	//Other business methods
	public Category categoryByTripId(final int tripId) {
		Category result;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		if (this.tripService.findOne(tripId) != null)
			Assert.isTrue(userAccount.equals(this.tripService.findOne(tripId).getManager().getUserAccount())); //Solo lo puede usar el manager del trip
		result = this.categoryRepository.findCategoryByTripId(tripId);

		return result;
	}

	public Category findCategoryWithoutFather() {
		Category result;

		result = this.categoryRepository.findCategoryWithoutFather();

		return result;
	}

	public boolean checkSpamWords(final Category category) {
		Collection<String> spamWords;
		boolean result;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = category.getName() != null && category.getName().toLowerCase().contains(spamWord);
			if (result == true)
				break;
		}

		return result;
	}

}
