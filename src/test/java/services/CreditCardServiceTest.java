
package services;

import java.util.ArrayList;
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
import domain.CreditCard;
import domain.Explorer;
import domain.Sponsor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class CreditCardServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private CreditCardService	creditCardService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private SponsorService		sponsorService;


	// Tests ----------------------------------------------

	/*
	 * Creamos una nueva CreditCard y comprobamos que sus atributos tengan el
	 * valor esperado
	 */
	@Test
	public void testCreate() {
		CreditCard creditCard;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(explorer);

		creditCard = this.creditCardService.create(explorer);

		Assert.isNull(creditCard.getBrandName());
		Assert.isNull(creditCard.getHolderName());
		Assert.isNull(creditCard.getNumber());
	}

	/*
	 * Creamos una creditCard logeados como un Sponsor y la guardamos en la base
	 * de datos
	 */
	@Test
	public void testSave1() {

		super.authenticate("sponsor1");

		CreditCard creditCard;
		int oldCountCreditCard;
		Sponsor sponsor;

		oldCountCreditCard = this.creditCardService.findAll().size();

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		creditCard = this.creditCardService.create(sponsor);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		this.creditCardService.save(creditCard);

		Assert.isTrue(this.creditCardService.findAll().size() == oldCountCreditCard + 1);

		super.authenticate(null);

	}

	/*
	 * Creamos una creditCard logeados como un Explorer y la guardamos en la
	 * base de datos
	 */
	@Test
	public void testSave2() {
		CreditCard creditCard;
		CreditCard creditCardSaved;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(explorer);

		creditCard = this.creditCardService.create(explorer);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		creditCardSaved = this.creditCardService.save(creditCard);

		Assert.notNull(creditCardSaved);
		Assert.isTrue(this.creditCardService.findAll().contains(creditCardSaved));

		super.authenticate(null);

	}

	/*
	 * Creamos una creditCard logeados como un Sponsor, la guardamos en la base
	 * de datos y la borramos
	 */
	@Test
	public void testDelete1() {
		CreditCard creditCard, creditCardSaved;
		int oldCountCreditCard;
		Sponsor sponsor;

		oldCountCreditCard = this.creditCardService.findAll().size();

		super.authenticate("sponsor1");

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(sponsor);

		creditCard = this.creditCardService.create(sponsor);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		creditCardSaved = this.creditCardService.save(creditCard);
		this.creditCardService.delete(creditCardSaved);

		Assert.isTrue(this.creditCardService.findAll().size() == oldCountCreditCard);

		super.authenticate(null);
	}

	/*
	 * Creamos una creditCard logeados como un Explorer, la guardamos en la base
	 * de datos y la borramos
	 */
	@Test
	public void testDelete2() {
		CreditCard creditCard, creditCardSaved;
		int oldCountCreditCard;
		Explorer explorer;

		oldCountCreditCard = this.creditCardService.findAll().size();

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(explorer);

		creditCard = this.creditCardService.create(explorer);
		creditCard.setBrandName("MasterCard");
		creditCard.setCvvcode(258);
		creditCard.setExpirationMonth(9);
		creditCard.setExpirationYear(2018);
		creditCard.setHolderName("Alejandro");
		creditCard.setNumber("5471664286416252");

		creditCardSaved = this.creditCardService.save(creditCard);
		this.creditCardService.delete(creditCardSaved);

		Assert.isTrue(this.creditCardService.findAll().size() == oldCountCreditCard);

		super.authenticate(null);
	}

	/*
	 * Creamos una creditCard logeados como un Sponsor, la guardamos en la base
	 * de datos y borramos el sponsor, no debiendose haber borrado la creditCard
	 */

	// Se cogen todas las creditCard y vemos si tiene el valor es el esperado.
	@Test
	public void testFindAll() {
		Integer allCreditCards;

		allCreditCards = this.creditCardService.findAll().size();

		Assert.isTrue(allCreditCards == 5);
	}

	// Una creditCard accede a ella misma a traves de findOne
	@Test
	public void testFindOne() {
		CreditCard creditCardCollection;
		CreditCard creditCard;
		Collection<CreditCard> saved;
		Sponsor sponsor;

		super.authenticate("sponsor1");

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.notNull(sponsor);

		saved = this.creditCardService.findAll();

		creditCardCollection = this.creditCardService.create(sponsor);

		for (final CreditCard crediCard : saved) {
			creditCardCollection = crediCard;
			break;
		}

		creditCard = this.creditCardService.findOne(creditCardCollection.getId());

		Assert.isTrue(creditCardCollection.getId() == creditCard.getId());

		super.authenticate(null);

	}

	/*
	 * Probamos el metodo findByExplorerId que debe devolver las creditCards de un
	 * explorer en concreto
	 */
	@Test
	public void testFindByExplorerId() {
		Collection<CreditCard> result;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = new ArrayList<CreditCard>();

		result = this.creditCardService.findByExplorerId(explorer.getId());

		for (final CreditCard creditCard : result)
			Assert.isTrue(creditCard.getNumber().equals("5471664286416252") || creditCard.getNumber().equals("5229783098148518"));
	}
}
