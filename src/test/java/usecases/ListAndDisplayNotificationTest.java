
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.NotificationService;
import utilities.AbstractTest;
import domain.Notification;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListAndDisplayNotificationTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private NotificationService	notificationService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Listar las notificaciones de un usuario y escoger una no abierta
	 * 2. Listar las notificaciones de otro usuario y escoger una abierta
	 */
	//CU: listar notificaciones de un usuario.
	@Test
	public void driverPositive() {

		//userName, notificationName, visited, expected
		final Object testingData[][] = {
			{
				"user1", "notification1", false, null
			}, {
				"user2", "notification7", true, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Display notification sin autenticar. IllegalArgumentException
	 * 2. Display notification con usuario distinto a al suyo. IllegalArgumentException
	 * 3. Display notification con company. IllegalArgumentException
	 */
	//CU: listar notificaciones de un usuario.
	@Test
	public void driverDeleteNegative() {

		//userName, notificationName, expected
		final Object testingData[][] = {
			{
				null, "notification2", IllegalArgumentException.class
			}, {
				"user2", "notification2", IllegalArgumentException.class
			}, {
				"company1", "notification3", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDisplayUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar las notificaciones
	 * 3. Escoger una.
	 */

	protected void templateListDisplay(final String userName, final String notificationName, final Boolean visited, final Class<?> expected) {
		Class<?> caught;

		Page<Notification> notifications;
		Integer countNotifications;
		Notification notification;
		Notification notificationChoosen;
		int countNotVisitedBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			notificationChoosen = null;
			countNotVisitedBefore = this.notificationService.countNotVisitedByActorId();
			notification = this.notificationService.findOne(super.getEntityId(notificationName));

			//Obtenemos las notifications
			notifications = this.notificationService.findByActorId(1, 1);

			countNotifications = notifications.getTotalPages();

			//Buscamos la que queremos modificar
			for (int i = 0; i < countNotifications; i++) {
				notifications = this.notificationService.findByActorId(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (notifications.getContent().size() == 0)
					break;

				// Navegar hasta la notification que queremos.
				for (final Notification newNotification : notifications.getContent())
					if (newNotification.equals(notification)) {
						notificationChoosen = newNotification;
						break;
					}

				if (notificationChoosen != null)
					break;
			}

			Assert.notNull(notificationChoosen);

			this.notificationService.findOneToDisplayAndDelete(notificationChoosen.getId(), "display");

			this.notificationService.flush();

			if (visited)
				Assert.isTrue(this.notificationService.countNotVisitedByActorId() == countNotVisitedBefore);
			else
				Assert.isTrue(this.notificationService.countNotVisitedByActorId() == countNotVisitedBefore - 1);
			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Acceder a notification por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDisplayUrl(final String userName, final String notificationName, final Class<?> expected) {
		Class<?> caught;

		Integer notificationId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			notificationId = super.getEntityId(notificationName);

			this.notificationService.findOneToDisplayAndDelete(notificationId, "display");

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
