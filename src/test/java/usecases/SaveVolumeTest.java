
package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Newspaper;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private UserService			userService;


	/*
	 * Test
	 * 1. Logueados como user1 creamos un volumen poniéndole el newspaper1
	 * 2. Logueados como user2 creamos un volumen poniéndole el newspaper2
	 * 3. Logueados como user3 creamos un volumen poniéndole el newspaper3
	 * 4. Logueados como user4 creamos un volumen poniéndole el newspaper1
	 * 5. Logueados como user5 creamos un volumen poniéndole el newspaper1
	 * 6. Logueados como user6 creamos un volumen poniéndole el newspaper1
	 * 7. Logueados como user1 creamos un volumen sobreescribiendo el user por ti mismo poniéndole el newspaper1
	 * 8. Logueados como user1 creamos un volumen poniéndole el newspaper1 sobreescribiendo posteriormente el newspaper por el mismo
	 * 
	 * Requisitos
	 * B.10.1: An actor who is authenticated as a user must be able to create a volume with as many published newspapers as he or she wishes. Note that
	 * the newspapers in a volume can be added or removed at any time. The same
	 * newspaper may be used to create different volumes
	 */
	@Test()
	public void testCreatePositive() {
		final Object testingData[][] = {
			{
				"user", "user1", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, null
			}, {
				"user", "user2", "Volumen1", "Descripción1", 2019, null, "newspaper2", null, null
			}, {
				"user", "user3", "Volumen1", "Descripción1", 2019, null, "newspaper3", null, null
			}, {
				"user", "user4", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, null
			}, {
				"user", "user5", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, null
			}, {
				"user", "user6", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, null
			}, {
				"user", "user1", "Volumen1", "Descripción1", 2019, "user1", "newspaper1", null, null
			}, {
				"user", "user1", "Volumen1", "Descripción1", 2019, null, "newspaper1", "newspaper1", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7],
					(Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Nos logueamos como user1 creamos un volume con los campos de texto a nulo (salta un ConstraintViolationException)
	 * 2.Nos logueamos como user2 creamos un volume con el título a nulo (salta un ConstraintViolationException)
	 * 3.Nos logueamos como user3 creamos un volume con la descripción a nulo (salta un ConstraintViolationException)
	 * 4.Nos logueamos como user5 creamos un volume añadiéndole el newspaper4 que no es público (salta un IllegalArgumentException)
	 * 5.Nos logueamos como user6 creamos un volume añadiéndole el newspaper1 y cambiando el user por el user2 (salta un IllegalArgumentException)
	 * 6.Nos logueamos como user1 creamos un volume añadiéndole el newspaper1 y cambiando el newspaper por el newspaper4 (salta un IllegalArgumentException)
	 * 7.Nos logueamos como user5 creamos un volume añadiéndole el newspaper5 que no está publicado (salta un IllegalArgumentException)
	 * 8.Nos logueamos como customer1 creamos un volume añadiéndole el newspaper1 (salta un IllegalArgumentException)
	 * 9.Nos logueamos como agent1 creamos un volume añadiéndole el newspaper1 (salta un IllegalArgumentException)
	 * 10.Nos logueamos como admin creamos un volume añadiéndole el newspaper1 (salta un IllegalArgumentException)
	 * 11.Sin estar logueados creamos un volume añadiéndole el newspaper1 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testCreateNegative() {
		final Object testingData[][] = {
			{
				"user", "user1", null, null, 0, null, "newspaper1", null, ConstraintViolationException.class
			}, {
				"user", "user2", null, "Descripción1", 2019, null, "newspaper2", null, ConstraintViolationException.class
			}, {
				"user", "user3", "Volumen1", null, 2019, null, "newspaper3", null, ConstraintViolationException.class
			}, {
				"user", "user5", "Volumen1", "Descripción1", 2019, null, "newspaper4", null, IllegalArgumentException.class
			}, {
				"user", "user6", "Volumen1", "Descripción1", 2019, "user2", "newspaper1", null, IllegalArgumentException.class
			}, {
				"user", "user1", "Volumen1", "Descripción1", 2019, "user1", "newspaper1", "newspaper4", IllegalArgumentException.class
			}, {
				"user", "user5", "Volumen1", "Descripción1", 2019, null, "newspaper5", null, IllegalArgumentException.class
			}, {
				"customer", "customer1", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, IllegalArgumentException.class
			}, {
				"agent", "agent1", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, IllegalArgumentException.class
			}, {
				"admin", "admin", "Volumen1", "Descripción1", 2019, null, "newspaper1", null, IllegalArgumentException.class
			}, {
				null, null, "Volumen1", "Descripción1", 2019, null, "newspaper1", null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7],
					(Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String title, final String description, final int year, final String userBean, final String newspaperBean, final String newspaperBean2, final Class<?> expected) {
		Class<?> caught;
		Volume volume;
		Volume saved;
		int newspaperIdAux;
		int newspaperId;

		DataBinder binder;
		Volume volumeReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			newspaperIdAux = super.getEntityId(newspaperBean);
			newspaperId = 0;
			if (user != null && user.equals("user")) {
				for (int i = 1; i <= this.newspaperService.findPublished(1, 5).getTotalPages(); i++)
					//Si estás como user cogemos el newspaper entre todos los publicados
					for (final Newspaper n : this.newspaperService.findPublished(i, 5).getContent())
						if (n.getId() == newspaperIdAux)
							newspaperId = n.getId();
			} else
				newspaperId = super.getEntityId(newspaperBean); //Si no se coge directamente
			volume = this.volumeService.create(newspaperId); //Creamos el volume
			volume.setTitle(title); //Editamos el título

			volume.setDescription(description);//Modificamos la descripción
			volume.setYear(year); //Editamos el año
			if (userBean != null)
				volume.setUser(this.userService.findOne(super.getEntityId(userBean))); //Editamos el user para hackeos
			if (newspaperBean2 != null) {
				volume.getNewspapers().clear();
				volume.getNewspapers().add(this.newspaperService.findOne(super.getEntityId(newspaperBean2))); //Cambiamos el newspaper para hackeos
			}

			binder = new DataBinder(volume);
			volumeReconstruct = this.volumeService.reconstruct(volume, binder.getBindingResult()); //Reconstruimos el volume
			saved = this.volumeService.save(volumeReconstruct); //Guardamos el volume
			super.flushTransaction();

			Assert.isTrue(this.volumeService.findAll().contains(saved)); //Miramos si están entre todos los volume de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
