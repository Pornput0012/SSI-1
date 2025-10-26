package ssi.backend.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (user.getUserType() == null) {
            throw new BadRequestException("Role is required");
        }
        if (getAllUserEmails().contains(user.getEmail())) {
            throw new BadRequestException("Email already exists: " + user.getEmail());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }


        if (user.getUserType().equalsIgnoreCase("SELLER")) {

            if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
                throw new BadRequestException("Phone number is required");
            }

            if (user.getBankAccount() == null || user.getBankAccount().isBlank()) {
                throw new BadRequestException("Bank account is required");
            }

            if (user.getNationalIdNumber() == null || user.getNationalIdNumber().isBlank()) {
                throw new BadRequestException("National ID number is required");
            }
            if (!idCardCheck(user.getNationalIdNumber())) {
                throw new BadRequestException("National ID number is not valid");
            }
            System.out.println(idCardCheck(user.getNationalIdNumber()));


            if (user.getBankName() == null || user.getBankName().isBlank()) {
                throw new BadRequestException("Bank name is required");
            }
        }

        User creatingUser = modelMapper.map(user, User.class);
        creatingUser.setId(null);
        creatingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        creatingUser.setUserType(user.getUserType().toUpperCase());

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
            //userResponseDTO.setIsActive(true);

            return userResponseDTO;

        }
        return null;
    }

    public UserProfileResponseDTO getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
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
        if(authentication==null){
            throw new UnauthorizedException("No Access token");
        }
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
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();
        if (id == null || !id.equals(userId)) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));

        user.setFullname(updateUser.getFullName());
        user.setNickname(updateUser.getNickName());

        return modelMapper.map(userRepository.save(user), UserProfileResponseDTO.class);
    }

    public ChangePasswordResponseDto changePasswordById(Integer id, ChangePasswordRequestDto updateUser) throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No Access token");
        }
        Integer userId = (Integer) authentication.getDetails();
        if (id == null || !id.equals(userId)) {
            throw new UnauthorizedException("Invalid token");
        }

        ChangePasswordResponseDto response = new ChangePasswordResponseDto();
        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));
        if (!passwordEncoder.matches(updateUser.getCurrentPassword(), user.getPassword())) {
            response.setMessage("Wrong password");
            response.setSuccess(Boolean.FALSE);
            return response;
        }

        user.setPassword(passwordEncoder.encode(updateUser.getNewPassword()));
        userRepository.save(user);
        response.setMessage("New password has been sent");
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    public ChangePasswordResponseDto sendUrlResetPassword(EmailRequestDTO emailRequestDTO) throws ParseException, JOSEException {
        User user = userRepository.findByEmail(emailRequestDTO.getEmail());
        if (user == null) {
            throw new BadRequestException("Email not found");
        }

        String token = jwtService.generateResetPasswordToken(user.getId(), user.getEmail());

        String resetUrl = hostProperties.getUrl() + "/reset-password?token=" + token;

        String to = user.getEmail();
        String subject = "Reset your password | ITB-MSHOP";
        String body = """
                Hello %s,
                
                We received a request to reset your password.
                You can reset it by clicking the link below:
                
                %s
                
                If you didn’t request a password reset, please ignore this email.
                This link will expire in 15 minutes.
                
                Best regards,
                ITB-MSHOP Team
                """.formatted(user.getFullname(), resetUrl);


        emailService.sendEmail(to, subject, body);
        ChangePasswordResponseDto response = new ChangePasswordResponseDto();
        response.setMessage("Send your password reset link");
        response.setSuccess(Boolean.TRUE);
        return response;

    }

    public ChangePasswordResponseDto resetPassword(String token, ResetPasswordDTO resetPassword) throws ParseException, JOSEException {
        jwtService.validateVerifyResetPassword(token);

        SignedJWT signedJWT = SignedJWT.parse(token);
        String email = signedJWT.getJWTClaimsSet().getClaim("email").toString();

        ChangePasswordResponseDto response = new ChangePasswordResponseDto();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("Email not found");
        }

        if(!resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(resetPassword.getNewPassword()));
        userRepository.save(user);
        response.setMessage("New password has been sent");
        response.setSuccess(Boolean.TRUE);

        return response;
    }

    public void updateAddressAndNote (Integer id,String address, String note) {
        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));
        user.setAddress(address);
        user.setNote(note);
        userRepository.save(user);
    }
    public boolean idCardCheck(String idCard) {

        if (idCard == null) {
            return false;
        }

        idCard = idCard.trim(); // ตัดช่องว่างหน้า-หลังออก

        // ตรวจสอบว่าเป็นตัวเลขทั้งหมดเท่านั้น
        if (!idCard.matches("^[0-9]{13}$")) {
            return false;
        }

        // ตัวแรกต้องไม่ใช่ 0
        if (idCard.charAt(0) == '0') {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(idCard.charAt(i));
            sum += digit * (13 - i);
        }

        int checkDigit = (11 - (sum % 11)) % 10;
        int lastDigit = Character.getNumericValue(idCard.charAt(12));

        return checkDigit == lastDigit;
    }
}
