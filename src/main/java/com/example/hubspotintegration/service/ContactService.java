package com.example.hubspotintegration.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.hubspotintegration.config.HubspotApiProperties;
import com.example.hubspotintegration.dto.ContactDto;
import com.example.hubspotintegration.exception.ContactCreationException;
import com.example.hubspotintegration.exception.RateLimitException;
import com.example.hubspotintegration.exception.ResourceConflictException;
import com.example.hubspotintegration.exception.ResourceNotFoundException;
import com.example.hubspotintegration.model.Account;
import com.example.hubspotintegration.repository.AccountRepository;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;
    private final OAuthService oAuthService;
    private final HubspotApiProperties hubspotApiProperties;

    public void createContact(String accountName, ContactDto dto) {
        log.info("Criando contato para conta: {}", accountName);

        Account account = accountRepository.findByAccountName(accountName)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + accountName));

        try {
            tryCreateContact(dto, account);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Token expirado. Tentando renovar...");

            oAuthService.refreshAccessToken(account);

            account = accountRepository.findById(account.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada após refresh"));

            tryCreateContact(dto, account);
        }
    }

    @Retry(name = "hubspotCreateContact", fallbackMethod = "handleCreateContactFallback")
    private void tryCreateContact(ContactDto dto, Account account) {
        String accessToken = account.getAccessToken();

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(buildRequestBody(dto), buildHeaders(accessToken));

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    hubspotApiProperties.getContactsUrl(),
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
            }
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Contato criado com sucesso");
            } else {
                log.warn("Resposta inesperada do HubSpot: {}", response.getStatusCode());
                throw new ContactCreationException("Erro ao criar contato: " + response.getBody());
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Contato já existe no HubSpot: {}", e.getMessage());
            throw new ResourceConflictException("Contato já existe: " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.TooManyRequests e) {
            String retryAfter = e.getResponseHeaders() != null
                    ? e.getResponseHeaders().getFirst("Retry-After")
                    : null;
            long waitTime = retryAfter != null ? Long.parseLong(retryAfter) : 2;
            log.warn("Rate limit atingido. Aguardando {} segundos antes de falhar...", waitTime);
            try {
                Thread.sleep(waitTime * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            throw new RateLimitException("Rate limit excedido, tente novamente em breve.");
        }
    }

    private Map<String, Object> buildRequestBody(ContactDto dto) {
        Map<String, Object> props = new HashMap<>();

        if (dto.getEmail() != null) {
            props.put("email", dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            props.put("firstname", dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            props.put("lastname", dto.getLastName());
        }
        if (dto.getPhone() != null) {
            props.put("phone", dto.getPhone());
        }

        Map<String, Object> request = new HashMap<>();
        request.put("properties", props);
        return request;
    }

    private HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private void handleCreateContactFallback(String accountName, ContactDto dto, Throwable ex) {
        log.error("Fallback ativado para criação de contato: {}", ex.getMessage());
        throw new ContactCreationException("Erro persistente ao criar contato. Fallback acionado.");
    }
}
