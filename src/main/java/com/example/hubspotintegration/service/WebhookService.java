package com.example.hubspotintegration.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hubspotintegration.dto.HubspotWebhookEventDto;
import com.example.hubspotintegration.model.WebhookContactEvent;
import com.example.hubspotintegration.repository.WebhookContactEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookContactEventRepository webhookContactEventRepository;

    /**
     * Processa a criação de contato e armazena no banco.
     *
     * @param events A lista de eventos recebidos pelo webhook do HubSpot.
     */
    @Transactional
    public void handleContactCreation(List<HubspotWebhookEventDto> events) {
        for (HubspotWebhookEventDto event : events) {
            log.info("Processando evento de criação de contato para o ID: {}", event.getObjectId());

            WebhookContactEvent webhookContactEvent = WebhookContactEvent.builder()
                    .objectId(event.getObjectId())
                    .subscriptionType(event.getSubscriptionType())
                    .changeSource(event.getChangeSource())
                    .changeFlag(event.getChangeFlag())
                    .portalId(event.getPortalId())
                    .appId(event.getAppId())
                    .eventId(event.getEventId())
                    .subscriptionId(event.getSubscriptionId())
                    .sourceId(event.getSourceId())
                    .attemptNumber(event.getAttemptNumber())
                    .receivedAt(LocalDateTime.now())
                    .build();

            webhookContactEventRepository.save(webhookContactEvent);

            log.info("Evento salvo com sucesso para o ID: {}", event.getObjectId());
        }
    }
}
