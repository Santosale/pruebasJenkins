
package usecases;

import java.util.Collection;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.TicketService;
import utilities.AbstractTest;
import domain.Ticket;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListTicketTest extends AbstractTest {
		
	// System under test ------------------------------------------------------
	
	@Autowired
	private TicketService ticketService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario entra en el listado de tiques
	 *		2. Una persona entra en el listado de tiques pero tiene que ser usuario
	 * 		3. Un moderador entra en el listado de tiques pero tiene que ser usuario
	 * 		4. Un patrocinador entra en el listado de tiques pero tiene que ser usuario
	 * 		5. Una compañía entra en el listado de tiques pero tiene que ser usuario
	 * 		6. Un usuario entra en el listado de tiques y navega de forma incorrecta
	 * 
	 * Requisitos:
	 * 		15.	Un usuario puede conseguir tiques para una rifa...
	 * 	
	 */
	@Test
	public void findByUserAccountIdTest() {
		final Object testingData[][] = {
			{
				"user1", "findByUserAccountId", 5, 0, 5, null, null
			}, {
				null, "findByUserAccountId", 0, 2, 5, null, IllegalArgumentException.class
			}, {
				"moderator2", "findByUserAccountId", 5, 0, 5, null, IllegalArgumentException.class
			}, {
				"sponsor3", "findByUserAccountId", 5, 0, 5, null, IllegalArgumentException.class
			}, {
				"company1", "findByUserAccountId", 5, 0, 5, null, IllegalArgumentException.class
			}, {
				"user1", "findByUserAccountId", 2, 3, 5, null, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 		1. Operacionalmente nos autenticamos
	 * 		2. Comprobamos si el método es findByUserAccountId
	 * 		4. Comprobamos que devuelve el valor esperado
	 * 		5. Cerramos sesión
	 */
	protected void template(final String userBean, final String method, final Integer tamano, final int page, final int size, final String raffleBean, final Class<?> expected) {
		Class<?> caught;
		Collection<Ticket> ticketCollection;
		Integer sizeRaffle;

		sizeRaffle = 0;
		caught = null;
		ticketCollection = null;
		try {
			
			if(userBean != null) super.authenticate(userBean);
			
			if (method.equals("findByUserAccountId")) {
				ticketCollection = this.ticketService.findByUserAccountId(LoginService.getPrincipal().getId(), page, size).getContent();
				Assert.notNull(ticketCollection);
				for(Ticket t: ticketCollection)
					Assert.isTrue(t.getUser().getUserAccount().equals(LoginService.getPrincipal()));
			}
			
			sizeRaffle = ticketCollection.size();
			Assert.isTrue(sizeRaffle == tamano); 
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
