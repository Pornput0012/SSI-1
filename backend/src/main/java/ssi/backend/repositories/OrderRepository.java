package ssi.backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ssi.backend.entities.Order;
import ssi.backend.entities.User;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.buyer = :buyerId")
    Page<Order> findAllBuyerOrder(User buyerId, Pageable pageable);

    @Query("""
                SELECT DISTINCT o FROM Order o
                JOIN o.orderItems oi
                JOIN oi.product p
                WHERE p.seller.id = :sellerId
            """)
    Page<Order> findAllSellerOrder(Integer sellerId, Pageable pageable);

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderItems oi
            LEFT JOIN oi.product p
            LEFT JOIN p.brand b
            WHERE o.buyer = :buyerId
            AND (:brandNames IS NULL OR b.name IN :brandNames)
            AND (:searchKeyWord IS NULL OR 
                 LOWER(oi.model) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) OR
                 LOWER(o.seller.nickname) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')))
            """)
    Page<Order> findFilteredBuyerOrders(
            User buyerId,
            List<String> brandNames,
            String searchKeyWord,
            Pageable pageable
    );
}
