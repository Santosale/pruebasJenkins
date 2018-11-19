package usecases;


import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import domain.Administrator;

import security.UserAccount;
import services.AdministratorService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditAdministratorTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private AdministratorService		administratorService;

	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Probando editar administrador con telefono y dirección a null
	 * 		2. Probando editar administrador con telefono pero con dirección a null
	 * 		3. Probando editar administrador con telefono a vacío y dirección a null
	 * 		4. Probando editar administrador con telefono a null y dirección
	 * 		5. Probando editar administrador con telefono a null y dirección a vacío
	 * 		6. Probando editar administrador con telefono y dirección
	 * 		7. Probando editar administrador con telefono y dirección a vacío
	 * 
	 * Requisitos:
	 *		Los actores pueden editar su perfil
	 */
	@Test
	public void positiveEditAdministratorTest() {
		final Object testingData[][] = {
			{ 
				"admin", "administrator", "admin", "admin", "Cristina", "Sgourakes", null, null, "administrator@mail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "Manuel", "Kudera", "987532146", null, "administrator@hotmail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", "", null, "carlosadministrator@mail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "Paco", "Jespersen", null, "Calle No tan Real", "paquito@mail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "José", "Rumer", null, "", "joselito@mail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "Pepe", "Escolar", "258753159", "Dirección Correcta", "pepe@mail.com", "20063918Y", null
			}, {
				"admin", "administrator", "admin", "admin", "Fran", "Vinson", "", "", "fran@mail.com", "20063918Y", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7],(String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Solo el administrador puede editarse a si mismo
	 * 		2. Solo el administrador puede editarse a si mismo
	 * 		3. Solo el administrador puede editarse a si mismo
	 * 		4. Solo el administrador puede editarse a si mismo
	 * 		5. El identificador debe ser válido
	 * 		6. El identificador no puede ser nulo
	 * 		7. El email tiene que tener el formato de un email
	 * 		8. El nombre no puede ser nulo
	 * 		9. El apellido no puede ser nulo
	 * 		10. El nombre no puede ser vacío
	 * 		11. El apellido no puede ser vacío
	 * 		12. El email no puede ser nulo
	 * 		13. El email no puede ser vacío
	 * 		14. El username no puede cambiar
	 * 		15. La password no puede cambiar
	 * 
	 * Requisitos:
	 * 		Los actores pueden editar su perfil
	 */
	@Test()
	public void negativeEditAdministratorTest() {
		final Object testingData[][] = {
			{
				"moderator1", "administrator", "admin", "admin", "Antonio", "Azaña", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"sponsor2", "administrator", "admin", "admin", "Jesús", "Harvey", "652147893", null, "harvey@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"user1", "administrator", "admin", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"company1", "administrator", "admin", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Alejandro", "Azaña", null, null, "alexito@mail.com", "2006A3-A-918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Manuel", "Sterne", null, null, "sterne@mail.com", null, ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Diana", "Martín", "664857123", "Calle Falsa 23", "diana", "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", null, "Ahmed", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Paco", null, "664857123", "Calle sin numero", "paco@gmail.es", "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "", "Ahmed", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Lucía", "", "664857123", "Calle sin numero", "lucia@gmail.es", "20063918Y", ConstraintViolationException.class 
			},{
				"admin", "administrator", "admin", "admin", "Marta", "Merdoz", "664857123", "Calle Novena", null, "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "María", "Villarín", "664254123", "Inserte dirección", "", "20063918Y", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "user50", "admin", "Gostin", "Perez", "", "Calle User NÂº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"admin", "administrator", "admin", "manager", "Gostin", "Perez", "", "Calle User NÂº41", "gostin@mail.com", "20063918Y", IllegalArgumentException.class 
			},
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7],(String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
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
	 * 1. Nos autentificamos como admin
	 * 2. Tomamos el id y la entidad de admin
	 * 3. Creamos una copia del admin y de userAccount para que no se guarde solo con un set
	 * 4. Le asignamos el username, el password, el name, el surname, el phone, la address, el birthdate y el email correspondientes
	 * 5. Guardamos el admin copiado con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void template(final String administratorAuthenticate, final String administratorEdit, final String username, final String password, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final Class<?> expected) {
		Class<?> caught;
		int administratorId;
		Administrator administratorEntity, administratorCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			
			super.authenticate(administratorAuthenticate);
			
			administratorId = 0;
			if(administratorAuthenticate.equals("admin"))
				administratorId = super.getEntityId("administrator");
			administratorEntity = this.administratorService.findOne(administratorId);
			administratorCopy = this.copyAdministrator(administratorEntity);
			administratorCopy.getUserAccount().setUsername(username);
			administratorCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			administratorCopy.setName(name);
			administratorCopy.setSurname(surname);
			administratorCopy.setPhone(phone);
			administratorCopy.setAddress(address);
			administratorCopy.setEmail(email);
			administratorCopy.setIdentifier(identifier);
			
			this.administratorService.save(administratorCopy);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private Administrator copyAdministrator(final Administrator administrator) {
		Administrator result;

		result = new Administrator();
		result.setId(administrator.getId());
		result.setVersion(administrator.getVersion());
		result.setUserAccount(copyUserAccount(administrator.getUserAccount()));
		result.setName(administrator.getName());
		result.setSurname(administrator.getSurname());
		result.setAddress(administrator.getAddress());
		result.setEmail(administrator.getEmail());
		result.setPhone(administrator.getPhone());
		result.setIdentifier(administrator.getIdentifier());

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

