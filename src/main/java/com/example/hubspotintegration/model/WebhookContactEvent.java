package com.example.hubspotintegration.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookContactEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long objectId;
    private String subscriptionType;
    private String changeSource;
    private String changeFlag;
    private Long portalId;
    private Long appId;
    private Long sourceId;
    private Long eventId;
    private Long subscriptionId;
    private Integer attemptNumber;
    private LocalDateTime receivedAt;

}
