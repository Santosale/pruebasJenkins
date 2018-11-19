package usecases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

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
public class EditRaffleTest extends AbstractTest {

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
	 * 		1. La compañía company3 edita una rifa quitando el campo productUrl y productImages
	 * 		2. La compañía company3 edita una rifa editando los campos
	 *		3. La compañía company3 edita una rifa poniendo dos imágenes
	 *		4. La compañía company3 edita una rifa poniéndola como gratuita
	 *
	 * Requisitos:
	 * 		23.6. Un actor que está autenticado como empresa debe ser capaz de 
	 * 		listar las rifas que ha creado, editarlas y borrarlas 
	 * 		si todavía nadie ha comprado un tique.
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"company3", "raffle14", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, null
			}, {
				"company3", "raffle14", "Raffle title", "Raffle description", "Product name", "http://example.com/", "http://example.com/image.jpg", "02/12/2018", 10.0, null, null
			}, {
				"company3", "raffle14", "Raffle title", "Raffle description", "Product name", "http://example.com/", "http://example.com/image.jpg,http://example.com/image.jpg", "02/12/2018", 10.0, null, null
			}, {
				"company3", "raffle14", "Raffle title", "Raffle description", "Product name", "http://example.com/", "http://example.com/image.jpg,http://example.com/image.jpg", "02/12/2018", 0.0, null, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Double) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. Una compañía trata de editar una rifa que ya ha sido sorteada
	 * 	 	2. Una compañía trata de editar una rifa que no es suya
	 * 		3. Una usuario trata de editar una rifa cuando solo pueden las compañías
	 * 		4. Un patrocinador trata de editar una rifa cuando solo pueden las compañías
	 * 		5. Un moderador trata de editar una rifa cuando solo pueden las compañías
	 * 
	 * Requisitos:
	 * 		23.6. Un actor que está autenticado como empresa debe ser capaz de 
	 * 		listar las rifas que ha creado, editarlas y borrarlas 
	 * 		si todavía nadie ha comprado un tique.	
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"company4", "raffle12", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, IllegalArgumentException.class
				}, {
					"company2", "raffle10", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, IllegalArgumentException.class
				}, {
					"user1", "raffle10", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, IllegalArgumentException.class
				}, {
					"sponsor1", "raffle10", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, IllegalArgumentException.class
				}, {
					"moderator1", "raffle10", "Raffle title", "Raffle description", "Product name", null, null, "02/12/2018", 10.0, null, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Double) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Editar una rifa
	 * Pasos:
	 * 		1. Autenticar como compañía
	 * 		2. Listar rifas
	 * 		3. Entrar en vista de editar rifa
	 * 		4. Editar rifa
	 * 		5. Volver al listado de rifas
	 */
	protected void template(final String actorBean, final String raffleBean, final String title, final String description, final String productName, final String productUrl, final String productImages, final String maxDate, final Double price, final String winnerBean, final Class<?> expected) {
		Class<?> caught;
		SimpleDateFormat format;
		Raffle raffle, reconstructedRaffle, newRaffle, oldRaffle;
		Page<Raffle> rafflePage;
		Company company;
		Integer companyId, winnerId, raffleId;
		Long oldTotalElements;
		User winner;
		DataBinder binder;
		Collection<String> productI;
		Date maxDateO;

		caught = null;
		winner = null;
		productI = new ArrayList<String>();
		try {
			
			// 1. Autenticar como compañía
			super.authenticate(actorBean);
			
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
			
			raffleId = super.getEntityId(raffleBean);
			Assert.notNull(raffleId);
			
			// 2. Listar rifas
			rafflePage = this.raffleService.findByCompanyAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(rafflePage);
			
			oldTotalElements = rafflePage.getTotalElements();
			
			// 3. Entrar en vista de editar rifa
			raffle = this.raffleService.findOneToEdit(raffleId);
			oldRaffle = this.copyRaffle(raffle);
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
			
			// 4. Editar rifa
			binder = new DataBinder(raffle);
			reconstructedRaffle = this.raffleService.reconstruct(raffle, binder.getBindingResult());
			newRaffle = this.raffleService.save(reconstructedRaffle);
			
			this.raffleService.flush();
									
			// 5. Volver al listado de rifas
			rafflePage = this.raffleService.findByCompanyAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(rafflePage);
			Assert.isTrue(oldTotalElements == rafflePage.getTotalElements());
			Assert.isTrue(!oldRaffle.getTitle().equals(newRaffle.getTitle()));
			Assert.isTrue(!oldRaffle.getDescription().equals(newRaffle.getDescription()));
			Assert.isTrue(!oldRaffle.getProductName().equals(newRaffle.getProductName()));
			if(newRaffle.getProductUrl() != null) Assert.isTrue(!newRaffle.getProductUrl().equals(oldRaffle.getProductUrl()));
			if(oldRaffle.getProductUrl() != null) Assert.isTrue(!oldRaffle.getProductUrl().equals(newRaffle.getProductUrl()));
			if(newRaffle.getProductImages() != null) Assert.isTrue(!newRaffle.getProductImages().equals(oldRaffle.getProductImages()));
			if(oldRaffle.getProductImages() != null) Assert.isTrue(!oldRaffle.getProductImages().equals(newRaffle.getProductImages()));
			Assert.isTrue(!oldRaffle.getMaxDate().equals(newRaffle.getMaxDate()));

			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Raffle copyRaffle(final Raffle raffle) {
		Raffle result;
		
		Assert.notNull(raffle);
		
		result = new Raffle();
		result.setId(raffle.getId());
		result.setVersion(raffle.getVersion());
		result.setTitle(raffle.getTitle());
		result.setDescription(raffle.getDescription());
		result.setMaxDate(raffle.getMaxDate());
		result.setProductName(raffle.getProductName());
		result.setProductUrl(raffle.getProductUrl());
		result.setProductImages(raffle.getProductImages());
		result.setPrice(raffle.getPrice());
		result.setCompany(raffle.getCompany());
		result.setWinner(raffle.getWinner());
		
		return result;
	}

}

