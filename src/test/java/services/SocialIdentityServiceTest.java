
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import domain.Actor;
import domain.Explorer;
import domain.Ranger;
import domain.SocialIdentity;
import domain.Sponsor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class SocialIdentityServiceTest extends AbstractTest {

	// Service under test ---------------------------------

	@Autowired
	private SocialIdentityService	socialIdentityService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private RangerService			rangerService;

	@Autowired
	private SponsorService			sponsorService;


	// Tests ----------------------------------------------

	/*
	 * Creamos un nuevo SocialIdentity y comprobamos que sus atributos tengan el
	 * valor esperado
	 */
	@Test
	public void testCreate() {
		SocialIdentity socialIdentity;
		Explorer explorer;

		super.authenticate("explorer1");

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentity = this.socialIdentityService.create(explorer);

		Assert.notNull(socialIdentity);

		Assert.isNull(socialIdentity.getLink());
		Assert.isNull(socialIdentity.getNick());
		Assert.isNull(socialIdentity.getPhoto());
	}

	/*
	 * Creamos un SocialIdentity desde un Explorer y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSaveExplorer() {
		SocialIdentity socialIdentity;
		Explorer explorer;

		super.authenticate("explorer1");

		int oldCountSocialIdentity;

		oldCountSocialIdentity = this.socialIdentityService.findAll().size();

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentity = this.socialIdentityService.create(explorer);

		socialIdentity.setLink("https://web.tuenti.com/");
		socialIdentity.setNick("antonio");
		socialIdentity.setPhoto("https://web.tuenti.com/photouser");
		socialIdentity.setSocialNetwork("Tuenti");

		this.socialIdentityService.save(socialIdentity);

		Assert.isTrue(this.socialIdentityService.findAll().size() == oldCountSocialIdentity + 1);

		super.authenticate(null);

	}

	/*
	 * Creamos un SocialIdentity desde un Explorer y lo guardamos en la base de
	 * datos
	 */
	@Test
	public void testSaveSponsor() {
		SocialIdentity socialIdentity;
		Sponsor sponsor;

		super.authenticate("sponsor1");

		int oldCountSocialIdentity;

		oldCountSocialIdentity = this.socialIdentityService.findAll().size();

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentity = this.socialIdentityService.create(sponsor);

		socialIdentity.setLink("https://web.tuenti.com/");
		socialIdentity.setNick("antonio");
		socialIdentity.setPhoto("https://web.tuenti.com/photouser");
		socialIdentity.setSocialNetwork("Tuenti");

		this.socialIdentityService.save(socialIdentity);

		Assert.isTrue(this.socialIdentityService.findAll().size() == oldCountSocialIdentity + 1);

		super.authenticate(null);

	}

	/*
	 * Creamos un SocialIdentity desde un Explorer, lo guardamos en la base de
	 * datos y la borramos
	 */
	@Test
	public void testDeleteExplorer() {
		SocialIdentity socialIdentity, result;
		Explorer explorer;

		super.authenticate("explorer1");

		int oldCountSocialIdentity;

		oldCountSocialIdentity = this.socialIdentityService.findAll().size();

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentity = this.socialIdentityService.create(explorer);

		socialIdentity.setLink("https://web.tuenti.com/");
		socialIdentity.setNick("antonio");
		socialIdentity.setPhoto("https://web.tuenti.com/photouser");
		socialIdentity.setSocialNetwork("Tuenti");

		result = this.socialIdentityService.save(socialIdentity);
		this.socialIdentityService.delete(result);

		Assert.isTrue(this.socialIdentityService.findAll().size() == oldCountSocialIdentity);

		super.authenticate(null);

	}

	/*
	 * Creamos un SocialIdentity desde un Ranger, lo guardamos en la base de
	 * datos y la borramos
	 */
	@Test
	public void testDeleteRanger() {
		SocialIdentity socialIdentity, result;
		Ranger ranger;

		super.authenticate("ranger1");

		int oldCountSocialIdentity;

		oldCountSocialIdentity = this.socialIdentityService.findAll().size();

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentity = this.socialIdentityService.create(ranger);

		socialIdentity.setLink("https://web.tuenti.com/");
		socialIdentity.setNick("antonio");
		socialIdentity.setPhoto("https://web.tuenti.com/photouser");
		socialIdentity.setSocialNetwork("Tuenti");

		result = this.socialIdentityService.save(socialIdentity);
		this.socialIdentityService.delete(result);

		Assert.isTrue(this.socialIdentityService.findAll().size() == oldCountSocialIdentity);

		super.authenticate(null);

	}

	// Se cogen todas los socialIdentity y vemos si tiene el valor es el
	// esperado.
	@Test
	public void testFindAll() {
		Integer allSocialIdentities;

		allSocialIdentities = this.socialIdentityService.findAll().size();

		Assert.isTrue(allSocialIdentities == 2);
	}

	// Un socialIdentity accede a el mismo a traves de findOne
	@Test
	public void testFindOne() {
		SocialIdentity socialIdentityCollection;
		SocialIdentity socialIdentity;
		Collection<SocialIdentity> saved;
		Ranger ranger;

		super.authenticate("ranger1");

		saved = this.socialIdentityService.findAll();

		ranger = this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId());

		socialIdentityCollection = this.socialIdentityService.create(ranger);

		for (final SocialIdentity contacts : saved) {
			socialIdentityCollection = contacts;
			break;
		}

		socialIdentity = this.socialIdentityService.findOne(socialIdentityCollection.getId());

		Assert.isTrue(socialIdentityCollection.getId() == socialIdentity.getId());

		super.authenticate(null);

	}

	@Test
	public void testFindByActorExplorer() {
		Collection<SocialIdentity> result, saved;
		Actor actor;

		super.authenticate("explorer1");

		actor = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());

		saved = this.socialIdentityService.findAll();

		result = this.socialIdentityService.findByActorId(actor.getId());

		Assert.isTrue(saved.containsAll(result));

		for (final SocialIdentity folders : result)
			Assert.isTrue(folders.getActor().equals(actor));
	}

}
