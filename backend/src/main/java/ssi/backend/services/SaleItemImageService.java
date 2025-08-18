package ssi.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.entities.Product;
import ssi.backend.entities.SaleItemImage;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.repositories.SaleItemImageRepository;

import java.util.List;

@Service
public class SaleItemImageService {

    @Autowired
    private SaleItemImageRepository saleItemImageRepository;
    @Autowired
    private ProductRepository productRepository;


    public SaleItemImage saveInfoImage(SaleItemImage saleItemImage) {
        return saleItemImageRepository.save(saleItemImage);
    }

    public List<SaleItemImage> findAllByProduct(Integer productId) {
        return saleItemImageRepository.findAllByProduct(productRepository.findById(productId).orElse(null));
    }

    public void deleteImage(Integer imageId) {
         saleItemImageRepository.deleteById(imageId);
    }

    public List<SaleItemImage> findSaleItemImageByName(String saleItemImageName) {
        return saleItemImageRepository.findSaleItemImageByFileName(saleItemImageName);
    }


    public Boolean imageExists(String fileName) {
        return findSaleItemImageByName(fileName).size() > 0;
    }

    public SaleItemImage findByProductAndFileName(Product product, String fileName) {
        return saleItemImageRepository.findByProductAndFileName(product, fileName).orElse(null);
    }

    public void updateImageOrder(Integer imageId, Integer newOrder) {
        SaleItemImage image = saleItemImageRepository.findById(imageId).orElse(null);
        if (image != null) {
            image.setImageViewOrder(newOrder);
            saleItemImageRepository.save(image);
        }
    }
}
