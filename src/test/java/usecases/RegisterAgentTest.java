package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Agent;

import services.AgentService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterAgentTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private AgentService		agentService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar agente con telefono y dirección a null
	 *		2. Probando registrar agente con telefono pero con dirección a null
	 * 		3. Probando registrar agente con telefono bueno y dirección a null
	 * 		4. Probando registrar agente con telefono a null y dirección
	 * 		5. Probando registrar agente con telefono a null y dirección a vacío
	 * 		6. Probando registrar agente con telefono y dirección
	 * 		7. Probando registrar agente con telefono bueno y dirección a vacío
	 * 
	 * Requisitos:
	 * 		An actor who is not authenticated must be able to:
			1. Register to the system as an agent. 
	 */
	@Test
	public void positiveRegisterUserTest() {
		final Object testingData[][] = {
				{
					null, "alejandro", "alejandro", "Alejandro", "Muriel", null, null, "asv@mail.com", null 
				}, {
					null, "sergio", "sergio", "Sergio", "Perez", "987532146", null, "sergi@hotmail.com", null 
				}, {
					null, "carlos", "carlos", "Carlos", "Sánchez", "+3452147896", null, "carlosuser@mail.com", null 
				}, {
					null, "domingo", "domingo", "Domingo", "Millán", null, "Calle Real Nº6", "domingo@mail.com", null 
				}, {
					null, "moscu", "moscu", "Moscu", "Guillen", null, "", "moscu@mail.com", null 
				}, {
					null, "paris", "paris", "Paris", "Escolar", "321456987", "Dirección incorrecta", "pepe@mail.com", null
				}, {
					null, "antonio", "antonio", "Antonio", "Cerrada", "245178634", "", "antonito@mail.com", null 
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
	 * 		1. Un agente logueado no puede registrar a otro
	 * 		2. Un agente logueado no puede registrar a otro
	 * 		3. Un agente logueado no puede registrar a otro
	 * 		4. El email tiene que tener el formato de un email
	 * 		5. El nombre no puede ser nulo
	 * 		6. El apellido no puede ser nulo
	 * 		7. El nombre no puede ser vacío
	 * 		8. El apellido no puede ser vacío
	 * 		9. El email no puede ser nulo
	 * 		10. El email no puede ser vacío
	 * 		11. El username debe estar entre 5 y 32
	 * 		12. La password debe estar entre 5 y 32
	 * 		13. El telefono debe cumplir el patrón
	 * 
	 * Requisitos:
	 * 		An actor who is not authenticated must be able to:
			1. Register to the system as an agent. 
	 */
	@Test
	public void negativeRegisterUserTest() {
		final Object testingData[][] = {
				{
					"user2", "user13", "user13", "Antonio", "Azaña", null, null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"admin", "user23", "user23", "Antonio", "Azaña", "652147893", null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"manager1", "user23", "user23", "Antonio", "Perez", "21457896", "Calle Manager Nº41", "ant@mail.com", IllegalArgumentException.class 
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
					null, "gost", "gostino", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}, {
					null, "administratoradministratoradministrator", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}, {
					null, "antonio", "antonio", "Antonio", "Cerrada", "", "", "antonito@mail.com", ConstraintViolationException.class 
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
	 * 1. Nos autentificamos como el agente agent
	 * 2. Creamos un nuevo user
	 * 3. Le asignamos el username, el password, el name, el surname, el phone, la address, el birthdate y el email correspondientes
	 * 4. Guardamos el nuevo user
	 * 5. Nos desautentificamos
	 * 6. Comprobamos se ha creado y existe
	 */
	protected void template(final String agent, final String username, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		Agent agentEntity, agentSave;

		caught = null;
		try {
			super.authenticate(agent);

			agentEntity = this.agentService.create();
			agentEntity.getUserAccount().setUsername(username);
			agentEntity.getUserAccount().setPassword(password);
			agentEntity.setName(name);
			agentEntity.setSurname(surname);
			agentEntity.setPhoneNumber(phoneNumber);
			agentEntity.setPostalAddress(postalAddress);
			agentEntity.setEmailAddress(emailAddress);
			
			agentSave = this.agentService.save(agentEntity);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.agentService.findAll().contains(agentSave));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

