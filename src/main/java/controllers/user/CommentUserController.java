
package controllers.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.BargainService;
import services.CommentService;
import services.PlanService;
import services.UserService;
import controllers.AbstractController;
import domain.Bargain;
import domain.Comment;
import domain.User;

@Controller
@RequestMapping("/comment/user")
public class CommentUserController extends AbstractController {

	@Autowired
	private CommentService		commentService;

	@Autowired
	private BargainService		bargainService;

	@Autowired
	private UserService			userService;

	@Autowired
	private PlanService			planService;
	
	// List
		@RequestMapping(value="/list", method = RequestMethod.GET)
		public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page) {
			ModelAndView result;
			Page<Comment> comments;
			Map<Comment, Boolean> mapCommentBoolean;
			Boolean hasPlan;
					
			comments = this.commentService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
			Assert.notNull(comments);
			
			mapCommentBoolean = new HashMap<Comment, Boolean>();
			for (Comment c : comments) {
				hasPlan = false;
				if (this.planService.findByUserId(c.getUser().getId()) != null)
					hasPlan = true;
				mapCommentBoolean.put(c, hasPlan);
			}
			
			result = new ModelAndView("comment/list");
			result.addObject("requestURI", "comment/user/list.do");
			result.addObject("comments", comments.getContent());
			result.addObject("page", page);
			result.addObject("pageNumber", comments.getTotalPages());
			result.addObject("mapCommentBoolean", mapCommentBoolean);
					
			return result;
	}
		
	//Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int bargainId, @RequestParam(required = false) final Integer repliedCommentId) {
		ModelAndView result;
		Comment repliedComment;
		final Comment comment;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);

		if (repliedCommentId != null) {
			repliedComment = this.commentService.findOne(repliedCommentId);
			Assert.notNull(repliedComment);
		} else
			repliedComment = null;

		comment = this.commentService.create(bargain, repliedComment);

		result = this.createEditModelAndView(comment);

		return result;

	}
	
	//Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int commentId) {
		ModelAndView result;
		Comment comment;

		comment = this.commentService.findOne(commentId);
		Assert.notNull(comment);

		result = this.createEditModelAndView2(comment);

		return result;
	}

	//Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Comment comment, final BindingResult binding) {
		ModelAndView result;

		comment = this.commentService.reconstruct(comment, binding);

		if (binding.hasErrors()) {
			result = this.createEditModelAndView(comment);
		} else
			try {
				this.commentService.save(comment);

				result = new ModelAndView("redirect:/comment/user/list.do");

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(comment, "comment.commit.error");
			}

		return result;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Comment comment, final BindingResult binding) {
		ModelAndView result;
		
		comment = this.commentService.reconstruct(comment, binding);

		try {
			Assert.notNull(comment.getBargain());
			this.commentService.delete(comment);
			result = new ModelAndView("redirect:/comment/user/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(comment, "comment.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Comment comment) {
		ModelAndView result;

		result = this.createEditModelAndView(comment, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Comment comment, final String messageCode) {
		ModelAndView result;
		Boolean canEdit, canImage;
		User user;

		result = new ModelAndView("comment/create");

		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);

		canEdit = false;
		canImage = false;
		
		if (comment.getUser().equals(user))
			canEdit = true;
		
		if (this.planService.findByUserId(comment.getUser().getId()) != null)
			canImage = true;

		Assert.isTrue(canEdit);
		result.addObject("canEdit", canEdit);
		result.addObject("canImage", canImage);
		result.addObject("comment", comment);
		result.addObject("message", messageCode);
		result.addObject("actor", "user");
		result.addObject("requestURI", "comment/user/create.do");

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView2(final Comment comment) {
		ModelAndView result;

		result = this.createEditModelAndView2(comment, null);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Comment comment, final String messageCode) {
		ModelAndView result;
		Boolean canEdit, canImage;
		User user;
		
		result = new ModelAndView("comment/edit");

		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);

		canEdit = false;
		canImage = false;
		
		if (comment.getUser().equals(user))
			canEdit = true;

		if (this.planService.findByUserId(comment.getUser().getId()) != null)
			canImage = true;
		
		Assert.isTrue(canEdit);
		result.addObject("comment", comment);
		result.addObject("actor", "user");
		result.addObject("message", messageCode);
		result.addObject("canImage", canImage);
		result.addObject("canEdit", canEdit);
		result.addObject("requestURI", "comment/user/edit.do");
		
		return result;
	}
}
