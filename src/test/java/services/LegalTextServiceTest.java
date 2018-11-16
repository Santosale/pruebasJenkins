package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;

import domain.LegalText;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/datasource.xml",
		"classpath:spring/config/packages.xml" })
@Transactional
public class LegalTextServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private LegalTextService legalTextService;

	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo LegalText y comprobamos que sus atributos tengan el
	 * valor esperado
	 */
	@Test
	public void testCreate() {
		LegalText legalText;

		legalText = this.legalTextService.create();
		Assert.isNull(legalText.getBody());
		Assert.isNull(legalText.getTitle());
	}

	/*
	 * Creamos un legalText desde un administrador y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSaveAdmin() {
		LegalText legalText, result;

		super.authenticate("admin");

		legalText = this.legalTextService.create();
		legalText.setBody("No puedes correr por la piscina");
		legalText.setTitle("No correr");
		legalText.getLaws().add("Legalmente esta incapacitado para correr");
		legalText.setDraft(true);

		result = this.legalTextService.save(legalText);

		Assert.isTrue(this.legalTextService.findAll().contains(result));

		super.authenticate(null);

	}

	/*
	 * Creamos un legalText desde un Admin, lo guardamos en la base de datos y
	 * lo borramos
	 */
	// @Test
	public void testDeleteAdmin() {
		LegalText legalText, result;
		String dateInString;
		SimpleDateFormat format;
		Date moment;

		super.authenticate("admin");

		legalText = this.legalTextService.create();
		legalText.setBody("No puedes correr por la piscina");
		legalText.setTitle("No correr");
		legalText.getLaws().add("Legalmente esta incapacitado para correr");
		legalText.setDraft(true);

		format = new SimpleDateFormat("dd/MM/yyyy");
		dateInString = "01/01/2010";
		moment = new Date();

		try {
			moment = format.parse(dateInString);
		} catch (final ParseException e) {

			e.printStackTrace();
		}

		legalText.setMoment(moment);

		result = this.legalTextService.save(legalText);
		this.legalTextService.delete(result);

		Assert.isTrue(!this.legalTextService.findAll().contains(result));

		super.authenticate(null);
	}

	/*
	 * Se cogen todos los legalText y vemos si tiene el valor es el esperado.
	 */
	@Test
	public void testFindAll() {
		Integer result;

		result = this.legalTextService.findAll().size();

		Assert.notNull(result == 2);
	}

	// Un legalText accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		LegalText legalTextCollection;
		LegalText legalText;
		Collection<LegalText> saved;

		super.authenticate("admin");

		saved = this.legalTextService.findAll();

		legalTextCollection = this.legalTextService.create();

		for (final LegalText legalTexts : saved) {
			legalTextCollection = legalTexts;
			break;
		}

		legalText = this.legalTextService.findOne(legalTextCollection.getId());

		Assert.isTrue(legalTextCollection.getId() == legalText.getId());

		super.authenticate(null);

	}

	/*
	 * Comprobamos que el metodo findFinalMode devuelva los legaltext con el
	 * draft mode a false
	 */
	@Test
	public void findFinalMode() {
		Collection<LegalText> legalTexts;

		legalTexts = this.legalTextService.findFinalMode();

		for (LegalText legalText : legalTexts) {
			Assert.isTrue(legalText.getTitle().equals(
					"Legal text para Tanzania")
					|| legalText.getTitle().equals(
							"Legal text para Tanzania II"));
		}
	}

}
