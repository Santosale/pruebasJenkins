
package controllers.moderator;

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

import services.CommentService;
import services.PlanService;
import controllers.AbstractController;
import domain.Comment;

@Controller
@RequestMapping("/comment/moderator")
public class CommentModeratorController extends AbstractController {

	// Services
	@Autowired
	private CommentService	commentService;
	
	@Autowired
	private PlanService		planService;
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page) {
		ModelAndView result;
		Page<Comment> comments;
		Map<Comment, Boolean> mapCommentBoolean;
		Boolean hasPlan;
				
		comments = this.commentService.findAllComments(page, 5);
		Assert.notNull(comments);
		

		mapCommentBoolean = new HashMap<Comment, Boolean>();
		for (Comment c : comments) {
			hasPlan = false;
			if (this.planService.findByUserId(c.getUser().getId()) != null)
				hasPlan = true;
			mapCommentBoolean.put(c, hasPlan);
		}
		
		result = new ModelAndView("comment/list");
		result.addObject("requestURI", "comment/moderator/list.do");
		result.addObject("comments", comments.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", comments.getTotalPages());
		result.addObject("mapCommentBoolean", mapCommentBoolean);
				
		return result;
	}
	
	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int commentId) {
		ModelAndView result;
		Comment comment;

		comment = this.commentService.findOne(commentId);
		Assert.notNull(comment);

		result = this.createEditModelAndView(comment);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Comment comment, final BindingResult binding) {
		ModelAndView result;
		
		comment = this.commentService.reconstruct(comment, binding);

		try {
			Assert.notNull(comment.getBargain());
			this.commentService.deleteModerator(comment);
			result = new ModelAndView("redirect:/comment/moderator/list.do");
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

		result = new ModelAndView("comment/edit");

		result.addObject("comment", comment);
		result.addObject("actor", "moderator");
		result.addObject("message", messageCode);
		result.addObject("requestURI", "comment/moderator/edit.do");

		return result;
	}

}
