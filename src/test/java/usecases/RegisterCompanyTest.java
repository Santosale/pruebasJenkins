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

import domain.Company;
import forms.CompanyForm;

import services.CompanyService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterCompanyTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CompanyService		companyService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar compa��a con telefono y direcci�n a null
	 *		2. Probando registrar compa��a con telefono pero con direcci�n a null
	 * 		3. Probando registrar compa��a con telefono a vac�o y direcci�n a null
	 * 		4. Probando registrar compa��a con telefono a null y direcci�n
	 * 		5. Probando registrar compa��a con telefono a null y direcci�n a vac�o
	 * 		6. Probando registrar compa��a con telefono y direcci�n
	 * 		7. Probando registrar compa��a con telefono y direcci�n a vac�o
	 * 		8. Probando registrar compa��a con la imagen vac�a
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no est� autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void positiveRegisterCompanyTest() {
		final Object testingData[][] = {
				{
					null, "antonio1", "antonio1", "antonio1", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", "Motos y carretillas", "SL", null 
				}, {
					null, "alexito", "alexito", "alexito", true, "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", "Example company", "SL", null 
				}, {
					null, "carlos", "carlos", "carlos", true, "Carlos", "S�nchez", null, null, "carloscompany@mail.com", "20063918Y", "Example company", "SL", null 
				}, {
					null, "paquito", "paquito", "paquito", true, "Paco", "Mill�n", null, "Calle Real N�6", "paquito@mail.com", "20063918Y", "Example company", "SL", null 
				}, {
					null, "manolo", "manolo", "manolo", true, "Manolo", "Guillen", null, "", "manolete@mail.com", "20063918Y", "Example company", "SL", null 
				}, {
					null, "pepito", "pepito", "pepito", true, "Pepe", "Escolar", "321456987", "Direcci�n incorrecta", "pepe@mail.com", "20063918Y", "Example company", "SL", null
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SL", null 
				}, {
					null, "francisco", "francisco", "francisco",  true,"Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SL", null 
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (String) testingData[i][12], (Class<?>) testingData[i][13]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Una compa��a no puede registrar un usuario
	 * 		2. Un patrocinador no puede registrar un usuario
	 * 		3. Un usuario no puede registrar un usuario
	 * 		4. Un administrador no puede registrar un usuario
	 * 		5. Un moderador no puede registrar un usuario
	 * 		6. El correo electr�nico debe cumplir un patr�n determinado
	 * 		7. El nombre no puede ser nulo
	 * 		8. El apellido no puede ser nulo
	 * 		9. El nombre no puede estar vac�o
	 * 		10. El apellido no puede estar vac�o
	 * 		11. El correo electr�nico no puede ser nulo
	 * 		12. El electr�nico no puede ser vac�o
	 * 		13. El companyname debe estar entre 5 y 32 caracteres
	 * 		14. El companyname debe estar entre 5 y 32 caracteres
	 * 		15. El identificador debe tener un formato v�lido
	 * 		16. El identificador no puede estar vac�o
	 * 		17. El identificador no puede ser nulo
	 * 		18. El nombre de la empresa no puede estar vac�o
	 * 		19. El nombre de la empresa no puede ser nulo
	 * 		20. El tipo de empresa tiene que cumplir el patr�n
	 * 		21. La confirmaci�n de la contrase�a tiene que coincidir con la contrase�a
	 * 		22. Se debe aceptar los t�rminos y condiciones
	 * 		23. El nombre de usuario no puede estar vac�o
	 * 	 	24. El tipo de empresa no puede estar vac�o
	 * 		25. El tipo de empresa no puede ser nulo
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no est� autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void negativeRegisterCompanyTest() {
		final Object testingData[][] = {
				{
					"company1", "company13", "company13", "company13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					"sponsor1", "company13", "company13", "company13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					"company2", "company13", "company13", "company13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					"admin", "company23", "company23", "company23", true, "Antonio", "Aza�a", "652147893", null, "ant@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					"moderator1", "company23", "company23", "company23", true, "Antonio", "Perez", "", "Calle Manager N�41", "ant@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					null, "marta", "marta", "marta", true, "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "aza�a", "aza�a", "aza�a", true, null, "Aza�a", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "marta", "marta", "marta", true, "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "aza�a2", "aza�a2", "aza�a2", true, "", "Aza�a", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "marta2", "marta2", "marta2", true, "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				},{
					null, "marta3", "marta3", "marta3", true, "Marta", "Aza�a", "664857123", "Calle Novena", null, "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "maria", "maria", "maria", true, "Mar�a", "Villar�n", "664254123", "Inserte direcci�n", "", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "gost", "gostino", "gostino", true, "Gostin", "Perez", "", "Calle Company N�41", "gostin@mail.com", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "administratoradministratoradministrator", "admin", "admin", true, "Gostin", "Perez", "", "Calle Company N�41", "gostin@mail.com", "20063918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "2006-A-3-A918Y", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "", "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", null, "Example company", "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "", "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null, "SL", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SLS", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "asd", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class 
				}, {
					null, "francisco", "francisco", "francisco", false, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class  
				}, {
					null, null, "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "SL", IllegalArgumentException.class  
				}, {
					null, null, "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", "", ConstraintViolationException.class  
				}, {
					null, null, "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Example company", null, ConstraintViolationException.class  
				}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (String) testingData[i][12], (Class<?>) testingData[i][13]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Registrarse como compa��a
	 * 	Pasos:
	 * 		1. Creamos una nueva compa��a
	 * 		2. Le asignamos los datos introducidos
	 * 		3. Guardamos la compa��a
	 * 		4. Cerramos sesi�n
	 * 		5. Comprobamos que se ha creado y con los datos correspondientes
	 */
	protected void template(final String companyBean, final String username, final String password, final String checkPassword, final boolean check, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final String companyName, final String type, final Class<?> expected) {
		Class<?> caught;
		Company reconstructedCompany, companySave, company;
		CompanyForm companyForm;
		DataBinder binder;

		caught = null;
		try {
			
			super.authenticate(companyBean);
			
			companyForm = new CompanyForm();
			companyForm.setUsername(username);
			companyForm.setPassword(password);
			companyForm.setCheckPassword(checkPassword);
			companyForm.setName(name);
			companyForm.setSurname(surname);
			companyForm.setPhone(phone);
			companyForm.setEmail(email);
			companyForm.setAddress(address);
			companyForm.setIdentifier(identifier);
			companyForm.setCompanyName(companyName);
			companyForm.setType(type);
			companyForm.setCheck(check);
			
			binder = new DataBinder(companyForm);
			reconstructedCompany = this.companyService.reconstruct(companyForm, binder.getBindingResult());
			companySave = this.companyService.save(reconstructedCompany);
			Assert.notNull(companySave);
			
			this.companyService.flush();
			
			company = this.companyService.findOne(companySave.getId());
			Assert.notNull(company);
			Assert.notNull(company.getUserAccount().getUsername());
			Assert.notNull(company.getUserAccount().getPassword());
			Assert.notNull(company.getName());
			Assert.isTrue(company.getName().equals(name));
			Assert.notNull(company.getSurname());
			Assert.isTrue(company.getSurname().equals(surname));
			if(address != null && !address.equals(""))Assert.isTrue(company.getAddress().equals(address));
			Assert.notNull(company.getEmail());
			Assert.isTrue(company.getEmail().equals(email));
			if(phone != null && !phone.equals(""))Assert.isTrue(company.getPhone().equals(phone));
			Assert.notNull(company.getCompanyName());
			Assert.isTrue(company.getCompanyName().equals(companyName));
			Assert.notNull(company.getType());
			Assert.isTrue(company.getType().equals(type));
			
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.companyService.findAll().contains(company));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

