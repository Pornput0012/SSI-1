package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi.backend.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}