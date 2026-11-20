package ssi.backend.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;
import ssi.backend.configuration.JwtProperties;
import ssi.backend.dtos.JwtClaimDTO;
import ssi.backend.entities.User;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.ForbiddenException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.UserRepository;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    private static final long ACCESS_TOKEN_EXP = 30 * 60 * 1000; // 30 นาที
    private static final long REFRESH_TOKEN_EXP = DEFAULT_EXPIRATION;
    private final UserRepository userRepository;


    public JwtService(JwtProperties jwtProperties, UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
    }

    public String generateVerifyEmailToken(Integer user_id, String username, String email, String userType) throws JOSEException {

        if (username == null || email == null) {
            throw new IllegalArgumentException("Username and email cannot be null");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + DEFAULT_EXPIRATION);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("email", email)
                .claim("role", userType)
                .claim("id", user_id)
                .issueTime(now)
                .expirationTime(expiryDate)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );

        signedJWT.sign(new MACSigner(jwtProperties.getSecretVerifyEmail().getBytes(StandardCharsets.UTF_8)));

        // get Token String
        return signedJWT.serialize();
    }

    public String generateResetPasswordToken(Integer userId, String email) throws JOSEException {
        if (userId == null || email == null) {
            throw new IllegalArgumentException("User ID and email cannot be null");
        }

        Date now = new Date();

        long RESET_EXPIRATION = 15 * 60 * 1000;
        Date expiryDate = new Date(now.getTime() + RESET_EXPIRATION);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("reset_password")
                .claim("email", email)
                .claim("user_id", userId)
                .issueTime(now)
                .expirationTime(expiryDate)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );

        signedJWT.sign(new MACSigner(jwtProperties.getSecretResetPassword().getBytes(StandardCharsets.UTF_8)));

        return signedJWT.serialize();
    }


    public void validateVerifyEmailToken(String token) throws JOSEException, ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Token is required");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretVerifyEmail().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new BadRequestException("Invalid verification token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || !new Date().before(expirationTime)) {
            throw new BadRequestException("Invalid verification token");
        }
    }

    public void validateVerifyResetPassword(String token) throws JOSEException, ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Token is required");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretResetPassword().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new BadRequestException("Invalid verification token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || !new Date().before(expirationTime)) {
            throw new BadRequestException("Invalid verification token");
        }
    }

    public String generateAccessToken(Integer userId, String nickname, String email, String role) throws JOSEException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXP);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("nickname", nickname)
                .claim("id", userId)
                .claim("email", email)
                .claim("role", role)
                .issuer(jwtProperties.getIssuer())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(new MACSigner(jwtProperties.getSecretAccess().getBytes(StandardCharsets.UTF_8)));

        return signedJWT.serialize();
    }

    public void validateAccessToken(String token) throws JOSEException, ParseException {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Access token is required");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretAccess().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new BadRequestException("Invalid access token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || new Date().after(expirationTime)) {
            throw new BadRequestException("Access token expired");
        }
    }

    // ================== REFRESH TOKEN ==================
    public String generateRefreshToken(Integer userId, String email,String role,String nickName) throws JOSEException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXP);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("id", userId)
                .claim("role", role)
                .claim("nickName", nickName)
                .issuer(jwtProperties.getIssuer())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(new MACSigner(jwtProperties.getSecretRefresh().getBytes(StandardCharsets.UTF_8)));

        return signedJWT.serialize();
    }

    public void validateRefreshToken(String token) throws JOSEException, ParseException {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("No refresh token");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretRefresh().getBytes(StandardCharsets.UTF_8));

        JwtClaimDTO claim = new JwtClaimDTO();
        Long user_id = (Long) signedJWT.getJWTClaimsSet().getClaim("id");

        Integer user_id_int = user_id.intValue();
        claim.setUserId(user_id_int);


        User user = userRepository.findById(claim.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found or access denied"));


        if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ForbiddenException("User is inactive");
        }
        if (!signedJWT.verify(verifier)) {
            throw new BadRequestException("Invalid refresh token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || new Date().after(expirationTime)) {
            throw new BadRequestException("token expired");
        }
    }

    public JwtClaimDTO extractUserClaims(String token) throws ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        String email = signedJWT.getJWTClaimsSet().getClaim("email").toString();
        String userType = signedJWT.getJWTClaimsSet().getClaim("role").toString();
        Long user_id = (Long) signedJWT.getJWTClaimsSet().getClaim("id");

        Integer user_id_int = user_id.intValue();
        JwtClaimDTO emailVerifyDTO = new JwtClaimDTO();
        emailVerifyDTO.setEmail(email);
        emailVerifyDTO.setRole(userType);
        emailVerifyDTO.setUserId(user_id_int);

        return emailVerifyDTO;
    }

    // ================== GET NEW ACCESS TOKEN FROM REFRESH TOKEN ==================
    public String getAccessTokenFromRefreshToken(String refreshToken)
            throws ParseException, JOSEException {
        validateRefreshToken(refreshToken);

        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        Integer userId = ((Long) signedJWT.getJWTClaimsSet().getClaim("id")).intValue();
        String email = signedJWT.getJWTClaimsSet().getSubject();
        String role =  (String) signedJWT.getJWTClaimsSet().getClaim("role");
        String nickname = (String) signedJWT.getJWTClaimsSet().getClaim("nickName");

        return generateAccessToken(userId, nickname, email, role);
    }
}
