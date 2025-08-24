package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Seller;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    List<Seller> findByEmail(String email);
}
