package com.CMS.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CMS.Model.Blog;
import com.CMS.Model.BlogRequest;
import com.CMS.Model.User;
import com.CMS.Repo.BlogRepository;
import com.CMS.Repo.UserRepository;
import com.CMS.Services.BlogServices;

@RestController
@RequestMapping("/blogs")
public class BlogController {
	
	@Autowired
	private BlogServices blogServices;
	
	@Autowired
	private BlogRepository blogRepository;
	
    @Autowired
    private UserRepository userRepository;

	
	@PostMapping("/{create-blog}")
	public ResponseEntity<?> createBlog(@RequestBody BlogRequest blogRequest) {

	    if (blogRequest.getUsername() == null) {
	        return ResponseEntity.badRequest().body("Username is required");
	    }
	    

	    User user = userRepository.findByUsername(blogRequest.getUsername())
	    		.orElseThrow();

	    Blog blog = new Blog();
	    blog.setTitle(blogRequest.getTitle());
	    blog.setContent(blogRequest.getContent());
	    blog.setUser(user);

	    Blog savedBlog = blogServices.SaveBlog(blog);

	    Map<String, Object> blogMap = new HashMap<>();
	    blogMap.put("id", savedBlog.getBlogID());
	    blogMap.put("title", savedBlog.getTitle());

	    String contentSnippet;
	    try {
	        if (savedBlog.getContent() != null && savedBlog.getContent().length() > 100) {
	            contentSnippet = savedBlog.getContent().substring(0, 100);
	        } else {
	            contentSnippet = savedBlog.getContent();
	        }
	    } catch (Exception e) {
	        contentSnippet = "Error processing content snippet";
	    }
	    blogMap.put("content", contentSnippet);
	    blogMap.put("created_at", savedBlog.getCreatedAt());
	    blogMap.put("updated_at", savedBlog.getUpdatedAt());

	    Map<String, Object> userMap = new HashMap<>();
	    userMap.put("firstName", user.getFirstName());
	    userMap.put("lastName", user.getLastName());
	    userMap.put("email", user.getEmail());
	    userMap.put("username", user.getUsername());

	    blogMap.put("user", userMap);

	    return ResponseEntity.ok(blogMap);
	}

	
//	@GetMapping("/{getBlog}/{BlogID}")
//	public Optional<Blog> getBlogById(@PathVariable Long BlogID)
//	{
//		return blogServices.getBlogByID(BlogID);
//	}
	
