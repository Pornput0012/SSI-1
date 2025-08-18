package ssi.backend.services;

import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.dtos.*;
import ssi.backend.entities.Brand;
import ssi.backend.entities.Product;
import ssi.backend.entities.SaleItemImage;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ItemNotFoundException;
import ssi.backend.repositories.BrandRepository;
import ssi.backend.repositories.ProductRepository;
import ssi.backend.utils.ListMapper;

import java.util.Arrays;
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
    private EntityManager entityManager;

    @Autowired
    private FileService fileService;
    @Autowired
    private SaleItemImageService saleItemImageService;

    public List<ProductItemDTO> getAllProducts() {
        return listMapper.mapList(productRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id"))),
                ProductItemDTO.class, modelMapper);
    }

    public ProductPageDTO getAllProductsV2(int page, int size, String sortField, String sortDirection,
                                           List<String> filterBrands, List<Integer> filterStorage,
                                           Integer filterPriceLower, Integer filterPriceUpper) {
        ProductPageDTO productPage;

        boolean hasSortField = sortField != null && !sortField.isBlank();
        boolean sortDesc = "desc".equalsIgnoreCase(sortDirection);

        Sort sort;
        if (hasSortField) {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc(sortField))
                    : Sort.by(Sort.Order.asc(sortField));
            sort = sort.and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = sortDesc
                    ? Sort.by(Sort.Order.desc("brand.createdOn")).and(Sort.by(Sort.Order.desc("id")))
                    : Sort.by(Sort.Order.asc("brand.createdOn")).and(Sort.by(Sort.Order.asc("id")));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> pageResult = productRepository.findFilteredProducts(
                (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                (filterStorage != null && !filterStorage.isEmpty()) ? filterStorage : null,
                filterPriceLower,
                filterPriceUpper,
                pageable);

        productPage = modelMapper.map(pageResult, ProductPageDTO.class);
        productPage.setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));

        productPage.setSort((sortField == null ? "brand.createOn" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));
        return productPage;
    }

    public ProductDetailDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        return modelMapper.map(product, ProductDetailDTO.class);
    }

    public ProductItemResponseDtoV2 getProductByIdV2(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem not found for this id :: " + id));
        return modelMapper.map(product, ProductItemResponseDtoV2.class);
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

    @Transactional
    public void deleteProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));
        productRepository.delete(product);
    }

    public void deleteProductByIdV2(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));
        saleItemImageService.findAllByProduct(id).forEach(saleItemImage -> {
            saleItemImageService.deleteImage(saleItemImage.getId());
            fileService.removeFile(product,saleItemImage.getFileName());
        });

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

    @Transactional
    public ProductItemResponseDtoV2 createProduct(ProductItemRequestDtoV2 product, List<MultipartFile> images) {

        if (images != null && images.size() > 4) {
            throw new BadRequestException("Images Size Exceeded");
        }
        Brand existingBrand = brandRepository.findById(product.getBrandId())
                .orElseThrow(
                        () -> new ItemNotFoundException("Brand id " + product.getBrandId() + " does not exist"));

        Product creatingProduct = modelMapper.map(product, Product.class);
        creatingProduct.setBrand(existingBrand);
        creatingProduct.setId(null);

        Product prodRes = productRepository.save(creatingProduct);
        if (images != null && !images.isEmpty()) {
         fileService.saveFile(images, prodRes);
        }

        productRepository.flush();

        entityManager.refresh(prodRes);
        return modelMapper.map(prodRes, ProductItemResponseDtoV2.class);
    }

    @Transactional
    public ProductItemRequestDtoV2 updateProductV2(Integer id, ProductItemRequestDtoV2 product, String removeImage, String orderImages, List<MultipartFile> newImages) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));
        Brand existingBrand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new ItemNotFoundException(
                        "Brand id " + product.getBrandId() + " does not exist"));

        // อัปเดตข้อมูลพื้นฐานของสินค้า
        existingProduct.setModel(product.getModel());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setRamGb(product.getRamGb());
        existingProduct.setScreenSizeInch(product.getScreenSizeInch());
        existingProduct.setStorageGb(product.getStorageGb());
        existingProduct.setColor(product.getColor());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setBrand(existingBrand);

        // ลบรูปภาพที่ระบุ
        if (removeImage != null && !removeImage.trim().isEmpty()) {
            List<String> removeImageList = Arrays.asList(removeImage.split(","));
            fileService.removeFiles(existingProduct, removeImageList);
        }

        // เพิ่มรูปภาพใหม่ที่ส่งมา
        if (newImages != null && !newImages.isEmpty()) {
            if (newImages.size() > 4) {
                throw new BadRequestException("Images Size Exceeded");
            }
            fileService.saveFile(newImages, existingProduct);
        }

        // จัดการ order ของรูปภาพ
        if (orderImages != null && !orderImages.trim().isEmpty()) {
            // รูปแบบ: fileName_1,orderImage_1|fileName_2,orderImage_2|fileName_3,orderImage_3
            String[] imageOrderPairs = orderImages.split("\\|");
            
            for (String pair : imageOrderPairs) {
                if (pair.trim().isEmpty()) continue;
                
                String[] parts = pair.split(",");
                if (parts.length != 2) continue;
                
                String fileName = parts[0].trim();
                try {
                    Integer newOrder = Integer.parseInt(parts[1].trim());
                    
                    // ตรวจสอบว่ามีไฟล์นี้ในฐานข้อมูลหรือไม่
                    SaleItemImage existingImage = saleItemImageService.findByProductAndFileName(existingProduct, fileName);
                    
                    if (existingImage != null) {
                        // ตรวจสอบว่า order ตรงกันหรือไม่
                        if (!newOrder.equals(existingImage.getImageViewOrder())) {
                            // อัปเดต order ใหม่
                            saleItemImageService.updateImageOrder(existingImage.getId(), newOrder);
                        }
                    } else {
                        // ไฟล์ไม่มีในฐานข้อมูล แสดงว่าถูกลบไปแล้ว
                        // ไม่ต้องทำอะไร เพราะไฟล์ถูกลบไปแล้ว
                    }
                    
                } catch (NumberFormatException e) {
                    // ถ้า order ไม่ใช่ตัวเลข ข้ามไป
                    continue;
                }
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);

        productRepository.flush();
        entityManager.refresh(updatedProduct);

        return modelMapper.map(updatedProduct, ProductItemRequestDtoV2.class);
    }
}
