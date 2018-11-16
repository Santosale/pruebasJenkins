
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ApplicationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Administrator;
import domain.Application;
import domain.Explorer;
import domain.Manager;
import domain.Message;
import domain.SurvivalClass;
import domain.Trip;

@Service
@Transactional
public class ApplicationService {

	// Managed repository----------------
	@Autowired
	private ApplicationRepository	applicationRepository;

	// Supporting Services-----------------
	@Autowired
	private MessageService			messageService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private AdministratorService	administratorService;


	// Constructor------------
	public ApplicationService() {
		super();
	}

	// Simple Crud Methods-----------------
	public Application create(final Explorer explorer, final Trip trip) {
		Application result;
		Date currentMoment;
		Authority authority;
		Collection<SurvivalClass> survivalClasses;

		authority = new Authority();
		authority.setAuthority("EXPLORER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority)); //Solo puede crearlo un explorer

		survivalClasses = new ArrayList<SurvivalClass>();

		currentMoment = new Date(System.currentTimeMillis() - 1);
		result = new Application();
		result.setMoment(currentMoment);
		result.setStatus("PENDING");
		result.setApplicant(explorer);
		result.setTrip(trip);
		result.setSurvivalClasses(survivalClasses);

		return result;
	}

	public Collection<Application> findAll() {
		Collection<Application> result;

		result = (this.applicationRepository.findAll());

		return result;
	}
	public Application findOne(final int applicationId) {
		Application result;

		Assert.isTrue(applicationId != 0);
		result = this.applicationRepository.findOne(applicationId);

		return result;
	}

	public Application save(final Application application) {
		Application result;
		UserAccount userAccount;
		Application saved;
		Message notification;
		Collection<Actor> actores;
		Date currentMoment;
		Administrator administrator;
		Collection<Administrator> administrators;
		Calendar calendar;

		currentMoment = new Date(System.currentTimeMillis() - 1);
		actores = new ArrayList<Actor>();
		administrator = null;

		Assert.notNull(application);
		userAccount = LoginService.getPrincipal();

		saved = this.applicationRepository.findOne(application.getId());
		Assert.isTrue(application.getTrip().getPublicationDate().compareTo(currentMoment) < 0, "application.publication.current");

		if (application.getId() != 0) {
			//	if (application.getSurvivalClasses().equals(saved.getSurvivalClasses())) {
			Assert.isTrue(application.getTrip().getManager().getUserAccount().equals(userAccount), "application.equals.manager"); //Si la aplicación el logeado es el manager del trip de la aplicacion
			Assert.isTrue(application.getTrip().getStartDate().compareTo(currentMoment) > 0, "application.start.current"); //Si se va a modificar el estado no puede si ya ha empezado el viaje
			//} else
			//Assert.isTrue(application.getApplicant().getUserAccount().equals(userAccount)); //Si se modifican las survival classes es el explorer
		} else {
			Assert.isTrue(application.getTrip().getStartDate().compareTo(currentMoment) > 0, "application.publication.current"); //No puede crearlo si ya ha empezado el viaje
			Assert.isTrue(application.getStatus().equals("PENDING"), "application.status.pending");
			Assert.isNull(this.applicationRepository.findByTripIdAndExplorerId(application.getTrip().getId(), application.getApplicant().getId()), "application.null");
			Assert.isTrue(application.getApplicant().getUserAccount().equals(userAccount), "application.equals.applicant");
			Assert.isTrue(application.getTrip().getCancellationReason() == null, "application.cancellation.null");
			Assert.isNull(application.getCreditCard(), "application.creditcard.null");
			Assert.isTrue(application.getDeniedReason() == "" || application.getDeniedReason() == null, "application.deniedreason.empty.null");
			calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
			calendar.getTime().setTime(calendar.getTimeInMillis() - 1);
			application.setMoment(calendar.getTime());
			//Si no persiste se tienen que cumple todos los Assert anteriores a la hora de persistor

		}

		if (saved != null)
			switch (saved.getStatus()) {
			case "PENDING":
				//Si exï¿½stia y el estado es pending al modificarlo la creditCard debe ser nula y el status DUE, REJECTED O PENDING
				Assert.isNull(application.getCreditCard(), "application.creditcard.null");
				Assert.isTrue(application.getStatus().equals("DUE") || application.getStatus().equals("REJECTED") || application.getStatus().equals("PENDING"), "application.status.due.rejected");

				if (application.getStatus().equals("DUE") || application.getStatus().equals("REJECTED")) {

					administrators = this.administratorService.findAll();
					Assert.notEmpty(administrators, "application.administrators.no.empty");
					for (final Administrator ad : administrators) {
						administrator = ad;
						break;
					}
					notification = this.messageService.create(administrator);
					notification.setPriority("NEUTRAL");
					notification.setSubject("Notification of status change");
					notification.setBody("Status " + application.getStatus());

					actores.add(application.getApplicant());
					actores.add(application.getTrip().getManager());
					//Notificaciï¿½n enviada a manager y explorer debido al cambio de estado

					this.messageService.sendNotification(notification, actores);
				}
				if (application.getStatus().equals("REJECTED")) {
					Assert.notNull(application.getDeniedReason(), "application.deniedreason.no.null"); //Si es rejected el deniedReason debe ser no nulo
					Assert.isTrue(!application.getDeniedReason().trim().isEmpty(), "application.deniedreason.no.empty");

				} else
					Assert.isNull(application.getDeniedReason(), "application.deniedreason.null"); //Si no es rejected el deniedReason debe ser nulo

				//Vamos a comprobar que solo se ha modificado el status. Ya que una vez creada solo se puede modificar el status. Lo haremos en todos los casos
				Assert.isTrue(saved.getMoment().compareTo(application.getMoment()) == 0, "application.equals.moment");
				Assert.isTrue(saved.getComments().equals(application.getComments()), "application.equals.comments");
				Assert.isNull(saved.getDeniedReason(), "application.deniedreason.null");
				Assert.isTrue(saved.getApplicant().equals(application.getApplicant()), "application.equals.applicant");
				Assert.isTrue(saved.getTrip().equals(application.getTrip()), "application.equals.trip");
				//Siempre comparamos el que vamos a salvar con el que ya persiste para comprobar que no se ha cambiado nada

				break;
			case "DUE":
				Assert.isTrue(application.getStatus().equals("DUE"), "application.status.due");
				Assert.isNull(application.getDeniedReason(), "application.deniedreason.null");
				Assert.isNull(saved.getDeniedReason(), "application.deniedreason.null");
				Assert.isNull(application.getCreditCard(), "application.creditcard.null");
				Assert.isTrue(saved.getMoment().compareTo(application.getMoment()) == 0, "application.equals.moment");
				Assert.isTrue(saved.getComments().equals(application.getComments()), "application.equals.comments");
				Assert.isTrue(saved.getApplicant().equals(application.getApplicant()), "application.equals.applicant");
				Assert.isTrue(saved.getTrip().equals(application.getTrip()), "application.equals.trip");
				break;
			case "REJECTED":
				Assert.isTrue(application.getStatus().equals("REJECTED"), "application.status.rejected");
				Assert.notNull(application.getDeniedReason(), "application.deniedreason.no.null");
				Assert.isNull(application.getCreditCard(), "application.creditcard.null");
				Assert.isTrue(saved.getMoment().compareTo(application.getMoment()) == 0, "application.equals.moment");
				Assert.isTrue(saved.getComments().equals(application.getComments()), "application.equals.comments");
				Assert.isTrue(saved.getDeniedReason().equals(application.getDeniedReason()), "application.equals.deniedreason");
				Assert.isTrue(saved.getApplicant().equals(application.getApplicant()), "application.equals.applicant");
				Assert.isTrue(saved.getTrip().equals(application.getTrip()), "application.equals.trip");
				break;
			case "ACCEPTED":
				Assert.notNull(application.getCreditCard(), "application.creditcard.no.null");
				Assert.isTrue(application.getStatus().equals("ACCEPTED"), "application.status.accepted");
				Assert.isNull(application.getDeniedReason(), "application.deniedreason.null");
				Assert.isNull(saved.getDeniedReason(), "application.deniedreason.null");
				Assert.isTrue(saved.getMoment().compareTo(application.getMoment()) == 0, "application.equals.moment");
				Assert.isTrue(saved.getComments().equals(application.getComments()), "application.equals.comments");
				Assert.isTrue(saved.getApplicant().equals(application.getApplicant()), "application.equals.applicant");
				Assert.isTrue(saved.getTrip().equals(application.getTrip()), "application.equals.trip");
				break;
			case "CANCELLED":
				Assert.notNull(application.getCreditCard(), "application.creditcard.no.null");
				Assert.isTrue(application.getStatus().equals("CANCELLED"), "application.satus.cancelled");
				Assert.isNull(application.getDeniedReason(), "application.deniedreason.null");
				Assert.isNull(saved.getDeniedReason(), "application.deniedreason.null");
				Assert.isTrue(saved.getMoment().compareTo(application.getMoment()) == 0, "application.equals.moment");
				Assert.isTrue(saved.getComments().equals(application.getComments()), "application.equals.comments");
				Assert.isTrue(saved.getApplicant().equals(application.getApplicant()), "application.equals.applicant");
				Assert.isTrue(saved.getTrip().equals(application.getTrip()), "application.equals.trip");
				break;
			default:
				break;
			}
		result = this.applicationRepository.save(application);

		return result;
	}

	public void delete(final Application application) {

		Assert.notNull(application);
		Assert.isTrue(application.getApplicant().getUserAccount().equals(LoginService.getPrincipal()), "application.applicant.login"); //Solo la puede borrar su explorer

		this.applicationRepository.delete(application);
	}

	// Other business methods
	public void addCreditCard(final Application application) {
		UserAccount userAccount;
		Collection<Actor> actores;
		Message notification;
		Explorer explorer;
		Administrator administrator;
		Collection<Administrator> administrators;

		Assert.notNull(application);

		actores = new ArrayList<Actor>();
		administrator = null;

		userAccount = LoginService.getPrincipal();

		explorer = application.getApplicant();
		Assert.isTrue(explorer.getUserAccount().equals(userAccount)); // Solo puede añadirla su explorer
		Assert.isTrue(application.getCreditCard().getActor().getId() == explorer.getId());
		Assert.notNull(application.getCreditCard());

		Assert.isTrue(application.getStatus().equals("DUE")); // El estado previo debía ser Due

		application.setStatus("ACCEPTED");

		Assert.isNull(application.getDeniedReason()); // No podía tener razón de denegación

		// Enviar notification
		actores.add(application.getApplicant());
		actores.add(application.getTrip().getManager());

		administrators = this.administratorService.findAll();
		Assert.notEmpty(administrators);
		for (final Administrator ad : administrators) {
			administrator = ad;
			break;
		}

		notification = this.messageService.create(administrator);
		notification.setPriority("NEUTRAL");
		notification.setSubject("Notification of status change");
		notification.setBody("Status ACCEPTED");
		this.messageService.sendNotification(notification, actores);

		//Enviamos la notificación
		this.applicationRepository.save(application);
	}
	public void cancelApplication(final int applicationId) {

		Application application;
		UserAccount userAccount;
		Collection<Actor> actores;
		Message notification;
		Date currentMoment;
		Administrator administrator;
		Collection<Administrator> administrators;

		currentMoment = new Date();
		actores = new ArrayList<Actor>();
		administrator = null;

		Assert.isTrue(applicationId != 0);
		userAccount = LoginService.getPrincipal();
		application = this.applicationRepository.findOne(applicationId);
		Assert.isTrue(application.getTrip().getStartDate().compareTo(currentMoment) > 0); //StartedDate posterior que el momento actual para poder cancelar una application

		Assert.notNull(application.getCreditCard());
		Assert.isNull(application.getDeniedReason());
		Assert.isTrue(application.getStatus().equals("ACCEPTED"));
		Assert.isTrue(application.getApplicant().getUserAccount().equals(userAccount));

		application.setStatus("CANCELLED");

		actores.add(application.getApplicant());
		actores.add(application.getTrip().getManager());

		administrators = this.administratorService.findAll();
		Assert.notEmpty(administrators);
		for (final Administrator ad : administrators) {
			administrator = ad;
			break;
		}

		notification = this.messageService.create(administrator);
		notification.setPriority("NEUTRAL");
		notification.setSubject("Notification of status change");
		notification.setBody("Status CANCELLED");
		this.messageService.sendNotification(notification, actores);
		//Mensaje enviado

		this.applicationRepository.save(application);

	}

	public void registerToASurvivalClass(final Application application, final SurvivalClass survivalClass) {
		Assert.isTrue(application.getTrip().equals(survivalClass.getTrip()));
		Assert.isTrue(application.getApplicant().getUserAccount().getId() == LoginService.getPrincipal().getId());
		Assert.isTrue(!application.getSurvivalClasses().contains(survivalClass));
		Assert.isTrue(survivalClass.getMoment().compareTo(new Date()) > 0);
		Assert.isTrue(application.getStatus().equals("ACCEPTED"));
		Assert.isTrue(application.getTrip().getEndDate().compareTo(new Date()) > 0);

		application.getSurvivalClasses().add(survivalClass);

		this.applicationRepository.save(application);
	}

	public void unRegisterToASurvivalClass(final Application application, final SurvivalClass survivalClass) {
		Assert.isTrue(application.getTrip().equals(survivalClass.getTrip()));
		Assert.isTrue(application.getApplicant().getUserAccount().getId() == LoginService.getPrincipal().getId());
		Assert.isTrue(application.getSurvivalClasses().contains(survivalClass));
		Assert.isTrue(application.getStatus().equals("ACCEPTED"));
		Assert.isTrue(application.getTrip().getEndDate().compareTo(new Date()) > 0);

		application.getSurvivalClasses().remove(survivalClass);

		this.applicationRepository.save(application);
	}

	public Collection<Application> findByManagerId(final int managerId) {
		Collection<Application> result;

		Assert.isTrue(managerId != 0);
		//Solo accesible desde el manager
		result = this.applicationRepository.findByManagerId(managerId);

		return result;
	}

	public Collection<Application> findByExplorerId(final int explorerId) {
		Collection<Application> result;
		Explorer explorer;
		UserAccount userAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(explorerId != 0);
		explorer = this.explorerService.findOne(explorerId);
		if (explorer != null)
			Assert.isTrue(explorer.getUserAccount().equals(userAccount) || LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.applicationRepository.findByExplorerId(explorerId);

		return result;
	}

	//Listar aplicaciones de un trip
	public Collection<Application> findByTripId(final int tripId) {
		Collection<Application> result;
		Manager manager;
		UserAccount userAccount;
		Authority authority;
		Trip trip;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(tripId != 0);
		trip = this.tripService.findOne(tripId);
		if (trip != null) {
			manager = trip.getManager();
			Assert.isTrue(manager.getUserAccount().equals(userAccount) || LoginService.getPrincipal().getAuthorities().contains(authority)); //Solo accesible desde el manager
		}
		result = this.applicationRepository.findByTripId(tripId);

		return result;
	}

	public Application findByTripIdAndExplorerId(final int tripId, final int explorerId) {
		Application result;
		UserAccount userAccount;
		UserAccount managerUserAccount;
		UserAccount explorerUserAccount;

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(tripId != 0);
		Assert.isTrue(explorerId != 0);

		result = this.applicationRepository.findByTripIdAndExplorerId(tripId, explorerId);
		if (result != null) {
			managerUserAccount = this.tripService.findOne(tripId).getManager().getUserAccount();
			explorerUserAccount = this.explorerService.findOne(explorerId).getUserAccount();
			Assert.isTrue(explorerUserAccount.equals(userAccount) || managerUserAccount.equals(userAccount)); //Pueden acceder tanto el manager como el explorer
		}
		return result;
	}

	public void deleteFromTrip(final int applicationId) {
		Application application;
		Assert.isTrue(applicationId != 0);
		application = this.findOne(applicationId);
		this.applicationRepository.delete(application);
	}

	public Double ratioApplicantionsDue() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.applicationRepository.ratioApplicationsWithStatusDue();

		return result;
	}

	public Double ratioApplicantionsAccepted() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.applicationRepository.ratioApplicationsWithStatusAccepted();

		return result;
	}

	public Double ratioApplicantionsCancelled() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.applicationRepository.ratioApplicationsWithStatusCancelled();

		return result;
	}

	public Double ratioApplicantionsPending() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.applicationRepository.ratioApplicationsWithStatusPending();

		return result;
	}

	public Double[] avgMinMaxStandardDNumberApplications() {
		Double[] result;
		Authority authority;

		//Solo puede acceder admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.applicationRepository.avgMinMaxStandardDNumberApplications();

		return result;
	}

	public boolean checkSpamWords(final Application application) {
		List<String> spamWords;
		boolean result;

		result = false;
		spamWords = new ArrayList<String>();
		spamWords.addAll(this.configurationService.findSpamWords());

		for (final String spamWord : spamWords) {
			result = (application.getComments() != null && application.getComments().toLowerCase().contains(spamWord)) || (application.getDeniedReason() != null && application.getDeniedReason().toLowerCase().contains(spamWord));
			if (result == true)
				break;
		}
		return result;
	}

}
