package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Administrator;
import domain.Chirp;
import security.LoginService;
import services.AdministratorService;
import services.ChirpService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteChirpTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ChirpService		chirpService;

	@Autowired
	private AdministratorService	administratorService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un administrador trata de borrar un chirp
	 * 
	 * Requisitos:
	 * 		17. An actor who is authenticated as an administrator must be able to: 
	 * 			5. Remove a chirp that he or she thinks is inappropriate.
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"admin", "chirp1", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. 
	 * 
	 * Requisitos:
	 * 		17. An actor who is authenticated as an administrator must be able to: 
	 * 			5. Remove a chirp that he or she thinks is inappropriate.
	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
				{
					"user1", "chirp1", IllegalArgumentException.class
				}, {
					"customer1", "chirp1", IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Crear un chirp
	 * Pasos:
	 * 		1. Autenticar como administador
	 * 		2. Listar chirps
	 * 		2. Eliminar chirp
	 * 		3. Volver al listado de chirp
	 */
	protected void template(final String adminBean, final String chirpBean, final Class<?> expected) {
		Class<?> caught;
		Integer chirpId, pageResult;
		Page<Chirp> chirps;
		Chirp chirp;
		Administrator admin;

		caught = null;
		try {
			
			// 1. Autenticar como administrador
			super.authenticate(adminBean);
			admin = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(admin);
			
			// 2. Listar chirps
			chirpId = super.getEntityId(chirpBean);
			Assert.notNull(chirpId);
			chirp = this.chirpService.findOne(chirpId);
			Assert.notNull(chirp);
			
			chirps = this.chirpService.findAllPaginated(this.getPage(chirp), 5);
			Assert.notNull(chirps);
			
			// 3. Eliminar chirp
			this.chirpService.delete(chirp);
			
			// 4. Volver al listado de chirps
			pageResult = this.getPage(chirp);
			Assert.isNull(pageResult);
//			chirps = this.chirpService.findAllPaginated(pageResult, 5);
//			Assert.isTrue(!chirps.getContent().contains(chirp));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Integer getPage(final Chirp chirp) {
		Integer result;
		Page<Chirp> pageChirp, pageChirpAux;

		pageChirp = this.chirpService.findAllPaginated(0, 5);

		result = null;
		for (int i = 0; i <= pageChirp.getTotalPages(); i++) {
			pageChirpAux = this.chirpService.findAllPaginated(i, 5);
			if (pageChirpAux.getContent().contains(chirp)) {
				result = i;
				break;
			}
		}

		return result;
	}


}

