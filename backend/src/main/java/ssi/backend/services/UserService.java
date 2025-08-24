package ssi.backend.services;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtService jwtService;

    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<String> getAllBuyerEmails() {
        return buyerRepository.findAllEmailsBuyer();
    }

    public List<String> getAllSellerEmails() {
        return sellerRepository.findAllEmailsSeller();
    }

    public Buyer createBuyer(RegisterRequestDTO buyer) {
        if (getAllBuyerEmails().contains(buyer.getEmail()) ||
                getAllSellerEmails().contains(buyer.getEmail())) {
            throw new BadRequestException("Email already exists: " + buyer.getEmail());
        }

        Buyer creatingBuyer = modelMapper.map(buyer, Buyer.class);
        creatingBuyer.setId(null);
        Buyer savedBuyer = buyerRepository.save(creatingBuyer);

        try {
            String token = jwtService.generateToken(creatingBuyer.getId(), creatingBuyer.getFullname(), creatingBuyer.getEmail(), "buyer");
            emailService.sendEmail(savedBuyer.getEmail(), "Welcome to ITB-MSHOP!",
                    "สวัสดีคุณ " + savedBuyer.getFullname()
                            + " 🎉\n\nการสมัครสมาชิกของคุณเสร็จสมบูรณ์แล้ว โปรดกดยืนยันอีเมลเพื่อเข้าสู่ระบบที่ลิงก์ด้านล่าง\n\n"
                            + "http://localhost:5173/verify-email/?jwtToken=" + token
                            + "\n\nขอบคุณที่เข้าร่วมกับเรา!");
        } catch (Exception e) {
            System.err.println("❌ ส่งอีเมลไปยัง Buyer ไม่สำเร็จ: " + e.getMessage());
        }
        return savedBuyer;
    }

    public Seller createSeller(RegisterRequestDTO seller, List<MultipartFile> images) {
        if (getAllBuyerEmails().contains(seller.getEmail()) ||
                getAllSellerEmails().contains(seller.getEmail())) {
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
            sellerRes = sellerRepository.save(creatingSeller);
            try {
                String token = jwtService.generateToken(sellerRes.getId(), sellerRes.getFullname(), sellerRes.getEmail(), "seller");
                emailService.sendEmail(sellerRes.getEmail(), "Welcome to ITB-MSHOP!",
                        "สวัสดีคุณ " + sellerRes.getFullname()
                                + " 🎉\n\nการสมัครสมาชิกของคุณเสร็จสมบูรณ์แล้ว โปรดกดยืนยันอีเมลเพื่อเข้าสู่ระบบที่ลิงก์ด้านล่าง\n\n"
                                + "http://localhost:5173/verify-email/?jwtToken=" + token
                                + "\n\nขอบคุณที่เข้าร่วมกับเรา!");
            } catch (Exception e) {
                System.err.println("❌ ส่งอีเมลไปยัง Seller ไม่สำเร็จ: " + e.getMessage());
            }
            System.out.println(sellerRes);
            return sellerRes;

        } catch (Exception e) {
            fileService.removeFiles(fileNames);

            throw new BadRequestException("Seller not created" + e.getMessage());
        }

    }
}
