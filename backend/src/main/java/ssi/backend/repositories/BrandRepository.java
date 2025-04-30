package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi.backend.entities.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
