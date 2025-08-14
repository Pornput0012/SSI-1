package ssi.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.entities.SaleItemImage;
import ssi.backend.repositories.SaleItemImageRepository;

@Service
public class SaleItemImageService {

    @Autowired
    private SaleItemImageRepository saleItemImageRepository;


    public SaleItemImage saveInfoImage(SaleItemImage saleItemImage) {
        return saleItemImageRepository.save(saleItemImage);
    }
}
