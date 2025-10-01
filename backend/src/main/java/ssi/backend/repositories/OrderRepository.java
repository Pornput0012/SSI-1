package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ssi.backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
