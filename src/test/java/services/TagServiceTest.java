
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Administrator;
import domain.Tag;
import domain.TagValue;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class TagServiceTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private TagService				tagService;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private TagValueService			tagValueService;


	// Tests ----------------------------------------------
	@Test
	public void testCreate() {
		Tag tag;

		tag = this.tagService.create();
		tag.setName("funny");
	}

	//Creamos un objeto y vemos si lo encontramos
	@Test
	public void testFindOne() {
		Tag tag;
		Tag saved;

		tag = this.tagService.create();

		this.authenticate("admin");
		tag.setName("funny");
		saved = this.tagService.save(tag);
		this.authenticate(null);

		Assert.notNull(this.tagService.findOne(saved.getId()));

	}

	//Creamos un objetos y vemos que hay uno más, mirando que está contenido en el conjunto
	@Test
	public void testFindAll() {
		Tag tag;
		Tag saved;
		Integer oldCount;
		Integer newCount;

		tag = this.tagService.create();
		oldCount = this.tagService.findAll().size();

		this.authenticate("admin");
		tag.setName("funny");
		saved = this.tagService.save(tag);
		this.authenticate(null);

		newCount = this.tagService.findAll().size();
		Assert.isTrue(this.tagService.findAll().contains(saved));
		Assert.isTrue(oldCount == newCount - 1);
	}

	//Intentamos guardar un tag sin estar autenticado como admin no nos deja, autenticando a otro actor distinto de admin, tampoco. Y con un admin autenticado. Vemos que se puede modificar
	@Test
	public void testSave1() {
		Tag tag;
		Tag saved;

		tag = this.tagService.create();

		tag.setName("funny");

		//Lo guardamos sin autenticarnos
		try {
			saved = this.tagService.save(tag);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
		}

		//Lo creamos  autenticándonos como sponsor
		this.authenticate("sponsor1");
		try {
			saved = this.tagService.save(tag);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
		}
		this.authenticate(null);

		//Hacemos que el administrador cree un nuevo tag
		this.authenticate("admin");
		saved = this.tagService.save(tag);
		Assert.notNull(saved);

		//Vemos que se pueda modificar
		saved.setName("funny +");
		saved = this.tagService.save(saved);
		Assert.notNull(saved);
		Assert.isTrue(this.tagService.findOne(saved.getId()).getName().equals("funny +"));

	}

	//Intentamos crear tener dos tagValues por trip y tag
	@Test
	public void testSave3() {
		Tag tag;
		Trip trip;
		Tag saved;
		Administrator administrator;
		TagValue tagValue1;
		TagValue tagValue2;

		tag = this.tagService.create();

		tag.setName("funny");

		this.authenticate("admin");
		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(administrator);
		saved = this.tagService.save(tag);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		super.authenticate(null);
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		tagValue1 = this.tagValueService.create(trip);
		tagValue1.setValue("excelent");
		tagValue1.setTag(saved);
		tagValue1 = this.tagValueService.save(tagValue1);

		//Intentamos crear un nuevo tagValue con mismo trip y tag. No nos deja
		try {
			tagValue2 = this.tagValueService.create(trip);
			tagValue2.setValue("not excelent");
			tagValue2.setTag(saved);
			tagValue2 = this.tagValueService.save(tagValue2);
			Assert.isNull(tagValue2);
		} catch (final IllegalArgumentException e) {
		}
		super.authenticate(null);

		//Intentamos modificar el valor de un tag que tiene tagValues. No nos deja
		super.authenticate("admin");
		saved = this.copyTag(saved);
		try {
			saved.setName("funny 2");
			this.tagService.save(saved);
		} catch (final IllegalArgumentException e) {
		}

		//Miramos que no se ha cambiado realmente
		Assert.isTrue(this.tagService.findOne(saved.getId()).getName().equals("funny"));
		super.authenticate(null);

	}

	//Intentamos borrar sin estar autenticado, no podemos. Intentamos borrar autenticados como admin y borramos también sus tagsValues
	@Test
	public void testDelete() {
		Tag tag;
		Tag saved;
		Trip trip;
		TagValue tagValue1;
		Administrator administrator;

		tag = this.tagService.create();

		tag.setName("funny");

		super.authenticate("admin");
		//Guardamos el tag
		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(administrator);
		saved = this.tagService.save(tag);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		super.authenticate(null);
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		tagValue1 = this.tagValueService.create(trip);
		tagValue1.setTag(saved);
		tagValue1.setValue("good");
		tagValue1 = this.tagValueService.save(tagValue1);

		//Intenamos borrar el tag sin ser admin
		try {
			this.tagService.delete(saved);
		} catch (final IllegalArgumentException e) {
		}
		//Vemos que siguen estando
		Assert.isTrue(this.tagService.findAll().contains(saved));
		Assert.isTrue(this.tagValueService.findAll().contains(tagValue1));

		super.authenticate(null);

		//Intentamos borrar siendo admin
		super.authenticate("admin");
		this.tagService.delete(saved);
		//Vemos que ya no están
		Assert.isTrue(!this.tagService.findAll().contains(saved));
		Assert.isTrue(!this.tagValueService.findAll().contains(tagValue1));
		super.authenticate(null);
	}

	@Test
	public void testTagToSomeTrip() {
		Collection<Tag> tags;

		tags = this.tagService.findAll();

		for (final Tag tag : tags)
			Assert.isTrue(this.tagService.tagToSomeTrip(tag.getId()));
	}

	//Hacemos este metodo para evitar que Spring salve sin usar save, si no con simples setPropiedad...
	private Tag copyTag(final Tag tag) {
		Tag result;

		result = new Tag();
		result.setName(tag.getName());
		result.setId(tag.getId());
		result.setVersion(tag.getVersion());

		return result;
	}

	private Trip getTripPublicate(final boolean publicate) {
		List<Trip> trips;
		Trip result;
		Date currentMoment;

		currentMoment = new Date();
		trips = new ArrayList<Trip>();
		trips.addAll(this.tripService.findAll());
		result = null;

		for (final Trip tripIterator : trips)
			if (tripIterator.getPublicationDate().compareTo(currentMoment) > 0 && !publicate) {
				result = tripIterator;

				break;
			} else if (tripIterator.getPublicationDate().compareTo(currentMoment) < 0 && publicate) {
				result = tripIterator;
				break;
			}

		return result;
	}
}
