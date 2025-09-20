package ssi.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ssi.backend.filters.JwtAuthFilter;
import ssi.backend.services.JwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/itb-mshop/v2/sale-items/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/itb-mshop/v2/sale-items/**").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/v2/sale-items/**").authenticated()
//                        .requestMatchers(HttpMethod.DELETE, "/v2/sale-items/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 8, 1024 * 128, 2);
    }

}
