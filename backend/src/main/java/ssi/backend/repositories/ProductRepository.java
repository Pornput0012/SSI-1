package ssi.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p JOIN p.brand brand " +
            "WHERE " +
            "(:brandNames IS NULL OR brand.name IN :brandNames) " +
            "AND ( " +
            "      (:storages IS NULL AND p.storageGb IS NULL) " +
            "   OR (:storages IS NOT NULL AND p.storageGb IN :storages) " +
            ") " +
            "AND (:priceLower IS NULL OR p.price >= :priceLower) " +
            "AND (:priceUpper IS NULL OR p.price <= :priceUpper) " +
            "AND (:searchKeyWord IS NULL OR " +
            "     LOWER(p.description) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(p.model) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(p.color) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) )")
    Page<Product> findFilteredProducts(
            List<String> brandNames,
            List<Integer> storages,
            Integer priceLower,
            Integer priceUpper,
            String searchKeyWord,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p JOIN p.brand brand " +
                    "WHERE " +
                    "(:brandNames IS NULL OR brand.name IN :brandNames) " +
                    "AND (:priceLower IS NULL OR p.price >= :priceLower) " +
                    "AND (:priceUpper IS NULL OR p.price <= :priceUpper)" +
            "AND (:searchKeyWord IS NULL OR " +
            "     LOWER(p.description) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(p.model) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) " +
            "     OR LOWER(p.color) LIKE LOWER(CONCAT('%', :searchKeyWord, '%')) )")
    Page<Product> findFilteredProducts(
                    List<String> brandNames,
                    Integer priceLower,
                    Integer priceUpper,
                    String searchKeyWord,
                    Pageable pageable);
}