	@GetMapping("/get-blog/{BlogID}")
    public ResponseEntity<Map<String, Object>> getBlogById(@PathVariable Long BlogID) {
        Optional<Blog> blogOptional = blogRepository.findById(BlogID);
        
        if (!blogOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Blog blog = blogOptional.get();
        User user = blog.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("blogID", blog.getBlogID());
        response.put("title", blog.getTitle());
        
        String contentSnippet;
        try {
        	 contentSnippet = blog.getContent();
        } catch (Exception e) {
            contentSnippet = "Error processing content snippet";
        }
        response.put("content", contentSnippet);
        response.put("createdAt", blog.getCreatedAt());
        response.put("updatedAt", blog.getUpdatedAt());


        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", user.getUserId());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());

        response.put("user", userMap);

        return ResponseEntity.ok(response);
    }
	
	@DeleteMapping("/delete-blog/{BlogID}")
	public String DeleteBlog(@PathVariable Long BlogID)
	{
		blogServices.DeleteBlog(BlogID);
		return "Blog Deleted Successfully";
	}
	
	@GetMapping
	public List<Blog> getAllBlog()
	{
		return blogServices.getAllBlog();
	}
	
	@PutMapping("/{update-blog}/{BlogID}")
	public ResponseEntity<Blog> updateBlog(@PathVariable Long BlogID, @RequestBody Blog blog) 
	{
	    Blog existingBlog = blogServices.getBlogByID(BlogID)
	            .orElseThrow(() -> new RuntimeException("Blog not found"));
	    

	    existingBlog.setTitle(blog.getTitle());
	    existingBlog.setContent(blog.getContent());
	    existingBlog.setUser(blog.getUser() != null ? blog.getUser() : existingBlog.getUser());
	    existingBlog.setCategory(blog.getCategory() != null ? blog.getCategory() : existingBlog.getCategory());

	    Blog updatedBlog = blogServices.updateBlog(existingBlog);
	    return ResponseEntity.ok(updatedBlog);

    }
	
//	@GetMapping("/{BlogsSortedOnCreationDateAsc}")
//	public List<Blog> getAllBlogByCreationDateAsc()
//	{
//		return blogServices.getAllBlogByCreationDateAsc();
//	}
	
	

	@GetMapping("/blog-data-sorted")
	public ResponseEntity<Map<String, Object>> getAllBlogByCreationDate(
	        @RequestParam int q, @RequestParam int page, @RequestParam int size) {

	    Page<Blog> blogPage;
	    if (q == 0) {
	        blogPage = blogServices.getAllBlogByCreationDateAsc(page, size);
	    } else {
	        blogPage = blogServices.getAllBlogByCreationDateDesc(page, size);
	    }

	    List<Map<String, Object>> blogs = blogPage.getContent().stream()
	        .map(blog -> {
	            Map<String, Object> blogMap = new HashMap<>();
	            blogMap.put("blogID", blog.getBlogID());
	            blogMap.put("title", blog.getTitle());
	            
	            
	            String contentSnippet;
                try {
                    if (blog.getContent() != null && (blog.getContent().length() > 200 ) ) {
                        contentSnippet = blog.getContent().substring(0, 200);
                    } else {
                        contentSnippet = blog.getContent(); 
                    }
                } catch (Exception e) {
                    contentSnippet = "Error processing content snippet";
                }
	            
	            
	            blogMap.put("createdAt", blog.getCreatedAt());
	            blogMap.put("updatedAt", blog.getUpdatedAt());
	            blogMap.put("content", contentSnippet);
	            // Extract only the required fields from the user
	            User user = blog.getUser();
	            Map<String, Object> userMap = new HashMap<>();
	            userMap.put("userID", user.getUserId());
	            userMap.put("firstName", user.getFirstName());
	            userMap.put("lastName", user.getLastName());
	            userMap.put("email", user.getEmail());

	            blogMap.put("user", userMap);
	            return blogMap;
	        })
	        .collect(Collectors.toList());

	    // Prepare the response
	    Map<String, Object> response = new HashMap<>();
	    response.put("blogs", blogs);
	    response.put("totalPages", blogPage.getTotalPages());
	    response.put("totalElements", blogPage.getTotalElements());
	    response.put("currentPage", blogPage.getNumber());
	    response.put("pageSize", blogPage.getSize());

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/blog-count")
    public ResponseEntity<Map<String, Long>> getBlogCount() {
        long count = blogServices.BlogCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/blogs-by-userid")
    public ResponseEntity<Map<String, Object>> getBlogs(@RequestParam(required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();

        List<Blog> blogs;
        if (userId != null) {
            blogs = blogServices.getBlogsByUserId(userId);
        } else {

            blogs = blogServices.getAllBlog();
        }

        List<Map<String, Object>> blogData = blogs.stream()
                .map(blog -> {
                    Map<String, Object> blogMap = new HashMap<>();
                    blogMap.put("id", blog.getBlogID());
                    blogMap.put("title", blog.getTitle());
                    
                    String contentSnippet;
                    try {
                        if (blog.getContent() != null && blog.getContent().length() > 100) {
                            contentSnippet = blog.getContent().substring(0, 100);
                        } else {
                            contentSnippet = blog.getContent(); 
                        }
                    } catch (Exception e) {

                        contentSnippet = "Error processing content snippet";

                    }
                    blogMap.put("content", contentSnippet);
                    blogMap.put("created_At", blog.getCreatedAt());
                    blogMap.put("updated_at", blog.getUpdatedAt());
                    
                    return blogMap;
                })
                .collect(Collectors.toList());

        response.put("success", true);
        response.put("blogs", blogData);
        response.put("message", "Blogs fetched successfully for user");

        return ResponseEntity.ok(response);
    }
	
}
