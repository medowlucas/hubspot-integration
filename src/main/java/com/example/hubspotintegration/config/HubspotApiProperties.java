package com.example.hubspotintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "hubspot.api")
@Getter
@Setter
@Validated
public class HubspotApiProperties {
    private String contactsUrl;
}
