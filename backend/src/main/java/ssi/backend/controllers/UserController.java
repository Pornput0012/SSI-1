package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AccessTokenDTO> authenticateUser(@RequestBody LoginRequestDTO dto) throws JOSEException {
        String email = dto.getEmail();
        String rawPassword = dto.getPassword();

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            throw new BadRequestException("Invalid email format");
        }

        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                String accessToken = jwtService.generateAccessToken(user.getId(), user.getNickname(), user.getEmail(), user.getUserType());
                String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail(), user.getUserType());

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .maxAge(REFRESH_TOKEN_EXP)
                        .path("/")
                        .build();

                ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
                        .maxAge(ACCESS_TOKEN_EXP)
                        .path("/")
                        .build();

                AccessTokenDTO dtoResponse = new AccessTokenDTO();
                dtoResponse.setAccessToken(accessToken);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                        .body(dtoResponse);
            } else {
                throw new UnauthorizedException("Email or Password is incorrect.");
            }
        }

        throw new UnauthorizedException("Email or Password is incorrect.");
    }


    @PostMapping("/v2/auth/refresh")
    public ResponseEntity<AccessTokenDTO> getAccessTokenFromRefreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) throws JOSEException, ParseException {
        jwtService.validateRefreshToken(refreshToken);

        String accessToken = jwtService.getAccessTokenFromRefreshToken(refreshToken);
        AccessTokenDTO dtoResponse = new AccessTokenDTO();
        dtoResponse.setAccessToken(accessToken);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .maxAge(ACCESS_TOKEN_EXP)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(dtoResponse);
    }


    @PostMapping("/v2/auth/logout")
    public ResponseEntity<Object> logoutUser() {
        ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie deleteRefreshToken = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString(), deleteRefreshToken.toString())
                .build();
    }


    @GetMapping("/v2/users")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @CookieValue(name = "accessToken", required = false) String accessToken) throws ParseException, JOSEException {

        if (accessToken == null) {
            throw new ForbiddenException("You are not signing in.");
        }

        return ResponseEntity.ok(userService.getUserProfile(accessToken));
    }

    @GetMapping("/v2/users/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Integer id,
                                                                     @CookieValue(name = "accessToken", required = false) String accessToken) throws ParseException, JOSEException {

        return ResponseEntity.ok(userService.getUserProfileById(id, accessToken));
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


}
