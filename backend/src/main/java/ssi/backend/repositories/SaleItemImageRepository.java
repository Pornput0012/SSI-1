package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Product;
import ssi.backend.entities.SaleItemImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
    @Modifying
    @Query("DELETE FROM SaleItemImage s WHERE s.fileName = :fileName AND s.product.id = :productId")
    void deleteByFileNameAndProductId(@Param("fileName") String fileName, @Param("productId") Integer productId);

    boolean findByFileName(String fileName);

    boolean existsByFileName(String fileName);
    boolean existsByProduct(Product product);
    List<SaleItemImage> findAllByProduct(Product product);

    List<SaleItemImage> findSaleItemImageByFileName(String saleItemImageName);

    @NativeQuery("select * from sale_item_images s where s.fileName = :fileName")
    SaleItemImage findSaleItemImageByFileNameV2(String fileName);
    
    Optional<SaleItemImage> findByProductAndFileName(Product product, String fileName);
}
