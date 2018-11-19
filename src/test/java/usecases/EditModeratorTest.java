package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import domain.Moderator;

import security.UserAccount;
import services.ModeratorService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditModeratorTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ModeratorService		moderatorService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Probando editar moderator con telefono y direccion a null
	 * 		2. Probando editar moderator con telefono pero con direccion a null
	 * 		3. Probando editar moderator con telefono a vacio y direccion a null
	 * 		4. Probando editar moderator con telefono a null y direccion
	 * 		5. Probando editar moderator con telefono a null y direccion a vacio
	 * 		6. Probando editar moderator con telefono y direccion
	 * 		7. Probando editar moderator con telefono y direccion a vacio
	 * 
	 * Requsitos:
	 * 		Los actores pueden editar su perfil
	 */
	@Test
	public void positiveEditModeratorTest() {
		final Object testingData[][] = {
			{
				"moderator1", "moderator1", "moderator1", "moderator1", "Pedro", "Berlin", null, null, "ant@hotmail.com", "20063918Y", null 
			}, {
				"moderator2", "moderator2", "moderator2", "moderator2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", null
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Eustaquio", "Sánchez", "123456789", null, "carlosmoderator@mail.com", "20063918Y", null
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Eustaquio", "Sánchez", null, "Dirección Real Nº6", "paquito@mail.com", "20063918Y", null 
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Eustaquio", "Sánchez", null, "", "manolete@mail.com", "20063918Y", null 
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Eustaquio", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", "20063918Y", null 
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Francisco", "Cerrada", "", "", "fran@mail.com", "20063918Y", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un moderator puede editarse a si mismo
	 * 		2. Un moderator puede editarse a si mismo
	 * 		3. Un moderator puede editarse a si mismo
	 * 		4. Un moderator puede editarse a si mismo
	 * 		5. El identificador debe cumplir un formato
	 *		6. El correo electrónico tiene que cumplir el formato correspondiente
	 * 		7. El nombre no puede ser nulo
	 * 		8. El apellido no puede ser nulo
	 * 		9. El nombre no puede ser vacio
	 * 		10. El apellido no puede ser vacio
	 * 		11. El EmailAddress no puede ser nulo
	 * 		12. El EmailAddress no puede ser vacio
	 * 		13. El moderatorname no puede cambiar
	 * 		14. La password no puede cambiar
	 * 		15. El identificador debe cumplir el patrón
	 * 		16. El identificador no puede estar vacío
	 * 		17. El identificador no puede ser nulo
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un moderatore.
	 */
	@Test
	public void negativeEditModeratorTest() {
		final Object testingData[][] = {
			{
				"company1", "moderator2", "moderator2", "moderator2", "Pedro", "Berlin", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"admin", "moderator2", "moderator2", "moderator2", "Pedro", "Berlin", "652147893", null, "ant@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"sponsor1", "moderator2", "moderator2", "moderator2", "Pedro", "Perez", "123456789", "Dirección Manager Nº41", "ant@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"user1", "moderator2", "moderator2", "moderator2", "Pedro", "Perez", "123456789", "Dirección Manager Nº41", "ant@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Francisco", "Cerrada", "123456789", "", "fran@mail.com", "2006-A-A3918Y", ConstraintViolationException.class 
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Marta", "Sanchez", "664857123", "Dirección Falsa 23", "manuelito", "20063918Y", ConstraintViolationException.class 
			}, {
				"moderator2", "moderator2", "moderator2", "moderator2", null, "Berlin", "664857123", "Dirección Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Marta", null, "664857123", "Dirección sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "", "Berlin", "664857123", "Dirección Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Marta", "", "664857123", "Dirección sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
			},{
				"moderator1", "moderator1", "moderator1", "moderator1", "Eustaquio", "Sánchez", "664857123", "Dirección Novena", null, "20063918Y", ConstraintViolationException.class 
			}, {
				"moderator1", "moderator1", "moderator1", "moderator1", "Maria", "Villarin", "664254123", "Inserte direccion", "", "20063918Y", ConstraintViolationException.class 
			}, {
				"moderator1", "moderator1", "manager", "moderator1", "Miquel", "Perez", "123456789", "Dirección Moderator Nº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"moderator1", "moderator1", "moderator1", "admin", "Miquel", "Perez", "123456789", "Dirección Moderator Nº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"moderator1", "moderator1", "moderator1", "admin", "Miquel", "Perez", "123456789", "Dirección Moderator Nº41", "gostin@mail.com", "2006-A-3918Y", IllegalArgumentException.class
			}, {
				"moderator1", "moderator1", "moderator1", "admin", "Miquel", "Perez", "123456789", "Dirección Moderator Nº41", "gostin@mail.com", "", IllegalArgumentException.class
			}, {
				"moderator1", "moderator1", "moderator1", "admin", "Miquel", "Perez", "123456789", "Dirección Moderator Nº41", "gostin@mail.com", null, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como moderator
	 * 2. Tomamos el id y la entidad de moderator
	 * 3. Creamos una copia del moderator y de userAccount para que no se guarde solo con un set
	 * 4. Le asignamos el nombre de usuario, la contraseña, el nombre, el apellido, el teléfono, la dirección, el correo electrónico y el identificador correspondientes
	 * 5. Guardamos el moderator copiado con los parámetros
	 * 6. Cerramos sesión
	 */
	protected void template(final String moderatorAuthenticate, final String moderatorEdit, final String moderatorname, final String password, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final Class<?> expected) {
		Class<?> caught;
		int moderatorId;
		Moderator moderatorEntity, moderatorCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			super.authenticate(moderatorAuthenticate);

			moderatorId = super.getEntityId(moderatorEdit);
			moderatorEntity = this.moderatorService.findOne(moderatorId);
			
			moderatorCopy = this.copyModerator(moderatorEntity);
			
			moderatorCopy.getUserAccount().setUsername(moderatorname);
			moderatorCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			moderatorCopy.setName(name);
			moderatorCopy.setSurname(surname);
			moderatorCopy.setAddress(address);
			moderatorCopy.setEmail(email);
			moderatorCopy.setPhone(phone);
			moderatorCopy.setIdentifier(identifier);

			this.moderatorService.save(moderatorCopy);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	
	private Moderator copyModerator(final Moderator moderator) {
		Moderator result;

		result = new Moderator();
		result.setId(moderator.getId());
		result.setVersion(moderator.getVersion());
		result.setUserAccount(copyUserAccount(moderator.getUserAccount()));
		result.setName(moderator.getName());
		result.setSurname(moderator.getSurname());
		result.setAddress(moderator.getAddress());
		result.setEmail(moderator.getAddress());
		result.setPhone(moderator.getPhone());
		result.setIdentifier(moderator.getIdentifier());

		return result;
	}
	
	private UserAccount copyUserAccount(final UserAccount userAccount) {
		UserAccount result;

		result = new UserAccount();
		result.setId(userAccount.getId());
		result.setVersion(userAccount.getVersion());
		result.setUsername(userAccount.getUsername());
		result.setPassword(userAccount.getPassword());
		result.setAuthorities(userAccount.getAuthorities());

		return result;
	}

}