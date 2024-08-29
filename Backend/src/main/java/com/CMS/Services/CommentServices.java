package com.CMS.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CMS.Model.Comment;
import com.CMS.Repo.CommentRepository;

@Service
public class CommentServices {
	
	@Autowired
	public CommentRepository commentRepository;
	
	public Comment createComment(Comment comment)
	{
		return commentRepository.save(comment);
	}
	
	public void deleteComment (Long ComID)
	{
		commentRepository.deleteById(ComID);
	}
	
	public Optional<Comment> findById(Long ComID)
	{
		return commentRepository.findById(ComID);
	}
	
	public List<Comment> getAllComments()
	{
		return commentRepository.findAll();
	}
	
	public Comment updateComment(Long ComID, Comment Updatecomment)
	{
		Optional<Comment> ExistingComment = commentRepository.findById(ComID);
		if(ExistingComment.isPresent())
		{
			Comment comment = ExistingComment.get();
			comment.setComID(Updatecomment.getComID());
			comment.setContent(Updatecomment.getContent());
			comment.setUpdatedAt(Updatecomment.getUpdatedAt());
			
			return commentRepository.save(comment);
		}
		else
		{
			throw new RuntimeException("Comment Not Found with id: "+ ComID);
		}
	}
	

}
