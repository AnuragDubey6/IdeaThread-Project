package com.CMS.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CMS.Model.Blog;
import com.CMS.Model.Comment;
import com.CMS.Model.CommentRequest;
import com.CMS.Model.User;
import com.CMS.Repo.BlogRepository;
import com.CMS.Repo.CommentRepository;
import com.CMS.Repo.UserRepository;
import com.CMS.Services.CommentServices;
import com.CMS.Services.UserServices;

@RestController
@RequestMapping("/")
public class CommentController {
	
	@Autowired
	private CommentServices comServices;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BlogRepository blogRepository;
	
	@PostMapping("/{createComment}")
	public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
	    if (commentRequest.getContent() == null || commentRequest.getBlogId() == null || commentRequest.getUser() == null) {
	        return ResponseEntity.badRequest().body("Content, blogId, and author must be provided");
	    }

	    User author = userRepository.findById(commentRequest.getUser().getUserId())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Blog blog = blogRepository.findById(commentRequest.getBlogId())
	            .orElseThrow(() -> new RuntimeException("Blog not found"));

	    Comment comment = new Comment();
	    comment.setContent(commentRequest.getContent());
	    comment.setBlog(blog);
	    comment.setUser(author);

	    Comment savedComment = comServices.createComment(comment);
	    return ResponseEntity.ok(savedComment);
	}

	
	@GetMapping("/{getComment}/{ComID}")
	public Optional<Comment> getByID(@PathVariable Long ComID)
	{
		return comServices.findById(ComID);
	}
	
	@GetMapping("/getallComments")
	public List<Comment> getAllComment()
	{
		return comServices.getAllComments();
	}
	
	@DeleteMapping("/{deleteComment}/{ComID}")
	public void deleteCom(@PathVariable Long ComID)
	{
		comServices.deleteComment(ComID);
	}
	
	@PutMapping("/{updateComment}/{ComID}")
	public ResponseEntity<?> updateComment(@PathVariable Long ComID, @RequestBody Comment commentRequest) {

	    Comment existingComment = comServices.findById(ComID)
	            .orElseThrow(() -> new RuntimeException("Comment not found"));


	    existingComment.setContent(commentRequest.getContent());


	    if (commentRequest.getBlog() != null) {
	        Blog blog = blogRepository.findById(commentRequest.getBlog().getBlogID())
	                .orElseThrow(() -> new RuntimeException("Blog not found"));
	        existingComment.setBlog(blog);
	    }
	    
	    if (commentRequest.getUser() != null) {
	        User user = userRepository.findById(commentRequest.getUser().getUserId())
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        existingComment.setUser(user);
	    }


	    Comment updatedComment = comServices.createComment(existingComment);

	    return ResponseEntity.ok(updatedComment);
	}


}
