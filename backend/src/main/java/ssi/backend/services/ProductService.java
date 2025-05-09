package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi.backend.dtos.*;
import ssi.backend.entities.Brand;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.utils.ListMapper;

import java.math.BigDecimal;
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
        return listMapper.mapList(productRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id"))),
                ProductItemDTO.class, modelMapper);
    }

    public List<Product> getAll2Products() {
        return productRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id")));
    }

    public ProductDetailDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        return modelMapper.map(product, ProductDetailDTO.class);
    }

    public ProductItemResponseDTO updateProduct(Integer id, ProductItemRequestDTO productItemRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));
        Brand existingBrand = brandRepository.findById(productItemRequestDTO.getBrand().getId())
                .orElseThrow(() -> new ItemNotFoundException(
                        "Brand id " + productItemRequestDTO.getBrand().getId() + " does not exist"));

        if (!existingBrand.getName().equals(productItemRequestDTO.getBrand().getName())) {
            throw new BadRequestException("Brand name does not match");
        }

        existingProduct.setModel(productItemRequestDTO.getModel());
        existingProduct.setDescription(productItemRequestDTO.getDescription());
        existingProduct.setPrice(productItemRequestDTO.getPrice());
        existingProduct.setRamGb(productItemRequestDTO.getRamGb());
        existingProduct.setScreenSizeInch(productItemRequestDTO.getScreenSizeInch());
        existingProduct.setStorageGb(productItemRequestDTO.getStorageGb());
        existingProduct.setColor(productItemRequestDTO.getColor());
        existingProduct.setQuantity(productItemRequestDTO.getQuantity());
        existingProduct.setBrand(existingBrand);

        return modelMapper.map(productRepository.save(existingProduct), ProductItemResponseDTO.class);
    }

    public void deleteProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));
        productRepository.delete(product);
    }

    public ProductItemResponseDTO createProduct(ProductItemRequestDTO product) {

        Brand existingBrand = brandRepository.findById(product.getBrand().getId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Brand id " + product.getBrand().getId() + " does not exist"));
        if (!existingBrand.getName().equals(product.getBrand().getName())) {
            throw new BadRequestException("Brand name does not match");
        }
        Product creatingProduct = modelMapper.map(product, Product.class);
        creatingProduct.setBrand(existingBrand);
        // Product existingProduct = productRepository.findById(product.getId())
        // .orElse(null);

        // if (existingProduct != null) {
        // throw new BadRequestException("SaleItem id " + product.getId() + " already
        // exist");
        // }

        return modelMapper.map(productRepository.save(creatingProduct), ProductItemResponseDTO.class);
    }
}
