package ssi.backend.filters;

import com.nimbusds.jose.JOSEException;
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
                System.out.println("token valid");

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
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT");
                return;
            }
        } else if(header != null){
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT");
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
