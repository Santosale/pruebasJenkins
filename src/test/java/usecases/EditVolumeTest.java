
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

import security.LoginService;
import services.NewspaperService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Newspaper;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private VolumeService		volumeService;


	/*
	 * Test
	 * 1.Editamos el volume1 logeados como user1 (no salta excepción)
	 * 2.Editamos el volume2 logeados como user2 (no salta excepción)
	 * 3.Editamos el volume3 logeados como user3 (no salta excepción)
	 * 4.Editamos el volume1 logeados como user1 poniendo el título y la descripción a nulo (salta un ConstraintViolationException)
	 * 5.Editamos el volume1 logeados como user1 poniendo la descripción a nulo (salta un ConstraintViolationException)
	 * 6.Editamos el volume1 logeados como user1 poniendo el título a nulo (salta un ConstraintViolationException)
	 * 7.Editamos el volume2 logeados como user2 el cual no tiene ese volumen (salta un IllegalArgumentException)
	 */
	@Test()
	public void testEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "volume1", "Volumen1", "Descripción1", 2019, true, null
			}, {
				"user", "user2", "volume2", "Volumen1", "Descripción1", 2019, true, null
			}, {
				"user", "user3", "volume3", "Volumen1", "Descripción1", 2019, true, null
			}, {
				"user", "user1", "volume1", null, null, 2019, true, ConstraintViolationException.class
			}, {
				"user", "user1", "volume1", "Volumen1", null, 2019, true, ConstraintViolationException.class
			}, {
				"user", "user1", "volume1", null, "Descripción", 2019, true, ConstraintViolationException.class
			}, {
				"user", "user1", "volume2", "Volumen1", "Descripción1", 2019, true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (int) testingData[i][5], (boolean) testingData[i][6], (Class<?>) testingData[i][7]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logeados como user1 le añadimos al volume1 el newspaper2 (no salta excepción)
	 * 2.Logeados como user2 le añadimos al volume2 el newspaper1 (no salta excepción)
	 * 3.Logeados como user1 le añadimos al volume1 el newspaper1 el cual ya tiene (salta un IllegalArgumentException)
	 * 4.Logeados como user2 le añadimos al volume1 que no es suyo el newspaper2(salta un IllegalArgumentException)
	 * 5.Logeados como user1 le añadimos al volume1 el newspaper5 que no está publicado (salta un IllegalArgumentException)
	 * 6.Logeados como customer1 le añadimos al volume1 el newspaper2 (salta un IllegalArgumentException)
	 * 7.Logeados como agent1 le añadimos al volume1 el newspaper2 (salta un IllegalArgumentException)
	 * 8.Logeados como admin le añadimos al volume1 el newspaper2 (salta un IllegalArgumentException)
	 * 9.Sin estar logeado le añadimos al volume1 el newspaper2 (salta un IllegalArgumentException)
	 * 
	 * Requisitos:
	 * B.10.1: An actor who is authenticated as an administrator must be able to Create a volume with as many published newspapers as he or she wishes. Note that
	 * the newspapers in a volume can be added or removed at any time. The same
	 * newspaper may be used to create different volumes
	 */
	@Test()
	public void testAddNewspaper() {
		final Object testingData[][] = {
			{
				"user", "user1", "volume1", "newspaper2", true, null
			}, {
				"user", "user2", "volume2", "newspaper1", true, null
			}, {
				"user", "user1", "volume1", "newspaper1", false, IllegalArgumentException.class
			}, {
				"user", "user2", "volume1", "newspaper2", false, IllegalArgumentException.class
			}, {
				"user", "user1", "volume1", "newspaper5", false, IllegalArgumentException.class
			}, {
				"customer", "customer1", "volume1", "newspaper2", false, IllegalArgumentException.class
			}, {
				"agent", "agent1", "volume1", "newspaper2", false, IllegalArgumentException.class
			}, {
				"admin", "admin", "volume1", "newspaper2", false, IllegalArgumentException.class
			}, {
				null, null, "volume1", "newspaper2", false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateAddNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logeados como user3 le borramos al volume3 el newspaper1 (no salta excepción)
	 * 2.Logeados como user3 le borramos al volume3 el newspaper3 (no salta excepción)
	 * 3.Logeados como user1 le borramos al volume1 el newspaper1 que es el único que tiene(salta un IllegalArgumentException)
	 * 4.Logeados como user1 le borramos al volume1 el newspaper2 que no es del volumen(salta un IllegalArgumentException)
	 * 5.Logeados como user1 le borramos al volume1 el newspaper5 que no es del volumen y no está publicado(salta un IllegalArgumentException)
	 * 6.Logeados como user1 le borramos al volume3 que no es nuestro el newspaper1 (salta un IllegalArgumentException)
	 * 7.Logeados como customer1 le borramos al volume3 el newspaper1 (salta un IllegalArgumentException)
	 * 8.Logeados como agent1 le borramos al volume3 el newspaper1 (salta un IllegalArgumentException)
	 * 9.Logeados como admin le borramos al volume3 el newspaper1 (salta un IllegalArgumentException)
	 * 10.Sin estar logeados le borramos al volume3 el newspaper1 (salta un IllegalArgumentException)
	 * 
	 * Requisitos:
	 * B.10.1: An actor who is authenticated as an administrator must be able to Create a volume with as many published newspapers as he or she wishes. Note that
	 * the newspapers in a volume can be added or removed at any time. The same
	 * newspaper may be used to create different volumes
	 */
	@Test()
	public void testDeleteNewspaper() {
		final Object testingData[][] = {
			{
				"user", "user3", "volume3", "newspaper1", true, null
			}, {
				"user", "user3", "volume3", "newspaper3", true, null
			}, {
				"user", "user1", "volume1", "newspaper1", false, IllegalArgumentException.class
			}, {
				"user", "user1", "volume1", "newspaper2", false, IllegalArgumentException.class
			}, {
				"user", "user1", "volume1", "newspaper5", false, IllegalArgumentException.class
			}, {
				"user", "user1", "volume3", "newspaper1", false, IllegalArgumentException.class
			}, {
				"customer", "customer1", "volume3", "newspaper1", false, IllegalArgumentException.class
			}, {
				"agent", "agent1", "volume3", "newspaper1", false, IllegalArgumentException.class
			}, {
				"admin", "admin", "volume3", "newspaper1", false, IllegalArgumentException.class
			}, {
				null, null, "volume3", "newspaper1", false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateDeleteNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	/*
	 * Test
	 * 1. Hacemos el findOneToEdit del volume1 logeados como user1 (no salta excepción)
	 * 2. Hacemos el findOneToEdit del volume2 logeados como user2 (no salta excepción)
	 * 3. Hacemos el findOne del volume3 logeados como user2 (no salta excepción)
	 * 4. Hacemos el findOneToEdit del volume2 logeados como user1 que no tiene dicho volumen (salta un IllegalArgumentException)
	 * 5. Hacemos el findOneToEdit de un volumen con id 0 (salta un IllegalArgumentException)
	 * 6. Hacemos el findOne de un volumen con id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "volume1", false, true, true, null
			}, {
				"user", "user2", "volume2", false, true, true, null
			}, {
				"user", "user2", "volume3", false, false, false, null
			}, {
				"user", "user1", "volume2", false, true, true, IllegalArgumentException.class
			}, {
				"user", "user1", "volume1", true, false, true, IllegalArgumentException.class
			}, {
				"user", "user1", "volume3", true, false, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String volumeBean, final String title, final String description, final int year, final boolean myVolume, final Class<?> expected) {
		Class<?> caught;
		Volume volume;
		Volume saved;
		Volume copyVolume;
		int volumeIdAux;
		int volumeId;

		DataBinder binder;
		Volume volumeReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			volumeIdAux = super.getEntityId(volumeBean);
			volumeId = 0;
			if (user != null && myVolume == true) { //Si es de tus volúmenes lo pillas de tu lista de volúmenes
				for (int i = 1; i <= this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5).getTotalPages(); i++)
					for (final Volume v : this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), i, 5).getContent())
						if (v.getId() == volumeIdAux)
							volumeId = v.getId();
			} else
				volumeId = super.getEntityId(volumeBean); //Si no lo pillas directamente para los hackeos

			volume = this.volumeService.findOneToEdit(volumeId); //Creamos el volume
			copyVolume = this.copyVolume(volume);
			copyVolume.setTitle(title);

			copyVolume.setDescription(description);
			copyVolume.setYear(year);
			//Editamos sus parámetros

			binder = new DataBinder(copyVolume);
			volumeReconstruct = this.volumeService.reconstruct(copyVolume, binder.getBindingResult()); //LO reconstruimos
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

	protected void templateAddNewspaper(final String user, final String username, final String volumeBean, final String newspaperBean, final boolean correctBeans, final Class<?> expected) {
		Class<?> caught;
		int newspaperId;
		final int newspaperIdAux;
		int volumeIdAux;
		int volumeId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario
			newspaperId = 0;
			volumeId = 0;
			if (user != null && user.equals("user")) {
				if (correctBeans == false)
					volumeId = super.getEntityId(volumeBean); // Si no es tu volumen pillas la id directamente
				else { //Si no lo miras entre tus volúmenes
					volumeIdAux = super.getEntityId(volumeBean);
					for (int i = 1; i <= this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5).getTotalPages(); i++)
						for (final Volume v : this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), i, 5))
							//Cogemos el newspaper entre los nuestros
							if (volumeIdAux == v.getId()) {
								volumeId = v.getId();
								break;
							}
				}

			} else
				//Si no eres un user cogemos la id directamente para probar hackeos
				volumeId = super.getEntityId(volumeBean);

			if (user != null && user.equals("user")) {
				if (correctBeans == false)
					newspaperId = super.getEntityId(newspaperBean); // Si los newspapers no son tuyos pillas la id directamente
				else { //Lo cogemos entre los posibles newspapers para añadir
					newspaperIdAux = super.getEntityId(newspaperBean);
					for (int i = 1; i <= this.newspaperService.findAddNewspaper(volumeId, 1, 5).getTotalPages(); i++)
						for (final Newspaper n : this.newspaperService.findAddNewspaper(volumeId, i, 5))
							//Cogemos el newspaper entre los nuestros
							if (newspaperIdAux == n.getId()) {
								newspaperId = n.getId();
								break;
							}
				}

			} else
				//Si no cogemos la id directamente
				newspaperId = super.getEntityId(newspaperBean); //Si no estás logeado o no eres user coges la id directamente

			this.volumeService.addNewspaper(volumeId, newspaperId); //Añadimos el periódico al volumen

			super.flushTransaction();

			Assert.isTrue(this.volumeService.findOne(volumeId).getNewspapers().contains(this.newspaperService.findOne(newspaperId))); //Miramos que el periódico esté entre la lista de periódicos del volumen

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateDeleteNewspaper(final String user, final String username, final String volumeBean, final String newspaperBean, final boolean correctBeans, final Class<?> expected) {
		Class<?> caught;
		int newspaperId;
		final int newspaperIdAux;
		int volumeIdAux;
		int volumeId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario
			newspaperId = 0;
			volumeId = 0;
			if (user != null && user.equals("user")) {
				if (correctBeans == false)
					volumeId = super.getEntityId(volumeBean); // Si el volumen no es tuyo coges la id directamente
				else { //Si no lo coges entre tus volúmenes
					volumeIdAux = super.getEntityId(volumeBean);
					for (int i = 1; i <= this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5).getTotalPages(); i++)
						for (final Volume v : this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), i, 5))
							//Cogemos el newspaper entre los nuestros
							if (volumeIdAux == v.getId()) {
								volumeId = v.getId();
								break;
							}
				}

			} else
				//Si no coges la id directamemte para probar hackeos
				volumeId = super.getEntityId(volumeBean);

			if (user != null && user.equals("user")) {
				if (correctBeans == false)
					newspaperId = super.getEntityId(newspaperBean); // Si los newspapers no son tuyos pillas la id directamente
				else {
					newspaperIdAux = super.getEntityId(newspaperBean);
					for (final Newspaper n : this.volumeService.findOne(volumeId).getNewspapers())
						//Cogemos el newspaper entre los nuestros
						if (newspaperIdAux == n.getId()) {
							newspaperId = n.getId();
							break;
						}
				}

			} else
				newspaperId = super.getEntityId(newspaperBean); //Si no estás logeado o no eres user coges la id directamente

			this.volumeService.deleteNewspaper(volumeId, newspaperId); //Borramos el newspaper

			super.flushTransaction();

			Assert.isTrue(!this.volumeService.findOne(volumeId).getNewspapers().contains(this.newspaperService.findOne(newspaperId))); //Miramos que ya no esté entre la lista de newspapers

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String volumeBean, final boolean falseId, final boolean myVolume, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int volumeId;
		final Volume volume;
		final int volumeIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			volumeId = 0;
			if (myVolume == true) { //Si es de tus volúmenes lo coges desde el listado de tus volúmenes
				volumeIdAux = super.getEntityId(volumeBean);
				for (int i = 1; i <= this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5).getTotalPages(); i++)
					for (final Volume v : this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), i, 5).getContent())
						if (volumeIdAux == v.getId())
							volumeId = v.getId();
			} else
				volumeId = super.getEntityId(volumeBean); // Si no cogemos la id directamente para simular hackeos

			if (findOneToEdit == true) {
				if (falseId == false)
					volume = this.volumeService.findOneToEdit(volumeId); //Se prueba el findOneToEdit
				else
					volume = this.volumeService.findOneToEdit(0); //Se prueba el findOneToEdit con id 0

			} else if (falseId == false)
				volume = this.volumeService.findOne(volumeId); //Se prueba el findOne
			else
				volume = this.volumeService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(volume); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	private Volume copyVolume(final Volume volume) {
		Volume result;

		result = new Volume();
		result.setId(volume.getId());
		result.setVersion(volume.getVersion());
		result.setTitle(volume.getTitle());
		result.setDescription(volume.getDescription());
		result.setYear(volume.getYear());
		result.setUser(volume.getUser());
		result.setNewspapers(volume.getNewspapers());

		return result;
	}
}
