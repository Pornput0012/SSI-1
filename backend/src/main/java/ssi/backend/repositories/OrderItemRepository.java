package ssi.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ssi.backend.entities.Order;
import ssi.backend.entities.OrderItem;
import ssi.backend.entities.Product;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
}