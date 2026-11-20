package ssi.backend.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secretVerifyEmail;
    private String secretResetPassword;
    private String secretAccess;
    private String secretRefresh;
    private String issuer;
}
