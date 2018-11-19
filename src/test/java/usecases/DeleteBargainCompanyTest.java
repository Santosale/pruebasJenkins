
package usecases;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.BargainService;
import services.CategoryService;
import services.CommentService;
import services.SponsorshipService;
import services.TagService;
import services.UserService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Category;
import domain.Comment;
import domain.Sponsorship;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteBargainCompanyTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private UserService			userService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private TagService			tagService;

	@Autowired
	private CommentService		commentService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Borrar chollo con comentarios, sponsorships...
	 * 2. Borrar chollo con una tag que se debe borrar del sistema.
	 */

	@Test
	public void driverDeletePositive() {

		//userName, bargainName, tagsName, expected
		final Object testingData[][] = {
			{
				"company1", "bargain1", "", null
			}, {
				"company1", "bargain10", "tag4", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDelete((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Borrar chollo de otra compañía. IllegalArgumentException
	 * 2. Borrar chollo con user. IllegalArgumentException
	 * 3. Borrar chollo sin autenticarse. IllegalArgumentException
	 */

	@Test
	public void driverDeleteNegative() {

		//userName, bargainName, tagsName, expected
		final Object testingData[][] = {
			{
				"company2", "bargain1", IllegalArgumentException.class
			}, {
				"user1", "bargain12", IllegalArgumentException.class
			}, {
				null, "bargain12", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollos de la compañía.
	 * 3. Escoger un chollo.
	 * 4. Borrarlo
	 */

	protected void templateDelete(final String userName, final String bargainName, final String tagsName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;
		Collection<Sponsorship> sponsorships;
		Collection<Comment> comments;
		Map<Category, Integer> categoryNumberBargain;
		Map<User, Integer> userNumberBargain;
		Integer commentNumber;
		int bargainToDelete;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));
			bargainToDelete = bargain.getId();

			//Vemos las categorías que lo contienen cuantos bargains tienen
			categoryNumberBargain = new HashMap<Category, Integer>();
			for (final Category category : this.categoryService.findAllByBargainId(bargain.getId()))
				categoryNumberBargain.put(category, category.getBargains().size());

			//Vemos los Sponsorships que tiene
			sponsorships = this.sponsorshipService.findByBargainId(bargain.getId());

			//Vemos los comments que tiene
			commentNumber = this.commentService.findByBargainId(bargain.getId(), 1, 1).getTotalPages();
			comments = this.commentService.findByBargainId(bargain.getId(), 1, commentNumber).getContent();

			//Vemos los usuarios que lo tienen cuantos bargains tienen
			userNumberBargain = new HashMap<User, Integer>();
			for (final User user : this.userService.findAll())
				if (user.getWishList().contains(bargain))
					userNumberBargain.put(user, user.getWishList().size());

			//Obtenemos los bargains
			bargains = this.bargainService.findByCompanyId(1, 1);
			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findByCompanyId(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (bargains.getContent().size() == 0)
					break;

				// Navegar hasta el bargain que queremos.
				for (final Bargain newBargain : bargains.getContent())
					if (newBargain.equals(bargain)) {
						bargainChoosen = newBargain;
						break;
					}

				if (bargainChoosen != null)
					break;
			}

			//Ya tenemos el bargain
			Assert.notNull(bargainChoosen);

			this.bargainService.delete(bargain);

			this.bargainService.flush();

			//Comprobamos que se actualiza y se crea todo
			Assert.isNull(this.bargainService.findOne(bargainToDelete));

			for (final Comment comment : comments)
				Assert.isNull(this.commentService.findOne(comment.getId()));

			for (final Sponsorship sponsorship : sponsorships)
				Assert.isNull(this.sponsorshipService.findOne(sponsorship.getId()));

			for (final User user : userNumberBargain.keySet())
				Assert.isTrue(userNumberBargain.get(user) - 1 == this.userService.findOne(user.getId()).getWishList().size());

			for (final Category category : categoryNumberBargain.keySet())
				Assert.isTrue(categoryNumberBargain.get(category) - 1 == this.categoryService.findOne(category.getId()).getBargains().size());

			for (final String tagName : tagsName.split(","))
				if (!tagName.equals(""))
					Assert.isNull(this.tagService.findOne(super.getEntityId(tagName)));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Borrar bargain por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDeleteUrl(final String userName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Bargain bargain;
		Integer bargainId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			bargainId = super.getEntityId(bargainName);
			bargain = this.bargainService.findOne(bargainId);

			this.bargainService.delete(bargain);

			this.bargainService.flush();

			//Comprobamos que no existe ya
			Assert.notNull(this.bargainService.findOne(bargainId));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
