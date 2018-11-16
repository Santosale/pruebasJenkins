
package services;

import java.util.ArrayList;
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
import domain.Sponsor;
import domain.Sponsorship;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class SponsorServiceTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private SponsorService		sponsorService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private FolderService		folderService;


	//Supporting services -----------------------------------------------------------

	// Tests ----------------------------------------------
	@Test
	public void testCreate() {
		Sponsor sponsor;
		//final UserAccount a = new UserAccount();
		sponsor = this.sponsorService.create();
		sponsor.setName("Carlos");
		sponsor.setSurname("Ortiz");
		sponsor.setEmail("hola@gmail.com");
		sponsor.setPhone("955699871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor.setSuspicious(false);

		//		Assert.notNull(sponsor.getFolders());

	}

	@Test
	public void testFindOne() {
		List<Sponsor> sponsors;
		Sponsor sponsor;
		int sponsorId;

		sponsors = new ArrayList<Sponsor>(this.sponsorService.findAll());
		sponsorId = sponsors.get(0).getId();

		sponsor = this.sponsorService.findOne(sponsorId);
		Assert.notNull(sponsor);

		//Con un id no valido
		try {
			this.sponsorService.findOne(0);
		} catch (final Exception e) {

		}

	}

	@Test
	public void testFindAll() {
		List<Sponsor> sponsors;

		sponsors = new ArrayList<Sponsor>(this.sponsorService.findAll());

		Assert.notEmpty(sponsors);
	}

	@Test
	public void testSave1() {
		Sponsor sponsor;
		Sponsor saved;
		Sponsor sponsor2;

		sponsor = this.sponsorService.create();
		sponsor.setName("Carlos");
		sponsor.setSurname("Ortiz");
		sponsor.setEmail("hola@gmail.com");
		sponsor.setPhone("955699871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor.setSuspicious(false);
		sponsor.getUserAccount().setPassword("asdsA");
		sponsor.getUserAccount().setUsername("Carlos");

		//		Assert.notNull(sponsor.getFolders());

		//Creamos un sponsor sin estar autenticado un admin, debería dar fallo
		try {
			saved = this.sponsorService.save(sponsor);
		} catch (final IllegalArgumentException e) {
		}

		//Creamos un sponsor estando un admin autenticado
		super.authenticate("admin");
		saved = this.sponsorService.save(sponsor);

		sponsor2 = this.sponsorService.findOne(saved.getId());

		Assert.isTrue(this.folderService.findByActorId(sponsor2.getId()).size() == 5);

		//Comprobamos que se haya guardado el hash de la password en vez de esta
		Assert.isTrue(!saved.getUserAccount().getPassword().equals("asds"));

		super.authenticate(null);

	}

	//Creamos un actor e intentamos que el se actualice y que otro no pueda actualizarlo
	@Test
	public void testSave2() {
		Sponsor sponsor;
		Sponsor saved;
		//Para que intente modificar al otro sponsor
		final Sponsor sponsor2;
		Sponsor updated;

		sponsor = this.sponsorService.create();
		sponsor2 = this.sponsorService.create();

		sponsor.getUserAccount().setPassword("asdAsa");
		sponsor.getUserAccount().setUsername("Carlos");
		sponsor2.getUserAccount().setPassword("asdAsa");
		sponsor2.getUserAccount().setUsername("PacoSponsor");

		sponsor.setName("Carlos");
		sponsor2.setName("Paco");
		sponsor.setSurname("Ortiz");
		sponsor2.setSurname("Ortiz");
		sponsor.setEmail("carlos@gmail.com");
		sponsor2.setEmail("paco@gmail.com");
		sponsor.setPhone("955699871");
		sponsor2.setPhone("655679871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor2.setAddress("C/ Alegría Nº6");
		sponsor.setSuspicious(false);
		sponsor2.setSuspicious(false);

		super.authenticate("admin");
		saved = this.sponsorService.save(sponsor);
		this.sponsorService.save(sponsor2);
		super.authenticate(null);

		//Nadie autenticado lo intenta modificar
		try {
			saved.setAddress("Nueva calle Nº2");
			updated = this.sponsorService.save(saved);
			Assert.isNull(updated);
		} catch (final IllegalArgumentException e) {

		}

		//Lo intenta modificar otro usuario autenticado
		super.authenticate("PacoSponsor");
		try {
			saved.setAddress("Nueva calle Nº2");
			updated = this.sponsorService.save(saved);
			Assert.isNull(updated);
		} catch (final IllegalArgumentException e) {

		}
		super.authenticate(null);

		//Se intenta modificar a sí mismo
		super.authenticate("Carlos");
		saved.setAddress("Nueva calle Nº2");
		updated = this.sponsorService.save(saved);
		Assert.isTrue(updated.getAddress().equals("Nueva calle Nº2"));
		super.authenticate(null);
	}

	//Creamos un actor con valores incorrectos, ban y suspicious a true, vemos que no se puede, intentamos actualizar estas propiedades a traves del sponsor y tampoco nos deja. Vemos si al publicar spam se actualiza su propiedad
	@Test
	public void testSave3() {
		Sponsor sponsor;
		Sponsor saved;

		sponsor = this.sponsorService.create();

		sponsor.setName("Carlos");
		sponsor.setSurname("Ortiz");
		sponsor.setEmail("carlos@gmail.com");
		sponsor.setPhone("955699871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor.setSuspicious(true);
		sponsor.getUserAccount().setPassword("asdAsa");
		sponsor.getUserAccount().setUsername("Carlos");

		//Se está creando un sponsor ya sospechoso, debe fallar
		try {
			super.authenticate("admin");
			saved = this.sponsorService.save(sponsor);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);

		}

		//Creamos el Sponsor correctamente
		sponsor.setSuspicious(false);
		super.authenticate("admin");
		saved = this.sponsorService.save(sponsor);
		super.authenticate(null);
		Assert.notNull(saved);

		//Intentamos modificarle la propiedad suspicious a true, ya logeado
		super.authenticate("Carlos");
		try {
			saved.setSuspicious(true);
			saved = this.sponsorService.save(saved);
			Assert.isNull(saved);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

		//Hacemos que el actor publique una spamWord
		super.authenticate("Carlos");
		saved.setAddress("Calle sex Nº1");
		Assert.isTrue(!sponsor.getSuspicious());
		saved = this.sponsorService.save(saved);
		Assert.isTrue(saved.getSuspicious());

		super.authenticate(null);

	}

	//Creamos un actor sospecho y vemos si se lista
	@Test
	public void testFindAllSuspicious1() {
		Sponsor sponsor;
		Sponsor saved;

		sponsor = this.sponsorService.create();

		sponsor.setName("Carlos");
		sponsor.setSurname("Ortiz  viagra");
		sponsor.setEmail("carlos@gmail.com");
		sponsor.setPhone("955699871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor.setSuspicious(false);
		sponsor.getUserAccount().setPassword("asdAsa");
		sponsor.getUserAccount().setUsername("Carlos");

		super.authenticate("admin");
		saved = this.sponsorService.save(sponsor);
		super.authenticate(null);

		//Miramos que no sea sospechoso al principio
		Assert.isTrue(!saved.getSuspicious());

		//Ejecutamos la búsqueda de sospechosos
		this.authenticate("admin");
		this.sponsorService.searchSuspicious();
		this.authenticate(null);

		//Vemos que el actor sea sospechoso
		Assert.isTrue(this.sponsorService.findOne(saved.getId()).getSuspicious());
	}

	//Probamos el método con un actor ya creado
	@Test
	public void testFindAllSuspicious2() {
		Sponsor sponsor;
		Sponsorship sponsorship;

		sponsor = this.sponsorService.create();

		this.authenticate("sponsor1");
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(!sponsor.getSuspicious());

		//Cambiamos su sponsorship
		sponsorship = (Sponsorship) this.sponsorshipService.findBySponsorId(sponsor.getId()).toArray()[0];
		sponsorship.setBanner("http://viagra.com");
		this.sponsorshipService.save(sponsorship);

		//Ejecutamos search suspicious
		this.authenticate("admin");
		this.sponsorService.searchSuspicious();
		this.authenticate(null);

		//Vemos que el actor sea sospechoso
		Assert.isTrue(this.sponsorService.findOne(sponsor.getId()).getSuspicious());
	}

	//	@Test
	//	public void testDelete1() {
	//		Sponsor sponsor;
	//		Sponsor saved;
	//
	//		sponsor = this.sponsorService.create();
	//
	//		sponsor.setName("Carlos");
	//		sponsor.setSurname("Ortiz");
	//		sponsor.setEmail("carlos@gmail.com");
	//		sponsor.setPhone("955699871");
	//		sponsor.setAddress("C/ Alegría Nº3");
	//		sponsor.setSuspicious(false);
	//
	//		sponsor.getUserAccount().setPassword("asdAsa");
	//		sponsor.getUserAccount().setUsername("Carlos");
	//
	//		//Creamos el sponsor a borrar
	//		super.authenticate("admin");
	//		saved = this.sponsorService.save(sponsor);
	//		super.authenticate(null);
	//
	//		//Intentamos borrarlo sin estar autenticado
	//		try {
	//			this.sponsorService.delete(saved);
	//			Assert.isTrue(!this.sponsorService.findAll().contains(saved));
	//		} catch (final IllegalArgumentException e) {
	//		}
	//
	//		//Intentamos borrarlo estando autenticado
	//		super.authenticate("Carlos");
	//		this.sponsorService.delete(saved);
	//		Assert.isTrue(!this.sponsorService.findAll().contains(saved));
	//		super.authenticate(null);
	//
	//	}
	//
	//	//Borramos un sponsor y vemos que se hayan borrado sus sponsorship y creditCard
	//	@Test
	//	public void testDelete2() {
	//		Sponsor sponsor;
	//		Collection<Sponsorship> sponsorships;
	//		CreditCard creditCard;
	//		Sponsorship sponsorship;
	//
	//		this.authenticate("sponsor1");
	//		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
	//		sponsorships = new ArrayList<Sponsorship>(this.sponsorshipService.findBySponsorId(sponsor.getId()));
	//		sponsorship = (Sponsorship) sponsorships.toArray()[0];
	//		creditCard = sponsorship.getCreditCard();
	//
	//		this.sponsorService.delete(sponsor);
	//
	//		Assert.isTrue(!this.sponsorshipService.findAll().containsAll(sponsorships));
	//		Assert.isTrue(!this.creditCardService.findAll().contains(creditCard));
	//	}
	@Test
	public void testFindByUserAccountId() {
		Sponsor sponsor;
		Sponsor saved;

		sponsor = this.sponsorService.create();

		sponsor.setName("Carlos");
		sponsor.setSurname("Ortiz  viagra");
		sponsor.setEmail("carlos@gmail.com");
		sponsor.setPhone("955699871");
		sponsor.setAddress("C/ Alegría Nº3");
		sponsor.setSuspicious(false);
		sponsor.getUserAccount().setPassword("asdAsa");
		sponsor.getUserAccount().setUsername("Carlos");

		super.authenticate("admin");
		saved = this.sponsorService.save(sponsor);
		super.authenticate(null);

		Assert.isTrue(saved.equals(this.sponsorService.findByUserAccountId(saved.getUserAccount().getId())));

	}
}
