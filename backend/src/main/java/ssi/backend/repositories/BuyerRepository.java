package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Buyer;

import java.util.List;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
    Buyer findByEmail(String email);

    @Query("SELECT b.email FROM Buyer b")
    List<String> findAllEmailsBuyer();

    Buyer findByIdAndEmail(Integer id, String email);
}
