
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
public class DeleteNotificationTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private NotificationService	notificationService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Borrar notificaci�n de un usuario.
	 * 2. Borrar notificaci�n de otro usuario.
	 */
	//CU: Borrar notificaci�n de un usuario
	@Test
	public void driverDeletePositive() {

		//userName, notificationName, expected
		final Object testingData[][] = {
			{
				"user1", "notification1", null
			}, {
				"user2", "notification7", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDelete((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Eliminar notification de otro user. IllegalArgumentException
	 * 2. Eliminar notification sin autenticarse. IllegalArgumentException
	 * 3. Eliminar notification autentic�ndose como moderator. IllegalArgumentException
	 */
	//CU: Borrar notificaci�n de un usuario
	@Test
	public void driverDeleteNegative() {

		//userName, notificationName, expected
		final Object testingData[][] = {
			{
				"user2", "notification2", IllegalArgumentException.class
			}, {
				null, "notification2", IllegalArgumentException.class
			}, {
				"moderator1", "notification2", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar las notificaciones del user.
	 * 3. Escoger una.
	 * 4. Borrarla
	 */

	protected void templateDelete(final String userName, final String notificationName, final Class<?> expected) {
		Class<?> caught;

		Page<Notification> notifications;
		Integer countNotifications;
		Notification notificationChoosen;
		Notification notification;
		int notificationToDeleteId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			notificationChoosen = null;
			notification = this.notificationService.findOne(super.getEntityId(notificationName));
			notificationToDeleteId = notification.getId();

			//Obtenemos las notifications
			notifications = this.notificationService.findByActorId(1, 1);
			countNotifications = notifications.getTotalPages();

			//Buscamos la que queremos modificar
			for (int i = 0; i < countNotifications; i++) {

				notifications = this.notificationService.findByActorId(i + 1, 5);

				//Si estamos pidiendo una p�gina mayor
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

			//Ya tenemos la notification
			Assert.notNull(notificationChoosen);

			notification = this.notificationService.findOneToDisplayAndDelete(notificationChoosen.getId(), "delete");

			this.notificationService.delete(notification);

			this.notificationService.flush();

			//Comprobamos que se actualiza y se borra
			Assert.isNull(this.notificationService.findOne(notificationToDeleteId));

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
	 * 2. Borrar notification por URL. Sin usar los formularios de la aplicaci�n
	 */
	protected void templateDeleteUrl(final String userName, final String notificationName, final Class<?> expected) {
		Class<?> caught;

		Notification notification;
		Integer notificationId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			notificationId = super.getEntityId(notificationName);
			notification = this.notificationService.findOne(notificationId);

			this.notificationService.delete(notification);

			this.notificationService.flush();

			//Comprobamos que no existe ya
			Assert.notNull(this.notificationService.findOne(notificationId));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
