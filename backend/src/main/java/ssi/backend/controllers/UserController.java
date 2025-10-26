package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import ssi.backend.dtos.*;
import ssi.backend.entities.User;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ForbiddenException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.UserRepository;
import ssi.backend.services.FileService;
import ssi.backend.services.JwtService;
import ssi.backend.services.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}",
        allowCredentials = "true")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60; // 24 hours

    private static final long ACCESS_TOKEN_EXP = 30 * 60; // 30 นาที
    private static final long REFRESH_TOKEN_EXP = DEFAULT_EXPIRATION;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileService fileService;

    @Autowired
    private Validator validator;

    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/v2/auth/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String jwtToken) throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }
        jwtService.validateVerifyEmailToken(jwtToken);

        return ResponseEntity.ok("Successfully verified");
    }

    @PostMapping("/v2/auth/forgot-password")
    public ResponseEntity<ChangePasswordResponseDto> forgotPassword(@Valid @RequestBody EmailRequestDTO email) throws ParseException, JOSEException {

        return ResponseEntity.ok(userService.sendUrlResetPassword(email));
    }

    @PostMapping("/v2/auth/reset-password")
    public ResponseEntity<ChangePasswordResponseDto> resetPassword(@RequestParam String token, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.resetPassword(token, resetPasswordDTO));
    }


    @PostMapping("/v2/auth/verify-email")
    public ResponseEntity<UserResponseDTO> verifyEmail(@RequestParam String jwtToken)
            throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadRequestException("Token is missing.");
        }

        jwtService.validateVerifyEmailToken(jwtToken);

        UserResponseDTO user = userService.verifyEmail(jwtToken);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/v2/auth/login")
    public ResponseEntity<AccessTokenDTO> authenticateUser(@RequestBody(required = false) LoginRequestDTO dto) throws JOSEException {

        if (dto == null) {
            throw new BadRequestException("Username or password is missing.");
        }

        Errors errors = validator.validateObject(dto);
        if (errors.hasErrors()) {
            throw new RuntimeException(errors.toString());
        }

        String email = dto.getEmail();
        String rawPassword = dto.getPassword();
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            throw new BadRequestException("Invalid email format");
        }

        User user = userRepository.findByEmail(email);
        System.out.println("Active Login");
        if (user != null) {
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                String access_token = jwtService.generateAccessToken(user.getId(), user.getNickname(), user.getEmail(), user.getUserType());
                String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail(), user.getUserType(), user.getNickname());

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                        .httpOnly(true)
                        .maxAge(REFRESH_TOKEN_EXP)
                        .path("/")
                        .sameSite("Strict")
                        .build();

                AccessTokenDTO dtoResponse = new AccessTokenDTO();
                dtoResponse.setAccess_token(access_token);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                        .body(dtoResponse);
            } else {
                System.out.println(" Login Failure");
                throw new UnauthorizedException("Email or Password is incorrect.");
            }
        }
        System.out.println("User not found");
        throw new UnauthorizedException("Email or Password is incorrect.");
    }


    @PostMapping("/v2/auth/refresh")
    public ResponseEntity<AccessTokenDTO> getAccessTokenFromRefreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) throws JOSEException, ParseException {
        jwtService.validateRefreshToken(refreshToken);

        String access_token = jwtService.getAccessTokenFromRefreshToken(refreshToken);
        AccessTokenDTO dtoResponse = new AccessTokenDTO();
        dtoResponse.setAccess_token(access_token);

        return ResponseEntity.ok(dtoResponse);
    }


    @PostMapping("/v2/auth/logout")
    public ResponseEntity<Object> logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("No access token provided or Invalid token");
        }


        ResponseCookie deleteRefreshToken = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString())
                .build();
    }


    @GetMapping("/v2/users")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile() throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @GetMapping("/v2/users/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Integer id) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.getUserProfileById(id));
    }

    @GetMapping("/users/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType;
        try {
            Path filePath = resource.getFile().toPath();
            contentType = Files.probeContentType(filePath); // ตรวจ MIME type
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"") // inline = แสดงใน browser
                .body(resource);
    }

    @PutMapping("/v2/users/{id}")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfileById(@PathVariable Integer id, @RequestBody UpdateUserRequestDto update) throws ParseException, JOSEException {
        return ResponseEntity.ok(userService.updateUserProfile(id, update));
    }

    @PatchMapping("/v2/users/{id}/password")
    public ResponseEntity<ChangePasswordResponseDto> changePasswordById(@PathVariable Integer id, @RequestBody ChangePasswordRequestDto updatePassword) throws ParseException, JOSEException {
        ChangePasswordResponseDto dtoResponese = userService.changePasswordById(id, updatePassword);
        if (dtoResponese.isSuccess()) {
            return ResponseEntity.ok(dtoResponese);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dtoResponese);
    }

}
