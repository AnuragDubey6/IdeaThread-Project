package com.CMS.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.CMS.Model.Blog;
import com.CMS.Model.User;
import com.CMS.Repo.UserRepository;

@Service
public class UserServices {
	
	@Autowired
	private UserRepository userRepository;
	
	public User SaveUser(User user)
	{
		return userRepository.save(user);
	}
	
	public Optional<User> getUserById(Long userId) {
	    if (userId == null) {
	        return Optional.empty();
	    }
	    return userRepository.findById(userId);
	}

	
	public boolean deleteUser(Long userID) {
	    if (userRepository.existsById(userID)) {
	        userRepository.deleteById(userID);
	        return true;
	    } else {
	        return false;
	    }
	}
	
	public User UpdateUser (Long UserID, User UpdatedUser)
	{
		Optional<User> ExistingUser = userRepository.findById(UserID);
		System.out.println("Updated User Phone No: "+ UpdatedUser.getPhoneNo());
		System.out.println("Updated User Email: "+ UpdatedUser.getEmail());
		System.out.println("Updated User bio: "+ UpdatedUser.getBio());
		
		if(ExistingUser.isPresent())
		{
			User user = ExistingUser.get();
			user.setPhoneNo(UpdatedUser.getPhoneNo());
			user.setEmail(UpdatedUser.getEmail());
			user.setBio(UpdatedUser.getBio());
			
			System.out.println("Updated User Phone No: "+ UpdatedUser.getPhoneNo());
			System.out.println("Updated User Email: "+ UpdatedUser.getEmail());
			System.out.println("Updated User bio: "+ UpdatedUser.getBio());
			return userRepository.save(user);
		}
		else
		{
			throw new RuntimeException("User Not Found with id: "+ UserID);
		}
	}

	public User getUsersByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElse(null);
    }
	
	public boolean checkUserExist(String username, String password)
	{
		return userRepository.findByUsernameAndPassword(username, password).isPresent();
	}
	
    public long UserCount()
    {
    	return userRepository.count();
    }
	
    public Page<User> getAllUserByCreationDateDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }
}
