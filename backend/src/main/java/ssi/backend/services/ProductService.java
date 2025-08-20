package ssi.backend.services;

import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import java.util.ArrayList;
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
            List<String> filterBrands, List<Integer> filterStorages,
            Integer filterPriceLower, Integer filterPriceUpper, String keyword) {
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

        // Logic สำหรับกรณี filterStorages
        if (filterStorages != null && !filterStorages.isEmpty()) {
            boolean hasZero = filterStorages.stream().anyMatch(ele -> ele == 0);
            List<Integer> nonZeroStorages = filterStorages.stream().filter(ele -> ele != 0).toList();

            if (hasZero && !nonZeroStorages.isEmpty()) {
                // กรณีมี 0 และเลขอื่น
                // Query รอบแรกด้วย nonZeroStorages
                Page<Product> pageResult1 = productRepository.findFilteredProducts(
                        (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                        nonZeroStorages,
                        filterPriceLower,
                        filterPriceUpper,
                        keyword,
                        pageable);
                // Query รอบสองด้วย [0]
                Page<Product> pageResult2 = productRepository.findFilteredProducts(
                        (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                        null,
                        filterPriceLower,
                        filterPriceUpper,
                        keyword,
                        pageable);
                // รวมผลลัพธ์
                List<Product> combinedList = new ArrayList<>();
                combinedList.addAll(pageResult1.getContent());
                combinedList.addAll(pageResult2.getContent());

                Page<Product> combinedPage = new PageImpl<>(combinedList, pageable,
                        combinedList.size());
                productPage = modelMapper.map(combinedPage, ProductPageDTO.class);
                productPage
                        .setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));
                productPage.setSort(
                        (sortField == null ? "brand.createOn" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));
                return productPage;
            } else if (hasZero) {
                // มีแต่ 0 อย่างเดียว
                Page<Product> pageResult = productRepository.findFilteredProducts(
                        (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                        null,
                        filterPriceLower,
                        filterPriceUpper,
                        keyword,
                        pageable);
                productPage = modelMapper.map(pageResult, ProductPageDTO.class);
                productPage
                        .setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));
                productPage.setSort(
                        (sortField == null ? "brand.createOn" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));
                return productPage;
            } else {
                Page<Product> pageResult = productRepository.findFilteredProducts(
                        (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                        filterStorages,
                        filterPriceLower,
                        filterPriceUpper,
                        keyword,
                        pageable);
                productPage = modelMapper.map(pageResult, ProductPageDTO.class);
                productPage
                        .setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));
                productPage.setSort(
                        (sortField == null ? "brand.createOn" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));
                return productPage;
            }
        } else {
            Page<Product> pageResult = productRepository.findFilteredProducts(
                    (filterBrands != null && !filterBrands.isEmpty()) ? filterBrands : null,
                    filterPriceLower,
                    filterPriceUpper,
                    pageable);
            productPage = modelMapper.map(pageResult, ProductPageDTO.class);
            productPage.setContent(listMapper.mapList(productPage.getContent(), ProductDetailDTO.class, modelMapper));
            productPage
                    .setSort((sortField == null ? "brand.createOn" : sortField) + ": " + (sortDesc ? "DESC" : "ASC"));
            return productPage;
        }
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
            fileService.removeFile(product, saleItemImage.getFileName());
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
    public ProductItemRequestDtoV2 updateProductV2(Integer id, ProductItemRequestDtoV2 product, String removeImage,
            String orderImages, List<MultipartFile> newImages) {
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
            // รูปแบบ:
            // fileName_1,orderImage_1|fileName_2,orderImage_2|fileName_3,orderImage_3
            String[] imageOrderPairs = orderImages.split("\\|");
            for (String pair : imageOrderPairs) {
                if (pair.trim().isEmpty())
                    continue;
                String[] parts = pair.split(",");
                if (parts.length != 2)
                    continue;
                String fileNameRaw = parts[0].trim();
                try {
                    Integer newOrder = Integer.parseInt(parts[1].trim());
                    // ตัด order เดิมออก เช่น B.2.1.jpg -> B.2.jpg หรือ B.2.1.png -> B.2.png
                    String[] fileNameSplit = fileNameRaw.split("\\.");
                    if (fileNameSplit.length < 3)
                        continue; // ต้องมีอย่างน้อยชื่อ, order, นามสกุล
                    // สร้างชื่อไฟล์สำหรับ DB เช่น B.2.jpg
                    String fileNameForDb = fileNameSplit[0] + "." + fileNameSplit[1] + "." + fileNameSplit[2];
                    String extension = fileNameSplit[fileNameSplit.length - 1];
                    // หาใน DB ด้วยชื่อที่ตัดแล้ว
                    SaleItemImage existingImage = saleItemImageService.findByProductAndFileName(existingProduct,
                            fileNameForDb);
                    if (existingImage != null) {
                        // สร้างชื่อไฟล์ใหม่ เช่น B.1.jpg หรือ B.1.png
                        String newFileName = fileNameSplit[0] + "." + newOrder + "." + extension;
                        // ถ้าชื่อไฟล์เปลี่ยน
                        if (!existingImage.getFileName().equals(newFileName)) {
                            // เปลี่ยนชื่อไฟล์ในโฟลเดอร์
                            fileService.renameFile(existingProduct, existingImage.getFileName(), newFileName);
                            // เปลี่ยนชื่อไฟล์ใน DB
                            saleItemImageService.updateFileName(existingImage.getId(), newFileName);
                        }
                        // อัปเดต order ใหม่ถ้าไม่ตรง
                        if (!newOrder.equals(existingImage.getImageViewOrder())) {
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

    @Transactional
    public ProductItemRequestDtoV2 updateProductV2(Integer id, String removeImage, String orderImages,
            List<MultipartFile> newImages) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("SaleItem id " + id + " does not exist"));

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

        // อัปเดตลำดับและชื่อไฟล์ตาม orderImages ที่ส่งเข้ามา
        if (orderImages != null && !orderImages.trim().isEmpty()) {
            String[] imageOrderPairs = orderImages.split("\\|");
            for (String pair : imageOrderPairs) {
                if (pair.trim().isEmpty()) {
                    System.out.println("[DEBUG] Empty pair, skip");
                    continue;
                }
                String[] parts = pair.split(",");
                if (parts.length != 2) {
                    System.out.println("[DEBUG] Invalid pair format: " + pair);
                    continue;
                }
                String fileNameRaw = parts[0].trim();
                String orderStr = parts[1].trim();
                System.out.println("[DEBUG] Processing file: " + fileNameRaw + ", order: " + orderStr);
                try {
                    Integer newOrder = Integer.parseInt(orderStr);
                    String[] fileNameSplit = fileNameRaw.split("\\.");
                    if (fileNameSplit.length < 3) {
                        System.out.println("[DEBUG] fileNameSplit < 3: " + fileNameRaw);
                        continue;
                    }
                    String baseName = fileNameSplit[0];
                    String extension = fileNameSplit[fileNameSplit.length - 1];
                    String oldOrder = fileNameSplit[1];
                    String fileNameForDb = baseName + "." + oldOrder + "." + extension;
                    String newFileName = baseName + "." + newOrder + "." + extension;
                    SaleItemImage existingImage = saleItemImageService.findByProductAndFileName(existingProduct,
                            fileNameForDb);
                    if (existingImage != null) {
                        System.out.println("[DEBUG] Found image in DB: " + fileNameForDb);
                        if (!existingImage.getFileName().equals(newFileName)) {
                            System.out.println(
                                    "[DEBUG] Rename file " + existingImage.getFileName() + " -> " + newFileName);
                            fileService.renameFile(existingProduct, existingImage.getFileName(), newFileName);
                            saleItemImageService.updateFileName(existingImage.getId(), newFileName);
                        }
                        if (!newOrder.equals(existingImage.getImageViewOrder())) {
                            System.out.println("[DEBUG] Update imageViewOrder for " + newFileName + " to " + newOrder);
                            saleItemImageService.updateImageOrder(existingImage.getId(), newOrder);
                        }
                    } else {
                        System.out.println("[DEBUG] Image not found in DB: " + fileNameForDb);
                        if (fileService.fileExists(existingProduct, fileNameRaw)) {
                            System.out.println("[DEBUG] File exists in folder, save to DB: " + fileNameRaw);
                            SaleItemImage newImage = new SaleItemImage();
                            newImage.setProduct(existingProduct);
                            newImage.setFileName(fileNameRaw);
                            newImage.setImageViewOrder(newOrder);
                            saleItemImageService.saveImage(newImage);
                        } else {
                            System.out.println("[DEBUG] File not found in folder: " + fileNameRaw);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[DEBUG] Invalid order number: " + orderStr);
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
