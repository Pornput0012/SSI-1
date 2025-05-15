package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.*;
import ssi.backend.entities.Brand;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.utils.ListMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<BrandDTO> getAllBrandsDto() {
        return listMapper.mapList(brandRepository.findAll(Sort.by(Sort.Order.asc("name"))), BrandDTO.class, modelMapper);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll(Sort.by(Sort.Order.asc("name")));
    }

    public BrandResponseDTO getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Brand not found for this id :: " + id));
        return modelMapper.map(brand, BrandResponseDTO.class);
    }

    public BrandResponseDTO createBrand(BrandRequestDTO brand) {
        Brand creatingBrand = modelMapper.map(brand,Brand.class);
        Boolean duplicateName = getAllBrands().stream().anyMatch(b -> b.getName().equals(brand.getName()));
        if (duplicateName) {
            throw new BadRequestException("Duplicate Name : " + brand.getName());
        }

        if (brand.getIsActive() == null) {
            creatingBrand.setIsActive(false);
        }
        return modelMapper.map(brandRepository.save(creatingBrand), BrandResponseDTO.class);
    }


    public BrandResponseDTO updateBrand(Integer id, BrandRequestDTO brand) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Brand id " + id + " does not exist"));

        Boolean duplicateName = getAllBrands().stream().anyMatch(b -> (b.getId() != id && b.getName().equals(brand.getName())));
        if (duplicateName) {
            throw new BadRequestException("Duplicate Name : " + brand.getName());
        }

        if (brand.getIsActive() == null) {
            existingBrand.setIsActive(false);
        } else {
            existingBrand.setIsActive(brand.getIsActive());
        }
        existingBrand.setName(brand.getName());
        existingBrand.setWebsiteUrl(brand.getWebsiteUrl());
        existingBrand.setCountryOfOrigin(brand.getCountryOfOrigin());

        return modelMapper.map(brandRepository.save(existingBrand), BrandResponseDTO.class);
    }


    public void deleteBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Brand id " + id + " does not exist"));
        if (!brand.getProducts().isEmpty()) {
            throw new BadRequestException("Brand has sale Item :" + brand.getProducts().stream().map(Product::getModel).collect(Collectors.toList()));
        }

        brandRepository.delete(brand);
    }

}
