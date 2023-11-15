package com.bimawib.orderapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bimawib.orderapi.model.User;
import com.bimawib.orderapi.repository.OrderRepository;
import com.bimawib.orderapi.repository.UserRepository;
import com.bimawib.orderapi.response.ApiResponse;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;
	
	@GetMapping("/user")
	public ResponseEntity<?> index(){
		List<User> users = userRepository.findAll();
		
		if (!users.isEmpty()) {
	        ApiResponse<List<User>> successResponse = new ApiResponse<>("Users found", HttpStatus.OK.value(), users);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("User data not found!", HttpStatus.NOT_FOUND.value(), null);
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@PostMapping("/user")
	public ResponseEntity<?> create(@RequestBody User user){
		try {
	        String emailInput = user.getEmail();
	        String usernameInput = user.getUsername();

	        Optional<User> emailCheck = userRepository.findByEmail(emailInput);
	        if (emailCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The email is already taken", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        Optional<User> usernameCheck = userRepository.findByUsername(usernameInput);
	        if (usernameCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The username is already taken", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        String uuid = UUID.randomUUID().toString();
	        user.setId(uuid);

	        userRepository.save(user);

	        ApiResponse<User> successResponse = new ApiResponse<>("User created successfully", HttpStatus.OK.value(), user);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } catch (Exception e) {
	        ApiResponse<String> errorResponse = new ApiResponse<>("Error creating user", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<User> userResult = userRepository.findById(id);

	    if (userResult.isPresent()) {
	        ApiResponse<User> successResponse = new ApiResponse<>("User found", HttpStatus.OK.value(), userResult.get());
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("User not found", HttpStatus.NOT_FOUND.value(), "User with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@PutMapping("/user/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody User user){
		Optional<User> userResult = userRepository.findById(id);

	    if (userResult.isPresent()) {
	        User userToUpdate = userResult.get();

	        String newUsername = user.getUsername();
	        String oldUsername = userToUpdate.getUsername();

	        String newEmail = user.getEmail();
	        String oldEmail = userToUpdate.getEmail();

	        if (!newUsername.equalsIgnoreCase(oldUsername)) {
	            Optional<User> usernameCheckResult = userRepository.findByUsername(newUsername);

	            if (usernameCheckResult.isPresent()) {
	                ApiResponse<String> conflictResponse = new ApiResponse<>("The username is already taken", HttpStatus.CONFLICT.value(), null);
	                return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	            }
	        } else if (!newEmail.equalsIgnoreCase(oldEmail)) {
	            Optional<User> emailCheckResult = userRepository.findByEmail(newEmail);

	            if (emailCheckResult.isPresent()) {
	                ApiResponse<String> conflictResponse = new ApiResponse<>("The email is already taken", HttpStatus.CONFLICT.value(), null);
	                return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	            }
	        }

	        userToUpdate.setUsername(user.getUsername() != null ? user.getUsername() : userToUpdate.getUsername());
	        userToUpdate.setFirstname(user.getFirstname() != null ? user.getFirstname() : userToUpdate.getFirstname());
	        userToUpdate.setLastname(user.getLastname() != null ? user.getLastname() : userToUpdate.getLastname());
	        userToUpdate.setEmail(user.getEmail() != null ? user.getEmail() : userToUpdate.getEmail());
	        userToUpdate.setRole(user.getRole() != null ? user.getRole() : userToUpdate.getRole());

	        userRepository.save(userToUpdate);

	        ApiResponse<User> successResponse = new ApiResponse<>("User updated successfully", HttpStatus.OK.value(), userToUpdate);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("User not found", HttpStatus.NOT_FOUND.value(), "User with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> destroy(@PathVariable("id") String id){
		try {
	        // Because no cascade in MongoDB
	        orderRepository.deleteByUserId(id);
	        userRepository.deleteById(id);

	        ApiResponse<String> successResponse = new ApiResponse<>("User deleted successfully", HttpStatus.OK.value(), "User " + id + " has been deleted!");
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } catch (EmptyResultDataAccessException e) {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("User not found", HttpStatus.NOT_FOUND.value(), "User with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        ApiResponse<String> errorResponse = new ApiResponse<>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
