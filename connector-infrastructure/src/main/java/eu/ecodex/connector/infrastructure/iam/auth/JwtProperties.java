package eu.ecodex.connector.infrastructure.iam.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector.auth.security.jwt")
public class JwtProperties {
    String secret;
    long expiration;
}
