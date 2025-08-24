package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Buyer;

import java.util.List;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
    List<Buyer> findByEmail(String email);
}
