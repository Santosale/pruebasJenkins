
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.VolumeRepository;
import security.Authority;
import security.LoginService;
import domain.Newspaper;
import domain.SubscriptionVolume;
import domain.Volume;

@Service
@Transactional
public class VolumeService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private VolumeRepository			volumeRepository;

	@Autowired
	private Validator					validator;

	// Supporting
	@Autowired
	private NewspaperService			newspaperService;

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;

	@Autowired
	private UserService					userService;


	// services-----------------------------------------------------------

	// Constructors -----------------------------------------------------------
	public VolumeService() {
		super();
	}

	public Volume create(final int newspaperId) {
		Volume result;
		Authority authority;
		Collection<Newspaper> newspapers;
		Newspaper newspaper;

		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		newspapers = new ArrayList<Newspaper>();
		Assert.isTrue(newspaperId != 0);
		newspaper = this.newspaperService.findOne(newspaperId);
		Assert.isTrue(newspaper.getPublicationDate().compareTo(new Date()) <= 0 && newspaper.getIsPublished() == true);
		Assert.notNull(newspaper);
		newspapers.add(newspaper);
		result = new Volume();
		result.setUser(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		result.setNewspapers(newspapers);

		return result;
	}

	public Collection<Volume> findAll() {
		Collection<Volume> result;

		result = this.volumeRepository.findAll();

		return result;
	}

	public Volume findOne(final int volumeId) {
		Volume result;

		Assert.isTrue(volumeId != 0);

		result = this.volumeRepository.findOne(volumeId);

		return result;
	}

	public Volume findOneToEdit(final int volumeId) {
		Volume result;

		Assert.isTrue(volumeId != 0);
		result = this.findOne(volumeId);
		Assert.notNull(result);

		Assert.isTrue(result.getUser().getUserAccount().getId() == LoginService.getPrincipal().getId());

		return result;
	}

	public Volume save(final Volume volume) {
		Volume result;
		Newspaper newspaper;

		Assert.notNull(volume);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(volume.getUser().getUserAccount().getId() == LoginService.getPrincipal().getId());
		if (volume.getId() == 0) {
			Assert.isTrue(volume.getNewspapers().size() == 1);
			newspaper = (Newspaper) volume.getNewspapers().toArray()[0];
			Assert.isTrue(newspaper.getPublicationDate().compareTo(new Date()) <= 0 && newspaper.getIsPublished() == true);
		} else
			Assert.isTrue(volume.getNewspapers().size() >= 1);
		result = this.volumeRepository.save(volume);

		return result;
	}

	public void addNewspaper(final int volumeId, final int newspaperId) {
		Volume volume;
		Newspaper newspaper;

		Assert.isTrue(volumeId != 0);
		Assert.isTrue(newspaperId != 0);
		volume = this.findOne(volumeId);
		newspaper = this.newspaperService.findOne(newspaperId);
		Assert.notNull(newspaper);
		Assert.notNull(volume);

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(volume.getUser().getUserAccount().getId() == LoginService.getPrincipal().getId());

		Assert.isTrue(newspaper.getPublicationDate().compareTo(new Date()) <= 0 && newspaper.getIsPublished() == true);
		Assert.isTrue(!volume.getNewspapers().contains(newspaper));

		volume.getNewspapers().add(newspaper);
		this.volumeRepository.save(volume);

	}

	public void deleteNewspaper(final int volumeId, final int newspaperId) {
		Volume volume;
		Newspaper newspaper;

		Assert.isTrue(volumeId != 0);
		Assert.isTrue(newspaperId != 0);
		volume = this.findOne(volumeId);
		newspaper = this.newspaperService.findOne(newspaperId);
		Assert.notNull(newspaper);
		Assert.notNull(volume);

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(volume.getUser().getUserAccount().getId() == LoginService.getPrincipal().getId());

		Assert.isTrue(newspaper.getPublicationDate().compareTo(new Date()) <= 0 && newspaper.getIsPublished() == true);
		Assert.isTrue(volume.getNewspapers().size() > 1);
		Assert.isTrue(volume.getNewspapers().contains(newspaper));

		volume.getNewspapers().remove(newspaper);
		this.volumeRepository.save(volume);

	}

	public void deleteFromNewspaper(final Volume volume) {
		Volume volumeToDelete;

		for (final SubscriptionVolume s : this.subscriptionVolumeService.findByVolumeId(volume.getId()))
			this.subscriptionVolumeService.deleteFromVolume(s);
		volumeToDelete = this.findOne(volume.getId());

		this.volumeRepository.delete(volumeToDelete);
	}

	public void saveFromNewspaper(final Volume volume) {

		Assert.notNull(volume);
		this.volumeRepository.save(volume);
	}
	public Page<Volume> findByUserAccountId(final int userAccountId, final int page, final int size) {
		Page<Volume> result;
		Authority authority;

		Assert.isTrue(userAccountId != 0);
		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getUserAccount().getId() == userAccountId);

		result = this.volumeRepository.findByUserAccountId(userAccountId, this.getPageable(page, size));

		return result;

	}

	public Collection<Volume> findByNewspaperId(final int newspaperId) {
		Collection<Volume> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(newspaperId != 0);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.volumeRepository.findByNewspaperId(newspaperId);

		return result;

	}

	public Collection<Volume> findByCustomerIdAndNewspaperId(final int customerId, final int newspaperId) {
		Collection<Volume> result;

		Assert.isTrue(newspaperId != 0);
		Assert.isTrue(customerId != 0);

		result = this.volumeRepository.findByCustomerIdAndNewspaperId(customerId, newspaperId);

		return result;

	}

	public Page<Volume> findAllPaginated(final int page, final int size) {
		Page<Volume> result;

		result = this.volumeRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	public Volume reconstruct(final Volume volume, final BindingResult binding) {
		Volume result;
		Volume aux;

		if (volume.getId() == 0)
			result = volume;
		else {
			result = volume;
			aux = this.volumeRepository.findOne(volume.getId());
			result.setVersion(aux.getVersion());
			result.setUser(aux.getUser());
			result.setNewspapers(aux.getNewspapers());
			result.setTitle(volume.getTitle());
			result.setDescription(volume.getDescription());
			result.setYear(volume.getYear());
		}

		this.validator.validate(result, binding);

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

}
