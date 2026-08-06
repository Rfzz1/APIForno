package com.rafael.monitor_forno.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;
    private LocalDate nascimento;

    @NotBlank(message = "Senha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "A senha deve possuir no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número"
    )
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;
}
