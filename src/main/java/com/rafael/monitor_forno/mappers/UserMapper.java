package com.rafael.monitor_forno.mappers;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toUserResponseDTO(Usuario usuario) {

        return UserResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .nascimento(usuario.getNascimento())
                .build();
    }

}
