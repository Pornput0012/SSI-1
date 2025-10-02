package ssi.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ssi.backend.entities.Order;
import ssi.backend.entities.OrderItem;
import ssi.backend.entities.Product;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // // คำนวณยอดรวมของ order
    // @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.id = :orderId")
    // Integer calculateTotalAmountByOrderId(Integer orderId);

    // // หา order items ตาม order ID
    // List<OrderItem> findAllByOrderId(Integer orderId);

    // // หา order items ทั้งหมดของ order นั้นๆ
    // List<OrderItem> findAllByOrder(Order order);

    // // หา order items ตาม product
    // List<OrderItem> findAllByProduct(Product product);
}