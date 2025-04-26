package com.example.hubspotintegration.controller;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.DelegatingServletInputStream;

import com.example.hubspotintegration.config.HubspotOAuthProperties;
import com.example.hubspotintegration.dto.HubspotWebhookEventDto;
import com.example.hubspotintegration.exception.InvalidSignatureException;
import com.example.hubspotintegration.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @Mock
    private HubspotOAuthProperties hubspotOAuthProperties;

    @InjectMocks
    private WebhookController webhookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReceiveWebhookSuccessfully() throws Exception {
        HubspotOAuthProperties.Registration registration = new HubspotOAuthProperties.Registration();
        HubspotOAuthProperties.Registration.Hubspot hubspot = new HubspotOAuthProperties.Registration.Hubspot();
        hubspot.setClientSecret("test-secret");
        registration.setHubspot(hubspot);

        when(hubspotOAuthProperties.getRegistration()).thenReturn(registration);

        HubspotWebhookEventDto event = new HubspotWebhookEventDto();
        List<HubspotWebhookEventDto> events = List.of(event);

        String payload = objectMapper.writeValueAsString(events);
        String expectedSignature = computeSignature("test-secret", payload);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(
            new DelegatingServletInputStream(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)))
        );
        when(request.getHeader("X-HubSpot-Signature")).thenReturn(expectedSignature);

        ResponseEntity<String> response = webhookController.receiveWebhook(request);

        verify(webhookService, times(1)).handleContactCreation(anyList());
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Webhook recebido com sucesso");
    }

    @Test
    void shouldThrowInvalidSignatureExceptionWhenSignatureIsInvalid() throws Exception {
        HubspotOAuthProperties.Registration registration = new HubspotOAuthProperties.Registration();
        HubspotOAuthProperties.Registration.Hubspot hubspot = new HubspotOAuthProperties.Registration.Hubspot();
        hubspot.setClientSecret("test-secret");
        registration.setHubspot(hubspot);

        when(hubspotOAuthProperties.getRegistration()).thenReturn(registration);

        HubspotWebhookEventDto event = new HubspotWebhookEventDto();
        List<HubspotWebhookEventDto> events = List.of(event);

        String payload = objectMapper.writeValueAsString(events);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(
            new DelegatingServletInputStream(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)))
        );
        when(request.getHeader("X-HubSpot-Signature")).thenReturn("invalid-signature");

        assertThatThrownBy(() -> webhookController.receiveWebhook(request))
                .isInstanceOf(InvalidSignatureException.class)
                .hasMessageContaining("Assinatura inválida do webhook");
    }

    private String computeSignature(String secret, String payload) throws Exception {
        String dataToHash = secret + payload;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
