package com.bimawib.orderapi.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
			return new ResponseEntity<>("Product's Data Not Found", HttpStatus.NOT_FOUND);
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
	
}
