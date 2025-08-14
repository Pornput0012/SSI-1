package ssi.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.dtos.*;
import ssi.backend.entities.Brand;
import ssi.backend.entities.Product;
import ssi.backend.exceptions.BadRequestException;
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

    @Autowired
    private FileService fileService;

    public List<ProductItemDTO> getAllProducts() {
        return listMapper.mapList(productRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id"))),
                ProductItemDTO.class, modelMapper);
    }

    public ProductPageDTO getAllProductsV2(int page, int size, String sortField, String sortDirection,
            List<String> filterBrands) {
        ProductPageDTO productPage;

        Pageable pageable;
        boolean hasSortField = sortField != null && !sortField.isBlank();
        boolean hasFilter = filterBrands != null && !filterBrands.isEmpty();

        boolean sortDesc = "desc".equalsIgnoreCase(sortDirection);

        if (hasSortField) {
            Sort sort = sortDesc
                    ? Sort.by(Sort.Order.desc(sortField))
                    : Sort.by(Sort.Order.asc(sortField));
            pageable = PageRequest.of(page, size, sort.and(Sort.by(Sort.Order.asc("id"))));

            if (hasFilter) {
                productPage = modelMapper.map(productRepository.findByBrandNameIn(filterBrands, pageable),
                        ProductPageDTO.class);
            } else {
                productPage = modelMapper.map(productRepository.findAll(pageable), ProductPageDTO.class);
            }

        } else {
            pageable = PageRequest.of(page, size);
            if (hasFilter) {
                productPage = sortDesc
                        ? modelMapper.map(productRepository.findAllByBrandNameInOrderByBrandCreatedOnDesc(filterBrands,
                                pageable), ProductPageDTO.class)
                        : modelMapper.map(productRepository.findAllByBrandNameInOrderByBrandCreatedOnAsc(filterBrands,
                                pageable), ProductPageDTO.class);
            } else {
                productPage = sortDesc
                        ? modelMapper.map(
                                productRepository.findAllOrderByBrandCreatedOnDesc(pageable),
                                ProductPageDTO.class)
                        : modelMapper.map(productRepository.findAllOrderByBrandCreatedOnAsc(pageable),
                                ProductPageDTO.class);
            }
        }

        productPage.setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));
        return productPage;
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

        Product creatingProduct = modelMapper.map(product, Product.class);
        creatingProduct.setBrand(existingBrand);
        creatingProduct.setId(null);

        return modelMapper.map(productRepository.save(creatingProduct), ProductItemResponseDTO.class);
    }

    public ProductItemResponseDTO createProduct(ProductItemRequestDtoV2 product,List<MultipartFile> images) {

        if (images.size() > 4) {
            throw new BadRequestException("Images Size Exceeded");
        }
        Brand existingBrand = brandRepository.findById(product.getBrandId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Brand id " + product.getBrandId() + " does not exist"));


        Product creatingProduct = modelMapper.map(product, Product.class);
        creatingProduct.setBrand(existingBrand);
        creatingProduct.setId(null);

        Product prodRes = productRepository.save(creatingProduct);
        fileService.saveFile(images,prodRes);
        return modelMapper.map(prodRes, ProductItemResponseDTO.class);
    }
}
