package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ssi.backend.dtos.*;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ForbiddenException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.services.JwtService;
import ssi.backend.services.UserService;

import java.text.ParseException;

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
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
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

        Buyer buyer = buyerRepository.findByEmail(email);
        if (buyer != null) {
            if (!"ACTIVE".equalsIgnoreCase(buyer.getStatus())) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            if (passwordEncoder.matches(rawPassword, buyer.getPassword())) {
                String accessToken = jwtService.generateAccessToken(buyer.getId(), buyer.getNickName(), buyer.getEmail(), "BUYER");
                String refreshToken = jwtService.generateRefreshToken(buyer.getId(), buyer.getEmail(), "BUYER");

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

        Seller seller = sellerRepository.findByEmail(email);
        if (seller != null) {
            if (!"ACTIVE".equalsIgnoreCase(seller.getStatus())) {
                throw new ForbiddenException("You need activate your account before signing in.");
            }
            if (passwordEncoder.matches(rawPassword, seller.getPassword())) {
                String accessToken = jwtService.generateAccessToken(seller.getId(), seller.getNickName(), seller.getEmail(), "SELLER");
                String refreshToken = jwtService.generateRefreshToken(seller.getId(), seller.getEmail(), "SELLER");

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
