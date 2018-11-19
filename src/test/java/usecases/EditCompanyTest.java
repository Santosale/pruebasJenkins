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
public class EditCompanyTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CompanyService		companyService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Una compañía trata de editar su perfil quitando el campo dirección y teléfono
	 * 		2. Una compañía trata de editar su perfil quitando el campo dirección
	 * 		3. Una compañía trata de editar su perfil dejando teléfono vacío y quitando el campo dirección
	 * 		4. Una compañía trata de editar su perfil dejando dirección vacía y quitando el campo teléfono
	 * 		5. Una compañía trata de editar su perfil cambiando los valores normalmente
	 * 
	 * Requsitos:
	 * 		Los actores pueden editar su perfil
	 * 
	 */
	@Test
	public void positiveEditCompanyTest() {
		final Object testingData[][] = {
			{
				"company1", "company1", "company1", "company1", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", "Company S.L.", "SL", null 
			}, {
				"company2", "company2", "company2", "company2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", "Company S.L.", "SL", null
			}, {
				"company3", "company3", "company3", "company3", "Carlos", "Sánchez", "", null, "carloscompany@mail.com", "20063918Y", "Company S.L.", "SL", null
			}, {
				"company3", "company3", "company3", "company3", "Carlos", "Sánchez", null, "", "paquito@mail.com", "20063918Y", "Company S.L.", "SL", null 
			}, {
				"company3", "company3", "company3", "company3", "Carlos", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", "20063918Y", "Company S.L.", "SL", null 
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (Class<?>) testingData[i][12]);
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
	 * 		11. El identificador debe cumplir el patrón
	 * 		12. El identificador no puede estar vacío
	 * 		13. El identificador no puede ser nulo
	 * 		14. El nombre de la compañía no puede estar vacío
	 * 		15. El nombre de la compañía no puede ser nulo
	 * 		16. El tipo de empresa debe cumplir el patrón
	 * 		17. El tipo de empresa no puede estar vacío
	 * 		18. El tipo de empresa no puede ser nulo
	 * 
	 * Requisitos:
	 * 		Los actores pueden editar su perfil
	 * 
	 */
	@Test
	public void negativeEditCompanyTest() {
		final Object testingData[][] = {
			{
				"company1", "company2", "company2", "company2", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", "Company S.L.", "SL", IllegalArgumentException.class 
			}, {
				"admin", "company2", "company2", "company2", "Antonio", "Azana", "652147893", null, "ant@mail.com", "20063918Y", "Company S.L.", "SL", IllegalArgumentException.class
			}, {
				"manager1", "company2", "company2", "company2", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", "20063918Y", "Company S.L.", "SL", IllegalArgumentException.class
			}, {
				"company1", "company1", "company1", "company1", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company2", "company2", "company2", "company2", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class
			}, {
				"company1", "company1", "company1", "company1", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class
			}, {
				"company3", "company3", "company3", "company3", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class
			}, {
				"company1", "company1", "company1", "company1", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Carlos", "Sánchez", "664857123", "Calle Novena", null, "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company1", "company1", "company1", "company1", "Maria", "Villarin", "664254123", "Inserte direccion", "", "20063918Y", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063-A-A918Y", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "", "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", null, "Company S.L.", "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "", "ACH", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null, "SL", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Company S.L.", "SLAA", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Company S.L.", "", ConstraintViolationException.class 
			}, {
				"company3", "company3", "company3", "company3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", "Company S.L.", null, ConstraintViolationException.class 
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (Class<?>) testingData[i][12]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Editar una compañía
	 * 	Pasos:
	 * 		1. Autenticar como compañía
	 * 		2. Entramos en la vista de editar compañía
	 * 		3. Le asignamos los datos introducidos
	 * 		4. Guardamos la compañía
	 * 		5. Cerramos sesión
	 * 		6. Comprobamos que se ha editado y con los datos correspondientes
	 */
	protected void template(final String companyBean, final String companyEdit, final String username, final String password, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final String companyName, final String type, final Class<?> expected) {
		Class<?> caught;
		Company reconstructedCompany, companySave, company, companyEntity;
		CompanyForm companyForm;
		Integer companyId;
		DataBinder binder;

		caught = null;
		try {
			
			super.authenticate(companyBean);
			
			companyId = super.getEntityId(companyEdit);
			Assert.notNull(companyId);
			companyEntity = this.companyService.findOne(companyId);
			Assert.notNull(companyEntity);
			companyForm = this.copyCompanyToForm(companyEntity);
			companyForm.setUsername(username);
			companyForm.setPassword(password);
			companyForm.setName(name);
			companyForm.setSurname(surname);
			companyForm.setPhone(phone);
			companyForm.setEmail(email);
			companyForm.setAddress(address);
			companyForm.setIdentifier(identifier);
			companyForm.setCompanyName(companyName);
			companyForm.setType(type);
			
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
	
	private CompanyForm copyCompanyToForm(final Company company) {
		CompanyForm result;

		result = new CompanyForm();
		result.setId(company.getId());
		result.setName(company.getName());
		result.setSurname(company.getSurname());
		result.setAddress(company.getAddress());
		result.setEmail(company.getEmail());
		result.setPhone(company.getPhone());
		result.setIdentifier(company.getIdentifier());
		result.setCompanyName(company.getCompanyName());
		result.setType(company.getType());

		return result;
	}

}