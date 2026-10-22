package com.priv.birdie.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("birdie.service")
public class AppProperties {

    private Jwt jwt;

    @Data
    public static class Jwt {
        private String secret;
        private Long expired;
    }
}
