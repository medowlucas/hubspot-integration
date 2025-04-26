package com.example.hubspotintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hubspotintegration.model.WebhookContactEvent;

@Repository
public interface WebhookContactEventRepository extends JpaRepository<WebhookContactEvent, Long> {
}
