package ru.netology.cloudstorage.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.jwt")
@NoArgsConstructor
@Data
public class JwtConfig {

    private String secretKey;
    private String claimsMapKey;
    private String authorizationHeader;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;

}
