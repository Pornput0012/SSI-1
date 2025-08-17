package ssi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi.backend.entities.Product;
import ssi.backend.entities.SaleItemImage;

import java.util.List;

@Repository
public interface SaleItemImageRepository extends JpaRepository<SaleItemImage, Integer> {
    List<SaleItemImage> findAllByProduct(Product product);

    List<SaleItemImage>  findSaleItemImageByFileName(String saleItemImageName);
}
