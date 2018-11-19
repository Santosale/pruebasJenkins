package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import domain.Agent;

import security.UserAccount;
import services.AgentService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditAgentTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private AgentService		agentService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probando editar agente con telefono y direccion a null
	 * 2. Probando editar agente con telefono pero con direccion a null
	 * 3. Probando editar agente con telefono a vacio y direccion a null
	 * 4. Probando editar agente con telefono a null y direccion
	 * 5. Probando editar agente con telefono a null y direccion a vacio
	 * 6. Probando editar agente con telefono y direccion
	 * 7. Probando editar agente con telefono y direccion a vacio
	 * 
	 * Requsitos:
	 * 
	 * 	Se desea probar la correcta edicion de un agente.
	 */
	@Test
	public void positiveEditAgentTest() {
		final Object testingData[][] = {
			{
				"agent1", "agent1", "agent1", "agent1", "Pedro", "Berlin", null, null, "ant@mail.com", null 
			}, {
				"agent2", "agent2", "agent2", "agent2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null
			}, {
				"agent1", "agent1", "agent1", "agent1", "Eustaquio", "Sánchez", "123456789", null, "carlosagent@mail.com", null
			}, {
				"agent1", "agent1", "agent1", "agent1", "Eustaquio", "Sánchez", null, "Dirección Real Nº6", "paquito@mail.com", null 
			}, {
				"agent1", "agent1", "agent1", "agent1", "Eustaquio", "Sánchez", null, "", "manolete@mail.com", null 
			}, {
				"agent1", "agent1", "agent1", "agent1", "Eustaquio", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", null 
			}, {
				"agent1", "agent1", "agent1", "agent1", "Francisco", "Cerrada", "123456789", "", "fran@mail.com", null
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
	 * 1. Solo un agente registrado puede registrarse a si mismo
	 * 2. Solo un agente registrado puede registrarse a si mismo
	 * 3. Solo un agente registrado puede registrarse a si mismo
	 * 6. El EmailAddress tiene que tener el formato de un EmailAddress
	 * 7. El nombre no puede ser nulo
	 * 8. El apellido no puede ser nulo
	 * 9. El nombre no puede ser vacio
	 * 10. El apellido no puede ser vacio
	 * 11. El EmailAddress no puede ser nulo
	 * 12. El EmailAddress no puede ser vacio
	 * 13. El agentname no puede cambiar
	 * 14. La password no puede cambiar
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un agente.
	 */
	@Test
	public void negativeEditAgentTest() {
		final Object testingData[][] = {
			{
				"agent1", "agent2", "agent2", "agent2", "Pedro", "Berlin", null, null, "ant@mail.com", IllegalArgumentException.class 
			}, {
				"admin", "agent2", "agent2", "agent2", "Pedro", "Berlin", "652147893", null, "ant@mail.com", IllegalArgumentException.class
			}, {
				"manager1", "agent2", "agent2", "agent2", "Pedro", "Perez", "123456789", "Dirección Manager Nº41", "ant@mail.com", IllegalArgumentException.class
			}, {
				"agent1", "agent1", "agent1", "agent1", "Marta", "Sanchez", "664857123", "Dirección Falsa 23", "manuelito", ConstraintViolationException.class 
			}, {
				"agent2", "agent2", "agent2", "agent2", null, "Berlin", "664857123", "Dirección Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"agent1", "agent1", "agent1", "agent1", "Marta", null, "664857123", "Dirección sin numero", "martita@gmail.es", ConstraintViolationException.class
			}, {
				"agent1", "agent1", "agent1", "agent1", "", "Berlin", "664857123", "Dirección Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"agent1", "agent1", "agent1", "agent1", "Marta", "", "664857123", "Dirección sin numero", "martita@gmail.es", ConstraintViolationException.class 
			},{
				"agent1", "agent1", "agent1", "agent1", "Eustaquio", "Sánchez", "664857123", "Dirección Novena", null, ConstraintViolationException.class 
			}, {
				"agent1", "agent1", "agent1", "agent1", "Maria", "Villarin", "664254123", "Inserte direccion", "", ConstraintViolationException.class 
			}, {
				"agent1", "agent1", "manager", "agent1", "Miquel", "Perez", "123456789", "Dirección Agent Nº41", "gostin@mail.com", IllegalArgumentException.class
			}, {
				"agent1", "agent1", "agent1", "admin", "Miquel", "Perez", "123456789", "Dirección Agent Nº41", "gostin@mail.com", IllegalArgumentException.class
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
	 * 1. Nos autentificamos como agent
	 * 2. Tomamos el id y la entidad de agent
	 * 3. Creamos una copia del agent y de agentAccount para que no se guarde solo con un set
	 * 4. Le asignamos el agentname, el password, el name, el surname, el PhoneNumber, la PostalAddress, el birthdate y el EmailAddress correspondientes
	 * 5. Guardamos el agent copiado con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void template(final String agentAuthenticate, final String agentEdit, final String agentname, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		int agentId;
		Agent agentEntity, agentCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			super.authenticate(agentAuthenticate);

			agentId = super.getEntityId(agentEdit);
			agentEntity = this.agentService.findOne(agentId);
			
			agentCopy = this.copyAgent(agentEntity);
			
			agentCopy.getUserAccount().setUsername(agentname);
			agentCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			agentCopy.setName(name);
			agentCopy.setSurname(surname);
			agentCopy.setPhoneNumber(phoneNumber);
			agentCopy.setPostalAddress(postalAddress);
			agentCopy.setEmailAddress(emailAddress);

			this.agentService.save(agentCopy);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	
	private Agent copyAgent(final Agent agent) {
		Agent result;

		result = new Agent();
		result.setId(agent.getId());
		result.setVersion(agent.getVersion());
		result.setUserAccount(copyUserAccount(agent.getUserAccount()));
		result.setName(agent.getName());
		result.setSurname(agent.getSurname());
		result.setPostalAddress(agent.getPostalAddress());
		result.setEmailAddress(agent.getEmailAddress());
		result.setPhoneNumber(agent.getPhoneNumber());

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