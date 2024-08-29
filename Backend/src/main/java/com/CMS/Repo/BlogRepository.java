package com.CMS.Repo;


import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CMS.Model.Blog;


public interface BlogRepository extends JpaRepository<Blog, Long>{

	 List<Blog> findAllByOrderByCreatedAtAsc();
	 
	 List<Blog> findAllByOrderByCreatedAtDesc();
	 
	 Page<Blog> findAll(Pageable pageable);
	 
	 List<Blog> findByUser_UserId(Long userId);
	 
}
