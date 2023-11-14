package com.bimawib.orderapi.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="user")
public class User {
	
	@Id
	private String id;
	
	@NotNull(message = "The username field cannot be null")
	@NotEmpty(message = "The username field cannot be empty")
	private String username;
	
	@NotNull(message = "The first name field cannot be null")
	@NotEmpty(message = "The first name field cannot be empty")
	private String firstname;
	
	@NotNull(message = "The last name field cannot be null")
	private String lastname;
	
	@NotNull(message = "The email field cannot be null")
	@NotEmpty(message = "The email field cannot be empty")
	@Email(message = "Please provide a valid email address")
	private String email;
	
	@NotNull(message = "The role field cannot be null")
	@NotEmpty(message = "The role field cannot be empty")
	@Pattern(regexp = "SELLER|CUSTOMER", message = "Role must be either 'SELLER' or 'CUSTOMER'")
	private String role;
}
