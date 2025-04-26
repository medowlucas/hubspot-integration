package com.example.hubspotintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    @Schema(description = "Email do contato", example = "joao@silva.com")
    @JsonProperty("email")
    private String email;

    @Pattern(
        regexp = "^$|^[A-Za-zÀ-ÖØ-öø-ÿ\\s'-]{2,50}$",
        message = "Nome deve conter apenas letras"
    )
    @Schema(description = "Primeiro nome", example = "João")
    @JsonProperty("firstName")
    private String firstName;

    @Pattern(
        regexp = "^$|^[A-Za-zÀ-ÖØ-öø-ÿ\\s'-]{2,50}$",
        message = "Sobrenome deve conter apenas letras"
    )
    @Schema(description = "Último nome", example = "Silva")
    @JsonProperty("lastName")
    private String lastName;

    @Pattern(
        regexp = "^$|^(?=(?:.*\\d){8,})[\\d\\s\\-().+]{8,20}$",
        message = "Telefone inválido. Deve conter no mínimo 8 digitos, apenas números, espaços, parênteses, traços e pontos"
    )
    @Schema(description = "Telefone do contato", example = "+55 (11) 91234-5678")
    @JsonProperty("phone")
    private String phone;
}