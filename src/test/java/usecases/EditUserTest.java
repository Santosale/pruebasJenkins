package usecases;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.User;

import security.UserAccount;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditUserTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private UserService		userService;
	
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
	public void positiveEditUserTest() {
		final Object testingData[][] = {
			{
				"user1", "user1", "user1", "user1", "Antonio", "Azana", null, null, "ant@mail.com", null 
			}, {
				"user2", "user2", "user2", "user2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null
			}, {
				"user3", "user3", "user3", "user3", "Carlos", "Sánchez", null, null, "carlosuser@mail.com", null
			}, {
				"user4", "user4", "user4", "user4", "Paco", "Millán", null, "Calle Real Nº6", "paquito@mail.com", null 
			}, {
				"user5", "user5", "user5", "user5", "Manolo", "Guillen", null, "", "manolete@mail.com", null 
			}, {
				"user6", "user6", "user6", "user6", "Pepe", "Escolar", "321456987", "Direccion incorrecta", "pepe@mail.com", null 
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", null
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
	 * 13. El username no puede cambiar
	 * 14. La password no puede cambiar
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un usuario.
	 */
	@Test
	public void negativeEditUserTest() {
		final Object testingData[][] = {
			{
				"user1", "user2", "user2", "user2", "Antonio", "Azana", null, null, "ant@mail.com", IllegalArgumentException.class 
			}, {
				"admin", "user2", "user2", "user2", "Antonio", "Azana", "652147893", null, "ant@mail.com", IllegalArgumentException.class
			}, {
				"manager1", "user2", "user2", "user2", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", IllegalArgumentException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", ConstraintViolationException.class 
			}, {
				"user2", "user2", "user2", "user2", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class
			}, {
				"user3", "user3", "user3", "user3", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
			},{
				"user4", "user4", "user4", "user4", "Marta", "Azana", "664857123", "Calle Novena", null, ConstraintViolationException.class 
			}, {
				"user1", "user1", "user1", "user1", "Maria", "Villarin", "664254123", "Inserte direccion", "", ConstraintViolationException.class 
			}, {
				"user1", "user1", "manager", "user1", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", IllegalArgumentException.class
			}, {
				"user3", "user3", "user3", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", IllegalArgumentException.class
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
	 * 		1. Un usuario sigue a otro usuario
	 * 		2. Un usuario deja de seguir a otro usuario
	 * 
	 * Requisitos:
	 * 		16. An actor who is authenticated as a user must be able to: 
	 * 			2. Follow or unfollow another user. 
	 */
	@Test
	public void driverPositiveTestFollow() {
		final Object testingData[][] = {
			{
				"user1", "user4", "follow", null
			}, {
				"user1", "user2", "unfollow", null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFollow((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * 
	 * Pruebas:
	 * 		1. Un customer trata de seguir a un usuario
	 * 		2. Un usuario trata de seguir a un customer
	 * 
	 * Requisitos:
	 * 		16. An actor who is authenticated as a user must be able to: 
	 * 			2. Follow or unfollow another user. 
	 */
	@Test
	public void driverNegativeTestFollow() {
		final Object testingData[][] = {
			{
				"customer1", "user4", "follow", IllegalArgumentException.class
			}, {
				"user1", "customer1", "follow", IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFollow((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*	Seguir a un usuario
	 * 	Pasos:
	 * 1. Autenticar usuario
	 * 2. Listar usuarios
	 * 3. Escoger usuario
	 * 4. Seguir o dejar de seguir
	 */
	protected void templateFollow(final String userAuthenticatedBean, final String userToFollowBean, final String action, final Class<?> expected) {
		Class<?> caught;
		User userAuthenticated, userToFollow;
		Collection<User> users;
		Integer pageUser;

		caught = null;
		try {
			
			super.authenticate(userAuthenticatedBean);
			userToFollow = this.userService.findOne(super.getEntityId(userToFollowBean));
			Assert.notNull(userToFollow);
			
			// 1. Autenticar usuario
			userAuthenticated = this.userService.findOne(super.getEntityId(userAuthenticatedBean));
			Assert.notNull(userAuthenticated);

			// 2. Listar usuarios
			pageUser = this.getPage(userToFollow);
			Assert.notNull(pageUser);
			users = this.userService.findAllPaginated(pageUser, 5).getContent();
			Assert.notNull(users);
			
			// 3. Escoger usuario
			for(User u: users)
				if(u.getUserAccount().getUsername().equals(userToFollowBean)) userToFollow = this.userService.findOne(u.getId());
			
			// 4. Seguir o dejar de seguir usuario
			if(action.equals("follow")) {
				this.userService.addFollower(userToFollow.getId());
				Assert.isTrue(this.userService.findOne(userToFollow.getId()).getFollowers().contains(userAuthenticated));
			}else if(action.equals("unfollow")) {
				this.userService.removeFollower(userToFollow.getId());
				Assert.isTrue(!this.userService.findOne(userToFollow.getId()).getFollowers().contains(userAuthenticated));
			}
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	
	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Tomamos el id y la entidad de user
	 * 3. Creamos una copia del user y de userAccount para que no se guarde solo con un set
	 * 4. Le asignamos el username, el password, el name, el surname, el PhoneNumber, la PostalAddress, el birthdate y el EmailAddress correspondientes
	 * 5. Guardamos el user copiado con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void template(final String userAuthenticate, final String userEdit, final String username, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		int userId;
		User userEntity, userCopy;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			super.authenticate(userAuthenticate);

			userId = super.getEntityId(userEdit);
			userEntity = this.userService.findOne(userId);
			
			userCopy = this.copyUser(userEntity);
			
			userCopy.getUserAccount().setUsername(username);
			userCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			userCopy.setName(name);
			userCopy.setSurname(surname);
			userCopy.setPhoneNumber(phoneNumber);
			userCopy.setPostalAddress(postalAddress);
			userCopy.setEmailAddress(emailAddress);

			this.userService.save(userCopy);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private User copyUser(final User user) {
		User result;

		result = new User();
		result.setId(user.getId());
		result.setVersion(user.getVersion());
		result.setUserAccount(copyUserAccount(user.getUserAccount()));
		result.setName(user.getName());
		result.setSurname(user.getSurname());
		result.setPostalAddress(user.getPostalAddress());
		result.setEmailAddress(user.getEmailAddress());
		result.setPhoneNumber(user.getPhoneNumber());
		result.setFollowers(user.getFollowers());

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
	
	private Integer getPage(final User user) {
		Integer result;
		Page<User> pageUser, pageUserAux;

		pageUser = this.userService.findAllPaginated(0, 5);

		result = null;
		for (int i = 0; i <= pageUser.getTotalPages(); i++) {
			pageUserAux = this.userService.findAllPaginated(i, 5);
			if (pageUserAux.getContent().contains(user)) {
				result = i;
				break;
			}
		}

		return result;
	}

}