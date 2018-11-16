
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
import domain.EducationRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class EducationRecordServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private EducationRecordService	educationRecordService;

	@Autowired
	private CurriculumService		curriculumService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo EducationRecord y comprobamos que sus atributos tengan
	 * el valor esperado
	 */
	@Test
	public void testCreate() {
		EducationRecord educationRecord;
		Curriculum curriculum;

		curriculum = (Curriculum) this.curriculumService.findAll().toArray()[0];

		educationRecord = this.educationRecordService.create(curriculum);

		Assert.isNull(educationRecord.getTitle());
		Assert.isNull(educationRecord.getStartedStudyDate());
		Assert.isNull(educationRecord.getFinishedStudyDate());
		Assert.isNull(educationRecord.getInstitution());
	}

	/*
	 * Creamos un educationRecord logeados como un Ranger y lo guardamos en la
	 * base de datos
	 */
	@Test
	public void testSave1() {
		EducationRecord educationRecord, result;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		educationRecord = this.educationRecordService.create(curriculum);
		educationRecord.setTitle("Bachillerato");
		educationRecord.setStartedStudyDate(new Date(System.currentTimeMillis() - 2));
		educationRecord.setFinishedStudyDate(new Date(System.currentTimeMillis() - 1));
		educationRecord.setInstitution("IES Nueva Corriente");
		educationRecord.setComments("Bachiller tecnologico");

		result = this.educationRecordService.save(educationRecord);

		Assert.isTrue(this.educationRecordService.findAll().contains(result));

		super.authenticate(null);
	}

	/*
	 * Creamos un educationRecord logeados como un Ranger y lo guardamos en la
	 * base de datos, comprobamos que la curriculum contenga al educationRecord
	 */
	@Test
	public void testSave2() {
		EducationRecord educationRecord, result;
		Curriculum curriculum;
		Collection<EducationRecord> educationRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		educationRecord = this.educationRecordService.create(curriculum);
		educationRecord.setTitle("Bachillerato");
		educationRecord.setStartedStudyDate(new Date(System.currentTimeMillis() - 2));
		educationRecord.setFinishedStudyDate(new Date(System.currentTimeMillis() - 1));
		educationRecord.setInstitution("IES Nueva Corriente");
		educationRecord.setComments("Bachiller tecnologico");

		result = this.educationRecordService.save(educationRecord);

		Assert.isTrue(this.educationRecordService.findAll().contains(result));

		educationRecords = this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId()));

		Assert.notEmpty(educationRecords);

		super.authenticate(null);
	}

	/*
	 * Creamos un educationRecord logeados como un Ranger, lo guardamos en la
	 * base de datos y lo borramos
	 */
	@Test
	public void testDelete1() {
		EducationRecord educationRecord, result;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		educationRecord = this.educationRecordService.create(curriculum);
		educationRecord.setTitle("Bachillerato");
		educationRecord.setStartedStudyDate(new Date(System.currentTimeMillis() - 2));
		educationRecord.setFinishedStudyDate(new Date(System.currentTimeMillis() - 1));
		educationRecord.setInstitution("IES Nueva Corriente");
		educationRecord.setComments("Bachiller tecnologico");

		result = this.educationRecordService.save(educationRecord);
		this.educationRecordService.delete(result);

		Assert.isTrue(!this.educationRecordService.findAll().contains(result));

		super.authenticate(null);
	}

	/*
	 * Creamos un educationRecord logeados como un Ranger, lo guardamos en la
	 * base de datos y lo borramos, esperando borrar tambien el educationRecord
	 * de la curriculum
	 */
	@Test
	public void testDelete2() {
		EducationRecord educationRecord, result;
		Curriculum curriculum;
		Collection<EducationRecord> educationRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		educationRecord = this.educationRecordService.create(curriculum);
		educationRecord.setTitle("Bachillerato");
		educationRecord.setStartedStudyDate(new Date(System.currentTimeMillis() - 2));
		educationRecord.setFinishedStudyDate(new Date(System.currentTimeMillis() - 1));
		educationRecord.setInstitution("IES Nueva Corriente");
		educationRecord.setComments("Bachiller tecnologico");

		result = this.educationRecordService.save(educationRecord);
		this.educationRecordService.delete(result);

		Assert.isTrue(!this.educationRecordService.findAll().contains(result));

		educationRecords = this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId()));

		Assert.isTrue(!educationRecords.contains(result));

		super.authenticate(null);
	}

	/*
	 * Se cogen todos los educationRecord y vemos si tiene el valor es el
	 * esperado.
	 */
	@Test
	public void testFindAll() {
		Integer allEducationRecords;

		allEducationRecords = this.educationRecordService.findAll().size();

		Assert.isTrue(allEducationRecords == 3);

	}

	// Un educationRecord accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		Collection<EducationRecord> saved;
		EducationRecord educationRecord, educationRecordCollection;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		saved = this.educationRecordService.findAll();

		educationRecordCollection = this.educationRecordService.create(curriculum);
		for (final EducationRecord eduRecord : saved) {
			educationRecordCollection = eduRecord;
			break;
		}

		educationRecord = this.educationRecordService.findOne(educationRecordCollection.getId());

		Assert.isTrue(educationRecordCollection.getId() == educationRecord.getId());

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo findBycurriculumId devuelva los educationRecord
	 * que contiene la curriculum
	 */
	@Test
	public void testFindByCurriculumId() {
		Collection<EducationRecord> result;
		Curriculum curriculum;
		Collection<EducationRecord> educationRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		result = this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId()));

		educationRecords = this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId()));

		Assert.isTrue(educationRecords.containsAll(result));

		super.authenticate(null);
	}

	@Test
	public void testCountByCurriculumId() {
		Curriculum curriculum;

		this.authenticate("ranger1");
		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.educationRecordService.countByCurriculumId(curriculum.getId()) == 1);
	}

}
