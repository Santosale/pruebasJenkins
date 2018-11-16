
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Category;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class CategoryServiceTest extends AbstractTest {

	//Service under test----------------

	@Autowired
	private CategoryService	categoryService;

	@Autowired
	private TripService		tripService;


	@Test
	public void testCreateCategory() {
		Category saved;

		saved = this.categoryService.create();

		Assert.isTrue(saved.getChildrenCategories().isEmpty());
		Assert.isNull(saved.getName());
		Assert.isNull(saved.getFatherCategory());

	}
	@Test
	public void testFindAll() {
		Collection<Category> categories;
		Trip trip;
		Collection<Trip> trips;

		super.authenticate("manager1");
		trip = null;
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		categories = this.categoryService.findAll();

		for (final Trip t : trips) {
			trip = t;
			break;
		}

		Assert.isTrue(categories.contains(trip.getCategory()));

		super.authenticate(null);
	}

	@Test
	public void testFindOne() {
		Collection<Category> categories;
		Trip trip;
		Category category;
		Collection<Trip> trips;

		super.authenticate("manager1");
		trip = null;

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		categories = this.categoryService.findAll();

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		category = this.categoryService.findOne(trip.getCategory().getId());

		Assert.isTrue(categories.contains(category));

		super.authenticate(null);
	}

	//Guardamos una categoría
	@Test
	public void testSave() {
		Category saved;
		Trip trip;
		Category categorySaved;
		Category categoryFather;
		Collection<Trip> trips;

		saved = this.categoryService.create();
		saved.setName("Submarinismo");
		trip = null;
		super.authenticate("manager1");

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		saved.setFatherCategory(trip.getCategory());
		super.authenticate(null);

		super.authenticate("admin");

		categorySaved = this.categoryService.save(saved);

		categoryFather = this.categoryService.findOne(categorySaved.getFatherCategory().getId());

		Assert.isTrue(this.categoryService.findAll().contains(categorySaved));
		Assert.isTrue(categorySaved.getFatherCategory().getName().equals(trip.getCategory().getName()));
		Assert.isTrue(categoryFather.getChildrenCategories().contains(categorySaved));

	}

	//Guardamos una categoría no autenticándonos como admin. Salta una excepción.
	@Test
	public void testSave2() {
		Category saved;
		Trip trip;
		Category categorySaved;
		Collection<Trip> trips;

		saved = this.categoryService.create();
		saved.setName("Submarinismo");
		trip = null;
		super.authenticate("manager1");

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		saved.setFatherCategory(trip.getCategory());
		try {
			categorySaved = this.categoryService.save(saved);
			Assert.isNull(categorySaved);
			Assert.isTrue(this.categoryService.findAll().contains(categorySaved));
			Assert.isTrue(categorySaved.getFatherCategory().getName().equals(trip.getCategory().getName()));
			super.authenticate(null);

		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}

	}
	//Intentamos gardar una categoria con el mismo nombre que un hermano suyo. Salta una excepción
	@Test
	public void testSave3() {
		Category saved;
		Trip trip;
		Category categorySaved;
		Category categoryFather;
		Category categoryAux;
		Collection<Trip> trips;

		saved = this.categoryService.create();
		trip = null;
		categoryAux = null;
		super.authenticate("manager1");

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}

		for (final Category c : trip.getCategory().getChildrenCategories())
			categoryAux = c;
		saved.setFatherCategory(trip.getCategory());
		saved.setName(categoryAux.getName());
		super.authenticate(null);

		super.authenticate("admin");
		try {
			categorySaved = this.categoryService.save(saved);

			categoryFather = this.categoryService.findOne(categorySaved.getFatherCategory().getId());

			Assert.isTrue(this.categoryService.findAll().contains(categorySaved));
			Assert.isTrue(categorySaved.getFatherCategory().getName().equals(trip.getCategory().getName()));
			Assert.isTrue(categoryFather.getChildrenCategories().contains(categorySaved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}
	}
	//No puede tener el nombre de uno de sus ancestros
	@Test
	public void testSave4() {
		Category saved;
		Trip trip;
		Category categorySaved;
		Category categoryFather;
		Collection<Trip> trips;

		saved = this.categoryService.create();
		saved.setName("CATEGORY");
		trip = null;
		super.authenticate("manager1");

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		saved.setFatherCategory(trip.getCategory());
		super.authenticate(null);

		super.authenticate("admin");
		try {
			categorySaved = this.categoryService.save(saved);

			categoryFather = this.categoryService.findOne(categorySaved.getFatherCategory().getId());

			Assert.isTrue(this.categoryService.findAll().contains(categorySaved));
			Assert.isTrue(categorySaved.getFatherCategory().getName().equals(trip.getCategory().getName()));
			Assert.isTrue(categoryFather.getChildrenCategories().contains(categorySaved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}
	}
	//Modificamos una categoria
	@Test
	public void testSave5() {
		Category category;
		Trip trip;
		Category categorySaved;
		Category categoryFather;
		Collection<Trip> trips;

		super.authenticate("manager1");
		trip = null;
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		category = trip.getCategory();
		category.setName("nombreCambiado");
		super.authenticate(null);

		super.authenticate("admin");

		categorySaved = this.categoryService.save(category);

		categoryFather = this.categoryService.findOne(categorySaved.getFatherCategory().getId());

		Assert.isTrue(this.categoryService.findAll().contains(categorySaved));
		Assert.isTrue(categoryFather.getChildrenCategories().contains(categorySaved));
		Assert.isTrue(categorySaved.getName().equals("nombreCambiado"));
	}
	@Test
	public void testDelete() {
		Category category;
		Trip trip;
		Collection<Trip> trips;

		super.authenticate("manager1");
		trip = null;
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		category = trip.getCategory();
		super.authenticate(null);
		super.authenticate("admin");
		this.categoryService.delete(category);
		Assert.isNull(this.categoryService.findOne(category.getId()));
		super.authenticate(null);
	}
	//Miramos que al borrar una categoria, sus trips adquieren la categoría del padre
	@Test
	public void testDelete2() {
		Category category;
		Trip trip;
		Collection<Trip> trips;
		Collection<Trip> trips2;
		Category father;

		super.authenticate("manager2");
		trip = null;
		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		category = trip.getCategory();
		super.authenticate(null);
		super.authenticate("admin");
		trips2 = this.tripService.findByCategoryId(category.getId());
		father = category.getFatherCategory();
		this.categoryService.delete(category);
		Assert.isNull(this.categoryService.findOne(category.getId()));
		Assert.isTrue(this.tripService.findByCategoryId(father.getId()).containsAll(trips2));
		super.authenticate(null);
	}

	public void testCategoryByTripId() {
		Trip trip;
		Category saved;
		Collection<Trip> trips;

		super.authenticate("manager1");

		trips = this.tripService.findByManagerUserAccountId(LoginService.getPrincipal().getId());

		trip = null;

		for (final Trip t : trips) {
			trip = t;
			break;
		}
		saved = this.categoryService.categoryByTripId(trip.getId());

		Assert.isTrue(this.categoryService.findAll().contains(saved));

		super.authenticate(null);

	}

	public void testCategoryWithoutFather() {
		Category category;

		category = this.categoryService.findCategoryWithoutFather();

		Assert.isTrue(category.getName().equals("CATEGORY"));

	}
}
