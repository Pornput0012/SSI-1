package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Seller;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Seller findByEmail(String email);

    @Query("SELECT s.email FROM Seller s")
    List<String> findAllEmailsSeller();

    Seller findByIdAndEmail(Integer id, String email);
}
