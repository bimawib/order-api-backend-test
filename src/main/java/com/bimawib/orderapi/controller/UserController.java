package com.bimawib.orderapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;
	
	@GetMapping("/user")
	public ResponseEntity<?> index(){
		List<User> user = userRepository.findAll();
		
		if(user.size() > 0) {
			return new ResponseEntity<List<User>>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("User data not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/user")
	public ResponseEntity<?> create(@RequestBody User user){
		try {
			String emailInput = user.getEmail();
			String usernameInput = user.getUsername();
			
			Optional<User> emailCheck = userRepository.findByEmail(emailInput);
			if(emailCheck.isPresent()) {
				return new ResponseEntity<>("The email is already taken", HttpStatus.CONFLICT);
			}
			
			Optional<User> usernameCheck = userRepository.findByUsername(usernameInput);
			if(usernameCheck.isPresent()) {
				return new ResponseEntity<>("The username is already taken", HttpStatus.CONFLICT);
			}
			
			String uuid = UUID.randomUUID().toString();
			user.setId(uuid);
			
			userRepository.save(user);
			
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<User> userResult = userRepository.findById(id);
		
		if(userResult.isPresent()) {
			return new ResponseEntity<>(userResult.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("User with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/user/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody User user){
		Optional<User> userResult = userRepository.findById(id);
		
		if(userResult.isPresent()) {
			User userToUpdate = userResult.get();
			
			String newUsername = user.getUsername();
			String oldUsername = userToUpdate.getUsername();
			
			String newEmail = user.getEmail();
			String oldEmail = userToUpdate.getEmail();
			
			if (!newUsername.equalsIgnoreCase(oldUsername)) {
				Optional<User> usernameCheckResult = userRepository.findByUsername(newUsername);
				
				if(usernameCheckResult.isPresent()) {
					return new ResponseEntity<>("The username is already taken", HttpStatus.CONFLICT);
				}
			} else if (!newEmail.equalsIgnoreCase(oldEmail)) {
				Optional<User> emailCheckResult = userRepository.findByEmail(newEmail);
				
				if(emailCheckResult.isPresent()) {
					return new ResponseEntity<>("The email is already taken", HttpStatus.CONFLICT);
				}
			}
			
			userToUpdate.setUsername(user.getUsername() != null ? user.getUsername() : userToUpdate.getUsername());
			userToUpdate.setFirstname(user.getFirstname() != null ? user.getFirstname() : userToUpdate.getFirstname());
			userToUpdate.setLastname(user.getLastname() != null ? user.getLastname() : userToUpdate.getLastname());
			userToUpdate.setEmail(user.getEmail() != null ? user.getEmail() : userToUpdate.getEmail());
			userToUpdate.setRole(user.getRole() != null ? user.getRole() : userToUpdate.getRole());
			
			userRepository.save(userToUpdate);
			
			return new ResponseEntity<>(userToUpdate, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("User with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> destroy(@PathVariable("id") String id){
		try {
			// Because no cascade in mongoDB
			orderRepository.deleteByProductId(id);
			userRepository.deleteById(id);
			
			return new ResponseEntity<>("User " + id + " has been deleted!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
