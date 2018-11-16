
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
import domain.EducationRecord;
import domain.EndorserRecord;
import domain.MiscellaneousRecord;
import domain.ProfessionalRecord;
import domain.Ranger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class CurriculumServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private CurriculumService			curriculumService;

	@Autowired
	private RangerService				rangerService;

	@Autowired
	private EducationRecordService		educationRecordService;

	@Autowired
	private EndorserRecordService		endorserRecordService;

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;

	@Autowired
	private ProfessionalRecordService	professionalRecordService;


	// Tests ----------------------------------------------

	/*
	 * Creamos una nueva Curricula y comprobamos que sus atributos tengan el
	 * valor esperado
	 */
	@Test
	public void testCreate() {
		Curriculum curriculum;
		Ranger ranger;

		super.authenticate("ranger2");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		curriculum = this.curriculumService.create(ranger);
		Assert.isNull(curriculum.getFullNamePR());
		Assert.isNull(curriculum.getPhotoPR());
		Assert.isNull(curriculum.getEmailPR());
		Assert.isNull(curriculum.getPhonePR());
		Assert.isNull(curriculum.getLinkedinPR());
	}

	// Creamos una curricula logeados como un Ranger y la guardamos en la base
	// de datos
	@Test
	public void testSave1() {
		Curriculum curriculum, curriculumSaved;
		Ranger ranger;

		super.authenticate("ranger2");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		curriculum = this.curriculumService.create(ranger);
		curriculum.setFullNamePR("Antonio");
		curriculum.setPhotoPR("http://www.image.com/fotaso.png");
		curriculum.setEmailPR("a@hotmail.es");
		curriculum.setPhonePR("614392997");
		curriculum.setLinkedinPR("http://www.linkedin.com/in/antonio/");

		curriculumSaved = this.curriculumService.save(curriculum);

		Assert.isTrue(this.curriculumService.findAll().contains(curriculumSaved));

		super.authenticate(null);
	}

	/*
	 * Creamos una curricula logeados como un Ranger y la guardamos en la base
	 * de datos, esperando que tambien se haya guardado en la curricula del
	 * Ranger
	 */
	@Test
	public void testSave2() {
		Curriculum curriculum, curriculumSaved;
		Ranger ranger;

		super.authenticate("ranger2");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		curriculum = this.curriculumService.create(ranger);
		curriculum.setFullNamePR("Antonio");
		curriculum.setPhotoPR("http://www.image.com/fotaso.png");
		curriculum.setEmailPR("a@hotmail.es");
		curriculum.setPhonePR("614392997");
		curriculum.setLinkedinPR("http://www.linkedin.com/in/antonio/");

		curriculumSaved = this.curriculumService.save(curriculum);

		Assert.isTrue(this.curriculumService.findAll().contains(curriculumSaved));

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(curriculum);

		super.authenticate(null);
	}

	/*
	 * Creamos una curricula logeados como un Sponsor, la guardamos en la base
	 * de datos y la borramos
	 */
	@Test
	public void testDelete1() {
		Curriculum curriculum, curriculumSaved;
		Ranger ranger;

		super.authenticate("ranger2");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		curriculum = this.curriculumService.create(ranger);
		curriculum.setFullNamePR("Antonio");
		curriculum.setPhotoPR("http://www.image.com/fotaso.png");
		curriculum.setEmailPR("a@hotmail.es");
		curriculum.setPhonePR("614392997");
		curriculum.setLinkedinPR("http://www.linkedin.com/in/antonio/");

		curriculumSaved = this.curriculumService.save(curriculum);
		this.curriculumService.delete(curriculumSaved);

		Assert.isTrue(!this.curriculumService.findAll().contains(curriculumSaved));

		super.authenticate(null);
	}

	/*
	 * Creamos una Curricula logeados como un Sponsor, la guardamos en la base
	 * de datos y la borramos, esperando que tambien se borre la curricula del
	 * Ranger
	 */
	@Test
	public void testDelete2() {
		Curriculum curriculum, curriculumSaved;
		Ranger ranger;

		super.authenticate("ranger2");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		curriculum = this.curriculumService.create(ranger);
		curriculum.setFullNamePR("Antonio");
		curriculum.setPhotoPR("http://www.image.com/fotaso.png");
		curriculum.setEmailPR("a@hotmail.es");
		curriculum.setPhonePR("614392997");
		curriculum.setLinkedinPR("http://www.linkedin.com/in/antonio/");

		curriculumSaved = this.curriculumService.save(curriculum);

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		// Assert.isTrue(curricula != null);
		this.curriculumService.delete(curriculumSaved);

		Assert.isTrue(!this.curriculumService.findAll().contains(curriculumSaved));

		Assert.notNull(curriculum);

		super.authenticate(null);
	}

	/*
	 * Probamos el metodo delete, al borrar un curriculum, se debe borrar los
	 * xRecords asociadas
	 */
	@Test
	public void testDelete3() {
		Curriculum curriculum;
		Collection<EducationRecord> educationRecords;
		Collection<EndorserRecord> endorserRecords;
		Collection<MiscellaneousRecord> miscellaneousRecords;
		Collection<ProfessionalRecord> professionalRecords;

		super.authenticate("ranger1");

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());

		professionalRecords = this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId()));
		educationRecords = this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId()));
		endorserRecords = this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId()));
		miscellaneousRecords = this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId()));

		this.curriculumService.delete(curriculum);

		Assert.isTrue(!this.educationRecordService.findAll().containsAll(educationRecords));
		Assert.isTrue(!this.endorserRecordService.findAll().containsAll(endorserRecords));
		Assert.isTrue(!this.miscellaneousRecordService.findAll().containsAll(miscellaneousRecords));
		Assert.isTrue(!this.professionalRecordService.findAll().containsAll(professionalRecords));

	}

	// Se cogen todas las curricula y vemos si tiene el valor es el esperado.
	@Test
	public void testFindAll() {
		Integer result;

		result = this.curriculumService.findAll().size();

		Assert.isTrue(result == 3);
	}

	// Una curricula accede a ella misma a traves de findOne
	@Test
	public void testFindOne() {
		Curriculum curriculumCollection;
		Curriculum curriculum;
		Collection<Curriculum> saved;

		super.authenticate("ranger1");

		saved = this.curriculumService.findAll();

		curriculumCollection = null;

		for (final Curriculum curricula : saved) {
			curriculumCollection = curricula;
			break;
		}

		curriculum = this.curriculumService.findOne(curriculumCollection.getId());

		Assert.isTrue(curriculumCollection.getId() == curriculum.getId());

		super.authenticate(null);

	}

	/*
	 * Comparamos la curricula devuelta por la consulta con la que deberia
	 * devolver
	 */
	@Test
	public void testFindByRangerId() {
		Curriculum result;
		Ranger ranger;

		super.authenticate("ranger1");

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		result = this.curriculumService.findByRangerUserAccountId(ranger.getUserAccount().getId());

		Assert.isTrue(result.getTicker().equals("171212-ASDF"));

		super.authenticate(null);

	}
}
