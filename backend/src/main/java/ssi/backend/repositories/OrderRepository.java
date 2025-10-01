package ssi.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi.backend.entities.Order;
import ssi.backend.entities.User;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    // หา orders ตาม buyer ID
    List<Order> findAllByBuyerId(Integer buyerId);

    // หา orders ตาม seller ID
    List<Order> findAllBySellerId(Integer sellerId);

    // หา orders ทั้งหมดของ buyer
    List<Order> findAllByBuyer(User buyer);

    // หา orders ทั้งหมดของ seller
    List<Order> findAllBySeller(User seller);

    // หา orders ตาม status
    List<Order> findAllByStatusOrder(String status);
}
