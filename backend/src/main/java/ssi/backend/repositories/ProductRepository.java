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

    // แก้ไข: เอา DISTINCT ออกและใช้ custom query แทน
    @Query("SELECT p FROM Product p JOIN p.brand brand WHERE brand.name IN :brandNames")
    Page<Product> findByBrandNameIn(List<String> brandNames, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.brand brand ORDER BY brand.createdOn ASC, p.id ASC")
    Page<Product> findAllOrderByBrandCreatedOnAsc(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.brand brand ORDER BY brand.createdOn DESC, p.id DESC")
    Page<Product> findAllOrderByBrandCreatedOnDesc(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.brand brand WHERE brand.name IN :brandNames ORDER BY brand.createdOn ASC, p.id ASC")
    Page<Product> findAllByBrandNameInOrderByBrandCreatedOnAsc(List<String> brandNames, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.brand brand WHERE brand.name IN :brandNames ORDER BY brand.createdOn DESC, p.id DESC")
    Page<Product> findAllByBrandNameInOrderByBrandCreatedOnDesc(List<String> brandNames, Pageable pageable);

    // หากต้องการ DISTINCT จริงๆ ให้ใช้แบบนี้
    @Query("SELECT DISTINCT p FROM Product p JOIN p.brand brand WHERE brand.name IN :brandNames ORDER BY p.id")
    Page<Product> findDistinctByBrandNameInOrderById(List<String> brandNames, Pageable pageable);
}