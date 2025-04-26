package com.example.hubspotintegration.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Validated
public class HubspotOAuthProperties {
    private Registration registration = new Registration();
    private Provider provider = new Provider();

    @Data
    public static class Registration {
        private Hubspot hubspot = new Hubspot();

        @Data
        public static class Hubspot {
            private String clientId;
            private String clientSecret;
            private String redirectUri;
            private List<String> scope;
        }
    }

    @Data
    public static class Provider {
        private Hubspot hubspot = new Hubspot();

        @Data
        public static class Hubspot {
            private String authorizationUri;
            private String tokenUri;
        }
    }
}
