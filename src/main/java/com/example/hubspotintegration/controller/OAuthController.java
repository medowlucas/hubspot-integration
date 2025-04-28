package com.example.hubspotintegration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hubspotintegration.service.OAuthService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/authorize")
    public ResponseEntity<String> generateAuthorizationUrl(@RequestParam String accountName) {
        String url = oAuthService.generateAuthorizationUrl(accountName);
        return ResponseEntity.ok(url);
    }

    @Hidden
    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(
            @RequestParam String code,
            @RequestParam String state) {
        oAuthService.processAuthorizationCallback(code, state);
        return ResponseEntity.ok("Conta autorizada com sucesso!");
    }
}
