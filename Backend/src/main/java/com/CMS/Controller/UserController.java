package com.CMS.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import com.CMS.Model.User;
import com.CMS.Services.UserServices;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping("/home")
    public String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return "Current date and time: " + now.toString();
    }

    @PostMapping("/createUser")
    public User createUser(@RequestBody User user) {
        return userServices.SaveUser(user);
    }

    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> checkUserExist(@RequestBody User requestUser){
    	
    	String username = requestUser.getUsername();
    	String password=requestUser.getPassword();
    	
    	boolean userExists = userServices.checkUserExist(username, password);
    	
    	Map<String, Object> result = new HashMap<>(); 
    	result.put("valid", userExists);
    	
    	if(userExists)
    	{
    		User user = userServices.getUsersByUsernameAndPassword(username, password);
    		result.put("user", user);
    	}
    	else
    	{
    		result.put("User", null);
    	}
    	
    	return ResponseEntity.ok(result);
    }
    
    
    @PutMapping("/update-user/{userID}")
    public ResponseEntity<User> updateUser(@PathVariable Long userID, @RequestBody User user) {
        User updatedUser = userServices.UpdateUser(userID, user);
        
        System.out.println("Phone No.: "+ updatedUser.getPhoneNo());
        System.out.println("Email: "+ updatedUser.getPhoneNo());
        System.out.println("Bio : "+ updatedUser.getPhoneNo());
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/delete-user/{userID}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userID) {
    	Map<String, Object> result = new HashMap<>(); 
        boolean deleted = userServices.deleteUser(userID);
        if (deleted) {

        	result.put("deleted", deleted);
        	
           return ResponseEntity.ok(result);
        } else {
        	result.put("deleted", false);
//        	return ResponseEntity.notFound().build();
        	return ResponseEntity.badRequest().body(result); // HTTP 400 Bad Request

        }
        
        
    }
    
    @PostMapping("/get-user-by-id")
    public ResponseEntity<Map<String, Object>> getUserById(@RequestBody Map<String, Long> request) {
        Long userID = request.get("userId");
        Optional<User> user = userServices.getUserById(userID);
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("user", user);
            return ResponseEntity.ok(result);
        } else {
            result.put("Error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
    
	@GetMapping("/user-count")
    public ResponseEntity<Map<String, Long>> getusercount() {
        long count = userServices.UserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/get-all-users")
	public ResponseEntity<Map<String, Object>> getAllUsersByCreationDate(
	        @RequestParam int q, @RequestParam int page, @RequestParam int size) {

	    Page<User> userPage;
	    if (q == 1) {
	    	userPage = userServices.getAllUserByCreationDateDesc(page, size);
	    } else {
	    	userPage = userServices.getAllUserByCreationDateDesc(page, size);
	    }

	    List<Map<String, Object>> users = userPage.getContent().stream()
	        .map(user -> {
	            Map<String, Object> userMap = new HashMap<>();
	            userMap.put("user_id", user.getUserId());
	            userMap.put("username", user.getUsername());
	            userMap.put("firstName", user.getFirstName());
	            userMap.put("lastName", user.getLastName());
	            userMap.put("bio", user.getBio());

	            return userMap;
	        })
	        .collect(Collectors.toList());

	    Map<String, Object> response = new HashMap<>();
	    response.put("users", users);
	    response.put("totalPages", userPage.getTotalPages());
	    response.put("totalElements", userPage.getTotalElements());
	    response.put("currentPage", userPage.getNumber());
	    response.put("pageSize", userPage.getSize());
	    
	    return ResponseEntity.ok(response);
	}
 
}
