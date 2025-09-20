package ssi.backend.services;

import com.nimbusds.jose.JOSEException;
import org.eclipse.angus.mail.imap.protocol.Item;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.configuration.HostProperties;
import ssi.backend.dtos.*;
import ssi.backend.entities.User;
import ssi.backend.exceptions.*;
import ssi.backend.repositories.UserRepository;

import java.text.ParseException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
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

    public List<User> getAllBuyers() {
        return userRepository.findAllByUserType("BUYER");
    }

    public List<User> getAllSellers() {
        return userRepository.findAllByUserType("SELLER");
    }

    public List<String> getAllUserEmails() {
        return userRepository.findAllEmail();
    }


    public User createUser(RegisterRequestDTO user, List<MultipartFile> images) {
        if (getAllUserEmails().contains(user.getEmail())) {
            throw new BadRequestException("Email already exists: " + user.getEmail());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (user.getUserType() == null) {
            throw new BadRequestException("Role is required");
        }
        System.out.println(user.getUserType());
        User creatingUser = modelMapper.map(user, User.class);
        System.out.println(creatingUser.getId());
        creatingUser.setId(null);
        creatingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        List<String> fileNames = null;

        if (user.getUserType().equalsIgnoreCase("SELLER")) {
            if (images != null && !images.isEmpty()) {
                fileNames = fileService.saveFile(images);
            } else {
                throw new BadRequestException("Images are empty");
            }
            if (fileNames != null && !fileNames.isEmpty()) {
                fileNames.stream().forEach(fileName -> {
                    if (fileName.contains("_fs_")) {
                        creatingUser.setNationalIdPhotoFront(fileName);
                    } else if (fileName.contains("_bs_")) {
                        creatingUser.setNationalIdPhotoBack(fileName);
                    } else {
                        throw new BadRequestException("Invalid file name side: " + fileName);
                    }
                });
            }
        }

        User savedUser;
        try {
            savedUser = userRepository.save(creatingUser);

            try {
                String token = jwtService.generateVerifyEmailToken(
                        savedUser.getId(),
                        savedUser.getFullname(),
                        savedUser.getEmail(),
                        savedUser.getUserType().toString()
                );
                emailService.sendEmail(
                        savedUser.getEmail(),
                        "Welcome to ITB-MSHOP!",
                        "สวัสดีคุณ " + savedUser.getFullname()
                                + " 🎉\n\nการสมัครสมาชิกของคุณเสร็จสมบูรณ์แล้ว โปรดกดยืนยันอีเมลเพื่อเข้าสู่ระบบที่ลิงก์ด้านล่าง\n\n"
                                + hostProperties.getUrl()
                                + "/verify-email?jwtToken=" + token
                                + "\n\nขอบคุณที่เข้าร่วมกับเรา!"
                );
            } catch (Exception e) {
                System.err.println("❌ ส่งอีเมลไปยัง " + savedUser.getUserType() + " ไม่สำเร็จ: " + e.getMessage());
            }

            return savedUser;

        } catch (Exception e) {

            if (user.getUserType().equalsIgnoreCase("SELLER") && images != null && !images.isEmpty()) {
                fileService.removeFiles(fileNames);
            }
            throw new BadRequestException("User not created: " + e.getMessage());
        }
    }


    public UserResponseDTO verifyEmail(String token) throws ParseException {
        JwtClaimDTO emailVerifyClaim = jwtService.extractUserClaims(token);
        if (emailVerifyClaim.getRole() != null && !emailVerifyClaim.getRole().isEmpty()) {
            User user = userRepository.findByIdAndEmail(emailVerifyClaim.getUserId(), emailVerifyClaim.getEmail());

            if (user == null) {
                throw new BadRequestException("Invalid email or User id");
            }

            if (user.getStatus().equalsIgnoreCase("active")) {
                throw new ConfilctResourceException("Email already verified");
            }

            user.setStatus("ACTIVE");
            user.setId(emailVerifyClaim.getUserId());
            userRepository.save(user);

            UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
            userResponseDTO.setUserType(emailVerifyClaim.getRole());
            userResponseDTO.setIsActive(true);

            return userResponseDTO;

        }
        return null;
    }

    public UserProfileResponseDTO getUserProfile()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        Integer userId = (Integer) authentication.getDetails();

        User user = userRepository.findByIdAndEmail(userId, email);
        if (user == null) {
            throw new BadRequestException("Invalid email or User id");
        }
        if (user.getStatus().equalsIgnoreCase("inactive")) {
            throw new ForbiddenException("You need activate your account before signing in.");
        }
        return modelMapper.map(user, UserProfileResponseDTO.class);
    }

    public UserProfileResponseDTO getUserProfileById(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getDetails();

        if (id == null || !id.equals(userId)) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));
        if (user.getStatus().equalsIgnoreCase("inactive")) {
            throw new ConfilctResourceException("User is not active");
        }

        return modelMapper.map(user, UserProfileResponseDTO.class);
    }

    public UserProfileResponseDTO updateUserProfile(Integer id, UpdateUserRequestDto updateUser) throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getDetails();
        if (id == null ||  !id.equals(userId)) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));

        user.setFullname(updateUser.getFullName());
        user.setNickname(updateUser.getNickName());

        return modelMapper.map(userRepository.save(user), UserProfileResponseDTO.class);
    }
}
