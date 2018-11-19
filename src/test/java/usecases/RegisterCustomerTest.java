package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Customer;

import services.CustomerService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterCustomerTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CustomerService		customerService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar usuario con telefono y dirección a null
	 *		2. Probando registrar usuario con telefono pero con dirección a null
	 * 		3. Probando registrar usuario con telefono a vacío y dirección a null
	 * 		4. Probando registrar usuario con telefono a null y dirección
	 * 		5. Probando registrar usuario con telefono a null y dirección a vacío
	 * 		6. Probando registrar usuario con telefono y dirección
	 * 		7. Probando registrar usuario con telefono y dirección a vacío
	 * 
	 * Requisitos:
	 * 		21. An actor who is not authenticated must be able to: 
	 * 			1. Register to the system as a customer.	 
	 */
	@Test
	public void positiveRegisterCustomerTest() {
		final Object testingData[][] = {
				{
					null, "antonio1", "antonio1", "Antonio", "Azaña", null, null, "ant@mail.com", null 
				}, {
					null, "alexito", "alexito", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null 
				}, {
					null, "carlos", "carlos", "Carlos", "Sánchez", null, null, "carloscustomer@mail.com", null 
				}, {
					null, "paquito", "paquito", "Paco", "Millán", null, "Calle Real Nº6", "paquito@mail.com", null 
				}, {
					null, "manolo", "manolo", "Manolo", "Guillen", null, "", "manolete@mail.com", null 
				}, {
					null, "pepito", "pepito", "Pepe", "Escolar", "321456987", "Dirección incorrecta", "pepe@mail.com", null
				}, {
					null, "francisco", "francisco", "Francisco", "Cerrada", null, "", "fran@mail.com", null 
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un customer logueado no puede registrar a otro
	 * 		2. Un customer logueado no puede registrar a otro
	 * 		3. Un customer logueado no puede registrar a otro
	 * 		4. El email tiene que tener el formato de un email
	 * 		5. El nombre no puede ser nulo
	 * 		6. El apellido no puede ser nulo
	 * 		7. El nombre no puede ser vacío
	 * 		8. El apellido no puede ser vacío
	 * 		9. El email no puede ser nulo
	 * 		10. El email no puede ser vacío
	 * 		11. El customername debe estar entre 5 y 32
	 * 		12. La password debe estar entre 5 y 32
	 * 
	 * Requisitos:
	 * 		21. An actor who is not authenticated must be able to: 
	 * 			1. Register to the system as a customer.	
	 */
	@Test
	public void negativeRegisterCustomerTest() {
		final Object testingData[][] = {
				{
					"customer2", "customer13", "customer13", "Antonio", "Azaña", null, null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"admin", "customer23", "customer23", "Antonio", "Azaña", "652147893", null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"manager1", "customer23", "customer23", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", IllegalArgumentException.class 
				}, {
					null, "marta", "marta", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", ConstraintViolationException.class 
				}, {
					null, "azaña", "azaña", null, "Azaña", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class 
				}, {
					null, "marta", "marta", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
				}, {
					null, "azaña2", "azaña2", "", "Azaña", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class 
				}, {
					null, "marta2", "marta2", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
				},{
					null, "marta3", "marta3", "Marta", "Azaña", "664857123", "Calle Novena", null, ConstraintViolationException.class 
				}, {
					null, "maria", "maria", "María", "Villarín", "664254123", "Inserte dirección", "", ConstraintViolationException.class 
				}, {
					null, "gost", "gostino", "Gostin", "Perez", "", "Calle Customer Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}, {
					null, "administratoradministratoradministrator", "admin", "Gostin", "Perez", "", "Calle Customer Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Class<?>) testingData[i][8]);
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
	 * 1. Nos autentificamos como el usuario customer
	 * 2. Creamos un nuevo customer
	 * 3. Le asignamos el customername, el password, el name, el surname, el phone, la address, el birthdate y el email correspondientes
	 * 4. Guardamos el nuevo customer
	 * 5. Nos desautentificamos
	 * 6. Comprobamos se ha creado y existe
	 */
	protected void template(final String customer, final String customername, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		Customer customerEntity, customerSave;

		caught = null;
		try {
			super.authenticate(customer);

			customerEntity = this.customerService.create();
			customerEntity.getUserAccount().setUsername(customername);
			customerEntity.getUserAccount().setPassword(password);
			customerEntity.setName(name);
			customerEntity.setSurname(surname);
			customerEntity.setPhoneNumber(phoneNumber);
			customerEntity.setPostalAddress(postalAddress);
			customerEntity.setEmailAddress(emailAddress);
			
			customerSave = this.customerService.save(customerEntity);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.customerService.findAll().contains(customerSave));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

