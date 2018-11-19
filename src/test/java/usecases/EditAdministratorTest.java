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
	 * 
	 * 1. Probando editar usuario con telefono y direccion a null
	 * 2. Probando editar usuario con telefono pero con direccion a null
	 * 3. Probando editar usuario con telefono a vacio y direccion a null
	 * 4. Probando editar usuario con telefono a null y direccion
	 * 5. Probando editar usuario con telefono a null y direccion a vacio
	 * 6. Probando editar usuario con telefono y direccion
	 * 7. Probando editar usuario con telefono y direccion a vacio
	 * 
	 * Requsitos:
	 * 
	 * 	Se desea probar la correcta edicion de un administrador.
	 */
	@Test
	public void positiveEditAdministratorTest() {
		final Object testingData[][] = {
			{
				"admin", "administrator", "admin", "admin", "Antonio", "Azana", null, null, "ant@mail.com", null 
			}, {
				"admin", "administrator", "admin", "admin", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null
			}, {
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", null, null, "carlosadmin1istrator@mail.com", null
			}, {
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", null, "Calle Real Nº6", "paquito@mail.com", null 
			}, {
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", null, "", "manolete@mail.com", null 
			}, {
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", null 
			}, {
				"admin", "administrator", "admin", "admin", "Francisco", "Cerrada", null, "", "fran@mail.com", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. El EmailAddress tiene que tener el formato de un EmailAddress
	 * 2. El nombre no puede ser nulo
	 * 3. El apellido no puede ser nulo
	 * 4. El nombre no puede ser vacio
	 * 5. El apellido no puede ser vacio
	 * 6. El EmailAddress no puede ser nulo
	 * 7. El EmailAddress no puede ser vacio
	 * 8. El administratorname no puede cambiar
	 * 9. La password no puede cambiar
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un administrador.
	 */
	@Test
	public void negativeEditAdministratorTest() {
		final Object testingData[][] = {
			{
				"admin", "administrator", "admin", "admin", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"admin", "administrator", "admin", "admin", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class
			}, {
				"admin", "administrator", "admin", "admin", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"admin", "administrator", "admin", "admin", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
			},{
				"admin", "administrator", "admin", "admin", "Carlos", "Sánchez", "664857123", "Calle Novena", null, ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "admin", "Maria", "Villarin", "664254123", "Inserte direccion", "", ConstraintViolationException.class 
			}, {
				"admin", "administrator", "admin", "newpassword", "Gostin", "Perez", "", "Calle Administrator Nº41", "gostin@mail.com", IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
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
	 * 1. Nos autenticamos como administrator
	 * 2. Tomamos el id y la entidad de administrator
	 * 3. Creamos una copia del administrator y de administratorAccount para que no se guarde solo con un set
	 * 4. Le asignamos el administratorname, el password, el name, el surname, el PhoneNumber, la PostalAddress, el birthdate y el EmailAddress correspondientes
	 * 5. Guardamos el administrator copiado con los parámetros
	 * 6. Nos desautenticamos
	 */
	protected void template(final String administratorAuthenticate, final String administratorEdit, final String administratorname, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		int administratorId;
		Administrator administratorEntity, administratorCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			super.authenticate(administratorAuthenticate);

			administratorId = super.getEntityId(administratorEdit);
			administratorEntity = this.administratorService.findOne(administratorId);
			
			administratorCopy = this.copyAdministrator(administratorEntity);
			
			administratorCopy.getUserAccount().setUsername(administratorname);
			administratorCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			administratorCopy.setName(name);
			administratorCopy.setSurname(surname);
			administratorCopy.setPhoneNumber(phoneNumber);
			administratorCopy.setPostalAddress(postalAddress);
			administratorCopy.setEmailAddress(emailAddress);

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
		result.setPostalAddress(administrator.getPostalAddress());
		result.setEmailAddress(administrator.getEmailAddress());
		result.setPhoneNumber(administrator.getPhoneNumber());

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