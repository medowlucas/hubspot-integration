package com.example.hubspotintegration.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.hubspotintegration.config.HubspotOAuthProperties;
import com.example.hubspotintegration.dto.TokenResponseDto;
import com.example.hubspotintegration.model.Account;
import com.example.hubspotintegration.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;
    private final HubspotOAuthProperties props;

    public String generateAuthorizationUrl(String accountName) {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("O nome da conta não pode ser nulo ou vazio.");
        }

        return UriComponentsBuilder
                .fromUriString(props.getProvider().getHubspot().getAuthorizationUri())
                .queryParam("client_id", props.getRegistration().getHubspot().getClientId())
                .queryParam("redirect_uri", props.getRegistration().getHubspot().getRedirectUri())
                .queryParam("scope", props.getRegistration().getHubspot().getScope())
                .queryParam("state", accountName)
                .toUriString();
    }

    public void processAuthorizationCallback(String code, String state) {
        log.info("Processando callback para conta: {}", state);

        if (code == null || code.isBlank() || state == null || state.isBlank()) {
            throw new IllegalArgumentException("Código ou estado inválido.");
        }

        TokenResponseDto token = fetchTokenFromHubSpot("authorization_code", code, null);

        Account account = new Account();
        account.setAccountName(state);
        account.setAccessToken(token.getAccessToken());
        account.setRefreshToken(token.getRefreshToken());
        account.setExpiresAt(LocalDateTime.now().plusSeconds(token.getExpiresIn() - 60));
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);
        log.info("Token salvo com sucesso para conta: {}", state);
    }

    public void refreshAccessToken(Account account) {
        log.info("Renovando token para conta: {}", account.getAccountName());

        TokenResponseDto token = fetchTokenFromHubSpot("refresh_token", null, account.getRefreshToken());

        account.setAccessToken(token.getAccessToken());
        account.setExpiresAt(LocalDateTime.now().plusSeconds(token.getExpiresIn() - 60));
        accountRepository.save(account);

        log.info("Novo token salvo para conta: {}", account.getAccountName());
    }

    private TokenResponseDto fetchTokenFromHubSpot(String grantType, String code, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", props.getRegistration().getHubspot().getClientId());
        params.add("client_secret", props.getRegistration().getHubspot().getClientSecret());
        params.add("redirect_uri", props.getRegistration().getHubspot().getRedirectUri());

        if ("authorization_code".equals(grantType)) {
            params.add("code", code);
        } else if ("refresh_token".equals(grantType)) {
            params.add("refresh_token", refreshToken);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<TokenResponseDto> response = restTemplate.postForEntity(
                    props.getProvider().getHubspot().getTokenUri(),
                    request,
                    TokenResponseDto.class);

            TokenResponseDto token = response.getBody();

            if (token == null || token.getAccessToken() == null) {
                log.error("Token inválido retornado pelo HubSpot: {}", response);
                throw new IllegalStateException("Token inválido recebido do HubSpot.");
            }

            return token;

        } catch (RestClientException e) {
            log.error("Erro ao obter token do HubSpot: {}", e.getMessage(), e);
            throw new IllegalStateException("Erro ao obter token do HubSpot.", e);
        }
    }
}
