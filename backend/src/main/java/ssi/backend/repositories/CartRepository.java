package ssi.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Cart;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findAllByUserId(int userId);

    @NativeQuery("select * from carts c where c.product_Id = :productId and user_Id = :userId")
    Cart findAllByProductIdAndUserId(int productId,int userId);
}
