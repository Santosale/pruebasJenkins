
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.ActorService;
import services.GrouponService;
import services.ParticipationService;
import utilities.AbstractTest;
import domain.Groupon;
import domain.Participation;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListParticipationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private GrouponService			grouponService;

	@Autowired
	private ActorService			actorService;


	/*
	 * Pruebas:
	 * 1. Probamos el findAll estando logeados como user
	 * 2. Probamos el findAll estando logeados como moderator
	 * 3. Probamos el findAll estando logeados como admin
	 * 4. Probamos el findAll estando logeados como company
	 * 5. Probamos el findAll estando logeados como sponsor
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 13, null, null, null
			}, {
				"moderator", "moderator1", "findAll", false, null, null, 13, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 13, null, null, null
			}, {
				"company", "company1", "findAll", false, null, null, 13, null, null, null
			}, {
				"sponsor", "sponsor1", "findAll", false, null, null, 13, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Probamos el findByGrouponId del groupon1 estando logueados como user1
	 * 2. Probamos el findByGrouponId del groupon1 estando logueados como moderator1
	 * 3. Probamos el findByGrouponId del groupon2 estando logueados como user1
	 * 4. Probamos el findByGrouponId del groupon3 estando logueados como moderator1
	 * 5. Probamos el findByGrouponId del groupon4 estando logueados como moderator1
	 * 6. Probamos el findByGrouponId de un groupon con id 0 estando logueados como user2 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByGrouponId() {
		final Object testingData[][] = {
			{
				"user", "user1", "findByGrouponId", false, "groupon1", null, 5, null, null, null
			}, {
				"moderator", "moderator1", "findByGrouponId", false, "groupon1", null, 5, null, null, null
			}, {
				"user", "user1", "findByGrouponId", false, "groupon2", null, 1, null, null, null
			}, {
				"moderator", "moderator1", "findByGrouponId", false, "groupon3", null, 3, null, null, null
			}, {
				"moderator", "moderator1", "findByGrouponId", false, "groupon4", null, 2, null, null, null
			}, {
				"user", "user2", "findByGrouponId", true, "groupon4", null, 2, null, null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Hacemos el findByUserId logueados como user2 viendo la primera página con tamaño 6 (no salta excepción)
	 * 2. Hacemos el findByUserId logueados como user2 viendo la primera página con tamaño 3 (no salta excepción)
	 * 3. Hacemos el findByUserId logueados como user2 viendo la segunda página con tamaño 3 (no salta excepción)
	 * 4. Hacemos el findByUserId logueados como user2 viendo la tercera página con tamaño 3 (no salta excepción)
	 * 5. Hacemos el findByUserId logueados como user2 viendo la primera página con tamaño 7 (no salta excepción)
	 * 6. Hacemos el findByUserId logueados como sponsor(salta un IllegalArgumentException)
	 * 7. Hacemos el findByUserId logueados como moderator(salta un IllegalArgumentException)
	 * 8. Hacemos el findByUserId logueados como company(salta un IllegalArgumentException)
	 * 9. Hacemos el findByUserId logueados como admin(salta un IllegalArgumentException)
	 * 10.Hacemos el findByUserId sin estar logueados (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByUserId() {
		final Object testingData[][] = {
			{
				"user", "user2", "findByUserId", false, null, 1, 6, 1, 6, null
			}, {
				"user", "user2", "findByUserId", false, null, 1, 3, 2, 3, null
			}, {
				"user", "user2", "findByUserId", false, null, 2, 3, 2, 3, null
			}, {
				"user", "user2", "findByUserId", false, null, 3, 0, 2, 3, null
			}, {
				"user", "user2", "findByUserId", false, null, 1, 6, 1, 7, null
			}, {
				"sponsor", "sponsor1", "findByUserId", false, null, 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "findByUserId", false, null, 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"company", "company1", "findByUserId", false, null, 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByUserId", false, null, 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				null, null, "findByUserId", false, null, 1, 6, 1, 6, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template2((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer tam, final Integer numPages, final Class<?> expected) {
		Class<?> caught;
		Collection<Participation> participations;
		int grouponIdAux;
		int grouponId;

		caught = null;
		participations = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				participations = this.participationService.findAll(); //Cogemos todos las suscripciones usando el findAll 
			if (method.equals("findByGrouponId")) {
				grouponIdAux = super.getEntityId(bean);
				grouponId = 0;
				for (int i = 1; i <= this.grouponService.findAllPaginated(1, 5).getTotalPages(); i++)
					for (final Groupon g : this.grouponService.findAllPaginated(i, 5).getContent())
						//Cogemos el groupon entre todos
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
				if (falseId == false)
					participations = this.participationService.findByGrouponId(grouponId);
				else
					participations = this.participationService.findByGrouponId(0);
			}

			Assert.isTrue(participations.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<Participation> participations;
		int userId;

		caught = null;
		participations = null;

		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findByUserId")) {
				if (user == null)
					userId = 0;
				else
					userId = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId()).getId();

				participations = this.participationService.findByUserId(userId, page, tam); //Cogemos las participaciones del usuario

			}

			Assert.isTrue(participations.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(participations.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
