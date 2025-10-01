package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ssi.backend.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}