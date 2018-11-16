
package services;

import java.util.Collection;
import java.util.Date;

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
import domain.ProfessionalRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ProfessionalRecordServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private ProfessionalRecordService	professionalRecordService;

	@Autowired
	private CurriculumService			curriculumService;


	// Tests ----------------------------------------------

	@Test
	public void testCreate() {

		//Creamos un nuevo ProfessionalRecord y comprobamos que sus atributos tengan valor nulo

		final ProfessionalRecord result;
		Curriculum curriculum;

		curriculum = (Curriculum) this.curriculumService.findAll().toArray()[0];

		result = this.professionalRecordService.create(curriculum);

		Assert.notNull(result);
		Assert.isNull(result.getCompanyName());
		Assert.isNull(result.getRole());
		Assert.isNull(result.getLink());
		Assert.isNull(result.getStartedWorkDate());
		Assert.isNull(result.getFinishedWorkDate());
		Assert.isNull(result.getComments());
	}

	@Test
	public void testSave1() {

		// Creamos un Miscellaneous Record logeados como Ranger y lo guardamos en la base de datos

		final ProfessionalRecord professionalRecord;
		final ProfessionalRecord result;
		Curriculum curriculum;
		Collection<ProfessionalRecord> professionalRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS Cream");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 2));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		result = this.professionalRecordService.save(professionalRecord);

		Assert.isTrue(this.professionalRecordService.findAll().contains(result));
		professionalRecords = this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(professionalRecords.contains(result));

		super.authenticate(null);
	}

	@Test
	public void testSave2() {

		// Creamos un miscellaneous record con un actor logeado que no es un ranger

		final ProfessionalRecord professionalRecord;
		final ProfessionalRecord result;
		Curriculum curriculum;

		super.authenticate("explorer1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS Cream");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 2));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		try {
			result = this.professionalRecordService.save(professionalRecord);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);
	}

	@Test
	public void testSave3() {

		// Creamos un miscellaneous record con un actor logeado que no es un ranger

		final ProfessionalRecord professionalRecord;
		final ProfessionalRecord result;
		final ProfessionalRecord result2;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS Cream");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 2));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		result = this.professionalRecordCopy(this.professionalRecordService.save(professionalRecord));

		result.setRole("Product Manager VP");

		result2 = this.professionalRecordService.save(result);

		Assert.isTrue(result2.getRole().equals(result.getRole()));

		super.authenticate(null);
	}

	@Test
	public void testDelete1() {

		// Creamos un miscellaneous Record perteneciente un Ranger, lo guardamos en la base de datos y lo borramos

		final ProfessionalRecord professionalRecord;
		final ProfessionalRecord result;
		final Curriculum curriculum;
		Collection<ProfessionalRecord> professionalRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS Cream");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 2));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		result = this.professionalRecordService.save(professionalRecord);

		this.professionalRecordService.delete(result);

		Assert.isTrue(!this.professionalRecordService.findAll().contains(result));

		professionalRecords = this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(!professionalRecords.contains(result));

		super.authenticate(null);
	}

	@Test
	public void testFindAll() {

		// Se cogen todos los miscellaneous record y vemos que no está vacío porque ya en el populate metimos algunos.
		final Collection<ProfessionalRecord> professionalRecords;

		professionalRecords = this.professionalRecordService.findAll();

		Assert.notEmpty(professionalRecords);

	}

	@Test
	public void testFindOne() {

		// Añadimos un nuevo professionalRecord y comprobamos que el findOne funciona

		final ProfessionalRecord result;
		final ProfessionalRecord professionalRecord;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS Cream");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 86400001));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		result = this.professionalRecordService.save(professionalRecord);

		Assert.isTrue(this.professionalRecordService.findOne(result.getId()).equals(result));

		super.authenticate(null);

	}

	@Test
	public void testFindByCurriculumId() {

		// Comprobamos que el metodo findBycurriculumId devuelva los miscellaneous record que contiene la curriculum

		final Collection<ProfessionalRecord> result;
		final Curriculum curriculum;
		Collection<ProfessionalRecord> professionalRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		result = this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId()));

		professionalRecords = this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(professionalRecords.containsAll(result));

		super.authenticate(null);
	}

	@Test
	public void testCountByCurriculumId() {
		Curriculum curriculum;

		this.authenticate("ranger1");
		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.professionalRecordService.countByCurriculumId(curriculum.getId()) == 1);
	}

	@Test
	public void testCheckSpamWords() {
		final ProfessionalRecord professionalRecord;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		professionalRecord = this.professionalRecordService.create(curriculum);
		professionalRecord.setCompanyName("AISS SEX");
		professionalRecord.setRole("CEO");
		professionalRecord.setLink("http://example.com");
		professionalRecord.setStartedWorkDate(new Date(System.currentTimeMillis() - 86400001));
		professionalRecord.setFinishedWorkDate(new Date(System.currentTimeMillis() - 1));
		professionalRecord.setComments("Fue un destacado director.");

		Assert.isTrue(this.professionalRecordService.checkSpamWords(professionalRecord));
	}

	private ProfessionalRecord professionalRecordCopy(final ProfessionalRecord professionalRecord) {
		final ProfessionalRecord result;

		result = new ProfessionalRecord();
		result.setId(professionalRecord.getId());
		result.setVersion(professionalRecord.getVersion());
		result.setCompanyName(professionalRecord.getCompanyName());
		result.setRole(professionalRecord.getRole());
		result.setLink(professionalRecord.getLink());
		result.setStartedWorkDate(professionalRecord.getStartedWorkDate());
		result.setFinishedWorkDate(professionalRecord.getFinishedWorkDate());
		result.setComments(professionalRecord.getComments());

		return result;
	}

}
