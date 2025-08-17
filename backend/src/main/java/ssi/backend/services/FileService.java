package ssi.backend.services;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.configuration.FileStorageProperties;
import ssi.backend.dtos.SaleItemImageDto;
import ssi.backend.entities.Product;
import ssi.backend.entities.SaleItemImage;
import ssi.backend.repositories.SaleItemImageRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class FileService {

    private final Path fileStorageLocation;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SaleItemImageService saleItemImageService;


    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to create directory storage location: " ,e);

        }
    }

    @Autowired
    private SaleItemImageRepository saleItemImageRepository;


    public String saveFile(MultipartFile image,Product product){
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());

        try {
            if(fileName.contains("..")){
                throw new RuntimeException("Cannot upload file outside current directory: " + fileName);
            }
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(image.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            SaleItemImageDto saleItemImageDto = new SaleItemImageDto();
            saleItemImageDto.setFileName(fileName);
            String[] parts = fileName.split("\\.");

            if (parts.length >= 2) {
                try {
                    int orderNo = Integer.parseInt(parts[1]);
                    saleItemImageDto.setImageViewOrder(orderNo);
                } catch (NumberFormatException e) {
                    saleItemImageDto.setImageViewOrder(null);
                }
            } else {
                saleItemImageDto.setImageViewOrder(null);
            }
            saleItemImageDto.setProduct(product);
            saleItemImageRepository.save(modelMapper.map(saleItemImageDto, SaleItemImage.class));
            return fileName;
        }

        catch (IOException e) {
            throw new RuntimeException("Unable to save file: " + fileName, e);
        }

    }

    public List<String> saveFile(List<MultipartFile> images, Product product){
        List<String> fileNames = new ArrayList<>(images.size());
        images.forEach(image -> fileNames.add(saveFile(image,product)));
        return fileNames;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }

    public void removeFile(Product product, String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                saleItemImageRepository.deleteByFileNameAndProductId(fileName, product.getId());
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation (DELETE) error: " + fileName, ex);
        }
    }

    public void removeFiles(Product product, List<String> fileNames) {
        List<String> failedFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            try {
                removeFile(product, fileName);
            } catch (Exception ex) {
                failedFiles.add(fileName);
            }
        }

        if (!failedFiles.isEmpty()) {
            throw new RuntimeException("Failed to delete files: " + String.join(", ", failedFiles));

    public void removeFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            if (Files.exists(filePath)) {
                if (!saleItemImageService.imageExists(fileName)) {
                 Files.delete(filePath);
                }
            } else  {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation error: " + fileName, ex);
        }
    }
}
