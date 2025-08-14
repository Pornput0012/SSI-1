package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.SaleItemImage;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
}
