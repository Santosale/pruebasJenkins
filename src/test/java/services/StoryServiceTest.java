
package services;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Application;
import domain.Explorer;
import domain.Story;
import domain.Trip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class StoryServiceTest extends AbstractTest {

	//Service under test----------------

	@Autowired
	private StoryService		storyService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private ApplicationService	applicationService;


	//Test-------------------
	@Test
	public void testCreate() {
		Story saved;
		Explorer explorer;
		Application application;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		application = null;
		for (final Application a : this.applicationService.findByExplorerId(explorer.getId())) {
			application = a;
			break;
		}

		saved = this.storyService.create(application.getTrip(), explorer);

		Assert.isTrue(saved.getTrip().equals(application.getTrip()));
		Assert.isTrue(saved.getWriter().equals(explorer));
		Assert.isTrue(saved.getAttachments().isEmpty());
		Assert.isNull(saved.getText());
		Assert.isNull(saved.getTitle());

		super.authenticate(null);

	}
	@Test
	public void testFindAll() {
		final Collection<Story> stories;
		Explorer explorer;
		Story story;

		super.authenticate("explorer1");
		story = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		stories = this.storyService.findAll();

		for (final Story s : this.storyService.findByExplorerId(explorer.getId())) {
			story = s;
			break;
		}

		Assert.isTrue(stories.contains(story));

		super.authenticate(null);
	}

	@Test
	public void testFindOne() {
		Collection<Story> stories;
		Explorer explorer;
		Story story;
		Story saved;

		super.authenticate("explorer1");
		story = null;
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		stories = this.storyService.findAll();

		for (final Story s : this.storyService.findByExplorerId(explorer.getId())) {
			story = s;
			break;
		}
		saved = this.storyService.findOne(story.getId());

		Assert.isTrue(stories.contains(saved));

		super.authenticate(null);
	}
	@Test
	public void testSave() {
		Story created;
		Explorer explorer;
		Application application;
		Story saved;
		Date currentMoment;

		super.authenticate("explorer1");
		currentMoment = new Date();
		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		application = null;
		for (final Application a : this.applicationService.findByExplorerId(explorer.getId()))
			if (a.getTrip().getEndDate().compareTo(currentMoment) < 0) {
				application = a;
				break;
			}

		created = this.storyService.create(application.getTrip(), explorer);
		created.setTitle("Título");
		created.setText("Un viaje mágico");

		saved = this.storyService.save(created);

		Assert.isTrue(this.storyService.findOne(saved.getId()).equals(saved));

	}
	//Intentamos modificar una nota, no se puede, por lo que salta una excepción
	@Test
	public void testSave2() {
		Story created;
		Explorer explorer;
		Story saved;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		created = null;
		for (final Story s : this.storyService.findByExplorerId(explorer.getId())) {
			created = s;
			break;
		}

		created.setText("texto nuevo");
		try {
			saved = this.storyService.save(created);
			Assert.isTrue(this.storyService.findOne(saved.getId()).equals(saved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}
	//Intentamos que otro explorer modifique la story asignada a otro explorer
	@Test
	public void testSave3() {
		Story created;
		Explorer explorer;
		Application application;
		Story saved;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		application = null;
		for (final Application a : this.applicationService.findByExplorerId(explorer.getId())) {
			application = a;
			break;
		}

		created = this.storyService.create(application.getTrip(), explorer);
		created.setTitle("Título");
		created.setText("Un viaje mágico");
		super.authenticate(null);
		super.authenticate("explorer2");
		try {
			saved = this.storyService.save(created);
			Assert.isNull(saved);
			Assert.isTrue(this.storyService.findOne(saved.getId()).equals(saved));
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}
	}
	@Test
	public void testDelete() {
		Story created;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		created = null;
		for (final Story s : this.storyService.findByExplorerId(explorer.getId())) {
			created = s;
			break;
		}

		this.storyService.delete(created);
		Assert.isNull(this.storyService.findOne(created.getId()));
		super.authenticate(null);

	}

	@Test
	public void testFindByTripId() {
		Trip trip;
		Story story;

		story = (Story) this.storyService.findAll().toArray()[0];
		trip = story.getTrip();

		Assert.isTrue(this.storyService.findByTripId(trip.getId()).contains(story));
	}

	@Test
	public void testFindByExplorerId() {
		Explorer explorer;
		Story story;

		story = (Story) this.storyService.findAll().toArray()[0];
		explorer = story.getWriter();

		Assert.isTrue(this.storyService.findByExplorerId(explorer.getId()).contains(story));
	}
}
