package com.CMS.Services;


import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.CMS.Model.Blog;
import com.CMS.Repo.BlogRepository;

@Service
public class BlogServices {
	
	@Autowired
	BlogRepository blogRepository;
	
	public Blog SaveBlog(Blog blog)
	{
		return blogRepository.save(blog);
	}
	
	public Optional<Blog> getBlogByID(Long blogId)
	{
		return blogRepository.findById(blogId);
	}
	
	public List<Blog> getAllBlog()
	{
		return blogRepository.findAll();
	}
	
	public void DeleteBlog(Long BlogID)
	{
		if(blogRepository.existsById(BlogID))
			blogRepository.deleteById(BlogID);
		else
			throw new RuntimeException("Blog not found with id: "+ BlogID);
	}
	
	public Blog updateBlog(Blog blog) {
        return blogRepository.save(blog);
    }
	
//	public List<Blog> getAllBlogByCreationDateAsc()
//	{
//		return blogRepository.findAllByOrderByCreatedAtAsc();
//	}
//	
//	public List<Blog> getAllBlogByCreationDateDesc()
//	{
//		return blogRepository.findAllByOrderByCreatedAtDesc();
//	}
	
	
	public Page<Blog> getAllBlogByCreationDateAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return blogRepository.findAll(pageable);
    }

    public Page<Blog> getAllBlogByCreationDateDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return blogRepository.findAll(pageable);
    }
    
    public long BlogCount()
    {
    	return blogRepository.count();
    }
    
    public List<Blog> getBlogsByUserId(Long userId) 
    {
        return blogRepository.findByUser_UserId(userId); 
    }
    
}
