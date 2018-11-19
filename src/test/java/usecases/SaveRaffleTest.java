package usecases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import domain.Company;
import domain.Raffle;
import domain.User;
import security.LoginService;
import services.CompanyService;
import services.RaffleService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveRaffleTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private RaffleService		raffleService;

	@Autowired
	private CompanyService		companyService;
	
	@Autowired
	private UserService 		userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Una compañía trata de crear una rifa de forma correcta dejando en blanco el campo productUrl y productImages.
	 * 		2. Una compañía trata de crear una rifa de forma correcta dejando en blanco el campo productImages.
	 *		3. Una compañía trata de crear una rifa de forma correcta rellenando todos los campos
	 *		4. Una compañía trata de crear una rifa de forma correcta introduciendo varias imágenes
	 *		5. Una compañía trata de crear una rifa introduciendo de precio 0.
	 *
	 * Requisitos:
	 * 		23.6. Un actor que está autenticado como empresa debe ser capaz de listar las rifas que ha creado, editarlas y borrarlas si todavía nadie ha comprado un tique.
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"company1", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, null
			}, {
				"company2", "Raffle title", "Raffle description", "Product name", "http://example.com/image.jpg", null, "02/12/2018", 10.0, null, null
			}, {
				"company3", "Raffle title", "Raffle description", "Product name", "http://example.com/image.jpg", "http://example.com/image.jpg", "02/12/2018", 10.0, null, null
			}, {
				"company4", "Raffle title", "Raffle description", "Product name", "http://example.com/image.jpg", "http://example.com/image.jpg,http://example.com/image.jpg", "02/12/2018", 10.0, null, null
			}, {
				"company4", "Raffle title", "Raffle description", "Product name", "http://example.com/image.jpg", "http://example.com/image.jpg,http://example.com/image.jpg", "02/12/2018", 0.0, null, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (double) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. ProductURL debe cumplir el formato de una URL
	 * 		2. ProductImages debe cumplir el formato de una URL
	 * 		3. Una compañía intenta crear una rifa en pasado
	 * 		4. Una compañía intenta crear una rifa con el precio negativo
	 * 		5. Una compañía intenta crear una rifa sin el nombre
	 * 		6. Una compañía intenta crear una rifa sin la descripción
	 * 		7. Una compañía intenta crear una rifa sin el nombre del producto
	 * 		8. Una compañía intenta crear una rifa sin la fecha límite
	 * 		9. Una compañía intenta crear una rifa con ganador
	 * 
	 * Requisitos:
	 * 		23.6. Un actor que está autenticado como empresa debe ser capaz de listar las rifas que ha creado, editarlas y borrarlas si todavía nadie ha comprado un tique.
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"company1", "Raffle title", "Raffle description", "Product name", "example", null, "02/12/2018", 10.0, null, ConstraintViolationException.class
				}, {
					"company1", "Raffle title", "Raffle description", "Product name", null, "example", "02/12/2018", 10.0, null, ConstraintViolationException.class
				}, {
					"company1", "Raffle title", "Raffle description", "Product name", null, null, "02/12/1997", 10.0, null, IllegalArgumentException.class
				}, {
					"company1", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", -10.0, null, ConstraintViolationException.class
				}, {
					"company1", null, "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, ConstraintViolationException.class
				}, {
					"company1", "Raffle title", null, "Product name", null, null, "02/12/2018", 10.0, null, ConstraintViolationException.class
				}, {
					"company1", "Raffle title", "Raffle description", null, null, null, "02/12/2018", 10.0, null, ConstraintViolationException.class
				}, {
					"company1", "Raffle title", "Raffle description", "Product name", null, null, null, 10.0, null, IllegalArgumentException.class
				}, {
					"company1", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, "user1", IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (double) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Crear una rifa
	 * Pasos:
	 * 		1. Autenticar como compañía
	 * 		2. Listar rifas
	 * 		3. Entrar en vista de crear rifa
	 * 		4. Crear rifa
	 * 		5. Volver al listado de rifas
	 */
	protected void template(final String actorBean, final String title, final String description, final String productName, final String productUrl, final String productImages, final String maxDate, final double price, final String winnerBean, final Class<?> expected) {
		Class<?> caught;
		SimpleDateFormat format;
		Raffle raffle, reconstructedRaffle;
		Page<Raffle> rafflePage;
		Company company;
		Integer companyId, winnerId;
		Long oldTotalElements;
		User winner;
		DataBinder binder;
		Collection<String> productI;
		Date maxDateO;

		caught = null;
		winner = null;
		productI = new ArrayList<String>();
		try {
			
			if(winnerBean != null) {
				winnerId = super.getEntityId(winnerBean);
				Assert.notNull(winnerId);
				winner = this.userService.findOne(winnerId);
				Assert.notNull(winner);
			}
			
			companyId = super.getEntityId(actorBean);
			Assert.notNull(companyId);
			
			company = this.companyService.findOne(companyId);
			Assert.notNull(company);
			
			// 1. Autenticar como compañía
			super.authenticate(actorBean);
			
			// 2. Listar rifas
			rafflePage = this.raffleService.findByCompanyAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(rafflePage);
			
			oldTotalElements = rafflePage.getTotalElements();
			
			// 3. Entrar en vista de crear rifa
			raffle = this.raffleService.create(company);
			raffle.setTitle(title);
			raffle.setDescription(description);
			raffle.setProductName(productName);
			raffle.setProductUrl(productUrl);
			
			if(productImages != null && productImages != "") {
				for(String p :productImages.split(","))
					productI.add(p);
			}
			
			raffle.setProductImages(productI);
			
			if(maxDate == null || maxDate == "") {
				Assert.notNull(maxDate);
				Assert.isTrue(maxDate != "");
			} else {
				format = new SimpleDateFormat("dd/MM/yyyy");
				maxDateO = format.parse(maxDate);
				raffle.setMaxDate(maxDateO);
			}
			
			raffle.setPrice(price);
			if(winner != null) raffle.setWinner(winner);
			
			// 4. Crear rifa
			binder = new DataBinder(raffle);
			reconstructedRaffle = this.raffleService.reconstruct(raffle, binder.getBindingResult());
			this.raffleService.save(reconstructedRaffle);
			
			this.raffleService.flush();
									
			// 3. Volver al listado de mensajes
			rafflePage = this.raffleService.findByCompanyAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(rafflePage);
			Assert.isTrue(oldTotalElements != rafflePage.getTotalElements());
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

