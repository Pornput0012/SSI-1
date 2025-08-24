package ssi.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.dtos.RegisterRequestDTO;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.exceptions.BadRequestException;

import java.util.List;

@Slf4j
@Service
public class RegisterService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<Buyer> getAllEmailBuyers(String email) {
        return buyerRepository.findByEmail(email);
    }

    public List<Seller> getAllEmailSellers(String email) {
        return sellerRepository.findByEmail(email);
    }

    public Buyer createBuyer(RegisterRequestDTO buyer) {
        if (!getAllEmailBuyers(buyer.getEmail()).isEmpty() ||
                !getAllEmailSellers(buyer.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + buyer.getEmail());
        }

        Buyer creatingBuyer = modelMapper.map(buyer, Buyer.class);
        creatingBuyer.setId(null);
        return buyerRepository.save(creatingBuyer);

    }

    public Seller createSeller(RegisterRequestDTO seller, List<MultipartFile> images) {
        if (!getAllEmailBuyers(seller.getEmail()).isEmpty() ||
                !getAllEmailSellers(seller.getEmail()).isEmpty()) {
            throw new BadRequestException("Email already exists: " + seller.getEmail());
        }

        Seller creatingSeller = modelMapper.map(seller, Seller.class);
        List<String> fileNames;
        if (images != null && !images.isEmpty()) {
            fileNames = fileService.saveFile(images);
        } else {
            throw new BadRequestException("Images are empty");
        }
        if (fileNames != null && !fileNames.isEmpty()) {
            fileNames.stream().forEach(fileName -> {
                if (fileName.contains("_fs_")) {
                    creatingSeller.setNationalIdPhotoFront(fileName);
                } else if (fileName.contains("_bs_")) {
                    creatingSeller.setNationalIdPhotoBack(fileName);
                } else {
                    throw new BadRequestException("Invalid file name side: " + fileName);
                }
            });
        }

        creatingSeller.setId(null);
        Seller sellerRes = new Seller();
        try {
            sellerRes =sellerRepository.save(creatingSeller);
            System.out.println(sellerRes);
            return sellerRes;


        } catch (Exception e) {
            fileService.removeFiles(fileNames);

            throw new BadRequestException("Seller not created" + e.getMessage());
        }



    }
}
