
package usecases;

import java.util.Calendar;
import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.CreditCardService;
import utilities.AbstractTest;
import domain.CreditCard;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListCreditCardTest extends AbstractTest {
		
	// System under test ------------------------------------------------------

	@Autowired
	private CreditCardService	creditCardService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario entra en una vista en la que se llama al método findByUserAccountId
	 *		2. Una persona entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 *		3. Un moderador entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 *		4. Un patrocinador entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 *		5. Una compañía entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 * 
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void findByUserAccountIdTest() {
		final Object testingData[][] = {
			{
				"user1", "findByUserAccountId", 6, 0, 0, null
			}, {
				null, "findByUserAccountId", 6, 0, 0, IllegalArgumentException.class
			}, {
				"moderator2", "findByUserAccountId", 6, 0, 0, IllegalArgumentException.class
			}, {
				"sponsor3", "findByUserAccountId", 6, 0, 0, IllegalArgumentException.class
			}, {
				"company1", "findByUserAccountId", 6, 0, 0, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 		1. Un usuario entra en una vista en la que se llama al método findByUserAccountId paginada
	 * 		2. Un usuario entra en una vista en la que se llama al método findByUserAccountId paginada y navega a la segunda página
	 * 		3. Una persona entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 * 		4. Un moderador entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 * 		5. Un patrocinador entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 * 		6. Una compañía entra en una vista en la que se llama al método findByUserAccountId, solo puede el usuario
	 * 
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 *
	 */
	@Test
	public void findByUserAccountIdPageTest() {
		final Object testingData[][] = {
			{
				"user1", "findByUserAccountIdPage", 5, 1, 5, null
			}, {
				"user1", "findByUserAccountIdPage", 1, 2, 5, null
			}, {
				null, "findByUserAccountIdPage", 4, 0, 0, IllegalArgumentException.class
			}, {
				"moderator2", "findByUserAccountIdPage", 1, 2, 1, IllegalArgumentException.class
			}, {
				"sponsor2", "findByUserAccountIdPage", 2, 0, 0, IllegalArgumentException.class				
			}, {
				"company1", "findByUserAccountIdPage", 6, 0, 0, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 		1. Un usuario entra en una vista en la que se llama al método findValidByUserAccountId
	 *		2. Una persona entra en una vista en la que se llama al método findValidByUserAccountId, solo puede el usuario
	 *		3. Un moderador entra en una vista en la que se llama al método findValidByUserAccountId, solo puede el usuario
	 *		4. Un patrocinador entra en una vista en la que se llama al método findValidByUserAccountId, solo puede el usuario
	 *		5. Una compañía entra en una vista en la que se llama al método findValidByUserAccountId, solo puede el usuario
	 * 
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void findValidByUserAccountIdTest() {
		final Object testingData[][] = {
			{
				"user1", "findValidByUserAccountId", 4, 0, 0, null
			}, {
				null, "findValidByUserAccountId", 5, 0, 0, IllegalArgumentException.class
			}, {
				"moderator2", "findValidByUserAccountId", 5, 1, 1, IllegalArgumentException.class
			}, {
				"company1", "findValidByUserAccountId", 1, 1, 1, IllegalArgumentException.class
			}, {
				"sponsor1", "findValidByUserAccountId", 1, 1, 1, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 		1. Nos autentificamos como usuario
	 * 		2. Comprobamos si el método es findValidByUserAccountId ó findByUserAccountId (Page) o findByUserAccountId (Collection)
	 * 		3. En el caso de que sea findByUserAccountId, obtenemos las entidades correspondientes al user para usar el método
	 * 		3. Según el método que sea, se llama a su método y se guarda en la variable sizeCreditCard el tamaño de los resultados de cada método
	 * 		4. Comprobamos que devuelve el valor esperado
	 * 		5. Cerramos sesión
	 */
	protected void template(final String userBean, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<CreditCard> creditCardCollection;
		int sizeCreditCard;
		Calendar calendar;

		sizeCreditCard = 0;
		caught = null;
		calendar = Calendar.getInstance();
		try {
			
			super.authenticate(userBean);
			
			Assert.isTrue(LoginService.isAuthenticated());

			if (method.equals("findByUserAccountId")) {
				creditCardCollection = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId());
				for(CreditCard c: creditCardCollection)
					Assert.isTrue(c.getUser().getUserAccount().equals(LoginService.getPrincipal()));
			} else if (method.equals("findByUserAccountIdPage")) {
				creditCardCollection = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId(), page, size).getContent();
				for(CreditCard c: creditCardCollection)
					Assert.isTrue(c.getUser().getUserAccount().equals(LoginService.getPrincipal()));
			} else {
				creditCardCollection = this.creditCardService.findValidByUserAccountId(LoginService.getPrincipal().getId());
				for(CreditCard c: creditCardCollection)
					if (calendar.get(Calendar.YEAR) % 100 == c.getExpirationYear())
						Assert.isTrue(((c.getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);
					else
						Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < c.getExpirationYear());
			}
			sizeCreditCard = creditCardCollection.size();
			Assert.isTrue(sizeCreditCard == tamano); 
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
