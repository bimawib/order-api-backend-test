package com.bimawib.orderapi.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="product")
public class Product {
	
	@Id
	private String id;
	
	@NotNull(message = "The name field cannot be null")
	@NotEmpty(message = "The name field cannot be empty")
	private String name;
}
