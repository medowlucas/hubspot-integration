package com.example.hubspotintegration.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hubspotintegration.config.HubspotOAuthProperties;
import com.example.hubspotintegration.dto.HubspotWebhookEventDto;
import com.example.hubspotintegration.exception.InvalidSignatureException;
import com.example.hubspotintegration.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Hidden
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookService webhookService;
    private final HubspotOAuthProperties hubspotOAuthProperties;

    @PostMapping("/hubspot")
    public ResponseEntity<String> receiveWebhook(HttpServletRequest request) throws Exception {
        byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
        String signature = request.getHeader("X-HubSpot-Signature");
    
        if (!isValidSignature(bodyBytes, signature)) {
            throw new InvalidSignatureException("Assinatura inválida do webhook.");
        }
    
        String payload = new String(bodyBytes, StandardCharsets.UTF_8);
    
        ObjectMapper objectMapper = new ObjectMapper();
        List<HubspotWebhookEventDto> events = objectMapper.readValue(
            payload,
            objectMapper.getTypeFactory().constructCollectionType(List.class, HubspotWebhookEventDto.class)
        );
    
        webhookService.handleContactCreation(events);
        return ResponseEntity.ok("Webhook recebido com sucesso");
    }

    private boolean isValidSignature(byte[] body, String receivedSignature) {
        try {
            String secret = hubspotOAuthProperties.getRegistration().getHubspot().getClientSecret();
            String dataToHash = secret + new String(body, StandardCharsets.UTF_8);
    
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
    
            String computedSignature = bytesToHex(hashBytes);
            return computedSignature.equals(receivedSignature);
        } catch (Exception e) {
            throw new InvalidSignatureException("Erro ao validar assinatura do webhook", e);
        }
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
