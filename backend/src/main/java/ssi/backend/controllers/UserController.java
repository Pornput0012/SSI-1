package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ssi.backend.dtos.*;
import ssi.backend.entities.User;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ForbiddenException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.UserRepository;
import ssi.backend.services.JwtService;
import ssi.backend.services.UserService;

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

    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/v2/users/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String jwtToken) throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }
        jwtService.validateVerifyEmailToken(jwtToken);

        return ResponseEntity.ok("Successfully verified");
    }

    @PostMapping("/v2/users/verify-email")
    public ResponseEntity<UserResponseDTO> verifyEmail(@RequestParam String jwtToken)
            throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadRequestException("Token is missing.");
        }

        jwtService.validateVerifyEmailToken(jwtToken);

        UserResponseDTO user = userService.verifyEmail(jwtToken);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/v2/users/authentications")
    public ResponseEntity<AccessAndRefreshTokenDTO> authenticateUser(@RequestBody LoginRequestDTO dto) throws JOSEException {
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
                        .build();

                ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
                        .maxAge(ACCESS_TOKEN_EXP)
                        .build();

                AccessAndRefreshTokenDTO dtoResponse = new AccessAndRefreshTokenDTO();
                dtoResponse.setAccessToken(accessToken);
                dtoResponse.setRefreshToken(refreshToken);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                        .body(dtoResponse);
            } else {
                throw new UnauthorizedException("Email or Password is incorrect.");
            }
        }

        throw new UnauthorizedException("Email or Password is incorrect.");
    }

    @GetMapping("/v2/users")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @CookieValue(name = "accessToken", required = false) String accessToken) throws ParseException, JOSEException {

        if (accessToken == null) {
            throw new ForbiddenException("You are not signing in.");
        }

        return ResponseEntity.ok(userService.getUserProfile(accessToken));
    }


}
