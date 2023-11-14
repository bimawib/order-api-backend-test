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

import com.bimawib.orderapi.model.Order;
import com.bimawib.orderapi.model.Product;
import com.bimawib.orderapi.model.User;
import com.bimawib.orderapi.repository.OrderRepository;
import com.bimawib.orderapi.repository.ProductRepository;
import com.bimawib.orderapi.repository.UserRepository;

@RestController
public class OrderController {
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/order")
	public ResponseEntity<?> index(){
		List<Order> orders = orderRepository.findAll();
		
		if(orders.size() > 0) {
			return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Order's data not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/order")
	public ResponseEntity<?> create(@RequestBody Order order){
		try {
			String uuid = UUID.randomUUID().toString();
			order.setId(uuid);
			
			String productId = order.getProductId();
			String userId = order.getUserId();
			
			Optional<Product> productCheck = productRepository.findById(productId);
			if(!productCheck.isPresent()) {
				return new ResponseEntity<>("The product id doesn't exist", HttpStatus.CONFLICT);
			}
			
			Optional<User> userCheck = userRepository.findById(userId);
			if(!userCheck.isPresent()) {
				return new ResponseEntity<>("The user id doesn't exist", HttpStatus.CONFLICT);
			}
			
			orderRepository.save(order);
			
			return new ResponseEntity<Order>(order, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/order/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<Order> orderResult = orderRepository.findById(id);
		
		if(orderResult.isPresent()) {
			return new ResponseEntity<>(orderResult.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Order with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/order/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Order order){
		Optional<Order> orderResult = orderRepository.findById(id);
		
		if(orderResult.isPresent()) {
			Order orderToUpdate = orderResult.get();
			
			String productId = orderToUpdate.getProductId();
			String userId = orderToUpdate.getUserId();
			
			Optional<Product> productCheck = productRepository.findById(productId);
			if(!productCheck.isPresent()) {
				return new ResponseEntity<>("The product id doesn't exist", HttpStatus.CONFLICT);
			}
			
			Optional<User> userCheck = userRepository.findById(userId);
			if(!userCheck.isPresent()) {
				return new ResponseEntity<>("The user id doesn't exist", HttpStatus.CONFLICT);
			}
			
			orderToUpdate.setProductId(order.getProductId() != null ? order.getProductId() : orderToUpdate.getProductId());
			orderToUpdate.setUserId(order.getUserId() != null ? order.getUserId() : orderToUpdate.getUserId());
			orderToUpdate.setAmount(order.getAmount() != null ? order.getAmount() : orderToUpdate.getAmount());
			orderToUpdate.setStatus(order.getStatus() != null ? order.getStatus() : orderToUpdate.getStatus());
			
			orderRepository.save(orderToUpdate);
			
			return new ResponseEntity<>(orderToUpdate, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Order with id: " + id + " not found!", HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/order/{id}")
	public ResponseEntity<?> destroy(@PathVariable("id") String id){
		try {
			orderRepository.deleteById(id);
			
			return new ResponseEntity<>("Order  " + id + " has been deleted!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
