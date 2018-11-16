
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.StageRepository;
import security.LoginService;
import security.UserAccount;
import domain.Stage;
import domain.Trip;

@Service
@Transactional
public class StageService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private StageRepository			stageRepository;

	//Supporting services -----------------------------------------------------------
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TripService				tripService;


	// Constructors -----------------------------------------------------------
	public StageService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Stage create(final Trip trip) {
		Stage result;

		result = new Stage();
		result.setTrip(trip);

		return result;
	}
	public Stage findOne(final int stageId) {
		Stage result;

		Assert.isTrue(stageId != 0);
		result = this.stageRepository.findOne(stageId);

		return result;
	}

	public Collection<Stage> findAll() {
		Collection<Stage> result;

		result = this.stageRepository.findAll();

		return result;
	}

	public Stage save(final Stage stage) {
		Stage result;
		UserAccount userAccount;
		Stage saved;

		Assert.notNull(stage);
		Assert.notNull(stage.getTrip());

		//Solo su manager puede crear o actualizar stages de sus trips.
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(stage.getTrip().getManager().getUserAccount().equals(userAccount), "stage.equals.manager");

		//Si se esta actualizando vemos que el numero de stage es el mismo. Si no metemos el siguiente.
		if (stage.getId() != 0) {
			saved = this.stageRepository.findOne(stage.getId());
			Assert.isTrue(saved.getNumStage() == stage.getNumStage(), "stage.equals.num.stage");

		} else
			stage.setNumStage(stage.getTrip().getStages().size() + 1);

		result = this.stageRepository.save(stage);

		//Guardamos los trips
		result.getTrip().getStages().add(result);
		this.tripService.save(result.getTrip());

		return result;
	}

	public void delete(final Stage stage) {
		UserAccount userAccount;
		Collection<Stage> stages;

		Assert.notNull(stage, "stage.not.null");
		userAccount = LoginService.getPrincipal();
		//Solo su manager puede borrarlo.
		Assert.isTrue(stage.getTrip().getManager().getUserAccount().equals(userAccount), "stage.equals.manager");
		stage.getTrip().getStages().remove(stage);
		this.tripService.save(stage.getTrip());
		this.stageRepository.delete(stage);

		//Actualizamos los numStages
		//Cogemos los stages que tienen un numero mayor que el que se borra
		stages = this.findByTripIdMinNumStage(stage.getNumStage(), stage.getTrip().getId());
		//Si hay algunos, los actualizamos
		if (stages != null && !stages.isEmpty())
			this.updateNumStage(stage.getNumStage(), stages);

	}
	//Other business methods -------------------------------------------------
	Collection<Stage> findByTripIdMinNumStage(final int numMin, final int tripId) {
		Collection<Stage> result;

		Assert.isTrue(tripId != 0);
		Assert.isTrue(numMin != 0);
		result = this.stageRepository.findByTripIdMinNumStage(numMin, tripId);

		return result;
	}

	public Collection<Stage> findByTripIdOrderByNumStage(final int tripId) {
		Collection<Stage> result;

		Assert.isTrue(tripId != 0);

		result = this.stageRepository.findByTripIdOrderByNumStage(tripId);
		return result;
	}

	public Collection<Stage> findByTripId(final int tripId) {
		Collection<Stage> result;
		Assert.isTrue(tripId != 0);

		result = this.stageRepository.findByTripId(tripId);
		return result;
	}

	//Si un trip tiene 3 stages y borramos la segunda, se cambian los números de stages para que sigan siendo consecutivos y positivos
	private void updateNumStage(Integer firstNum, final Collection<Stage> stagesUpdate) {
		Trip trip;
		Stage stage;
		Stage saved;

		//Guardamos el trip
		if (stagesUpdate.size() > 0) {
			stage = (Stage) stagesUpdate.toArray()[0];
			trip = stage.getTrip();

			//Cambiamos las stages, las guardamos y actualizamos los trips
			for (final Stage stageIterator : stagesUpdate) {
				stageIterator.setNumStage(firstNum);
				saved = this.stageRepository.save(stageIterator);
				trip.getStages().remove(stageIterator);
				trip.getStages().add(saved);
				firstNum++;

			}

			//Guardamos el trip
			this.tripService.save(trip);
		}

	}

	public boolean checkSpamWords(final Stage stage) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (stage.getDescription() != null && stage.getDescription().toLowerCase().contains(spamWord)) || (stage.getTitle() != null && stage.getTitle().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}

}
