
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Ranger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class RangerServiceTest extends AbstractTest {

	// Service under test
	@Autowired
	private RangerService	rangerService;

	// Supporting services
	@Autowired
	private FolderService	folderService;


	@Test
	public void testCreate() {
		Ranger result;

		result = this.rangerService.create();

		Assert.notNull(result);
		Assert.isNull(result.getName());
		//		Assert.isTrue(result.getFolders().isEmpty());
	}

	@Test
	public void testFindAll() {
		Collection<Ranger> ranger;

		ranger = this.rangerService.findAll();

		Assert.notNull(ranger);
		Assert.notEmpty(ranger);
	}

	@Test
	public void testSave1() {

		// Persistimos un ranger autenticados como administrador, todo debe ir bien

		Ranger ranger;
		Ranger result;

		super.authenticate("admin");

		ranger = this.rangerService.create();
		ranger.setName("Sergio");
		ranger.setSurname("Clebal");
		ranger.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		ranger.setEmail("sergioclebal@gmail.com");
		ranger.setPhone("+34618936001");

		ranger.getUserAccount().setUsername("sergioclebal");
		ranger.getUserAccount().setPassword("sergioclebal");

		result = this.rangerService.save(ranger);

		result = this.rangerService.findOne(result.getId());

		Assert.notNull(result);
		Assert.isTrue(this.rangerService.findAll().contains(result));
		Assert.isTrue(this.folderService.findByActorId(result.getId()).size() == 5);

		super.authenticate(null);
	}

	@Test
	public void testSave2() {

		// Intentamos persistir un ranger estando logeados como otro actor que no es admin

		Ranger ranger;
		Ranger result;

		super.authenticate("explorer1");

		ranger = this.rangerService.create();
		ranger.setName("Sergio");
		ranger.setSurname("Clebal");
		ranger.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		ranger.setEmail("sergioclebal@gmail.com");
		ranger.setPhone("+34618936001");

		ranger.getUserAccount().setUsername("sergioclebal");
		ranger.getUserAccount().setPassword("sergioclebal");

		try {
			result = this.rangerService.save(ranger);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);
	}

	@Test
	public void testSave3() {

		// Persistimos el ranger e intentamos cambiar el usuario y contraseña

		Ranger ranger;
		Ranger result;
		Ranger saved;

		super.authenticate("admin");

		ranger = this.rangerService.create();
		ranger.setName("Sergio");
		ranger.setSurname("Clebal");
		ranger.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		ranger.setEmail("sergioclebal@gmail.com");
		ranger.setPhone("+34618936001");

		ranger.getUserAccount().setUsername("sergioclebal");
		ranger.getUserAccount().setPassword("sergioclebal");

		saved = this.rangerService.save(ranger);

		result = this.rangerCopy(saved);
		result.getUserAccount().setUsername("eduardo");
		result.getUserAccount().setPassword("eduardo");

		try {
			result = this.rangerService.save(result);
			Assert.isNull(result);
		} catch (final IllegalArgumentException e) {
			Assert.notNull(e);
		}

		super.authenticate(null);
	}

	@Test
	public void testSearchSuspicious() {

		// Persistimos el ranger con una spamWord y comprobamos que findSuspicious devuelva dicho ranger 

		Ranger ranger;
		Ranger result;

		super.authenticate("admin");

		ranger = this.rangerService.create();
		ranger.setName("Free Sex");
		ranger.setSurname("Free Viagra");
		ranger.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		ranger.setEmail("sergioclebal@gmail.com");
		ranger.setPhone("+34618936001");

		ranger.getUserAccount().setUsername("sergioclebal");
		ranger.getUserAccount().setPassword("sergioclebal");

		result = this.rangerService.save(ranger);

		this.rangerService.searchSuspicious();

		Assert.isTrue(this.rangerService.findOne(result.getId()).getSuspicious());

		super.authenticate(null);
	}

	@Test
	public void testRatioMethods() {

		// En este test veremos que la implementación de los métodos de ratio sean correcto
		// el resultado ya se comprobó en el anterior entregable

		super.authenticate("admin");

		Assert.notNull(this.rangerService.ratioEndorsedCurriculum());
		Assert.notNull(this.rangerService.ratioRangersRegisteredCurriculum());
		Assert.notNull(this.rangerService.ratioSuspicious());

		super.authenticate(null);

	}

	@Test
	public void testFindByUserAccount() {

		Ranger result;
		Ranger ranger;

		super.authenticate("admin");

		ranger = this.rangerService.create();
		ranger.setName("Sergio");
		ranger.setSurname("Clebal");
		ranger.setAddress("Calle La Escuadra, 12, Alcala de Guadaira, Sevilla");
		ranger.setEmail("sergioclebal@gmail.com");
		ranger.setPhone("+34618936001");

		ranger.getUserAccount().setUsername("sergioclebal");
		ranger.getUserAccount().setPassword("sergioclebal");

		result = this.rangerService.save(ranger);

		super.authenticate(null);

		Assert.isTrue(this.rangerService.findByUserAccountId(result.getUserAccount().getId()).equals(result));

	}

	private Ranger rangerCopy(final Ranger ranger) {
		Ranger result;

		result = new Ranger();
		result.setId(ranger.getId());
		result.setVersion(ranger.getVersion());
		result.setName(ranger.getName());
		result.setSurname(ranger.getSurname());
		result.setAddress(ranger.getAddress());
		result.setEmail(ranger.getEmail());
		result.setPhone(ranger.getPhone());
		result.setSuspicious(ranger.getSuspicious());
		result.setUserAccount(ranger.getUserAccount());

		return result;
	}

}
