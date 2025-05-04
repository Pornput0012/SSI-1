package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.ProductItemDTO;
import ssi.backend.entities.Brand;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.utils.ListMapper;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper modelMapper;


    public Brand deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        brandRepository.deleteById(id);
        return brand;
    }


    public Brand createBrand(Brand brand) {
        Brand updateBrand = brandRepository.save(brand);
        return brandRepository.findById(updateBrand.getId()).orElse(null);
    }

    public Brand updateBrand(Integer id,Brand brand) {
        Brand haveBrand = brandRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        haveBrand.setName(brand.getName());
        haveBrand.setWebsiteUrl(brand.getWebsiteUrl());
        haveBrand.setIsActive(brand.getIsActive());
        haveBrand.setCountryOfOrigin(brand.getCountryOfOrigin());
        return brandRepository.save(haveBrand);
    }
}
