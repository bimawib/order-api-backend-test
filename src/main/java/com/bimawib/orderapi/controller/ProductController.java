package com.bimawib.orderapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bimawib.orderapi.model.Product;
import com.bimawib.orderapi.repository.ProductRepository;

@RestController
public class ProductController {
	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping("/product")
	public ResponseEntity<?> index(){
		List<Product> products = productRepository.findAll();
		
		if(products.size() > 0) {
			return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Product's data not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/product")
	public ResponseEntity<?> create(@RequestBody Product product){
		try {
			String uuid = UUID.randomUUID().toString();
			product.setId(uuid);
			
			productRepository.save(product);
			
			return new ResponseEntity<Product>(product, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/product/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<Product> productResult = productRepository.findById(id);
		if(productResult.isPresent()) {
			return new ResponseEntity<>(productResult.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Product with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/product/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Product product){
		Optional<Product> productResult = productRepository.findById(id);
		if(productResult.isPresent()) {
			Product productToUpdate = productResult.get();
			productToUpdate.setCompleted(product.getCompleted() != null ? product.getCompleted());
		} else {
			return new ResponseEntity<>("Product with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
}
