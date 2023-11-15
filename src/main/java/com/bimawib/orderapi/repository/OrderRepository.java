package com.bimawib.orderapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bimawib.orderapi.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
	Optional<Order> deleteByProductId(String productId);
	Optional<Order> deleteByUserId(String userId);
}
