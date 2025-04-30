package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi.backend.dtos.ProductDetailDTO;
import ssi.backend.dtos.ProductItemDTO;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.ItemNotFoundException;
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

    public List<ProductItemDTO> getAllProducts() {
        return listMapper.mapList(productRepository.findAll(), ProductItemDTO.class, modelMapper);
    }

    public ProductDetailDTO getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        return modelMapper.map(product, ProductDetailDTO.class);
    }
}
