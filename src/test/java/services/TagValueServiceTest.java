
package services;

import java.util.ArrayList;
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
public class TagValueServiceTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private TagService				tagService;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private TripService				tripService;

	@Autowired
	private TagValueService			tagValueService;

	@Autowired
	private AdministratorService	administratorService;


	// Tests ----------------------------------------------
	@Test
	public void testCreate() {
		Trip trip;
		TagValue tagValue;
		Tag tag;
		Tag savedTag;

		tag = this.tagService.create();

		this.authenticate("admin");
		tag.setName("funny");
		savedTag = this.tagService.save(tag);
		this.authenticate(null);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		tagValue = this.tagValueService.create(trip);

		tagValue.setValue("excelent");
		tagValue.setTag(savedTag);
		//tagValue1 = this.tagValueService.save(tagValue1);
		Assert.notNull(tagValue.getTag());
		Assert.notNull(tagValue.getTrip());
	}

	@Test
	public void testFindOne() {
		Trip trip;
		TagValue tagValue;
		Tag tag;
		Tag savedTag;
		TagValue saved;

		tag = this.tagService.create();

		this.authenticate("admin");
		tag.setName("funny");
		savedTag = this.tagService.save(tag);
		this.authenticate(null);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		tagValue = this.tagValueService.create(trip);

		tagValue.setValue("very");
		tagValue.setTag(savedTag);
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		saved = this.tagValueService.save(tagValue);
		this.authenticate(null);

		Assert.notNull(this.tagValueService.findOne(saved.getId()));
	}

	@Test
	public void testFindAll() {
		Integer oldCount;
		Integer newCount;
		Trip trip;
		TagValue tagValue;
		Tag tag;
		Tag savedTag;
		TagValue saved;

		tag = this.tagService.create();
		oldCount = this.tagValueService.findAll().size();

		this.authenticate("admin");
		tag.setName("funny");
		savedTag = this.tagService.save(tag);
		this.authenticate(null);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		tagValue = this.tagValueService.create(trip);

		tagValue.setValue("very");
		tagValue.setTag(savedTag);
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		tagValue.setValue("excelent");
		saved = this.tagValueService.save(tagValue);
		this.authenticate(null);

		newCount = this.tagValueService.findAll().size();
		Assert.isTrue(this.tagValueService.findAll().contains(saved));
		Assert.isTrue(oldCount == newCount - 1);

	}

	//Intentamos crear un tagValue sin estar autenticado y otro estando autenticado
	@Test
	public void testSave1() {
		Trip trip;
		TagValue tagValue;
		Tag tag;
		Tag savedTag;

		tag = this.tagService.create();

		this.authenticate("admin");
		tag.setName("funny");
		savedTag = this.tagService.save(tag);
		this.authenticate(null);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		tagValue = this.tagValueService.create(trip);
		tagValue.setValue("excelent");
		tagValue.setTag(savedTag);

		//Intentamos crearlo sin estar autenticado
		try {
			tagValue = this.tagValueService.save(tagValue);
		} catch (final IllegalArgumentException e) {
		}

		//Intentamos crearlo estando autenticado
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		tagValue = this.tagValueService.save(tagValue);

		//Intentamos modificarlo
		tagValue.setValue("not excelent");
		tagValue = this.tagValueService.save(tagValue);

		Assert.isTrue(this.tagValueService.findOne(tagValue.getId()).getValue().equals("not excelent"));

	}

	//Comprobamos que no pueda haber mas de un tag value
	@Test
	public void testSave2() {
		Tag tag;
		Trip trip;
		Tag saved;
		Administrator administrator;
		TagValue tagValue1;
		TagValue tagValue2;

		tag = this.tagService.create();

		tag.setName("funny");

		this.authenticate("admin");
		//Vemos que no era suspicious
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
	}

	//Intentamos dar un tagValue a un trip que ya se ha publicado
	@Test
	public void testSave3() {
		Tag tag;
		Trip trip;
		Tag saved;
		Administrator administrator;
		TagValue tagValue1;

		tag = this.tagService.create();

		tag.setName("funny");

		this.authenticate("admin");
		//Vemos que no era suspicious
		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(administrator);
		saved = this.tagService.save(tag);

		//Le damos un value para el trip
		trip = this.getTripPublicate(true);
		super.authenticate(null);
		super.authenticate(trip.getManager().getUserAccount().getUsername());

		tagValue1 = this.tagValueService.create(trip);
		try {
			tagValue1.setValue("excelent");
			tagValue1.setTag(saved);
			tagValue1 = this.tagValueService.save(tagValue1);
			//Vemos que no se haya creado
			Assert.isTrue(!this.tagValueService.findAll().contains(tagValue1));
		} catch (final IllegalArgumentException e) {
		}
	}

	@Test
	public void testDelete() {
		Trip trip;
		TagValue savedTagValue;
		TagValue tagValue;
		Tag tag;
		Tag savedTag;

		tag = this.tagService.create();

		this.authenticate("admin");
		tag.setName("funny");
		savedTag = this.tagService.save(tag);
		this.authenticate(null);

		//Le damos un value para el trip
		trip = this.getTripPublicate(false);
		tagValue = this.tagValueService.create(trip);
		tagValue.setValue("excelent");
		tagValue.setTag(savedTag);

		//Intentamos crearlo estando autenticado
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		savedTagValue = this.tagValueService.save(tagValue);

		//Intentamos borrarlo sin estar autenticado. No se puede
		super.authenticate(null);
		try {
			this.tagValueService.delete(tagValue);
		} catch (final IllegalArgumentException e) {
		}

		Assert.isTrue(this.tagValueService.findAll().contains(savedTagValue));

		//Intentamos borrarlo estando autenticado
		super.authenticate(trip.getManager().getUserAccount().getUsername());
		this.tagValueService.delete(savedTagValue);

		//Miramos si se ha borrado correctamente
		Assert.isTrue(!this.tagValueService.findAll().contains(savedTagValue));
	}

	@Test
	public void testFindByTripId() {
		Trip trip;
		TagValue tagValue;

		tagValue = (TagValue) this.tagValueService.findAll().toArray()[0];
		trip = tagValue.getTrip();

		Assert.isTrue(this.tagValueService.findByTripId(trip.getId()).contains(tagValue));
	}

	@Test
	public void testFindByTagId() {
		Tag tag;
		TagValue tagValue;

		tagValue = (TagValue) this.tagValueService.findAll().toArray()[0];
		tag = tagValue.getTag();

		Assert.isTrue(this.tagValueService.findByTagId(tag.getId()).contains(tagValue));
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
