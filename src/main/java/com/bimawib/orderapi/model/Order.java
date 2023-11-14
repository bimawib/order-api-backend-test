package com.bimawib.orderapi.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Document(collection="order")
public class Order {
	@Id
	private String id;
	
	@NotNull(message = "The user id field cannot be null")
	@NotEmpty(message = "The user id field cannot be empty")
	private String userId;
	
	@NotNull(message = "The product id field cannot be null")
	@NotEmpty(message = "The product id field cannot be empty")
	private String productId;

	@Min(value = 0, message = "The amount must be at least 0")
    @Max(value = 9999, message = "The amount must be at most 9999")
	private Integer amount;
	
	@NotNull(message = "The status field cannot be null")
	@NotEmpty(message = "The status field cannot be empty")
	@Pattern(regexp = "PROCESSED|SHIPPED|DELIVERED", message = "Role must be either 'PROCESSED', 'SHIPPED' or 'DELIVERED'")
	private String status;
}
