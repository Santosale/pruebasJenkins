
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
import domain.Curriculum;
import domain.MiscellaneousRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class MiscellaneousRecordServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;

	@Autowired
	private CurriculumService			curriculumService;


	// Tests ----------------------------------------------

	//Creamos un nuevo MiscellaneousRecord y comprobamos que sus atributos tengan valor nulo
	@Test
	public void testCreate() {
		MiscellaneousRecord result;
		Curriculum curriculum;

		curriculum = (Curriculum) this.curriculumService.findAll().toArray()[0];

		result = this.miscellaneousRecordService.create(curriculum);

		Assert.notNull(result);
		Assert.isNull(result.getTitle());
		Assert.isNull(result.getComments());
	}

	@Test
	public void testSave1() {

		// Creamos un Miscellaneous Record logeados como Ranger y lo guardamos en la base de datos

		final MiscellaneousRecord miscellaneousRecord;
		final MiscellaneousRecord result;
		Curriculum curriculum;
		Collection<MiscellaneousRecord> miscellaneousRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi curriculum.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		result = this.miscellaneousRecordService.save(miscellaneousRecord);

		Assert.isTrue(this.miscellaneousRecordService.findAll().contains(result));
		miscellaneousRecords = this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(miscellaneousRecords.contains(result));

		super.authenticate(null);
	}
	@Test
	public void testSave2() {

		// Creamos un miscellaneous record con un actor logeado que no es un ranger

		final MiscellaneousRecord miscellaneousRecord;
		final MiscellaneousRecord result;
		Curriculum curriculum;

		super.authenticate("explorer1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi curriculum.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		try {
			result = this.miscellaneousRecordService.save(miscellaneousRecord);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);
	}

	@Test
	public void testSave3() {

		// Creamos un miscellaneous record con un actor logeado que no es un ranger

		final MiscellaneousRecord miscellaneousRecord;
		final MiscellaneousRecord result;
		final MiscellaneousRecord result2;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi curriculum.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		result = this.miscellaneousRecordCopy(this.miscellaneousRecordService.save(miscellaneousRecord));

		result.setComments("No solo tengo el carnet del coche, que también tengo el del camión");

		result2 = this.miscellaneousRecordService.save(result);

		Assert.isTrue(result2.getTitle().equals(result.getTitle()));

		super.authenticate(null);
	}

	@Test
	public void testDelete1() {

		// Creamos un miscellaneous Record perteneciente un Ranger, lo guardamos en la base de datos y lo borramos

		final MiscellaneousRecord miscellaneousRecord;
		final MiscellaneousRecord result;
		final Curriculum curriculum;
		Collection<MiscellaneousRecord> miscellaneousRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi curriculum.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		result = this.miscellaneousRecordService.save(miscellaneousRecord);

		this.miscellaneousRecordService.delete(result);

		Assert.isTrue(!this.miscellaneousRecordService.findAll().contains(result));

		miscellaneousRecords = this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(!miscellaneousRecords.contains(result));

		super.authenticate(null);
	}

	@Test
	public void testFindAll() {

		// Se cogen todos los miscellaneous record y vemos que no está vacío porque ya en el populate metimos algunos.
		final Collection<MiscellaneousRecord> miscellaneousRecords;

		miscellaneousRecords = this.miscellaneousRecordService.findAll();

		Assert.notEmpty(miscellaneousRecords);

	}

	@Test
	public void testFindOne() {

		// Añadimos un nuevo miscellaneousRecord y comprobamos que el findOne funciona

		final MiscellaneousRecord result;
		final MiscellaneousRecord miscellaneousRecord;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi curriculum.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		result = this.miscellaneousRecordService.save(miscellaneousRecord);

		Assert.isTrue(this.miscellaneousRecordService.findOne(result.getId()).equals(result));

		super.authenticate(null);

	}

	@Test
	public void testFindByCurriculumId() {

		// Comprobamos que el metodo findBycurriculumId devuelva los miscellaneous record que contiene la curriculum

		final Collection<MiscellaneousRecord> result;
		final Curriculum curriculum;
		Collection<MiscellaneousRecord> miscellaneousRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		result = this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()));

		miscellaneousRecords = this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(miscellaneousRecords.containsAll(result));

		super.authenticate(null);
	}

	@Test
	public void testCountByCurriculumId() {
		Curriculum curriculum;

		this.authenticate("ranger1");
		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()) == 1);
	}

	@Test
	public void testCheckSpamWords() {

		final MiscellaneousRecord miscellaneousRecord;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		miscellaneousRecord = this.miscellaneousRecordService.create(curriculum);
		miscellaneousRecord.setTitle("Una cosa más para mi viagra.");
		miscellaneousRecord.setComments("Tengo el carnet del coche.");

		Assert.isTrue(this.miscellaneousRecordService.checkSpamWords(miscellaneousRecord));
	}

	private MiscellaneousRecord miscellaneousRecordCopy(final MiscellaneousRecord miscellaneousRecord) {
		final MiscellaneousRecord result;

		result = new MiscellaneousRecord();
		result.setId(miscellaneousRecord.getId());
		result.setVersion(miscellaneousRecord.getVersion());
		result.setTitle(miscellaneousRecord.getTitle());
		result.setLink(miscellaneousRecord.getLink());

		return result;
	}

}
