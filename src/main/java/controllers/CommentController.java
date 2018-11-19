
package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CommentService;
import services.ConfigurationService;
import services.PlanService;
import domain.Comment;

@Controller
@RequestMapping("/comment")
public class CommentController extends AbstractController {

	// Services
	@Autowired
	private CommentService			commentService;
	
	@Autowired
	private PlanService				planService;
	
	@Autowired
	private ConfigurationService	configurationService;
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int bargainId, @RequestParam(required = false, defaultValue="1") Integer page) {
		ModelAndView result;
		Page<Comment> comments;
		Map<Comment, Boolean> mapCommentBoolean;
		Boolean hasPlan;
				
		comments = this.commentService.findByBargainId(bargainId, page, 5);
		Assert.notNull(comments);
		mapCommentBoolean = new HashMap<Comment, Boolean>();
		for (Comment c : comments) {
			hasPlan = false;
			if (this.planService.findByUserId(c.getUser().getId()) != null)
				hasPlan = true;
			mapCommentBoolean.put(c, hasPlan);
		}
		
		result = new ModelAndView("comment/list");
		result.addObject("requestURI", "comment/list.do?bargainId=" + bargainId);
		result.addObject("comments", comments.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", comments.getTotalPages());
		result.addObject("mapCommentBoolean", mapCommentBoolean);
				
		return result;
	}
	
	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int commentId, @RequestParam(required = false) Integer page) {
		ModelAndView result;
		Comment comment;

		comment = this.commentService.findOneToDisplay(commentId);

		Assert.notNull(comment);

		if (page == null)
			page = 1;

		result = this.displayModelAndView(comment, page);

		return result;
	}
	
	//Ancillary methods -----------------------
	protected ModelAndView displayModelAndView(final Comment comment, final int page) {
		ModelAndView result;
		Page<Comment> comments;
		Boolean canComment;
		Map<Comment, Boolean> mapCommentBoolean;
		Boolean hasPlan, hasPlanComment;
		String defaultImage;
		
		canComment = true;
		
		comments = this.commentService.findByRepliedCommentId(comment.getId(), page, 5);
		
		hasPlanComment = false;
		if (this.planService.findByUserId(comment.getUser().getId()) != null)
			hasPlanComment = true;
		
		mapCommentBoolean = new HashMap<Comment, Boolean>();
		for (Comment c : comments) {
			hasPlan = false;
			if (this.planService.findByUserId(c.getUser().getId()) != null)
				hasPlan = true;
			mapCommentBoolean.put(c, hasPlan);
		}
		
		defaultImage = this.configurationService.findDefaultImage();

		result = new ModelAndView("comment/display");

		result.addObject("pageNumber", comments.getTotalPages());
		result.addObject("page", page);
		result.addObject("comment", comment);
		result.addObject("comments", comments.getContent());
		result.addObject("canComment", canComment);
		result.addObject("mapLinkBoolean", super.checkLinkImages(comment.getImages()));
		result.addObject("mapCommentBoolean", mapCommentBoolean);
		result.addObject("hasPlanComment", hasPlanComment);
		result.addObject("defaultImage", defaultImage);

		return result;

	}
}
