package ssi.backend.filters;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ssi.backend.configuration.JwtProperties;
import ssi.backend.dtos.JwtClaimDTO;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.services.JwtService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProperties jwtProperties;

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService,JwtProperties jwtProperties) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretAccess().getBytes(StandardCharsets.UTF_8));

                Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();


                if (!signedJWT.verify(verifier) || expirationTime == null || new Date().after(expirationTime)) {
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid access token");
                    return;
                }

                JwtClaimDTO accessTokenClaim = jwtService.extractUserClaims(token);
                if (accessTokenClaim.getUserId() == null && accessTokenClaim.getRole() == null && accessTokenClaim.getEmail() == null) {
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT");
                    return;
                }
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + accessTokenClaim.getRole().toUpperCase())
                );
                System.out.println("ROLE_" + accessTokenClaim.getRole().toUpperCase());
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(accessTokenClaim.getEmail(), null, authorities);

                auth.setDetails(accessTokenClaim.getUserId());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JOSEException | ParseException e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid access token");
                return;
            }
        } else if(header != null){
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid access token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d}",
                status.getReasonPhrase(),
                message,
                status.value()
        );

        response.getWriter().write(jsonResponse);
    }
}
