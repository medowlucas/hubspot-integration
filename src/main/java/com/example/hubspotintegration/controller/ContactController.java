package com.example.hubspotintegration.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hubspotintegration.dto.ContactDto;
import com.example.hubspotintegration.service.ContactService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Object> createContact(@RequestParam String accountName, @RequestBody @Valid ContactDto contactDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                .map(error -> Map.of(
                    "erro", error.getDefaultMessage()
                ))
                .collect(Collectors.toList());
    
            return ResponseEntity.badRequest().body(errors);
        }

        contactService.createContact(accountName, contactDto);
        return ResponseEntity.ok(Map.of("message", "Contato criado com sucesso!"));
    }
}
