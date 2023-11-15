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

import com.bimawib.orderapi.model.Product;
import com.bimawib.orderapi.repository.OrderRepository;
import com.bimawib.orderapi.repository.ProductRepository;
import com.bimawib.orderapi.response.ApiResponse;

@RestController
public class ProductController {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@GetMapping("/product")
	public ResponseEntity<?> index(){
		List<Product> products = productRepository.findAll();
		
		if(products.size() > 0) {
			ApiResponse<List<Product>> successResponse = new ApiResponse<>("Products found", HttpStatus.OK.value(), products);
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
		} else {
			ApiResponse<String> notFoundResponse = new ApiResponse<>("Product's data not found!", HttpStatus.NOT_FOUND.value(), null);
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@PostMapping("/product")
	public ResponseEntity<?> create(@RequestBody Product product){
		try {
			String uuid = UUID.randomUUID().toString();
			product.setId(uuid);
			
			productRepository.save(product);
			
			String successMessage = "Successfully create product data";
			ApiResponse<Product> response = new ApiResponse<>(successMessage, HttpStatus.OK.value(), product);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			String errorMessage = "Failed to create product data: " + e.getMessage();
	        ApiResponse<String> errorResponse = new ApiResponse<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	        
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/product/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<Product> productResult = productRepository.findById(id);
		
		if(productResult.isPresent()) {
			ApiResponse<Product> successResponse = new ApiResponse<>("Product found", HttpStatus.OK.value(), productResult.get());
			return new ResponseEntity<>(successResponse, HttpStatus.OK);
		} else {
			ApiResponse<String> notFoundResponse = new ApiResponse<>("Product with id: " + id + " not found!", HttpStatus.NOT_FOUND.value(), null);
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@PutMapping("/product/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Product product){
		Optional<Product> productResult = productRepository.findById(id);
		
		if(productResult.isPresent()) {
			Product productToUpdate = productResult.get();
			productToUpdate.setName(product.getName() != null ? product.getName() : productToUpdate.getName());
			
			productRepository.save(productToUpdate);
			
			ApiResponse<Product> successResponse = new ApiResponse<>("Product updated successfully", HttpStatus.OK.value(), productToUpdate);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
		} else {
			ApiResponse<String> notFoundResponse = new ApiResponse<>("Product with id: " + id + " not found!", HttpStatus.NOT_FOUND.value(), null);
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/product/{id}")
	public ResponseEntity<ApiResponse<String>> destroy(@PathVariable("id") String id) {
	    Optional<Product> productResult = productRepository.findById(id);

	    if (productResult.isPresent()) {
	        try {
	            // Because no cascade in MongoDB
	            orderRepository.deleteByProductId(id);
	            productRepository.deleteById(id);

	            ApiResponse<String> successResponse = new ApiResponse<>("Product deleted successfully", HttpStatus.OK.value(), "Product " + id + " has been deleted!");
	            return new ResponseEntity<>(successResponse, HttpStatus.OK);
	        } catch (Exception e) {
	            ApiResponse<String> errorResponse = new ApiResponse<>("Error deleting product", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("Product not found", HttpStatus.NOT_FOUND.value(), "Product with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}

	
}
