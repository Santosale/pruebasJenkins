
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
import domain.EndorserRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class EndorserRecordServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private EndorserRecordService	endorserRecordService;

	@Autowired
	private CurriculumService		curriculumService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo EndorserRecord y comprobamos que sus atributos tengan
	 * el valor esperado
	 */
	@Test
	public void testCreate() {
		EndorserRecord endorserRecord;
		Curriculum curriculum;

		curriculum = (Curriculum) this.curriculumService.findAll().toArray()[0];

		endorserRecord = this.endorserRecordService.create(curriculum);

		Assert.isNull(endorserRecord.getFullName());
		Assert.isNull(endorserRecord.getEmail());
		Assert.isNull(endorserRecord.getLinkLinkedin());
		Assert.isNull(endorserRecord.getPhone());

	}

	/*
	 * Creamos un endorserRecord logeados como un Ranger y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSave1() {
		EndorserRecord endorserRecord, result;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		endorserRecord = this.endorserRecordService.create(curriculum);
		endorserRecord.setFullName("Prueba de dificultad");
		endorserRecord.setEmail("ale@hotmail.com");
		endorserRecord.setLinkLinkedin("http://www.linkedin.com/in/");
		endorserRecord.setPhone("632145874");

		result = this.endorserRecordService.save(endorserRecord);

		// Assert.isTrue(this.educationRecordService.findAll().contains(result));
		Assert.isTrue(this.endorserRecordService.findAll().contains(result));

		super.authenticate(null);
	}

	/*
	 * Creamos un endorserRecord logeados como un Ranger y lo guardamos en la base de
	 * datos, comprobamos que la curriculum contenga al endorserRecord
	 */
	@Test
	public void testSave2() {
		EndorserRecord endorserRecord, result;
		Curriculum curriculum;
		Collection<EndorserRecord> endorserRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		endorserRecord = this.endorserRecordService.create(curriculum);
		endorserRecord.setFullName("Prueba de dificultad");
		endorserRecord.setEmail("ale@hotmail.com");
		endorserRecord.setLinkLinkedin("http://www.linkedin.com/in/");
		endorserRecord.setPhone("632145874");

		result = this.endorserRecordService.save(endorserRecord);

		// Assert.isTrue(this.educationRecordService.findAll().contains(result));
		Assert.isTrue(this.endorserRecordService.findAll().contains(result));

		endorserRecords = this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId()));
		Assert.notEmpty(endorserRecords);

		super.authenticate(null);
	}

	/*
	 * Creamos un endorserRecord logeados como un Ranger, lo guardamos en la base de
	 * datos y lo borramos
	 */
	@Test
	public void testDelete1() {
		EndorserRecord endorserRecord, result;
		final Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		endorserRecord = this.endorserRecordService.create(curriculum);
		endorserRecord.setFullName("Prueba de dificultad");
		endorserRecord.setEmail("ale@hotmail.com");
		endorserRecord.setLinkLinkedin("http://www.linkedin.com/in/");
		endorserRecord.setPhone("632145874");

		result = this.endorserRecordService.save(endorserRecord);
		this.endorserRecordService.delete(result);

		Assert.isTrue(!this.endorserRecordService.findAll().contains(result));

		super.authenticate(null);
	}

	/*
	 * Creamos un endorserRecord logeados como un Ranger, lo guardamos en la base de
	 * datos y lo borramos, esperando borrar tambien el endorserRecord de la
	 * curriculum
	 */
	@Test
	public void testDelete2() {
		EndorserRecord endorserRecord, result;
		Curriculum curriculum;
		Collection<EndorserRecord> endorserRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		endorserRecord = this.endorserRecordService.create(curriculum);
		endorserRecord.setFullName("Prueba de dificultad");
		endorserRecord.setEmail("ale@hotmail.com");
		endorserRecord.setLinkLinkedin("http://www.linkedin.com/in/");
		endorserRecord.setPhone("632145874");

		result = this.endorserRecordService.save(endorserRecord);
		this.endorserRecordService.delete(result);

		Assert.isTrue(!this.endorserRecordService.findAll().contains(result));

		endorserRecords = this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(!endorserRecords.contains(result));

		super.authenticate(null);
	}

	/*
	 * Se cogen todos los endorserRecord y vemos si tiene el valor es el
	 * esperado.
	 */
	@Test
	public void testFindAll() {
		Integer allEndorserRecords;

		allEndorserRecords = this.endorserRecordService.findAll().size();

		Assert.isTrue(allEndorserRecords == 2);

	}

	// Un endorserRecord accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		Collection<EndorserRecord> saved;
		EndorserRecord endorserRecord, endorserRecordCollection;
		Curriculum curriculum;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		saved = this.endorserRecordService.findAll();

		endorserRecordCollection = this.endorserRecordService.create(curriculum);
		for (final EndorserRecord endoRecord : saved) {
			endorserRecordCollection = endoRecord;
			break;
		}

		endorserRecord = this.endorserRecordService.findOne(endorserRecordCollection.getId());

		Assert.isTrue(endorserRecordCollection.getId() == endorserRecord.getId());

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo findByCurriculumId devuelva los endorserRecord
	 * que contiene la curriculum
	 */
	@Test
	public void testFindByCurriculumId() {
		Collection<EndorserRecord> result;
		Curriculum curriculum;
		Collection<EndorserRecord> endorserRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		result = this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId()));

		endorserRecords = this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId()));
		Assert.isTrue(endorserRecords.containsAll(result));

		super.authenticate(null);
	}

	@Test
	public void testCountByCurriculumId() {
		Curriculum curriculum;

		this.authenticate("ranger1");
		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(this.endorserRecordService.countByCurriculumId(curriculum.getId()) == 1);
	}

}
