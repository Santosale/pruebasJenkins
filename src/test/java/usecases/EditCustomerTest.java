package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import domain.Customer;

import security.UserAccount;
import services.CustomerService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditCustomerTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CustomerService		customerService;
	
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
	 * 	Se desea probar la correcta edicion de un usuario.
	 */
	@Test
	public void positiveEditCustomerTest() {
		final Object testingData[][] = {
			{
				"customer1", "customer1", "customer1", "customer1", "Antonio", "Azana", null, null, "ant@mail.com", null 
			}, {
				"customer2", "customer2", "customer2", "customer2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null
			}, {
				"customer3", "customer3", "customer3", "customer3", "Carlos", "Sánchez", null, null, "carloscustomer@mail.com", null
			}, {
				"customer3", "customer3", "customer3", "customer3", "Carlos", "Sánchez", null, "Calle Real Nº6", "paquito@mail.com", null 
			}, {
				"customer3", "customer3", "customer3", "customer3", "Carlos", "Sánchez", null, "", "manolete@mail.com", null 
			}, {
				"customer3", "customer3", "customer3", "customer3", "Carlos", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", null 
			}, {
				"customer3", "customer3", "customer3", "customer3", "Francisco", "Cerrada", null, "", "fran@mail.com", null
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
	 * 1. Solo un usuario registrado puede registrarse a si mismo
	 * 2. Solo un usuario registrado puede registrarse a si mismo
	 * 3. Solo un usuario registrado puede registrarse a si mismo
	 * 6. El EmailAddress tiene que tener el formato de un EmailAddress
	 * 7. El nombre no puede ser nulo
	 * 8. El apellido no puede ser nulo
	 * 9. El nombre no puede ser vacio
	 * 10. El apellido no puede ser vacio
	 * 11. El EmailAddress no puede ser nulo
	 * 12. El EmailAddress no puede ser vacio
	 * 13. El customername no puede cambiar
	 * 14. La password no puede cambiar
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un usuario.
	 */
	@Test
	public void negativeEditCustomerTest() {
		final Object testingData[][] = {
			{
				"customer1", "customer2", "customer2", "customer2", "Antonio", "Azana", null, null, "ant@mail.com", IllegalArgumentException.class 
			}, {
				"admin", "customer2", "customer2", "customer2", "Antonio", "Azana", "652147893", null, "ant@mail.com", IllegalArgumentException.class
			}, {
				"manager1", "customer2", "customer2", "customer2", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", IllegalArgumentException.class
			}, {
				"customer1", "customer1", "customer1", "customer1", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", ConstraintViolationException.class 
			}, {
				"customer2", "customer2", "customer2", "customer2", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"customer1", "customer1", "customer1", "customer1", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class
			}, {
				"customer3", "customer3", "customer3", "customer3", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"customer1", "customer1", "customer1", "customer1", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
			},{
				"customer3", "customer3", "customer3", "customer3", "Carlos", "Sánchez", "664857123", "Calle Novena", null, ConstraintViolationException.class 
			}, {
				"customer1", "customer1", "customer1", "customer1", "Maria", "Villarin", "664254123", "Inserte direccion", "", ConstraintViolationException.class 
			}, {
				"customer1", "customer1", "manager", "customer1", "Gostin", "Perez", "", "Calle Customer Nº41", "gostin@mail.com", IllegalArgumentException.class
			}, {
				"customer3", "customer3", "customer3", "admin", "Gostin", "Perez", "", "Calle Customer Nº41", "gostin@mail.com", IllegalArgumentException.class
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
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer
	 * 3. Creamos una copia del customer y de customerAccount para que no se guarde solo con un set
	 * 4. Le asignamos el customername, el password, el name, el surname, el PhoneNumber, la PostalAddress, el birthdate y el EmailAddress correspondientes
	 * 5. Guardamos el customer copiado con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void template(final String customerAuthenticate, final String customerEdit, final String customername, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		int customerId;
		Customer customerEntity, customerCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			super.authenticate(customerAuthenticate);

			customerId = super.getEntityId(customerEdit);
			customerEntity = this.customerService.findOne(customerId);
			
			customerCopy = this.copyCustomer(customerEntity);
			
			customerCopy.getUserAccount().setUsername(customername);
			customerCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			customerCopy.setName(name);
			customerCopy.setSurname(surname);
			customerCopy.setPhoneNumber(phoneNumber);
			customerCopy.setPostalAddress(postalAddress);
			customerCopy.setEmailAddress(emailAddress);

			this.customerService.save(customerCopy);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Customer copyCustomer(final Customer customer) {
		Customer result;

		result = new Customer();
		result.setId(customer.getId());
		result.setVersion(customer.getVersion());
		result.setUserAccount(copyUserAccount(customer.getUserAccount()));
		result.setName(customer.getName());
		result.setSurname(customer.getSurname());
		result.setPostalAddress(customer.getPostalAddress());
		result.setEmailAddress(customer.getEmailAddress());
		result.setPhoneNumber(customer.getPhoneNumber());

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