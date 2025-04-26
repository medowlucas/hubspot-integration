package com.example.hubspotintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HubspotWebhookEventDto {

    @JsonProperty("appId")
    private Long appId;

    @JsonProperty("sourceId")
    private Long sourceId;

    @JsonProperty("eventId")
    private Long eventId;

    @JsonProperty("subscriptionId")
    private Long subscriptionId;

    @JsonProperty("portalId")
    private Long portalId;

    @JsonProperty("occurredAt")
    private Long occurredAt;

    @JsonProperty("subscriptionType")
    private String subscriptionType;

    @JsonProperty("attemptNumber")
    private int attemptNumber;

    @JsonProperty("objectId")
    private Long objectId;

    @JsonProperty("changeSource")
    private String changeSource;

    @JsonProperty("changeFlag")
    private String changeFlag;
}
