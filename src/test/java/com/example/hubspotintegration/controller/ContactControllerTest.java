package com.example.hubspotintegration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.hubspotintegration.service.ContactService;

@ExtendWith(MockitoExtension.class)
public class ContactControllerTest {

    @InjectMocks
    private ContactController contactController;

    @Mock
    private ContactService contactService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
    }

    @Test
    void testCreateContactSuccess() throws Exception {
        doNothing().when(contactService).createContact(any(), any());

        mockMvc.perform(post("/api/contacts")
                .param("accountName", "accountNameTest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"email@example.com\",\"firstName\":\"First\",\"lastName\":\"Last\",\"phone\":\"123456789\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"Contato criado com sucesso!\"}"));
    }

    @Test
    void testCreateContactValidationError() throws Exception {
        mockMvc.perform(post("/api/contacts")
                .param("accountName", "accountNameTest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"firstName\":\"First\",\"lastName\":\"Last\",\"phone\":\"123456789\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("[{\"erro\":\"Email é obrigatório\"}]"));
    }
}
