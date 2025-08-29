package ssi.backend.filters;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ssi.backend.dtos.JwtClaimDTO;
import ssi.backend.services.JwtService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                jwtService.validateAccessToken(token);

                JwtClaimDTO accessTokenClaim = jwtService.extractUserClaims(token);
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + accessTokenClaim.getRole().toUpperCase())
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(accessTokenClaim.getEmail(), null, authorities);

                auth.setDetails(accessTokenClaim.getUserId());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JOSEException | ParseException e) {
                throw new RuntimeException("Invalid JWT", e);
            }
        }

        filterChain.doFilter(request, response);
    }

}
