
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.NewspaperService;
import utilities.AbstractTest;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private NewspaperService	newspaperService;


	/*
	 * Test
	 * 1. Nos logeamos como admin y borramos el newspaper1 (no salta excepción)
	 * 2. Nos logeamos como admin y borramos el newspaper2 (no salta excepción)
	 * 3. Nos logeamos como admin y borramos el newspaper3 (no salta excepción)
	 * 4. Nos logeamos como admin y borramos el newspaper4 (no salta excepción)
	 * 5. Nos logeamos como admin y borramos el newspaper5 (no salta excepción)
	 * 6. Nos logeamos como admin y borramos el newspaper6 (no salta excepción)
	 * 7. Nos logeamos como user1 y borramos el newspaper1 (salta un IllegalArgumentException.class)
	 * 8. Nos logeamos como customer1 y borramos el newspaper1 (salta un IllegalArgumentException.class)
	 * 9. Borramos el newspaper1 sin estar logeado (salta un IllegalArgumentException.class)
	 * 
	 * Requisitos:
	 * C.7.2: An actor who is authenticated as an administrator must be able to remove a newspaper that he or she thinks is inappropriate. Removing a newspaper
	 * implies removing all of the articles of which it is composed.
	 */
	@Test
	public void findDeleteTest() {
		final Object testingData[][] = {
			{
				"admin", "admin", "newspaper1", null
			}, {
				"admin", "admin", "newspaper2", null
			}, {
				"admin", "admin", "newspaper3", null
			}, {
				"admin", "admin", "newspaper4", null
			}, {
				"admin", "admin", "newspaper5", null
			}, {
				"admin", "admin", "newspaper6", null
			}, {
				"user", "user1", "newspaper1", IllegalArgumentException.class
			}, {
				"customer", "customer1", "newspaper1", IllegalArgumentException.class
			}, {
				null, null, "newspaper1", IllegalArgumentException.class
			}

		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateDelete((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void templateDelete(final String user, final String username, final String newspaperBean, final Class<?> expected) {
		Class<?> caught;
		int newspaperId;
		Newspaper newspaper;
		int newspaperIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			newspaperId = 0;
			if (user != null) {
				newspaperIdAux = super.getEntityId(newspaperBean);
				for (int i = 1; i <= this.newspaperService.findAllPaginated(1, 5).getTotalPages(); i++)
					for (final Newspaper n : this.newspaperService.findAllPaginated(i, 5).getContent())
						//Si estás logeado lo buscas entre todos los newspapers
						if (newspaperIdAux == n.getId())
							newspaperId = n.getId();
			} else
				newspaperId = super.getEntityId(newspaperBean); //Si no lo cogemos directamente

			newspaper = this.newspaperService.findOne(newspaperId);
			this.newspaperService.delete(newspaper); //Borramos el newspaper

			Assert.isTrue(!this.newspaperService.findAll().contains(newspaper)); //Miramos que el newspaper borrado no esté entre los newspapers del sistema

			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
