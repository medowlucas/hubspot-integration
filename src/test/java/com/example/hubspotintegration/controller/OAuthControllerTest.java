package com.example.hubspotintegration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.hubspotintegration.service.OAuthService;

class OAuthControllerTest {

    @Mock
    private OAuthService oAuthService;
    
    @Mock
    private OAuthController oAuthController;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        oAuthController = new OAuthController(oAuthService);
    }

    @Test
    void testGenerateAuthorizationUrl() {
        String accountName = "testAccount";
        String expectedUrl = "https://oauth.example.com/auth?account=" + accountName;
        when(oAuthService.generateAuthorizationUrl(accountName)).thenReturn(expectedUrl);

        ResponseEntity<String> response = oAuthController.generateAuthorizationUrl(accountName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(oAuthService).generateAuthorizationUrl(accountName);
    }

    @Test
    void testHandleCallback() {
        String code = "auth_code_123";
        String state = "state_456";

        ResponseEntity<String> response = oAuthController.handleCallback(code, state);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Conta autorizada com sucesso!", response.getBody());
        verify(oAuthService).processAuthorizationCallback(code, state);
    }
}