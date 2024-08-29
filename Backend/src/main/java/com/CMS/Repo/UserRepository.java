package com.CMS.Repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CMS.Model.Blog;
import com.CMS.Model.User;

public interface UserRepository extends JpaRepository<User, Long>
{
	 Optional<User> findByUsernameAndPassword(String username, String password);

	Optional<User> findByUsername(String username);
	 
}
