
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.LevelRepository;
import security.Authority;
import security.LoginService;
import domain.Level;

@Service
@Transactional
public class LevelService {

	// Managed repository
	@Autowired
	private LevelRepository	levelRepository;

	@Autowired
	private Validator		validator;


	// Supporting service

	// Constructor
	public LevelService() {
		super();
	}

	// Basic CRUD methods
	public Level create() {
		Level result;
		Authority authority;

		result = new Level();
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		return result;
	}

	public Collection<Level> findAll() {
		Collection<Level> result;

		result = this.levelRepository.findAll();

		return result;
	}

	public Level findOne(final int levelId) {
		Level result;

		Assert.isTrue(levelId != 0);

		result = this.levelRepository.findOne(levelId);

		return result;
	}

	public Level findOneToEdit(final int levelId) {
		Level result;
		Authority authority;

		result = this.findOne(levelId);
		Assert.notNull(result);
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		return result;
	}

	public Level save(final Level level) {
		Level result, saved, minLevel, maxLevel, lowerLevelToModified, upperLevelToModified;
		Authority authority;

		minLevel = this.levelRepository.minLevel();
		maxLevel = this.levelRepository.maxLevel();
		Assert.notNull(level);
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(level.getMinPoints() < level.getMaxPoints()); //Siempre los puntos mínimos son menores que los máximos
		if (level.getId() == 0) {
			Assert.isTrue(level.getMaxPoints() < minLevel.getMinPoints() || level.getMinPoints() > maxLevel.getMaxPoints()); //Si se crea uno siempre tiene que ser mayor que el máximo o menor que el mínimo
			if (level.getMaxPoints() < (minLevel.getMinPoints() - 1)) {
				minLevel.setMinPoints(level.getMaxPoints() + 1); //Se amplia el rango del que actualmente es el mínimo
				this.levelRepository.save(minLevel);
			} else if (level.getMinPoints() > (maxLevel.getMaxPoints() + 1)) {
				maxLevel.setMaxPoints(level.getMinPoints() - 1); //Se amplia el rango del que actualmente es el máximo
				this.levelRepository.save(maxLevel);
			}

		} else {
			saved = this.findOne(level.getId());
			Assert.notNull(saved);
			if (level.getId() != minLevel.getId() && level.getId() != maxLevel.getId()) {
				Assert.isTrue(level.getMinPoints() >= saved.getMinPoints() && level.getMaxPoints() <= saved.getMaxPoints()); // Si editas uno intermedio no puedes reducir los puntos mínimos ni aumentar los puntos máximos
				if (level.getMinPoints() > saved.getMinPoints() && level.getMaxPoints() == saved.getMaxPoints()) { //Si aumentas solo el mínimo actualizas el anterior
					lowerLevelToModified = this.findByPoints(saved.getMinPoints() - 1);
					lowerLevelToModified.setMaxPoints(level.getMinPoints() - 1);
					this.levelRepository.save(lowerLevelToModified);
				} else if (level.getMinPoints() == saved.getMinPoints() && level.getMaxPoints() < saved.getMaxPoints()) { //Si disminuyes solo el máximo actualizas el posterior
					upperLevelToModified = this.findByPoints(saved.getMaxPoints() + 1);
					upperLevelToModified.setMinPoints(level.getMaxPoints() + 1);
					this.levelRepository.save(upperLevelToModified);
				} else if (level.getMinPoints() > saved.getMinPoints() && level.getMaxPoints() < saved.getMaxPoints()) { //Si aumentas el mínimo y disminuyes el máximo actualizas el anterior y el posterior
					lowerLevelToModified = this.findByPoints(saved.getMinPoints() - 1);
					lowerLevelToModified.setMaxPoints(level.getMinPoints() - 1);
					upperLevelToModified = this.findByPoints(saved.getMaxPoints() + 1);
					upperLevelToModified.setMinPoints(level.getMaxPoints() + 1);
					this.levelRepository.save(lowerLevelToModified);
					this.levelRepository.save(upperLevelToModified);

				}
			} else if (level.getId() == minLevel.getId()) { //Si modificamos el mínimo
				Assert.isTrue(level.getMaxPoints() <= saved.getMaxPoints()); //No puedes aumentar el máximo
				if (level.getMaxPoints() < saved.getMaxPoints()) { //Si disminuyes el máximo actualizas el posterior
					upperLevelToModified = this.findByPoints(saved.getMaxPoints() + 1);
					upperLevelToModified.setMinPoints(level.getMaxPoints() + 1);
					this.levelRepository.save(upperLevelToModified);
				}
			} else if (level.getId() == maxLevel.getId()) { //Si modificamos el máximo
				Assert.isTrue(level.getMinPoints() >= saved.getMinPoints()); //No puedes disminuir el mínimo
				if (level.getMinPoints() > saved.getMinPoints()) { //Si aumentas el mínimo actualizas el anterior
					lowerLevelToModified = this.findByPoints(saved.getMinPoints() - 1);
					lowerLevelToModified.setMaxPoints(level.getMinPoints() - 1);
					this.levelRepository.save(lowerLevelToModified);
				}
			}
		}

		result = this.levelRepository.save(level);

		return result;
	}

	public void delete(final Level level) {
		Level levelToDelete, minLevel, maxLevel, lowerLevelToModified, upperLevelToModified;
		Authority authority;

		levelToDelete = this.findOne(level.getId());
		Assert.notNull(levelToDelete);
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.findAll().size() > 2);
		minLevel = this.levelRepository.minLevel();
		maxLevel = this.levelRepository.maxLevel();

		if (levelToDelete.getId() != minLevel.getId() && levelToDelete.getId() != maxLevel.getId()) {
			lowerLevelToModified = this.findByPoints(levelToDelete.getMinPoints() - 1);
			lowerLevelToModified.setMaxPoints((levelToDelete.getMinPoints() + levelToDelete.getMaxPoints()) / 2);
			upperLevelToModified = this.findByPoints(levelToDelete.getMaxPoints() + 1);
			upperLevelToModified.setMinPoints(((levelToDelete.getMinPoints() + levelToDelete.getMaxPoints()) / 2) + 1);
			this.levelRepository.save(lowerLevelToModified);
			this.levelRepository.save(upperLevelToModified);
		}
		this.levelRepository.delete(levelToDelete);
	}

	// Other business methods
	public Level findByPoints(final int points) {
		Level result;

		result = this.levelRepository.findByPoints(points);
		if (result == null)
			if (points > this.maxPoints())
				result = this.maxLevel();
			else if (points < this.minPoints())
				result = this.minLevel();

		return result;
	}

	public Integer maxPoints() {
		Integer result;

		result = this.levelRepository.maxPoints();

		return result;
	}

	public Integer minPoints() {
		Integer result;

		result = this.levelRepository.minPoints();

		return result;
	}

	public Level maxLevel() {
		Level result;

		result = this.levelRepository.maxLevel();

		return result;
	}

	public Level minLevel() {
		Level result;

		result = this.levelRepository.minLevel();

		return result;
	}

	public Page<Level> findAllPaginated(final int page, final int size) {
		Page<Level> result;

		result = this.levelRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}

	public Level reconstruct(final Level level, final BindingResult binding) {
		Level aux;
		Level result;

		result = level;
		if (level.getId() != 0) {
			aux = this.findOne(level.getId());

			result.setVersion(aux.getVersion());
		}
		this.validator.validate(result, binding);

		return result;
	}

}
