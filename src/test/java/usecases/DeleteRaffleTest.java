package usecases;

import java.util.Collection;

import javax.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import domain.Raffle;
import services.RaffleService;
import services.TicketService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteRaffleTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private RaffleService		raffleService;
	
	@Autowired
	private TicketService		ticketService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Una persona autenticada como moderator1 trata de borrar la rifa raffle1
	 *		2. Una persona autenticada como moderator2 trata de borrar la rifa raffle2
	 *		3. Una persona autenticada como company1 trata de borrar la rifa raffle3 que no tiene tiques
	 *
	 * Requisitos:
	 * 		26.5. Un actor que está autenticado como moderador debe ser capaz de eliminar una rifa si la considera inapropiada. 
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"moderator1", "raffle1", null
			}, {
				"moderator2", "raffle2", null
			}, {
				"company1", "raffle3", null
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
	 * 		1. Una compañía trata de eliminar una rifa que no es suya
	 * 		2. Una compañía trata de eliminar una rifa que tiene ya tiques
	 * 		3. Un usuario trata de borrar una rifa cuando no puede
	 * 		4. Un patrocinador trata de borrar una rifa cuando no puede
	 * 
	 * Requisitos:
	 * 		26.5. Un actor que está autenticado como moderador debe ser capaz de eliminar una rifa si la considera inapropiada. 
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"company4", "raffle1", IllegalArgumentException.class
				}, {
					"company1", "raffle1", IllegalArgumentException.class
				}, {
					"user1", "raffle1", IllegalArgumentException.class
				}, {
					"sponsor2", "raffle1", IllegalArgumentException.class
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
	 * Borrar una rifa
	 * Pasos:
	 * 		1. Autenticar como compañía o moderador
	 * 		2. Listar rifas
	 * 		3. Eliminar rifa
	 * 		4. Volver al listado de rifas
	 */
	protected void template(final String actorBean, final String raffleBean, final Class<?> expected) {
		Class<?> caught;
		Raffle raffle;
		Collection<Raffle> raffles;
		Integer raffleId, oldTickets;
		int oldTotalElements;

		caught = null;
		try {
			
			// 1. Autenticar como compañía o moderador
			super.authenticate(actorBean);
			
			raffleId = super.getEntityId(raffleBean);
			Assert.notNull(raffleId);
			
			raffle = this.raffleService.findOne(raffleId);
			Assert.notNull(raffle);
			
			// 2. Listar rifas
			raffles = this.raffleService.findAll();
			Assert.notNull(raffles);
			
			oldTotalElements = raffles.size();
			oldTickets = this.ticketService.findAll().size();
			
			// 3. Eliminar rifa
			this.raffleService.delete(raffle);
			
			this.raffleService.flush();
			this.ticketService.flush();
									
			// 4. Volver al listado de rifas
			raffles = this.raffleService.findAll();
			Assert.notNull(raffles);
			Assert.isTrue(oldTotalElements != raffles.size());
			if(oldTickets != 31)
				Assert.isTrue(oldTickets != this.ticketService.findAll().size());
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

