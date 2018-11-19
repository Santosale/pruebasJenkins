package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.User;

import security.UserAccount;
import services.ConfigurationService;
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
	
	@Autowired
	private ConfigurationService configurationService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Probando editar usuario con telefono y direccion a null
	 * 		2. Probando editar usuario con telefono pero con direccion a null
	 * 		3. Probando editar usuario con telefono a vacio y direccion a null
	 * 		4. Probando editar usuario con telefono a null y direccion
	 * 		5. Probando editar usuario con telefono a null y direccion a vacio
	 * 		6. Probando editar usuario con telefono y direccion
	 * 		7. Probando editar usuario con telefono y direccion a vacio
	 * 		8. Probando editar usuario dejando el avatar vacío
	 * 
	 * Requsitos:
	 * 	 	23.6. Un actor que está autenticado como empresa debe ser capaz de 
	 * 		listar las rifas que ha creado, editarlas y borrarlas 
	 * 		si todavía nadie ha comprado un tique.	
	 */
	@Test
	public void positiveEditUserTest() {
		final Object testingData[][] = {
			{
				"user1", "user1", "user1", "user1", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null 
			}, {
				"user2", "user2", "user2", "user2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null
			}, {
				"user3", "user3", "user3", "user3", "Carlos", "Sánchez", null, null, "carlosuser@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null
			}, {
				"user4", "user4", "user4", "user4", "Paco", "Millán", null, "Calle Real Nº6", "paquito@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null 
			}, {
				"user5", "user5", "user5", "user5", "Manolo", "Guillen", null, "", "manolete@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null 
			}, {
				"user6", "user6", "user6", "user6", "Pepe", "Escolar", "321456987", "Direccion incorrecta", "pepe@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null 
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "https://thumbs.dreamstime.com/b/dise%C3%B1o-del-hombre-de-negocios-icono-de-avatar-ejemplo-de-colorfull-73233172.jpg", null
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Solo un usuario registrado puede registrarse a si mismo
	 * 		2. Solo un usuario registrado puede registrarse a si mismo
	 * 		3. Solo un usuario registrado puede registrarse a si mismo
	 * 		4. El EmailAddress tiene que tener el formato de un EmailAddress
	 * 		5. El nombre no puede ser nulo
	 * 		6. El apellido no puede ser nulo
	 * 		7. El nombre no puede ser vacio
	 * 		8. El apellido no puede ser vacio
	 * 		9. El EmailAddress no puede ser nulo
	 * 		10. El EmailAddress no puede ser vacio
	 * 		11. El username no puede cambiar
	 * 		13. El avatar tiene que ser una URL
	 * 	 	14. El identificador debe cumplir el patrón
	 * 		15. El identificador no puede estar vacío
	 * 		16. El identificador no puede ser nulo
	 * 
	 * Requisitos:
	 * 
	 * 	Se desea probar la correcta edicion de un usuario.
	 */
	@Test
	public void negativeEditUserTest() {
		final Object testingData[][] = {
			{
				"user1", "user2", "user2", "user2", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", "", IllegalArgumentException.class 
			}, {
				"admin", "user2", "user2", "user2", "Antonio", "Azana", "652147893", null, "ant@mail.com", "20063918Y", "", IllegalArgumentException.class
			}, {
				"manager1", "user2", "user2", "user2", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", "20063918Y", "", IllegalArgumentException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", "", ConstraintViolationException.class 
			}, {
				"user2", "user2", "user2", "user2", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "", IllegalArgumentException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "", IllegalArgumentException.class
			}, {
				"user3", "user3", "user3", "user3", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "", ConstraintViolationException.class
			}, {
				"user1", "user1", "user1", "user1", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "", ConstraintViolationException.class 
			},{
				"user4", "user4", "user4", "user4", "Marta", "Azana", "664857123", "Calle Novena", null, "20063918Y", "", IllegalArgumentException.class 
			}, {
				"user1", "user1", "user1", "user1", "Maria", "Villarin", "664254123", "Inserte direccion", "", "20063918Y", "", ConstraintViolationException.class 
			}, {
				"user1", "user1", "manager", "user1", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", "20063918Y", "", IllegalArgumentException.class
			}, {
				"user3", "user3", "user3", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", "20063918Y", "", IllegalArgumentException.class
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "asd", ConstraintViolationException.class
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063-A--918Y", "", ConstraintViolationException.class
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", "", "", ConstraintViolationException.class
			}, {
				"user3", "user3", "user3", "user3", "Francisco", "Cerrada", null, "", "fran@mail.com", null, "", ConstraintViolationException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}


	// Ancillary methods ------------------------------------------------------

	/*
	 * Editar usuario
	 * Pasos:
	 * 		1. Iniciar sesión como usuario
	 * 		2. Entramos en la vista de edición del usuario
	 * 		3. Editamos los datos
	 * 		4. Guardamos los cambios
	 * 		5. Volvemos al inicio
	 */
	protected void template(final String userAuthenticate, final String userEdit, final String username, final String password, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final String avatar, final Class<?> expected) {
		Class<?> caught;
		int userId;
		User userEntity, userCopy, user;
		Md5PasswordEncoder encoder;
		String defaultAvatar;

		encoder = new Md5PasswordEncoder();
		caught = null;
		try {
			
			// 1. Iniciar sesión como usuario
			super.authenticate(userAuthenticate);

			// 2. Entramos en la vista de edición del usuario
			userId = super.getEntityId(userEdit);
			Assert.notNull(userId);
			
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			
			// 3. Editamos los datos
			userCopy = this.copyUser(userEntity);
			userCopy.getUserAccount().setUsername(username);
			userCopy.getUserAccount().setPassword(encoder.encodePassword(password, null));
			userCopy.setName(name);
			userCopy.setSurname(surname);
			userCopy.setAddress(address);
			userCopy.setEmail(email);
			userCopy.setPhone(phone);
			userCopy.setIdentifier(identifier);
			userCopy.setAvatar(avatar);

			// 4. Guardamos los cambios
			this.userService.save(userCopy);
			
			// 5. Volvemos al inicio - COMPROBACIÓN
			user = this.userService.findOne(userId);
			Assert.notNull(user);
			Assert.notNull(user.getName());
			Assert.isTrue(user.getName().equals(name));
			Assert.notNull(user.getSurname());
			Assert.isTrue(user.getSurname().equals(surname));
			if(address != null && !address.equals(""))Assert.isTrue(user.getAddress().equals(address));
			Assert.notNull(user.getEmail());
			Assert.isTrue(user.getEmail().equals(email));
			if(phone != null && !phone.equals(""))Assert.isTrue(user.getPhone().equals(phone));
			if(avatar != null && !avatar.equals("")) 
				Assert.isTrue(user.getAvatar().equals(avatar));
			else {
				defaultAvatar = this.configurationService.findDefaultAvatar();
				Assert.notNull(defaultAvatar);
				Assert.isTrue(user.getAvatar().equals(defaultAvatar));
			}
			
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
		result.setAddress(user.getAddress());
		result.setEmail(user.getEmail());
		result.setPhone(user.getPhone());
		result.setIsPublicWishList(user.getIsPublicWishList());
		result.setPoints(user.getPoints());
		result.setAvatar(user.getAvatar());
		result.setWishList(user.getWishList());

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