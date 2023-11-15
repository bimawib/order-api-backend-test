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

import com.bimawib.orderapi.model.Order;
import com.bimawib.orderapi.model.Product;
import com.bimawib.orderapi.model.User;
import com.bimawib.orderapi.repository.OrderRepository;
import com.bimawib.orderapi.repository.ProductRepository;
import com.bimawib.orderapi.repository.UserRepository;
import com.bimawib.orderapi.response.ApiResponse;

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

	    if (!orders.isEmpty()) {
	        ApiResponse<List<Order>> successResponse = new ApiResponse<>("Orders found", HttpStatus.OK.value(), orders);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("Order's data not found!", HttpStatus.NOT_FOUND.value(), null);
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
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
	        if (!productCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The product id doesn't exist", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        Optional<User> userCheck = userRepository.findById(userId);
	        if (!userCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The user id doesn't exist", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        orderRepository.save(order);

	        ApiResponse<Order> successResponse = new ApiResponse<>("Order created successfully", HttpStatus.OK.value(), order);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } catch (Exception e) {
	        ApiResponse<String> errorResponse = new ApiResponse<>("Error creating order", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/order/{id}")
	public ResponseEntity<?> show(@PathVariable("id") String id){
		Optional<Order> orderResult = orderRepository.findById(id);

	    if (orderResult.isPresent()) {
	        ApiResponse<Order> successResponse = new ApiResponse<>("Order found", HttpStatus.OK.value(), orderResult.get());
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("Order not found", HttpStatus.NOT_FOUND.value(), "Order with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@PutMapping("/order/{id}")
	public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Order order){
		Optional<Order> orderResult = orderRepository.findById(id);

	    if (orderResult.isPresent()) {
	        Order orderToUpdate = orderResult.get();

	        String productId = orderToUpdate.getProductId();
	        String userId = orderToUpdate.getUserId();

	        Optional<Product> productCheck = productRepository.findById(productId);
	        if (!productCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The product id doesn't exist", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        Optional<User> userCheck = userRepository.findById(userId);
	        if (!userCheck.isPresent()) {
	            ApiResponse<String> conflictResponse = new ApiResponse<>("The user id doesn't exist", HttpStatus.CONFLICT.value(), null);
	            return new ResponseEntity<>(conflictResponse, HttpStatus.CONFLICT);
	        }

	        orderToUpdate.setProductId(order.getProductId() != null ? order.getProductId() : orderToUpdate.getProductId());
	        orderToUpdate.setUserId(order.getUserId() != null ? order.getUserId() : orderToUpdate.getUserId());
	        orderToUpdate.setAmount(order.getAmount() != null ? order.getAmount() : orderToUpdate.getAmount());
	        orderToUpdate.setStatus(order.getStatus() != null ? order.getStatus() : orderToUpdate.getStatus());

	        orderRepository.save(orderToUpdate);

	        ApiResponse<Order> successResponse = new ApiResponse<>("Order updated successfully", HttpStatus.OK.value(), orderToUpdate);
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } else {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("Order not found", HttpStatus.NOT_FOUND.value(), "Order with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/order/{id}")
	public ResponseEntity<?> destroy(@PathVariable("id") String id){
		try {
	        orderRepository.deleteById(id);

	        ApiResponse<String> successResponse = new ApiResponse<>("Order deleted successfully", HttpStatus.OK.value(), "Order " + id + " has been deleted!");
	        return new ResponseEntity<>(successResponse, HttpStatus.OK);
	    } catch (EmptyResultDataAccessException e) {
	        ApiResponse<String> notFoundResponse = new ApiResponse<>("Order not found", HttpStatus.NOT_FOUND.value(), "Order with id: " + id + " not found!");
	        return new ResponseEntity<>(notFoundResponse, HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        ApiResponse<String> errorResponse = new ApiResponse<>("Error deleting order", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
