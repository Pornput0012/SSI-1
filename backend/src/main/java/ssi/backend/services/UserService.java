package ssi.backend.services;

import com.nimbusds.jose.JOSEException;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.configuration.HostProperties;
import ssi.backend.dtos.JwtClaimDTO;
import ssi.backend.dtos.RegisterRequestDTO;
import ssi.backend.dtos.UserProfileResponseDTO;
import ssi.backend.dtos.UserResponseDTO;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.exceptions.ConfilctResourceException;
import ssi.backend.exceptions.ForbiddenException;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.exceptions.BadRequestException;

import java.text.ParseException;
import java.util.List;

@Service
public class UserService {
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
    @Autowired
    private HostProperties hostProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (buyer.getPassword() == null || buyer.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        Buyer creatingBuyer = modelMapper.map(buyer, Buyer.class);
        creatingBuyer.setId(null);
        creatingBuyer.setPassword(passwordEncoder.encode(buyer.getPassword()));
        Buyer savedBuyer = buyerRepository.save(creatingBuyer);

        try {
            String token = jwtService.generateVerifyEmailToken(creatingBuyer.getId(), creatingBuyer.getFullName(), creatingBuyer.getEmail(), "BUYER");
            emailService.sendEmail(savedBuyer.getEmail(), "Welcome to ITB-MSHOP!",
                    "สวัสดีคุณ " + savedBuyer.getFullName()
                            + " 🎉\n\nการสมัครสมาชิกของคุณเสร็จสมบูรณ์แล้ว โปรดกดยืนยันอีเมลเพื่อเข้าสู่ระบบที่ลิงก์ด้านล่าง\n\n"
                            + hostProperties.getUrl()
                            + "/verify-email?jwtToken=" + token
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
        if (seller.getPassword() == null || seller.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
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
                    creatingSeller.setIdCardImageFront(fileName);
                } else if (fileName.contains("_bs_")) {
                    creatingSeller.setIdCardImageBack(fileName);
                } else {
                    throw new BadRequestException("Invalid file name side: " + fileName);
                }
            });
        }

        creatingSeller.setId(null);
        creatingSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        Seller sellerRes = new Seller();
        try {
            sellerRes = sellerRepository.save(creatingSeller);
            try {
                String token = jwtService.generateVerifyEmailToken(sellerRes.getId(), sellerRes.getFullName(), sellerRes.getEmail(), "SELLER");
                emailService.sendEmail(sellerRes.getEmail(), "Welcome to ITB-MSHOP!",
                        "สวัสดีคุณ " + sellerRes.getFullName()
                                + " 🎉\n\nการสมัครสมาชิกของคุณเสร็จสมบูรณ์แล้ว โปรดกดยืนยันอีเมลเพื่อเข้าสู่ระบบที่ลิงก์ด้านล่าง\n\n"
                                + hostProperties.getUrl()
                                + "/verify-email?jwtToken=" + token
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

    public UserResponseDTO verifyEmail(String token) throws ParseException {
        JwtClaimDTO emailVerifyClaim = jwtService.extractUserClaims(token);
        if (emailVerifyClaim.getRole().equalsIgnoreCase("buyer")) {
            Buyer buyer = buyerRepository.findByIdAndEmail(emailVerifyClaim.getUserId(), emailVerifyClaim.getEmail());

            if (buyer == null) {
                throw new BadRequestException("Invalid email or User id");
            }

            if (buyer.getStatus().equalsIgnoreCase("active")) {
                throw new ConfilctResourceException("Email already verified");
            }

            buyer.setStatus("ACTIVE");
            buyer.setId(emailVerifyClaim.getUserId());
            buyerRepository.save(buyer);

            UserResponseDTO userResponseDTO = modelMapper.map(buyer, UserResponseDTO.class);
            userResponseDTO.setUserType(emailVerifyClaim.getRole());
            userResponseDTO.setIsActive(true);

            return userResponseDTO;

        } else if (emailVerifyClaim.getRole().equalsIgnoreCase("seller")) {
            Seller seller = sellerRepository.findByIdAndEmail(emailVerifyClaim.getUserId(), emailVerifyClaim.getEmail());

            if (seller == null) {
                throw new BadRequestException("Invalid email or User id");
            }

            if (seller.getStatus().equalsIgnoreCase("active")) {
                throw new ConfilctResourceException("Email already verified");
            }

            seller.setStatus("ACTIVE");
            sellerRepository.save(seller);

            UserResponseDTO userResponseDTO = modelMapper.map(seller, UserResponseDTO.class);
            userResponseDTO.setUserType(emailVerifyClaim.getRole());
            userResponseDTO.setIsActive(true);

            return userResponseDTO;
        }
        return null;
    }

    public UserProfileResponseDTO getUserProfile(String token) throws ParseException, JOSEException {
        jwtService.validateAccessToken(token);

        JwtClaimDTO profileClaim = jwtService.extractUserClaims(token);
        if (profileClaim.getRole().equalsIgnoreCase("BUYER")) {
            Buyer buyer = buyerRepository.findByIdAndEmail(profileClaim.getUserId(), profileClaim.getEmail());
            if (buyer == null) {
                throw new BadRequestException("Invalid email or User id");
            }
            if (buyer.getStatus().equalsIgnoreCase("inactive")) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            return modelMapper.map(buyer, UserProfileResponseDTO.class);
        }
        else if (profileClaim.getRole().equalsIgnoreCase("SELLER")) {
            Seller seller = sellerRepository.findByIdAndEmail(profileClaim.getUserId(), profileClaim.getEmail());
            if (seller == null) {
                throw new BadRequestException("Invalid email or User id");
            }
            if (seller.getStatus().equalsIgnoreCase("inactive")) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            return modelMapper.map(seller, UserProfileResponseDTO.class);
        }

        throw new BadRequestException("Invalid token");
    }
}
