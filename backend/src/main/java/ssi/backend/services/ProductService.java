package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.ProductDetailDTO;
import ssi.backend.dtos.ProductItemDTO;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.utils.ListMapper;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BrandRepository brandRepository;

    public List<ProductItemDTO> getAllProducts() {
        return listMapper.mapList(productRepository.findAll(Sort.by(Sort.Order.asc("createdOn"),Sort.Order.asc("id"))), ProductItemDTO.class, modelMapper);
    }

    public ProductDetailDTO getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        return modelMapper.map(product, ProductDetailDTO.class);
    }

    public ProductItemDTO deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        productRepository.deleteById(id);
        return modelMapper.map(product,ProductItemDTO.class);
    }

    public ProductItemDTO createProduct(Product product) {
        System.out.println(product.getBrand());
        product.setBrand(brandRepository.findById(product.getBrand().getId()).orElse(null));
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(productRepository.findById(savedProduct.getId()).orElse(null),ProductItemDTO.class);
    }

    public Product updateProduct(Integer id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Product not found for this id :: " + id));

        existingProduct.setModel(product.getModel());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setRamGb(product.getRamGb());
        existingProduct.setScreenSizeInch(product.getScreenSizeInch());
        existingProduct.setStorageGb(product.getStorageGb());
        existingProduct.setColor(product.getColor());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setBrand(product.getBrand());

        return productRepository.save(existingProduct);
    }






}
