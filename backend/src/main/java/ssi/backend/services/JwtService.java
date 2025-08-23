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
import ssi.backend.exceptions.BadRequestException;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours


    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String username, String email) throws JOSEException {

        if (username == null || email == null) {
            throw new IllegalArgumentException("Username and email cannot be null");
        }

        Date now = new Date();
        // 24 hours
        Date expiryDate = new Date(now.getTime() + DEFAULT_EXPIRATION);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("email", email)
                .claim("type", "email_verification")
                .issueTime(now)
                .expirationTime(expiryDate)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );

        signedJWT.sign(new MACSigner(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)));

        // get Token String
        return signedJWT.serialize();
    }

    public String extractEmail(String token) throws ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        Object emailClaim = signedJWT.getJWTClaimsSet().getClaim("email");

        if (emailClaim == null) {
            throw new IllegalArgumentException("Email claim not found");
        }

        return emailClaim.toString();
    }

    // ตรวจสอบ token ว่าลายเซ็นตรงไหม และหมดอายุหรือยัง
    public void validateToken(String token) throws JOSEException, ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Token is required");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new BadRequestException("Invalid token signature");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || !new Date().before(expirationTime)) {
            throw new BadRequestException("Token has expired");
        }
    }
}
